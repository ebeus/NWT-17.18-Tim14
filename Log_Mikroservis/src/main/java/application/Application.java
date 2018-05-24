package application;

import application.model.LogClass;
import application.repositories.LogClassRepository;
import application.utils.Constants;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

@EnableDiscoveryClient
@ComponentScan()
@SpringBootApplication
//@EnableResourceServer
@EnableAutoConfiguration
public class Application {

    @Autowired
    LogClassRepository logClassRepository;

    @Component
    public class EventListener {

        private Logger LOG = LoggerFactory.getLogger(EventListener.class);
        private CountDownLatch latch = new CountDownLatch(1);

        @RabbitListener(queues = Constants.USERS_QUEUE)
        public void processUsersMessage(Object message) {
            if(!(message instanceof byte[])) message = ((Message) message).getBody();
            String recievedMessage = new String((byte[])message, StandardCharsets.UTF_8);
            LOG.info("KORISNICI MS | Received [" + recievedMessage + "]");
            logClassRepository.save(getRecievedLog(recievedMessage));
            latch.countDown();
        }

        @RabbitListener(queues = Constants.TRIPS_QUEUE)
        public void processTripsMessage(Object message) {
            if(!(message instanceof byte[])) message = ((Message) message).getBody();
            String recievedMessage = new String((byte[])message, StandardCharsets.UTF_8);
            LOG.info("PUTOVANJA MS | Received [" + recievedMessage + "]");
            logClassRepository.save(getRecievedLog(recievedMessage));
            latch.countDown();
        }

        private LogClass getRecievedLog(String message) {
            JSONObject obj = new JSONObject(message);

            int messageType = obj.getInt("messageType");
            int messageStatus = obj.getInt("messageStatus");
            String messageDescription = obj.getString("messageDescription");
            String messageMicroservice = obj.getString("messageMicroservice");
            String username = obj.getString("username");
            String tripName = "NO DATA";
            if(messageType > 3 && messageType < 6)
                tripName = obj.getString("tripName");

            return new LogClass((long) messageType,(long) messageStatus,messageDescription, messageMicroservice,username,tripName);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /*  Test Listener

   // private static final String topicExchangeName = "users-exchange";
    private static final String topicExchangeName = "putovanja-exchange";

    //private static final String queueName = "users-queue"; //putovanjeStart.queue "users-queue";
   private static final String queueName = "putovanja.queue"; //putovanjeStart.queue "users-queue";

    //private static final String routingKey = "user."; //trip.started i trip.ended "user."
    private static final String routingKey = "trip."; //trip.started i trip.ended "user."

    @Bean
    Queue queue() {
        return new Queue(queueName, false);
    }

    @Bean
    TopicExchange exchange(){
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    Binding binding(TopicExchange exchange) {
        return BindingBuilder.bind(queue()).to(exchange).with(routingKey+"*");
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
*/

}
