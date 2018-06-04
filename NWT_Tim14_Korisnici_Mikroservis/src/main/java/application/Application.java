package application;

import application.Models.GrupaKorisnika;
import application.Models.Korisnik;
import application.Models.TipKorisnika;
import application.Repositories.GrupaKorisnikaRepository;
import application.Repositories.KorisnikRepository;
import application.Repositories.TipKorisnikaRepository;
import application.Utils.Constants;
//import com.fasterxml.jackson.databind.ObjectMapper;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableDiscoveryClient
@ComponentScan()
@SpringBootApplication
@EnableAutoConfiguration
public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static final String topicExchangeName=Constants.TOPIC_EXCHANGE_NAME;
    private static final  String queueName = Constants.USERS_QUEUE;

	@SuppressWarnings("deprecation")
	@Bean
	@Primary
	public PasswordEncoder passwordEncoder() {
	    PasswordEncoder defaultEncoder = new BCryptPasswordEncoder();
	    Map<String, PasswordEncoder> encoders = new HashMap<>();
	    encoders.put("bcrypt", new BCryptPasswordEncoder());
	    encoders.put("noop", NoOpPasswordEncoder.getInstance());
	 
	    DelegatingPasswordEncoder passworEncoder = new DelegatingPasswordEncoder(
	      "bcrypt", encoders);
	    passworEncoder.setDefaultPasswordEncoderForMatches(defaultEncoder);
	 
	    return passworEncoder;
	}

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }
	
    @Bean
    Queue queue(){
        return new Queue(queueName,false);
    }

    @Bean
    TopicExchange exchange(){
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    Binding binding(Queue queue,TopicExchange topicExchange){
        return BindingBuilder.bind(queue).to(topicExchange).with(Constants.USERS_ROUTING_KEY+"*");
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        return  container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver){
        return new MessageListenerAdapter(receiver,"receiveMessage");
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Component
    class KorisniciCommandLineRunner implements CommandLineRunner{

        @Autowired
        KorisnikRepository korisnikRepository;
        @Autowired
        TipKorisnikaRepository tipKorisnikaRepository;
        @Autowired
        GrupaKorisnikaRepository grupaKorisnikaRepository;

       
        private final RabbitTemplate rabbitTemplate;
        private final Receiver receiver;

     //   RestTemplate restTemplate = new RestTemplate();

        public KorisniciCommandLineRunner(Receiver receiver,RabbitTemplate rabbitTemplate){
            this.receiver=receiver;
            this.rabbitTemplate=rabbitTemplate;
        }

        @Override
        public void run(String... args) throws Exception {

            tipKorisnikaRepository.save(new TipKorisnika("ADMIN"));
            tipKorisnikaRepository.save(new TipKorisnika("USER"));
            

            GrupaKorisnika grupa1=new GrupaKorisnika("Grupa1");
            GrupaKorisnika grupa2=new GrupaKorisnika("Grupa2");
            grupaKorisnikaRepository.save(grupa1);
            grupaKorisnikaRepository.save(grupa2);

            TipKorisnika tp = tipKorisnikaRepository.findByTypeName("ADMIN").get();
            TipKorisnika userType = tipKorisnikaRepository.findByTypeName("USER").get();
            GrupaKorisnika grupa = grupaKorisnikaRepository.findByGroupName("Grupa1").get();
            korisnikRepository.save(new Korisnik("Jack", "Bauer","jBauer", passwordEncoder().encode("1234"), "jack.bau@gmail.com",
            		tp,grupa));
            korisnikRepository.save(new Korisnik("Chloe", "O'Brian","coBrian", passwordEncoder().encode("1234"), "chlo.o.b@gmail.com",
                    userType,grupa));
            korisnikRepository.save(new Korisnik("Kim", "Bauer","kBauer", passwordEncoder().encode("1234"), "kim.bau@gmail.com",userType,grupa));
       

            List<TipKorisnika> tipoviKorisnika= (List<TipKorisnika>) tipKorisnikaRepository.findAll();
            List<GrupaKorisnika> grupeKorisnika = (List<GrupaKorisnika>) grupaKorisnikaRepository.findAll();

            Long adminId=tipoviKorisnika.get(0).getId();
            Long obicniId=tipoviKorisnika.get(1).getId();

            Long grupa1Id=grupeKorisnika.get(0).getId();
            Long grupa2Id=grupeKorisnika.get(1).getId();

            log.info("Admin: " + adminId);
            log.info("Obicni: " + obicniId);

/*            for (Korisnik korisnik : korisnikRepository.findAll()) {
                korisnik.setUserTypeId(adminId);
                korisnik.setUserGroupId(grupa2Id);
                korisnik.setUserGroup(grupa2);
                korisnik.setUserType(administrator);
                korisnikRepository.save(korisnik);
            }*/

            List<Korisnik> korisnici= (List<Korisnik>) korisnikRepository.findAll();

            log.info("Svi korisnici pomocu findAll(): ");
            log.info("-------------------------------");
            for (Korisnik korisnik : korisnici) {
                log.info(korisnik.toString());
            }
            log.info("");

            log.info("Svi tipovi korisnika pomocu findAll(): ");
            log.info("-------------------------------");
            for (TipKorisnika tipKorisnika : tipKorisnikaRepository.findAll()) {
                log.info(tipKorisnika.toString());
            }
            log.info("");


            // fetch an individual customer by ID
            korisnikRepository.findById(1L)
                    .ifPresent(korisnik -> {
                        log.info("Korisnik pomocu findById(1L):");
                        log.info("--------------------------------");
                        log.info(korisnik.toString());
                        log.info("");
                    });

            // fetch customers by last name
            log.info("Customer found with findByLastName('Bauer'):");
            log.info("--------------------------------------------");
            korisnikRepository.findByLastName("Bauer").forEach(bauer -> {
                log.info(bauer.toString());
            });
            // for (Customer bauer : korisnikRepository.findByLastName("Bauer")) {
            // 	log.info(bauer.toString());
            // }
            log.info("");


            ObjectMapper mapper = new ObjectMapper();


            String abc;

           // log.info("Sending message...");
           // rabbitTemplate.convertAndSend(Application.topicExchangeName,Constants.USERS_ROUTING_KEY,korisnici.get(0).toString());

           // String response= restTemplate.getForObject("http://localhost:8080/users/9",String.class);
           // log.info("Rest template response: " + response);
        }
    }
}