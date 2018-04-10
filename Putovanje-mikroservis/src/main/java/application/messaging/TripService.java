package application.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import application.models.Korisnik;
import application.models.Putovanje;

@Component
public class TripService{
	private final RabbitTemplate rabbitTemplate;
	private final Queue queue;
	
	public TripService(RabbitTemplate rabbitTemplate, Queue queue) {
		this.rabbitTemplate = rabbitTemplate;
		this.queue = queue;
	}
	
	public void tripStarted(Putovanje putovanje) {
		String routingKey = "trip.started";
		String message = "";
		
		try {
			message = serializeToJson(putovanje);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
	    rabbitTemplate.convertAndSend(routingKey, message);
	}
	
	public void tripEnded(Putovanje putovanje) {
		String routingKey = "trip.ended";
		String message = "";
		
		try {
			message = serializeToJson(putovanje);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
	    rabbitTemplate.convertAndSend(routingKey, message);
	}
	
	private String serializeToJson(Putovanje putovanje) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "";
		
		try {
			jsonString = mapper.writeValueAsString(putovanje);
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		
		return jsonString;
	}

}
