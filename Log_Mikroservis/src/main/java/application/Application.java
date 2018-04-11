package application;

import application.model.LogClass;
import application.model.LogStatusClass;
import application.model.LogTypeClass;
import application.repository.LogClassRepository;
import application.repository.LogStatusClassRepository;
import application.repository.LogTypeClassRepository;
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
import org.springframework.stereotype.Component;

import javax.sound.midi.Receiver;

@EnableDiscoveryClient
@ComponentScan()
@SpringBootApplication
@EnableAutoConfiguration
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    static final String topicExchangeName = "spring-boot-exchange";

    static final String queueName = "spring-boot";

    @Bean
    Queue queue() {
        return new Queue(queueName, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }


    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Component
    class LogCommandLineRunner implements CommandLineRunner{

        private final RabbitTemplate rabbitTemplate;
        private final Receiver receiver;

        public LogCommandLineRunner(Receiver receiver, RabbitTemplate rabbitTemplate) {
            this.receiver = receiver;
            this.rabbitTemplate = rabbitTemplate;
        }

        @Autowired
        LogClassRepository logClassRepository;

        @Autowired
        LogTypeClassRepository logTypeClassRepository;

        @Autowired
        LogStatusClassRepository logStatusClassRepository;

        @Override
        public void run(String... args) throws Exception {

            logTypeClassRepository.save(new LogTypeClass("Sign in"));
            logTypeClassRepository.save(new LogTypeClass("Sign in"));
            logTypeClassRepository.save(new LogTypeClass("Sign out"));
            logTypeClassRepository.save(new LogTypeClass("Register"));
            logTypeClassRepository.save(new LogTypeClass("Started trips"));
            logTypeClassRepository.save(new LogTypeClass("Stopped trips"));

            logStatusClassRepository.save(new LogStatusClass("Uspjesno"));
            logStatusClassRepository.save(new LogStatusClass("Neuspjesno"));


            logClassRepository.save(new LogClass((long) 1,(long) 1,"msg1", "korisnik","user1","null"));
            logClassRepository.save(new LogClass((long) 2,(long) 1,"msg2", "korisnik","user2","null"));
            logClassRepository.save(new LogClass((long) 2,(long) 1,"msg3", "korisnik", "user2","null"));
            logClassRepository.save(new LogClass((long) 3,(long) 1,"msg4", "korisnik", "user3","null"));
            logClassRepository.save(new LogClass((long) 4,(long) 1,"msg5", "trips", "user3","trip1"));
            logClassRepository.save(new LogClass((long) 5,(long) 1,"msg5", "trips","user3","trip1"));


            log.info("------------------------------------------ START ----------------------------------------");

            // fetch all log types
            log.info("Log types found with findAll():");
            log.info("-------------------------------");
            for (LogTypeClass type : logTypeClassRepository.findAll()) {
                Application.log.info(type.toString());
            }
            log.info("");

            // fetch all log types
            log.info("Statuses found with findAll():");
            log.info("-------------------------------");
            for (LogStatusClass status : logStatusClassRepository.findAll()) {
                Application.log.info(status.toString());
            }
            log.info("");

            // fetch all logs
            log.info("Logs found with findAll():");
            log.info("-------------------------------");
            for (LogClass log : logClassRepository.findAll()) {
                Application.log.info(log.toString());
            }
            log.info("");

            // fetch an individual log by ID
            logClassRepository.findById(4L)
                    .ifPresent(log -> {
                        Application.log.info("Logs found with findById(4L):");
                        Application.log.info("--------------------------------");
                        Application.log.info(log.toString());
                        Application.log.info("");
                    });

            // fetch application by application source
            log.info("LogClass found with findBySource('app'):");
            log.info("--------------------------------------------");
            logClassRepository.findBySource("app").forEach(l -> {
                Application.log.info(l.toString());
            });

            log.info("");
            log.info("------------------------------------------- END -----------------------------------------");

        }
    }

}
