package application;

import application.model.LogClass;
import application.model.LogStatusClass;
import application.model.LogTypeClass;
import application.repositories.LogClassRepository;
import application.repositories.LogStatusClassRepository;
import application.repositories.LogTypeClassRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Runner.class);

    @Autowired
    LogClassRepository logClassRepository;

    @Autowired
    LogTypeClassRepository logTypeClassRepository;

    @Autowired
    LogStatusClassRepository logStatusClassRepository;

    @Override
    public void run(String... args) throws Exception {

        logTypeClassRepository.save(new LogTypeClass("Sign in"));
        logTypeClassRepository.save(new LogTypeClass("Sign out"));
        logTypeClassRepository.save(new LogTypeClass("Register"));
        logTypeClassRepository.save(new LogTypeClass("Delete user"));
        logTypeClassRepository.save(new LogTypeClass("Started trips"));
        logTypeClassRepository.save(new LogTypeClass("Stopped trips"));

        logStatusClassRepository.save(new LogStatusClass("Success"));
        logStatusClassRepository.save(new LogStatusClass("Failed"));

        logClassRepository.save(new LogClass((long) 1,(long) 1,"msg1", "korisnik","user1","null"));
        logClassRepository.save(new LogClass((long) 2,(long) 1,"msg2", "korisnik","user2","null"));
        logClassRepository.save(new LogClass((long) 4,(long) 1,"msg5", "trips", "user3","trip1"));
        logClassRepository.save(new LogClass((long) 5,(long) 1,"msg5", "trips","user3","trip1"));
        logClassRepository.save(new LogClass((long) 1,(long) 1,"msg6", "korisnik","user6","null"));



        log.info("------------------------------------------ START ----------------------------------------");
        // fetch all log types
        log.info("Log types found with findAll():");
        log.info("-------------------------------");
        for (LogTypeClass type : logTypeClassRepository.findAll()) {
            log.info(type.toString());
        }
        log.info("--------------------------------------------");
        log.info("");

        // fetch all log statuses
        log.info("Statuses found with findAll():");
        for (LogStatusClass status : logStatusClassRepository.findAll()) {
            log.info(status.toString());
        }
        log.info("--------------------------------------------");
        log.info("");

        // fetch all logs
        log.info("Logs found with findAll():");
        for (LogClass logic : logClassRepository.findAll()) {
            log.info(logic.toString());
        }
        log.info("");
        log.info("--------------------------------------------");


        // fetch an individual log by ID
        logClassRepository.findById(4L)
                .ifPresent(logic -> {
                    log.info("Logs found with findById(4L):");
                    log.info(logic.toString());
                    log.info("");
                });
        log.info("--------------------------------------------");
        log.info("");

        // fetch all logs with typeName Sign in
        log.info("LogTypeClass found with findIdByTypeName('Sign in'):");
        logTypeClassRepository.findByTypeName("Sign in").ifPresent(logTypeClass -> {
            log.info("Type found findIdByTypeName('Sign in'):");
            log.info(logTypeClass.toString());
            log.info("");
        });
        log.info("--------------------------------------------");
        log.info("");

        // fetch all logs with Status name Success
        log.info("LogStatusClass found with findByStatusName('Success'):");
        logStatusClassRepository.findByStatusName("Success").ifPresent(logStatusClass -> {
            log.info("Status found findByStatusName('Success'):");
            log.info(logStatusClass.toString());
            log.info("");
        });
        log.info("--------------------------------------------");
        log.info("");


        // fetch application by application source
        log.info("LogClass found with findBySource('app'):");
        logClassRepository.findBySource("app").forEach(l -> {
            log.info(l.toString());
        });
        log.info("--------------------------------------------");
        log.info("");
        log.info("------------------------------------------- END -----------------------------------------");

        log.info("Listening for messages...");

    }
}
