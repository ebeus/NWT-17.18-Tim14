 package application.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;

import application.PutovanjeMikroservisApplication;
import application.models.Korisnik;
import application.responses.ApiError;
import javassist.NotFoundException;


public class RestClient {
	private static final Logger log = LoggerFactory.getLogger(RestClient.class);

	public Korisnik getUserByID(long id, EurekaClient eurekaClient, String token) throws NotFoundException {
		Application application = eurekaClient.getApplication("KORISNICI_MS");
		
		if(application == null)
			throw new NotFoundException("Application not found");
		
	    InstanceInfo instanceInfo = application.getInstances().get(0);
	    
	    if(instanceInfo == null)
	    	throw new NotFoundException("Application not found");
	    
	    String hostname = instanceInfo.getIPAddr();
	    int port = instanceInfo.getPort();
	    
	    String end = "/users/" + id;
	    String url = buildUrl("http://", hostname, port, end);
	    
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization",  token);
		log.info("HEADER: " + headers.getFirst("Authorization"));
		log.info("url " + url);
		HttpEntity<String> entity = new HttpEntity<String>("",headers);
		ResponseEntity<Korisnik> korisnik;
		log.info(entity.getBody());
		try {
			korisnik = restTemplate.exchange(url,HttpMethod.GET,entity, Korisnik.class);
		} catch (HttpClientErrorException e) {
			return null;
		}
		return (Korisnik)korisnik.getBody();
		
	}
	
	private String buildUrl(String protocol, String hostname, int port, String end) {
		String URL = protocol + hostname + ":" +port + end;
		return URL;
	}

}