package application;

import application.Models.GrupaKorisnika;
import application.Models.Korisnik;
import application.Models.TipKorisnika;
import application.Models.Uredjaj;
import application.Repositories.GrupaKorisnikaRepository;
import application.Repositories.KorisnikRepository;
import application.Repositories.TipKorisnikaRepository;
import application.Repositories.UredjajRepository;
import application.Utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
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
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;

import java.util.List;

@EnableDiscoveryClient
@ComponentScan()
@SpringBootApplication
//@EnableResourceServer
@EnableAutoConfiguration
public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static final String topicExchangeName=Constants.TOPIC_EXCHANGE_NAME;
    private static final  String queueName = Constants.USERS_QUEUE;

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
        UredjajRepository uredjajRepository;
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

            tipKorisnikaRepository.save(new TipKorisnika("Administrator"));
            tipKorisnikaRepository.save(new TipKorisnika("Obicni"));

            uredjajRepository.save(new Uredjaj("Mobitel",(long) 0));
            uredjajRepository.save(new Uredjaj("Tramvaj",(long) 1));
            uredjajRepository.save(new Uredjaj("Trola",(long) 2));
            uredjajRepository.save(new Uredjaj("Minibus",(long) 3));

            grupaKorisnikaRepository.save(new GrupaKorisnika("Grupa1"));
            grupaKorisnikaRepository.save(new GrupaKorisnika("Grupa2"));

            korisnikRepository.save(new Korisnik("Jack", "Bauer","jBauer","1234",0L,0L, 3L));
            korisnikRepository.save(new Korisnik("Chloe", "O'Brian","coBrian","1234",0L, 0L, 4L));
            korisnikRepository.save(new Korisnik("Kim", "Bauer","kBauer","1234",0L, 0L, 5L));



            List<TipKorisnika> tipoviKorisnika= (List<TipKorisnika>) tipKorisnikaRepository.findAll();
            List<Uredjaj> uredjaji= (List<Uredjaj>) uredjajRepository.findAll();
            List<GrupaKorisnika> grupeKorisnika = (List<GrupaKorisnika>) grupaKorisnikaRepository.findAll();

            Long admin=tipoviKorisnika.get(0).getId();
            Long obicni=tipoviKorisnika.get(1).getId();

            Long mobitel=uredjaji.get(0).getDeviceTypeId();
            Long tramvaj=uredjaji.get(1).getDeviceTypeId();

            Long grupa1=grupeKorisnika.get(0).getId();
            Long grupa2=grupeKorisnika.get(1).getId();

            log.info("Admin: " + admin);
            log.info("Obicni: " + obicni);

            for (Korisnik korisnik : korisnikRepository.findAll()) {
                korisnik.setUserTypeId(admin);
                korisnik.setDeviceId(tramvaj);
                korisnik.setUserGroupId(grupa2);
                korisnikRepository.save(korisnik);
            }

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