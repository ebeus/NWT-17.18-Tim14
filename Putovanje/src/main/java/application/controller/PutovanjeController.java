package application.controller;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.discovery.EurekaClient;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import application.DataValidation;
import application.FieldConstants;
import application.exceptions.ItemNotFoundException;
import application.messaging.ConstantMessages;
import application.messaging.RestClient;
import application.messaging.TripMessageReport;
import application.messaging.TripService;
import application.models.Korisnik;
import application.models.Lokacija;
import application.models.Putovanje;
import application.repository.LokacijaRepository;
import application.repository.PutovanjeRepository;
import application.responses.ApiError;
import application.responses.ApiSuccess;
import net.bytebuddy.implementation.bytecode.constant.FieldConstant;

@RestController
@RequestMapping("/trip/")
public class PutovanjeController {
	private PutovanjeRepository putovanjeRepo;
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	LokacijaRepository lokacijaRepo;
	
	@Autowired
	EurekaClient eurekaClient;

	Authentication auth;
	
    @Autowired
    public PutovanjeController(PutovanjeRepository putovanjeRepo, RabbitTemplate rabbitTemplate) {
        this.putovanjeRepo = putovanjeRepo;
        this.rabbitTemplate = rabbitTemplate;
    }
	
	private TripMessageReport tripMessageReport = null;

	
	@PreAuthorize("#oauth2.hasScope('mobile') or #oauth2.hasScope('admin')")
	@RequestMapping(value = "/by-user/{userId}", method = RequestMethod.GET)
	public ResponseEntity<?>  getByUserId(@PathVariable long userId
			,@RequestHeader("Authorization") String token) {
		ApiError apiError = null;
		
		long idAuthKorisnika = getUserIdFromAuth();
		System.out.println("GET BY USER: ID AUTH KORISNIKA " + idAuthKorisnika + " REQ ID: " + userId);
		if((!areUsersInSameGroup(idAuthKorisnika, userId, token) 
				&& !isUserThisType(FieldConstants.USER_TYPE_ADMIN))) {
			
			apiError = new ApiError(HttpStatus.UNAUTHORIZED
					.value(),ConstantMessages.DESC_UNAUTHORIZED,
					ConstantMessages.DESC_UNAUTHORIZED);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
		}
		
		List<Putovanje> putovanja = putovanjeRepo.findAllByidKorisnika(userId);

		for (int i=0; i< putovanja.size(); i++) {
			List<Lokacija> lokacijePutovanja = lokacijaRepo.findByPutovanje(putovanja.get(i));
			System.out.println("Lokacije: " + lokacijePutovanja);
			putovanja.get(i).setListaLokacija(lokacijePutovanja);
		}

		System.out.println("Putovanja lokacije: " + putovanja.get(0).getListaLokacija());

		return new ResponseEntity<List<Putovanje>>(putovanja, HttpStatus.OK);
	}
	
	@PreAuthorize("#oauth2.hasScope('mobile') or #oauth2.hasScope('admin')")
	@RequestMapping(value = "/{tripId}", method = RequestMethod.GET)
	public ResponseEntity<?> getTrip(@PathVariable long tripId,
			@RequestHeader("Authorization") String token) {
		
		ApiError apiError = null;
		Putovanje p = putovanjeRepo.findById(tripId);

		if(!canUserReadPutovanje(p, token)) {
			apiError = new ApiError(HttpStatus.UNAUTHORIZED
					.value(),ConstantMessages.DESC_UNAUTHORIZED,
					ConstantMessages.DESC_UNAUTHORIZED);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
		}
		
		return new ResponseEntity<Putovanje>(p, HttpStatus.OK);
	}
	
	@PreAuthorize("#oauth2.hasScope('mobile') or #oauth2.hasScope('admin')")
	@RequestMapping(value = "/by-name/{tripName}", method = RequestMethod.GET)
	public ResponseEntity<?>  getTripByName(@PathVariable String tripName,
			@RequestHeader("Authorization") String token) {
		ApiError apiError = null;

		Putovanje p = putovanjeRepo.findByNaziv(tripName);
		if(p == null)
			throw new ItemNotFoundException(tripName, "Trip");
		
		if(!canUserReadPutovanje(p, token)) {
			apiError = new ApiError(HttpStatus.UNAUTHORIZED
					.value(),ConstantMessages.DESC_UNAUTHORIZED,
					ConstantMessages.DESC_UNAUTHORIZED);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
		}

		return new ResponseEntity<Putovanje>(p, HttpStatus.OK);
	}
	
