package application;

import application.Models.Korisnik;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class Receiver {

    private static final Logger log = LoggerFactory.getLogger(Receiver.class);

    public void receiveMessage(String message) {
        log.info("Received <" + message + ">");
    }

}
