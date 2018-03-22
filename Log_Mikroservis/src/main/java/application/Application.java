package application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Calendar;

@ComponentScan()
@SpringBootApplication
public class Application {

    private static final Logger appLog = LoggerFactory.getLogger(Application.class);

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

            // fetch all logs
            appLog.info("Loga found with findAll():");
            appLog.info("-------------------------------");
            for (LogClass logic : logClassRepository.findAll()) {
                appLog.info(logic.toString());
            }
            appLog.info("");

            // fetch an individual application by ID
    /*        logMikroservisRepository.findById(1L)
                    .ifPresent(application -> {
                        appLog.info("LogClass found with findById(1L):");
                        appLog.info("--------------------------------");
                        appLog.info(logMikroservisRepository.toString());
                        appLog.info("");
                    });
*/
            // fetch application by application source
            appLog.info("LogClass found with findByLogSource('app'):");
            appLog.info("--------------------------------------------");
            logClassRepository.findByLogSource("app").forEach(app -> {
                appLog.info(app.toString());
            });
            appLog.info("");
        };
    }
}
