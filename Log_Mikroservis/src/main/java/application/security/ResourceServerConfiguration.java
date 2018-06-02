package application.security;

import application.security.CustomAccessTokenConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;


@Configuration
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private String SIGNING_KEY = "d98a8za0yzq55amr3e6mqr2";
	@Autowired
	CustomAccessTokenConverter customAccesstokenConverter;
    
	
    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .anyRequest()
            .permitAll();
    }
    
    @Override
    public void configure(ResourceServerSecurityConfigurer config) {
        config.tokenServices(tokenServices());
    }
 
 
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }
    
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setAccessTokenConverter(customAccesstokenConverter);
        converter.setSigningKey(SIGNING_KEY);
        return converter;
    }
 
    @Primary
    @Bean
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }
}

