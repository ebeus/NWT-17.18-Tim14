package application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

@ComponentScan()
@SpringBootApplication
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);


    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public CommandLineRunner initializeKorisniciDatabase(KorisnikRepository korisnikRepository, TipKorisnikaRepository tipKorisnikaRepository, UredjajRepository uredjajRepository, GrupaKorisnikaRepository grupaKorisnikaRepository) {
        return (args) -> {
            // save a couple of customers
            korisnikRepository.save(new Korisnik("Jack", "Bauer","jBauer","1234",(long) 0,(long) 0,(long) 0));
            korisnikRepository.save(new Korisnik("Chloe", "O'Brian","coBrian","1234",(long) 0,(long) 0,(long) 0));
            korisnikRepository.save(new Korisnik("Kim", "Bauer","kBauer","1234",(long) 0,(long) 0,(long) 0));

            tipKorisnikaRepository.save(new TipKorisnika("Administrator"));
            tipKorisnikaRepository.save(new TipKorisnika("Obicni"));

            uredjajRepository.save(new Uredjaj("Mobitel",(long) 0));
            uredjajRepository.save(new Uredjaj("Tramvaj",(long) 1));
            uredjajRepository.save(new Uredjaj("Trola",(long) 2));
            uredjajRepository.save(new Uredjaj("Minibus",(long) 3));

            grupaKorisnikaRepository.save(new GrupaKorisnika("Grupa1"));
            grupaKorisnikaRepository.save(new GrupaKorisnika("Grupa2"));

            List<TipKorisnika> tipoviKorisnika= (List<TipKorisnika>) tipKorisnikaRepository.findAll();
            List<Uredjaj> uredjaji= (List<Uredjaj>) uredjajRepository.findAll();
            List<GrupaKorisnika> grupeKorisnika = (List<GrupaKorisnika>) grupaKorisnikaRepository.findAll();

            Long admin=tipoviKorisnika.get(0).getId();
            Long obicni=tipoviKorisnika.get(1).getId();

            Long mobitel=uredjaji.get(0).getDeviceTypeId();
            Long tramvaj=uredjaji.get(1).getDeviceTypeId();

            Long grupa1=grupeKorisnika.get(0).getId();
            Long grupa2=grupeKorisnika.get(1).getId();

            log.info("Admin: " + admin);
            log.info("Obicni: " + obicni);

            for (Korisnik korisnik : korisnikRepository.findAll()) {
                    korisnik.setUserTypeId(admin);
                    korisnik.setDeviceId(tramvaj);
                    korisnik.setUserGroupId(grupa2);
                    korisnikRepository.save(korisnik);
            }

            log.info("Svi korisnici pomocu findAll(): ");
            log.info("-------------------------------");
            for (Korisnik korisnik : korisnikRepository.findAll()) {
                log.info(korisnik.toString());
            }
            log.info("");

            log.info("Svi tipovi korisnika pomocu findAll(): ");
            log.info("-------------------------------");
            for (TipKorisnika tipKorisnika : tipKorisnikaRepository.findAll()) {
                log.info(tipKorisnika.toString());
            }
            log.info("");


            // fetch an individual customer by ID
            korisnikRepository.findById(1L)
                    .ifPresent(korisnik -> {
                        log.info("Korisnik pomocu findById(1L):");
                        log.info("--------------------------------");
                        log.info(korisnik.toString());
                        log.info("");
                    });

            // fetch customers by last name
            log.info("Customer found with findByLastName('Bauer'):");
            log.info("--------------------------------------------");
            korisnikRepository.findByLastName("Bauer").forEach(bauer -> {
                log.info(bauer.toString());
            });
            // for (Customer bauer : korisnikRepository.findByLastName("Bauer")) {
            // 	log.info(bauer.toString());
            // }
            log.info("");
        };
    }

}