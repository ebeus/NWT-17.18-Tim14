package application;

import application.MScommunication.Receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

@EnableDiscoveryClient
@ComponentScan()
@SpringBootApplication
@EnableAutoConfiguration
public class Application {

    private static final String topicExchangeName = "users-exchange";

    private static final String queueName = "users-queue"; //putovanjeStart.queue "users-queue";
   // private static final String queueName = "putovanjeStart.queue"; //putovanjeStart.queue "users-queue";

    private static final String routingKey = "user."; //trip.started i trip.ended "user."
    //private static final String routingKey = "trip.started."; //trip.started i trip.ended "user."

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

   /* @Component
    public class EventListener {

        private Logger LOG = LoggerFactory.getLogger(EventListener.class);
        private CountDownLatch latch = new CountDownLatch(1);

        @RabbitListener(queues = "users-queue")
        public void processPaymentMessage(Object message) {
            LOG.info("Message is of type: " + message.getClass().getName());
            if(!(message instanceof byte[])) message = ((Message) message).getBody();
            String content = new String((byte[])message, StandardCharsets.UTF_8);
            LOG.info("Received on users-queue: " + content);
            latch.countDown();
        }

        @RabbitListener(queues = "putovanjeStart.queue")
        public void processOrderMessage(Object message) {
            LOG.info("Message is of type: " + message.getClass().getName());
            if(!(message instanceof byte[])) message = ((Message) message).getBody();
            String content = new String((byte[])message, StandardCharsets.UTF_8);
            LOG.info("Received on putovanjeStart.queue: " + content);
            latch.countDown();
        }
    } */

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    String getRoutingKey(int n){
        switch (n) {
            case 1:
                return "user.";
            case 2:
                return "trip.started";
            case 3:
                return "trip.ended";
        }
        return "";
    }

}
