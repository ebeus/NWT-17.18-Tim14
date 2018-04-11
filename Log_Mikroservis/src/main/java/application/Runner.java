package application;

import application.model.LogClass;
import application.model.LogStatusClass;
import application.model.LogTypeClass;
import application.rabbit.Receiver;
import application.repository.LogClassRepository;
import application.repository.LogStatusClassRepository;
import application.repository.LogTypeClassRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class Runner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Runner.class);

    private final RabbitTemplate rabbitTemplate;
    private final Receiver receiver;

    public Runner(Receiver receiver, RabbitTemplate rabbitTemplate) {
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
            log.info(type.toString());
        }
        log.info("");

        // fetch all log types
        log.info("Statuses found with findAll():");
        log.info("-------------------------------");
        for (LogStatusClass status : logStatusClassRepository.findAll()) {
            log.info(status.toString());
        }
        log.info("");

        // fetch all logs
        log.info("Logs found with findAll():");
        log.info("-------------------------------");
        for (LogClass logic : logClassRepository.findAll()) {
            log.info(logic.toString());
        }
        log.info("");

        // fetch an individual log by ID
        logClassRepository.findById(4L)
                .ifPresent(logic -> {
                    log.info("Logs found with findById(4L):");
                    log.info("--------------------------------");
                    log.info(logic.toString());
                    log.info("");
                });

        // fetch application by application source
        log.info("LogClass found with findBySource('app'):");
        log.info("--------------------------------------------");
        logClassRepository.findBySource("app").forEach(l -> {
            log.info(l.toString());
        });

        log.info("");
        log.info("------------------------------------------- END -----------------------------------------");

        log.info("Listening on message...");
        receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
    }
}