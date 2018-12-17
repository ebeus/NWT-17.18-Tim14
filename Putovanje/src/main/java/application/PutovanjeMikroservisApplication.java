package application;


import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import application.models.Lokacija;
import application.models.Putovanje;
import application.repository.LokacijaRepository;
import application.repository.PutovanjeRepository;

@EnableDiscoveryClient
@ComponentScan()
@SpringBootApplication
@EnableResourceServer
@EnableAutoConfiguration
public class PutovanjeMikroservisApplication {

	private static final Logger log = LoggerFactory.getLogger(PutovanjeMikroservisApplication.class);
    public static final String ROUTING_KEY="trip.";
    public static final String QUEUE_NAME="putovanja.queue";
    public static final String TOPIC_EXCHANGE_NAME="putovanja-exchange";

	public static void main(String[] args) {
		SpringApplication.run(PutovanjeMikroservisApplication.class, args);
	}

    @Bean
    Queue queue(){
        return new Queue(QUEUE_NAME,false);
    }

    @Bean
    TopicExchange exchange(){
        return new TopicExchange(TOPIC_EXCHANGE_NAME);
    }

    @Bean
    Binding binding(Queue queue,TopicExchange topicExchange){
        return BindingBuilder.bind(queue).to(topicExchange).with(ROUTING_KEY+"*");
    }
    
    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(QUEUE_NAME);
        container.setMessageListener(listenerAdapter);
        return container;
    }
    
    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver){
        return new MessageListenerAdapter(receiver,"receiveMessage");
    }
    
	@Bean
	public CommandLineRunner init(PutovanjeRepository putovanjeRepository, LokacijaRepository lokacijaRepo) {
		return (args) -> {
			Timestamp ts1 = new Timestamp(1521579640*1000L);
			Timestamp ts2 = new Timestamp(1521579641*1000L);
			Timestamp ts3 = new Timestamp(1521579642*1000L);
			Timestamp ts4 = new Timestamp(1521579643*1000L);

			putovanjeRepository.save(new Putovanje("P1", 1521579640L ,  1521581659L,1,15));
			putovanjeRepository.save(new Putovanje("P2", 1521579659L , 1521581659L , 2, 20d));
			putovanjeRepository.save(new Putovanje("P3", 1521579659L , 1521581659L , 3,18d));


			lokacijaRepo.save(new Lokacija(ts1, 43.232345, 18.33332, putovanjeRepository.findById(1)));
			lokacijaRepo.save(new Lokacija(ts2, 43.242445, 18.43352, putovanjeRepository.findById(1)));
			lokacijaRepo.save(new Lokacija(ts1, 43.252345, 18.53332, putovanjeRepository.findById(1)));
			lokacijaRepo.save(new Lokacija(ts1, 43.262345, 18.63332, putovanjeRepository.findById(1)));
			lokacijaRepo.save(new Lokacija(ts1, 43.272345, 18.73332, putovanjeRepository.findById(1)));
			lokacijaRepo.save(new Lokacija(ts1, 43.84850367, 18.38398315, putovanjeRepository.findById(1)));

			lokacijaRepo.save(new Lokacija(ts3, 43.84850367, 18.13532, putovanjeRepository.findById(2)));
			lokacijaRepo.save(new Lokacija(ts3, 43.84850367, 18.23532, putovanjeRepository.findById(2)));
			lokacijaRepo.save(new Lokacija(ts3, 43.84850367, 18.33532, putovanjeRepository.findById(2)));
			lokacijaRepo.save(new Lokacija(ts3, 43.262545, 18.43532, putovanjeRepository.findById(2)));
			lokacijaRepo.save(new Lokacija(ts3, 43.272545, 18.53532, putovanjeRepository.findById(2)));
			lokacijaRepo.save(new Lokacija(ts3, 43.282545, 18.63532, putovanjeRepository.findById(2)));
			lokacijaRepo.save(new Lokacija(ts3, 43.82850367, 18.32398315, putovanjeRepository.findById(2)));

			lokacijaRepo.save(new Lokacija(ts4, 43.212445, 18.46352, putovanjeRepository.findById(3)));
			lokacijaRepo.save(new Lokacija(ts4, 43.222445, 18.46352, putovanjeRepository.findById(3)));
			lokacijaRepo.save(new Lokacija(ts4, 43.232445, 18.46352, putovanjeRepository.findById(3)));
			lokacijaRepo.save(new Lokacija(ts4, 43.242445, 18.46352, putovanjeRepository.findById(3)));
			lokacijaRepo.save(new Lokacija(ts4, 43.252445, 18.46352, putovanjeRepository.findById(3)));
			lokacijaRepo.save(new Lokacija(ts4, 43.89850367, 18.34398315, putovanjeRepository.findById(3)));

			List<Lokacija> listaLokacija = lokacijaRepo.findAll();
			
			
			
			List<Putovanje> putovanja = putovanjeRepository.findAll();
			for (Putovanje p : putovanja) {
				log.info(p.getNaziv());
				log.info("=====================");
				List<Lokacija> lokacije = p.getListaLokacija();
				if(lokacije == null || lokacije.isEmpty()) {
					log.info("Nema lokacija.");
				} else {
					log.info("Lokacije");
					for(Lokacija l : lokacije) {
						log.info("LAT: " + l.getLatitude() + "LNG: " + l.getLongitude());
						log.info("Timestamp: " + l.getTimestamp().toLocalDateTime());
					}
				}
				
			} 
		};
	}
}
