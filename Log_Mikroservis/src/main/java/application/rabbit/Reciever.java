package application.rabbit;

import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class Reciever {
/*    public static final String USERS_ROUTING_KEY="user.";
    public static final String USERS_QUEUE="users-queue";
    public static final String TOPIC_EXCHANGE_NAME="users-exchange";*/

        private CountDownLatch latch = new CountDownLatch(1);

        public void receiveMessage(String message) {
            System.out.println("Received <" + message + ">");
            latch.countDown();
        }

        public CountDownLatch getLatch() {
            return latch;
        }

}
