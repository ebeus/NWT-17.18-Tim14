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
	public List<Putovanje> getByUserId(@PathVariable long userId) {
		List<Putovanje> putovanja = putovanjeRepo.findAllByidKorisnika(userId);
		if(putovanja.isEmpty())
			throw new ItemNotFoundException(userId, "Trip");
		else
			return putovanja;
	}
	
	@PreAuthorize("#oauth2.hasScope('mobile') or #oauth2.hasScope('admin')")
	@RequestMapping(value = "/{tripId}", method = RequestMethod.GET)
	public Putovanje getTrip(@PathVariable long tripId) {
		Putovanje p = putovanjeRepo.findById(tripId);

		
		if(p == null)
			throw new ItemNotFoundException(tripId, "Trip");
		
		return p;
	}
	
	@PreAuthorize("#oauth2.hasScope('mobile') or #oauth2.hasScope('admin')")
	@RequestMapping(value = "/by-name/{tripName}", method = RequestMethod.GET)
	public Putovanje getTripByName(@PathVariable String tripName) {
		Putovanje p = putovanjeRepo.findByNaziv(tripName);

		if(p == null)
			throw new ItemNotFoundException(tripName, "Trip");

		return p;
	}
	
	@PreAuthorize("#oauth2.hasScope('mobile') or #oauth2.hasScope('admin')")
	@RequestMapping(value = "/locations/{tripId}", method = RequestMethod.GET)
	public List<Lokacija> getLocations(@PathVariable long tripId) {
		Putovanje p = putovanjeRepo.findById(tripId);
		
		if(p == null)
			throw new ItemNotFoundException(tripId, "Trip");
		
		List<Lokacija> lokacije = p.getListaLokacija();
		
		if(lokacije.isEmpty())
			throw new ItemNotFoundException(tripId, "Location ");
		
		return lokacije;
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


		long idKorisnika = Long.parseLong(getExtraInfo("UID"));

		if(idKorisnika != korisnikId) {
			apiError = new ApiError(HttpStatus.BAD_REQUEST
					.value(),ConstantMessages.DESC_START_INVALID_USER_ID,
					ConstantMessages.DESC_START_INVALID_USER_ID);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
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
					korisnik.getUserName(), naziv);
			tripService.tripStarted(tripMessageReport);

			apiError = new ApiError(HttpStatus.BAD_REQUEST
					.value(),ConstantMessages.DESC_START_FAIL_INVALID_START_TIME, 
					ConstantMessages.DESC_START_FAIL_INVALID_START_TIME);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
		}
		
		
		Putovanje putovanje = new Putovanje(naziv, start_time, korisnikId);
		putovanjeRepo.save(putovanje);
		
		ApiSuccess apiSuccess = new ApiSuccess(HttpStatus.OK.value(), ConstantMessages.TYPE_TRIP_START_STRING, putovanje);
		
		tripMessageReport = new TripMessageReport(ConstantMessages.TYPE_TRIP_START, ConstantMessages.STATUS_SUCCESS,
				ConstantMessages.DESC_SUCCESS, ConstantMessages.MICROSERVICE_NAME, 
				korisnik.getUserName(), putovanje.getNaziv());
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

        long idKorisnika = Long.parseLong(getExtraInfo("UID"));

        if(idKorisnika != korisnik.getId()) {
            apiError = new ApiError(HttpStatus.BAD_REQUEST
                    .value(),ConstantMessages.DESC_START_INVALID_USER_ID,
                    ConstantMessages.DESC_START_INVALID_USER_ID);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
        }

		if(putovanje.getStart_time() > end_time) {
			tripMessageReport = new TripMessageReport(ConstantMessages.TYPE_TRIP_END, 
					ConstantMessages.STATUS_FAILED, 
					ConstantMessages.DESC_STOP_FAIL_INVALID_END_TIME, 
					ConstantMessages.MICROSERVICE_NAME,
					korisnik.getUserName(), putovanje.getNaziv());
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
					korisnik.getUserName(), putovanje.getNaziv());
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
				korisnik.getUserName(), putovanje.getNaziv());
		
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

        long idAuthKorisnika = Long.parseLong(getExtraInfo("UID"));
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
					ConstantMessages.MICROSERVICE_NAME, korisnik.getUserName(), putovanje.getNaziv());
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

        long idAuthKorisnika = Long.parseLong(getExtraInfo("UID"));
		String tipKorisnika = getExtraInfo("UType");
		long idKorisnikaPutovanja = putovanje.getIdKorisnika();

		//Admin ili autorizovan korisnik (koji je kreirao putovanje) mogu obrisati putovanje
		if((idAuthKorisnika != idKorisnikaPutovanja) && !tipKorisnika.equals("ADMIN")) {
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
	public Lokacija getLastLocationOfUser(@PathVariable long userId) {
		List<Putovanje> allTrips= putovanjeRepo.findAllByidKorisnika(userId);

		System.out.println("AALLL TRIPS: " + allTrips);

		if(allTrips.isEmpty())
			return new Lokacija();

		Putovanje lastTrip = allTrips.get(allTrips.size()-1);

		System.out.println("LAST TRIP: " + lastTrip);
		if(lastTrip == null)
			return new Lokacija();


		List<Lokacija> allLocations = lastTrip.getListaLokacija();

		System.out.println("AALLL locations: " + allLocations);

		if(allLocations.isEmpty())
			return new Lokacija();

		Lokacija lastLocation = allLocations.get(allLocations.size()-1);

		System.out.println("LAST location: " + lastLocation);
		if(lastLocation==null)
			return new Lokacija();

		return lastLocation;
	}

    private String getExtraInfo(String field) {
        auth = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationDetails oauthDetails = (OAuth2AuthenticationDetails) auth.getDetails();
        Map<String, Object> details = (Map<String, Object>) oauthDetails.getDecodedDetails();
        return details.get(field).toString();
    }
}
