package application.security;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.password.PasswordEncoder;

import application.Application;
import application.Repositories.KorisnikRepository;



@Configuration
@EnableWebSecurity
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {
		@Autowired
		private KorisnikRepository repo;
		
		@Autowired
		private PasswordEncoder passwordEncoder;
		
		@Autowired
		@Qualifier("userDetailsService")
		UserDetailsService userDetailsService;
		
	    
	    private static final Logger log = LoggerFactory.getLogger(Application.class);

		
	    @Override
		@Bean
		public AuthenticationManager authenticationManagerBean() throws Exception {
			return super.authenticationManagerBean();
		}

		
		@Autowired
		public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
		}

	
	    @Override
	    protected void configure(final HttpSecurity http) throws Exception {
			http.authorizeRequests()
			.antMatchers("/login").permitAll()
			.antMatchers("/oauth/token/revokeById/**").permitAll()
			.antMatchers("/users/add*").permitAll()
			.antMatchers("/tokens/**").permitAll()
            .antMatchers("/users/add").permitAll()
			.anyRequest().authenticated()
			.and().csrf().disable();
	    }
	    
}
