package application.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import application.PutovanjeMikroservisApplication;
import application.models.Korisnik;
import application.models.Putovanje;

@Component
public class TripService{
	private final RabbitTemplate rabbitTemplate;
	
	public TripService(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}
	
	public void tripStarted(TripMessageReport tripMessageReport) {
		String routingKey = PutovanjeMikroservisApplication.ROUTING_KEY;
		String message = "";
		
		try {
			message = serializeToJson(tripMessageReport);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
	    rabbitTemplate.convertAndSend(routingKey,message);
	}
	
	public void tripEnded(TripMessageReport tripMessageReport) {
		String routingKey = PutovanjeMikroservisApplication.ROUTING_KEY;
		String message = "";
		
		try {
			message = serializeToJson(tripMessageReport);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
	    rabbitTemplate.convertAndSend(routingKey, message);
	}
	
	public void tripDelete(TripMessageReport tripMessageReport) {
		String routingKey = PutovanjeMikroservisApplication.ROUTING_KEY;
		String message = "";
		
		try {
			message = serializeToJson(tripMessageReport);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
	    rabbitTemplate.convertAndSend(routingKey, message);	
	}
	
	private String serializeToJson(TripMessageReport tripMessageReport) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "";
		
		try {
			jsonString = mapper.writeValueAsString(tripMessageReport);
		} catch (JsonParseException e) {
			e.printStackTrace();
		}
		
		return jsonString;
	}

}