	@PreAuthorize("#oauth2.hasScope('mobile') or #oauth2.hasScope('admin')")
	@RequestMapping(value = "/locations/{tripId}", method = RequestMethod.GET)
	public ResponseEntity<?> getLocations(@PathVariable long tripId,
			@RequestHeader("Authorization") String token) {
		ApiError apiError = null;
		Putovanje p = putovanjeRepo.findById(tripId);
		
		if(p == null)
			throw new ItemNotFoundException(tripId, "Trip");
		
		if(!canUserReadPutovanje(p, token)) {
			apiError = new ApiError(HttpStatus.UNAUTHORIZED
					.value(),ConstantMessages.DESC_UNAUTHORIZED,
					ConstantMessages.DESC_UNAUTHORIZED);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
		}
		
		
		List<Lokacija> lokacije = p.getListaLokacija();
		
		if(lokacije.isEmpty())
			throw new ItemNotFoundException(tripId, "Location ");
		
		return new ResponseEntity<List<Lokacija>>(lokacije, HttpStatus.OK);
	}
	
	@PreAuthorize("#oauth2.hasScope('mobile')")
	@RequestMapping(value = "/start", method = RequestMethod.POST)
	ResponseEntity<?> add_trip(@RequestParam String naziv,
			@RequestParam long start_time,
			@RequestParam long korisnikId,
			@RequestHeader("Authorization") String token) {

		RestClient restClient = new RestClient();
		Korisnik korisnik = null;
		TripService tripService = new TripService(rabbitTemplate);
		ApiError apiError = null;

		long idKorisnika = getUserIdFromAuth();

		if(idKorisnika != korisnikId) {
			apiError = new ApiError(HttpStatus.UNAUTHORIZED
					.value(),ConstantMessages.DESC_UNAUTHORIZED,
					ConstantMessages.DESC_UNAUTHORIZED);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
		}

		//Check if user exists
		
		try {
			korisnik = restClient.getUserByID(korisnikId,eurekaClient, token);
		} catch (Exception e) {
			tripMessageReport = new TripMessageReport(ConstantMessages.TYPE_TRIP_START, ConstantMessages.STATUS_FAILED, 
					ConstantMessages.DESC_START_FAIL_USER_COMMUNICATION, ConstantMessages.MICROSERVICE_NAME, "", "");
			tripService.tripStarted(tripMessageReport);

			apiError = new ApiError(HttpStatus.NOT_FOUND
					.value(),ConstantMessages.DESC_START_FAIL_USER_COMMUNICATION, 
					ConstantMessages.DESC_START_FAIL_USER_COMMUNICATION);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
		}
		
		if(korisnik == null) {
			tripMessageReport = new TripMessageReport(ConstantMessages.TYPE_TRIP_START, ConstantMessages.STATUS_FAILED, 
					ConstantMessages.DESC_START_FAIL_USER_NOT_FOUND, ConstantMessages.MICROSERVICE_NAME, "", "");
			tripService.tripStarted(tripMessageReport);

			apiError = new ApiError(HttpStatus.NOT_FOUND
					.value(),ConstantMessages.DESC_START_FAIL_USER_NOT_FOUND, 
					ConstantMessages.DESC_START_FAIL_USER_NOT_FOUND);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
		}

	
		if(start_time < 0) {
			tripMessageReport = new TripMessageReport(ConstantMessages.TYPE_TRIP_START, ConstantMessages.STATUS_FAILED, 
					ConstantMessages.DESC_START_FAIL_INVALID_START_TIME, 
					ConstantMessages.MICROSERVICE_NAME, 
					korisnik.getUsername(), naziv);
			tripService.tripStarted(tripMessageReport);

			apiError = new ApiError(HttpStatus.BAD_REQUEST
					.value(),ConstantMessages.DESC_START_FAIL_INVALID_START_TIME, 
					ConstantMessages.DESC_START_FAIL_INVALID_START_TIME);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
		}

		if(putovanjeRepo.existsBynaziv(naziv))
			naziv=naziv+"1";
		
		Putovanje putovanje = new Putovanje(naziv, start_time, korisnikId);
		putovanjeRepo.save(putovanje);
		
		ApiSuccess apiSuccess = new ApiSuccess(HttpStatus.OK.value(), ConstantMessages.TYPE_TRIP_START_STRING, putovanje);
		
		tripMessageReport = new TripMessageReport(ConstantMessages.TYPE_TRIP_START, ConstantMessages.STATUS_SUCCESS,
				ConstantMessages.DESC_SUCCESS, ConstantMessages.MICROSERVICE_NAME, 
				korisnik.getUsername(), putovanje.getNaziv());
		tripService.tripStarted(tripMessageReport);
		
		return ResponseEntity.ok(apiSuccess);
	}
	
