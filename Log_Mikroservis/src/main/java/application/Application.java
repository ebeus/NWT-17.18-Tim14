package application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@ComponentScan()
@SpringBootApplication
@EnableAutoConfiguration
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Component
    class LogCommandLineRunner implements CommandLineRunner{

        @Autowired
        LogClassRepository logClassRepository;

        @Override
        public void run(String... args) throws Exception {

            logClassRepository.save(new LogClass((long) 1,"Sign in",(long) 1,"msg1", "korisnik","user1",""));
            logClassRepository.save(new LogClass((long) 2,"Sign up",(long) 1,"msg2", "korisnik","user2",""));
            logClassRepository.save(new LogClass((long) 2,"Sign up",(long) 1,"msg3", "korisnik", "user2",""));
            logClassRepository.save(new LogClass((long) 3,"Register",(long) 1,"msg4", "korisnik", "user3",""));
            logClassRepository.save(new LogClass((long) 4,"Started trips",(long) 1,"msg5", "trips", "user3","trip1"));
            logClassRepository.save(new LogClass((long) 5,"Stopped trips",(long) 1,"msg5", "trips","user3","trip1"));


            log.info("------------------------------------------ START ----------------------------------------");

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
            log.info("LogClass found with findByLogSource('app'):");
            log.info("--------------------------------------------");
            logClassRepository.findByLogSource("app").forEach(l -> {
                Application.log.info(l.toString());
            });
            log.info("");
            log.info("------------------------------------------- END -----------------------------------------");

        }
    }

}
