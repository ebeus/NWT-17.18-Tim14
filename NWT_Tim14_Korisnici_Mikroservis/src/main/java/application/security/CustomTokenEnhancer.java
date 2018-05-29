package application.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import application.Models.Korisnik;
import application.Repositories.KorisnikRepository;

public class CustomTokenEnhancer implements TokenEnhancer{
	@Autowired
	private KorisnikRepository korisniciRepo;
	
	private String getUsersGroup(String userName) {
		Optional<Korisnik> korisnik = korisniciRepo.findByUserName(userName);
		return korisnik.get().getUserGroup().getGroupName();
	}
	
	private Long getUserID(String userName) {
		Optional<Korisnik> korisnik = korisniciRepo.findByUserName(userName);
		return korisnik.get().getId();
	}
	
	private String getUserType(String userName) {
		Optional<Korisnik> korisnik = korisniciRepo.findByUserName(userName);
		return korisnik.get().getUserType().getTypeName();
	}
	
    @Override
    public OAuth2AccessToken enhance(
     OAuth2AccessToken accessToken, 
     OAuth2Authentication authentication) {
        Map<String, Object> additionalInfo = new HashMap<>();
       // additionalInfo.put("Group", getUsersGroup(authentication.getName()));
       // additionalInfo.put("UserID", getUserID(authentication.getName()));
        //additionalInfo.put("Type", getUserType(authentication.getName()));
       // additionalInfo.put("UID", getUserID(authentication.getName().));
        additionalInfo.put("UID","1");
        additionalInfo.put("Group", "Grupa1");
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}
