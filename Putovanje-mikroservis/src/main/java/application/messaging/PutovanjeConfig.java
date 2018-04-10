package application.messaging;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PutovanjeConfig {
	
    @Bean
    public Queue putovanjeStartQueue() {
        return new Queue("putovanjeStart.queue");
    }
}