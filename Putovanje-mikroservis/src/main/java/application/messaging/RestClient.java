package application.messaging;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import antlr.collections.List;
import application.models.Korisnik;

public class RestClient {
	
	public Korisnik getUserByID(long id) {
		String URL = "http://localhost:8080/users/{id}";
		RestTemplate restTemplate = new RestTemplate();
		Korisnik korisnik = null;
		
		try {
			korisnik = (Korisnik)restTemplate.getForObject(URL, Korisnik.class,id);
		} catch (HttpClientErrorException e) {
			return korisnik;
		}
		return korisnik;
	}
	
}
