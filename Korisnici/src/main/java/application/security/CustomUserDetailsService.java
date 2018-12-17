package application.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import application.Models.Korisnik;
import application.Repositories.KorisnikRepository;

import java.util.Arrays;
import java.util.List;

@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	private KorisnikRepository repo;
	

	public CustomUserDetailsService(KorisnikRepository repo) {
			this.repo = repo;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {	
		try {
			Korisnik korisnik = repo.findByUserName(username).get();
			if(korisnik == null)
				return null;
			
			return new User(korisnik.getUserName(), korisnik.getPassword(), getAuthority(korisnik));
		} catch (Exception e) {
			throw new UsernameNotFoundException("User not found");
		}
	}

	private List<SimpleGrantedAuthority> getAuthority(Korisnik korisnik) {
		return Arrays.asList(new SimpleGrantedAuthority("ROLE_" + korisnik.getUserType().getTypeName()));
	}
	
}