	@PreAuthorize("#oauth2.hasScope('mobile') or #oauth2.hasScope('admin')")
	@RequestMapping(value = "/stop", method = RequestMethod.POST)
	ResponseEntity<?> stop_trip(@RequestParam long id,
							   @RequestParam long end_time,
							   @RequestParam double distance,
							   @RequestHeader("Authorization") String token) {
		
		Putovanje putovanje = putovanjeRepo.findById(id);
		ApiError apiError = null;
		RestClient restClient = new RestClient();
		TripService tripService = new TripService(rabbitTemplate);
		
		Korisnik korisnik = null;
		
		if(putovanje == null) {
			tripMessageReport = new TripMessageReport(ConstantMessages.TYPE_TRIP_END,
					ConstantMessages.STATUS_FAILED, 
					ConstantMessages.DESC_STOP_FAIL_TRIP_NOT_FOUND, 
					ConstantMessages.MICROSERVICE_NAME, "", "");
			tripService.tripEnded(tripMessageReport);

			apiError = new ApiError(HttpStatus.NOT_FOUND
					.value(),ConstantMessages.DESC_STOP_FAIL_TRIP_NOT_FOUND,
					ConstantMessages.DESC_STOP_FAIL_TRIP_NOT_FOUND);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
		}

		
		try {
			korisnik = restClient.getUserByID(putovanje.getIdKorisnika(), eurekaClient, token);
		} catch (Exception e) {
			tripMessageReport = new TripMessageReport(ConstantMessages.TYPE_TRIP_END, ConstantMessages.STATUS_FAILED, 
					ConstantMessages.DESC_START_FAIL_USER_NOT_FOUND, ConstantMessages.MICROSERVICE_NAME, "", "");
			tripService.tripStarted(tripMessageReport);

			apiError = new ApiError(HttpStatus.NOT_FOUND
					.value(),ConstantMessages.DESC_START_FAIL_USER_COMMUNICATION, 
					ConstantMessages.DESC_START_FAIL_USER_COMMUNICATION);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
		}

        long idKorisnika = getUserIdFromAuth();

        if(idKorisnika != korisnik.getId()) {
            apiError = new ApiError(HttpStatus.UNAUTHORIZED
                    .value(),ConstantMessages.DESC_UNAUTHORIZED,
                    ConstantMessages.DESC_UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
        }

		if(putovanje.getStart_time() > end_time) {
			tripMessageReport = new TripMessageReport(ConstantMessages.TYPE_TRIP_END, 
					ConstantMessages.STATUS_FAILED, 
					ConstantMessages.DESC_STOP_FAIL_INVALID_END_TIME, 
					ConstantMessages.MICROSERVICE_NAME,
					korisnik.getUsername(), putovanje.getNaziv());
			tripService.tripEnded(tripMessageReport);

			apiError = new ApiError(HttpStatus.BAD_REQUEST
					.value(),ConstantMessages.DESC_STOP_FAIL_INVALID_END_TIME, 
					ConstantMessages.DESC_STOP_FAIL_INVALID_END_TIME);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
		}
		
		if(putovanje.getEnd_time() > 0) {
			tripMessageReport = new TripMessageReport(ConstantMessages.TYPE_TRIP_END, 
					ConstantMessages.STATUS_FAILED, 
					ConstantMessages.DESC_STOP_ALREADY_ENDED,
					ConstantMessages.MICROSERVICE_NAME, 
					korisnik.getUsername(), putovanje.getNaziv());
			tripService.tripEnded(tripMessageReport);

			apiError = new ApiError(HttpStatus.BAD_REQUEST
					.value(),ConstantMessages.DESC_STOP_ALREADY_ENDED, 
					ConstantMessages.DESC_STOP_ALREADY_ENDED);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
		}
		
		putovanje.setEnd_time(end_time);
        putovanje.setDistance(distance);
		putovanjeRepo.save(putovanje);
		
		ApiSuccess apiSuccess = new ApiSuccess(HttpStatus.OK.value(), "OK", putovanje);
		tripMessageReport = new TripMessageReport(ConstantMessages.TYPE_TRIP_END,
				ConstantMessages.STATUS_SUCCESS, 
				ConstantMessages.DESC_SUCCESS,
				ConstantMessages.MICROSERVICE_NAME,
				korisnik.getUsername(), putovanje.getNaziv());
		
		tripService.tripEnded(tripMessageReport);
		
		return ResponseEntity.ok(apiSuccess);
	}
	
	@PreAuthorize("#oauth2.hasScope('mobile') or #oauth2.hasScope('admin')")
	@RequestMapping(value = "/locations/add", method = RequestMethod.POST)
	ResponseEntity<?> add_location(@RequestParam long id_putovanja,
			@RequestParam long time,
			@RequestParam Double lat,
			@RequestParam Double lng,
			@RequestHeader("Authorization") String token) {
		
		TripService tripService = new TripService(rabbitTemplate);
		Putovanje putovanje = putovanjeRepo.findById(id_putovanja);
		Korisnik korisnik = null;
		RestClient restClient = new RestClient();

		if(putovanje == null) {
			tripMessageReport = new TripMessageReport(ConstantMessages.TYPE_TRIP_END,
					ConstantMessages.STATUS_FAILED, 
					ConstantMessages.DESC_LOC_TRIP_NOT_FOUND, 
					ConstantMessages.MICROSERVICE_NAME, "", "");
			tripService.tripEnded(tripMessageReport);
			
			ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(),
					ConstantMessages.DESC_LOC_TRIP_NOT_FOUND,
					ConstantMessages.DESC_LOC_TRIP_NOT_FOUND);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
		}

        long idAuthKorisnika = getUserIdFromAuth();
        long idKorisnikaPutovanja = putovanje.getIdKorisnika();

        if(idAuthKorisnika != idKorisnikaPutovanja) {
            ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED
                    .value(), ConstantMessages.DESC_UNAUTHORIZED,
                    ConstantMessages.DESC_UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
        }

		try {
			korisnik = restClient.getUserByID(putovanje.getIdKorisnika(),eurekaClient, token);
		} catch (Exception e) {
			tripMessageReport = new TripMessageReport(ConstantMessages.TYPE_TRIP_START, ConstantMessages.STATUS_FAILED,
					ConstantMessages.DESC_START_FAIL_USER_NOT_FOUND, ConstantMessages.MICROSERVICE_NAME, "", "");
			tripService.tripStarted(tripMessageReport);

			ApiError apiError = new ApiError(HttpStatus.NOT_FOUND
					.value(),ConstantMessages.DESC_START_FAIL_USER_COMMUNICATION, 
					ConstantMessages.DESC_START_FAIL_USER_COMMUNICATION);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
		}
		
		if(!DataValidation.validateCoordinates(lat, lng)) {

			ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST
					.value(),ConstantMessages.DESC_LOC_INVALID_COORDS,
					ConstantMessages.DESC_LOC_INVALID_COORDS);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
		}
		
		
		if(putovanje.getEnd_time() != 0) {
			tripMessageReport = new TripMessageReport(ConstantMessages.TYPE_TRIP_END,
					ConstantMessages.STATUS_FAILED,
					ConstantMessages.DESC_LOC_TRIP_ENDED,
					ConstantMessages.MICROSERVICE_NAME, korisnik.getUsername(), putovanje.getNaziv());
			tripService.tripStarted(tripMessageReport);

			ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST
					.value(), ConstantMessages.DESC_LOC_TRIP_ENDED,
					ConstantMessages.DESC_LOC_TRIP_ENDED);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
		}
		
		Timestamp ts = new Timestamp(time);
		Lokacija l = new Lokacija(ts, lat, lng, putovanje);
		lokacijaRepo.save(l);
		
		ApiSuccess apiSuccess = new ApiSuccess(HttpStatus.OK.value(), "OK", l);
		return ResponseEntity.ok(apiSuccess);
	}
	
	@PreAuthorize("#oauth2.hasScope('mobile') or #oauth2.hasScope('admin')")
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> delete_trip(@PathVariable("id") long id) {
		Putovanje putovanje = putovanjeRepo.findById(id);
		TripService tripService = new TripService(rabbitTemplate);

		if(putovanje == null){
			ApiError apiError = new ApiError(HttpStatus.NOT_FOUND
					.value(), ConstantMessages.DESC_TRIP_DEL_USER_NOT_FOUND, 
					ConstantMessages.DESC_TRIP_DEL_USER_NOT_FOUND);
			
			tripMessageReport = new TripMessageReport(ConstantMessages.TYPE_TRIP_DELETE,
					ConstantMessages.STATUS_FAILED,
					ConstantMessages.DESC_TRIP_DEL_NOT_FOUND,
					ConstantMessages.MICROSERVICE_NAME, "", "");
			tripService.tripDelete(tripMessageReport);

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
		}

        long idAuthKorisnika = getUserIdFromAuth();
		long idKorisnikaPutovanja = putovanje.getIdKorisnika();

		//Admin ili autorizovan korisnik (koji je kreirao putovanje) mogu obrisati putovanje
		if((idAuthKorisnika != idKorisnikaPutovanja) || !isUserThisType(FieldConstants.USER_TYPE_ADMIN)) {
            ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED
                    .value(), ConstantMessages.DESC_UNAUTHORIZED,
                    ConstantMessages.DESC_UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
        }
		
		putovanjeRepo.delete(putovanje);
		tripMessageReport = new TripMessageReport(ConstantMessages.TYPE_TRIP_DELETE,
				ConstantMessages.STATUS_SUCCESS,
				ConstantMessages.DESC_SUCCESS,
				ConstantMessages.MICROSERVICE_NAME, "", putovanje.getNaziv());
		tripService.tripDelete(tripMessageReport);
		ApiSuccess apiSuccess = new ApiSuccess(HttpStatus.OK.value(), "DELETED", putovanje);
		return ResponseEntity.ok(apiSuccess);
	}

    @PreAuthorize("#oauth2.hasScope('mobile') or #oauth2.hasScope('admin')")
	@RequestMapping(value = "/locations/last-trip/{userId}", method = RequestMethod.GET)
	public ResponseEntity<?> getLastLocationOfUser(@PathVariable long userId,
			@RequestHeader("Authorization") String token) {
    	
    	ApiError apiError = null;
    	if(!areUsersInSameGroup(getUserIdFromAuth(), userId, token) 
    			|| !isUserThisType(FieldConstants.USER_TYPE_ADMIN)) {
            apiError = new ApiError(HttpStatus.UNAUTHORIZED
                    .value(), ConstantMessages.DESC_UNAUTHORIZED,
                    ConstantMessages.DESC_UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    	}
    	
		List<Putovanje> allTrips= putovanjeRepo.findAllByidKorisnika(userId);

		if(allTrips.isEmpty())
			return new ResponseEntity<Lokacija>(new Lokacija(), HttpStatus.OK);

		Putovanje lastTrip = allTrips.get(allTrips.size()-1);

		if(lastTrip == null)
			return new ResponseEntity<Lokacija>(new Lokacija(), HttpStatus.OK);

		List<Lokacija> allLocations = lastTrip.getListaLokacija();

		if(allLocations.isEmpty())
			return new ResponseEntity<Lokacija>(new Lokacija(), HttpStatus.OK);

		Lokacija lastLocation = allLocations.get(allLocations.size()-1);

		if(lastLocation==null)
			return new ResponseEntity<Lokacija>(new Lokacija(), HttpStatus.OK);


		return new ResponseEntity<Lokacija>(lastLocation, HttpStatus.OK);
	}

    private String getExtraInfo(String field) {
        auth = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationDetails oauthDetails = (OAuth2AuthenticationDetails) auth.getDetails();
        Map<String, Object> details = (Map<String, Object>) oauthDetails.getDecodedDetails();
        return details.get(field).toString();
    }
    
    private boolean isUserThisType(String userType) {
    	String authType = getExtraInfo(FieldConstants.USER_TYPE_TOKEN_FIELD);
    	return authType.equals(userType);
    }
    
    private boolean areUsersInSameGroup(long UserId1, long UserId2, String token) {
    	RestClient restClient = new RestClient();
    	try {
    		long group1 = (long)restClient.getUserByID(UserId1, eurekaClient, token).getUserGroupId();
    		long group2 = (long)restClient.getUserByID(UserId2, eurekaClient, token).getUserGroupId();
    	return group1 == group2;
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
			return false;
		}
    	
    }
    
    private long getUserIdFromAuth() {
		return Long.parseLong(getExtraInfo(FieldConstants.USER_ID_TOKEN_FIELD));
    }
    
    private boolean canUserReadPutovanje(Putovanje p, String token) {
		if((p.getIdKorisnika() == getUserIdFromAuth() 
				|| isUserThisType(FieldConstants.USER_TYPE_ADMIN)
				|| areUsersInSameGroup(p.getIdKorisnika(), getUserIdFromAuth(), token))) {
			return true;
		}
		return false;
    }
}
