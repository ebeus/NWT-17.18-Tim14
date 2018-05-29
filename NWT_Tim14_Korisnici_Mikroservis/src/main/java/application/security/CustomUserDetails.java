package application.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import application.Models.Korisnik;


public class CustomUserDetails implements UserDetails {
	
	@Autowired
	public PasswordEncoder passwordEncoder;
	
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetails.class);
    
	private String username;
	private String password;
	Collection<? extends GrantedAuthority> authorities;
	
	public CustomUserDetails(Korisnik korisnik) {
		this.username = korisnik.getUserName();
		this.password = (korisnik.getPassword());
		log.info("-----------------------------------------");
		log.info("----------!@!@!@!@!@!!@!@!---------------");
		log.info(korisnik.getUserName());
		log.info(korisnik.getPassword());
	//	log.info("Encoded: " + this.password);
		log.info(korisnik.toString());

		List<GrantedAuthority> auths = new ArrayList<>();
		auths.add(new SimpleGrantedAuthority(korisnik.getUserType().getTypeName()));
		this.authorities = auths;
		
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return this.username;
	}

	@Override
	public String getUsername() {
		return this.password;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
	
}
