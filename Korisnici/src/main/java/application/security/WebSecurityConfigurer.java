package application.security;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


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
			http.cors().and().csrf().disable().authorizeRequests()
			.antMatchers("/login").permitAll()
			.antMatchers("/oauth/token/revokeById/**").permitAll()
					.antMatchers("/oauth/token").permitAll()
			.antMatchers("/tokens/**").permitAll()
			.anyRequest().authenticated();
	    }

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(ImmutableList.of("*"));
		configuration.setAllowedMethods(ImmutableList.of("HEAD",
				"GET", "POST", "PUT", "DELETE", "PATCH"));
		// setAllowCredentials(true) is important, otherwise:
		// The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
		configuration.setAllowCredentials(true);
		// setAllowedHeaders is important! Without it, OPTIONS preflight request
		// will fail with 403 Invalid CORS request
		configuration.setAllowedHeaders(ImmutableList.of("Authorization", "Cache-Control", "Content-Type", "Accept", "Origin"));
		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*");
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/users/**", configuration);
		source.registerCorsConfiguration("/groups/**",configuration);
		return source;
	}
}
