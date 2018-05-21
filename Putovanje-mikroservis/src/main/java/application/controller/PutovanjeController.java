package application.controller;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	@Autowired
	PutovanjeRepository putovanjeRepo;
	@Autowired
	LokacijaRepository lokacijaRepo;
	
	@Autowired
	EurekaClient eurekaClient;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	private TripMessageReport tripMessageReport = null;

	
	@RequestMapping(value = "/by-user/{userId}", method = RequestMethod.GET)
	public List<Putovanje> getByUserId(@PathVariable long userId) {
		List<Putovanje> putovanja = putovanjeRepo.findAllByidKorisnika(userId);
		if(putovanja.isEmpty())
			throw new ItemNotFoundException(userId, "Trip");
		else
			return putovanja;
	}
	
	@RequestMapping(value = "/{tripId}", method = RequestMethod.GET)
	public Putovanje getTrip(@PathVariable long tripId) {
		Putovanje p = putovanjeRepo.findById(tripId);
		
		if(p == null)
			throw new ItemNotFoundException(tripId, "Trip");
		
		return p;
	}
	
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
	
	
	@RequestMapping(value = "/start", method = RequestMethod.POST)
	ResponseEntity<?> add_trip(@RequestParam String naziv,
			@RequestParam long start_time,
			@RequestParam long korisnikId) {
		
		//Check if user exists
		
		
		RestClient restClient = new RestClient();
		Korisnik korisnik = null;
		TripService tripService = new TripService(rabbitTemplate);
		ApiError apiError = null;
		
		try {
			korisnik = restClient.getUserByID(korisnikId,eurekaClient);
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
	
	@RequestMapping(value = "/stop", method = RequestMethod.POST)
	ResponseEntity<?> add_trip(@RequestParam long id,
			@RequestParam long end_time) {
		
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
			korisnik = restClient.getUserByID(putovanje.getIdKorisnika(),eurekaClient);
		} catch (Exception e) {
			tripMessageReport = new TripMessageReport(ConstantMessages.TYPE_TRIP_END, ConstantMessages.STATUS_FAILED, 
					ConstantMessages.DESC_START_FAIL_USER_NOT_FOUND, ConstantMessages.MICROSERVICE_NAME, "", "");
			tripService.tripStarted(tripMessageReport);

			apiError = new ApiError(HttpStatus.NOT_FOUND
					.value(),ConstantMessages.DESC_START_FAIL_USER_COMMUNICATION, 
					ConstantMessages.DESC_START_FAIL_USER_COMMUNICATION);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
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
	
	@RequestMapping(value = "/locations/add", method = RequestMethod.POST)
	ResponseEntity<?> add_location(@RequestParam long id_putovanja,
			@RequestParam long time,
			@RequestParam Double lat,
			@RequestParam Double lng) {
		
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
		
		try {
			korisnik = restClient.getUserByID(putovanje.getIdKorisnika(),eurekaClient);
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
		
		putovanjeRepo.delete(putovanje);
		tripMessageReport = new TripMessageReport(ConstantMessages.TYPE_TRIP_DELETE,
				ConstantMessages.STATUS_SUCCESS,
				ConstantMessages.DESC_SUCCESS,
				ConstantMessages.MICROSERVICE_NAME, "", putovanje.getNaziv());
		tripService.tripDelete(tripMessageReport);
		ApiSuccess apiSuccess = new ApiSuccess(HttpStatus.OK.value(), "DELETED", putovanje);
		return ResponseEntity.ok(apiSuccess);
	}
}
