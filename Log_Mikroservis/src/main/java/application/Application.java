package application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Calendar;

@ComponentScan()
@SpringBootApplication
@EnableAutoConfiguration
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public CommandLineRunner initializeLogDatabase(final LogClassRepository logClassRepository) {
        return (args) -> {
            // save a couple of logs
            logClassRepository.save(new LogClass("msg1", "bla", Calendar.getInstance().getTime()));
            logClassRepository.save(new LogClass("msg2", "blah", Calendar.getInstance().getTime()));
            logClassRepository.save(new LogClass("msg3", "app", Calendar.getInstance().getTime()));
            logClassRepository.save(new LogClass("msg4", "mah", Calendar.getInstance().getTime()));
            logClassRepository.save(new LogClass("msg5", "app", Calendar.getInstance().getTime()));

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
            logClassRepository.findByLogSource("app").forEach(log -> {
                Application.log.info(log.toString());
            });
            log.info("");
            log.info("------------------------------------------- END -----------------------------------------");
        };
    }
}
