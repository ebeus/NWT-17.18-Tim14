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
import application.messaging.PutovanjeConfig;
import application.messaging.RestClient;
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
	
	@Autowired
	private Queue queue;
	
	
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
		Korisnik korisnik = restClient.getUserByID(korisnikId,eurekaClient);
		
		if(korisnik == null) {
			ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(),"User does not exists", "User does not exists");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
		}

	
		if(start_time < 0) {
			ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(),"Invalid time ", "Invalid start time");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
		}
		
		
		Putovanje putovanje = new Putovanje(naziv, start_time, korisnikId);
		putovanjeRepo.save(putovanje);
		ApiSuccess apiSuccess = new ApiSuccess(HttpStatus.OK.value(), "Trip started", putovanje);
		
		TripService tripService = new TripService(rabbitTemplate, queue);
		tripService.tripStarted(putovanje);
		
		return ResponseEntity.ok(apiSuccess);
	}
	
	@RequestMapping(value = "/stop", method = RequestMethod.POST)
	ResponseEntity<?> add_trip(@RequestParam long id,
			@RequestParam long end_time) {
		
		Putovanje putovanje = putovanjeRepo.findById(id);
		
		if(putovanje == null) {
			ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(),"Trip not found ", "Trip not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
		}
		
		
		if(putovanje.getStart_time() > end_time) {
			ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(),"Invalid time ", "Invalid end");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
		}
		
		putovanje.setEnd_time(end_time);
		putovanjeRepo.save(putovanje);
		
		ApiSuccess apiSuccess = new ApiSuccess(HttpStatus.OK.value(), "OK", putovanje);
		
		TripService tripService = new TripService(rabbitTemplate, queue);
		tripService.tripStarted(putovanje);
		
		return ResponseEntity.ok(apiSuccess);
	}
	
	@RequestMapping(value = "/locations/add", method = RequestMethod.POST)
	ResponseEntity<?> add_location(@RequestParam long id_putovanja,
			@RequestParam long time,
			@RequestParam Double lat,
			@RequestParam Double lng) {
		
		if(!DataValidation.validateCoordinates(lat, lng)) {
			ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(),"Invalid coordinates ", "Invalid coordinates");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
		}
		
		Putovanje putovanje = putovanjeRepo.findById(id_putovanja);
		
		if(putovanje == null) {
			ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(),"Trip not found ", "Trip not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
		}
		
		if(putovanje.getEnd_time() != 0) {
			ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(),"Trip already ended ", "Trip already ended");
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
		
		if(putovanje == null){
			ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(),"Trip not found ", "Trip not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
		}
		
		putovanjeRepo.delete(putovanje);
		
		ApiSuccess apiSuccess = new ApiSuccess(HttpStatus.OK.value(), "DELETED", putovanje);
		return ResponseEntity.ok(apiSuccess);
	}
}
