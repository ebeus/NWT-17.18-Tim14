 package application.messaging;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;

import application.models.Korisnik;


public class RestClient {
	
	public Korisnik getUserByID(long id, EurekaClient eurekaClient) {
		Application application = eurekaClient.getApplication("KORISNICI_MS");
	    InstanceInfo instanceInfo = application.getInstances().get(0);
	    
	    String hostname = instanceInfo.getHostName();
	    int port = instanceInfo.getPort();
	    
	    String end = "/users/{id}";
	    String url = buildUrl("http://", hostname, port, end);
	    
		RestTemplate restTemplate = new RestTemplate();
		Korisnik korisnik = null;
		
		try {
			korisnik = (Korisnik)restTemplate.getForObject(url, Korisnik.class,id);
		} catch (HttpClientErrorException e) {
			return korisnik;
		}
		return korisnik;
		
	}
	
	private String buildUrl(String protocol, String hostname, int port, String end) {
		String URL = protocol + hostname + ":" +port + end;
		return URL;
	}

}
