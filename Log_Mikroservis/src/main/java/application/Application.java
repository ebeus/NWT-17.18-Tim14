package application;

import application.MScommunication.Receiver;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@ComponentScan()
@SpringBootApplication
@EnableAutoConfiguration
public class Application {

    private static final String topicExchangeName = "users-exchange";

    private static final String queueName = "users-queue"; //putovanjeStart.queue

    private static final String routingKey = "user."; //trip.started i trip.ended

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
