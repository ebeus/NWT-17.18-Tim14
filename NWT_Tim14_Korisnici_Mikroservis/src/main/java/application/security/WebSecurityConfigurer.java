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
	    
/*		@Bean
		public DaoAuthenticationProvider authProvider() throws Exception {
		    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		    authProvider.setUserDetailsService(userDetailsServiceBean());
		    authProvider.setPasswordEncoder(passwordEncoder);
		    return authProvider;
		}*/

/*		@Override
		@Bean
		public UserDetailsService userDetailsServiceBean() throws Exception {
			return new CustomUserDetailsService(repo);
			//return super.userDetailsService();
		}*/
		
/*		@Autowired
		public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
			log.info("==========================================================================");
			log.info("------------------ CHECK IF MATCHES ------------------------");
			log.info("Encoded 1234:" + passwordEncoder.encode("1234"));
			log.info("For matching: {bcrypt}$2a$10$K01E7L5cfVZQcp6CXqQxS./7kLRvoAGT7RvXp1aOOtR7RNjAw9zEK");
			log.info("Result: " + passwordEncoder.matches("1234", "{bcrypt}$2a$10$K01E7L5cfVZQcp6CXqQxS./7kLRvoAGT7RvXp1aOOtR7RNjAw9zEK"));
		    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
		}*/
		
		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {	
					   
		//	auth.authenticationProvider(authProvider()).userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
			List<String> privileges = new ArrayList<>();
			privileges.add("USER");
			privileges.add("ADMIN");
			
	        List<GrantedAuthority> authorities = new ArrayList<>();
	        for (String privilege : privileges) {
	            authorities.add(new SimpleGrantedAuthority(privilege));
	        }
			
			
			auth.inMemoryAuthentication()
			.withUser("user")
			.password(passwordEncoder.encode("pass"))
			.roles("USER")
			.authorities("ROLE_USER")
			.and()
			.withUser("admin")
			.password(passwordEncoder.encode("pass"))
			.authorities(authorities)
			.authorities("ROLE_ADMIN")
			.roles("ADMIN");
		}
	
	    @Override
	    protected void configure(final HttpSecurity http) throws Exception {
			http.authorizeRequests().antMatchers("/login").permitAll()
			.antMatchers("/oauth/token/revokeById/**").permitAll()
			.antMatchers("/tokens/**").permitAll()
		//	.antMatchers("/users/").hasRole("ADMIN")
		//	.antMatchers("/users/**").hasRole("USER")
			.anyRequest().authenticated()
			.and().csrf().disable();
	    }
	    
}