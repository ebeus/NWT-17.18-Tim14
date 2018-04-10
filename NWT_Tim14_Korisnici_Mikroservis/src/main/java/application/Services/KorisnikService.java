package application.Services;

import application.Application;
import application.Models.Korisnik;
import application.Repositories.KorisnikRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class KorisnikService {

    private final KorisnikRepository korisnikRepository;
    private final RabbitTemplate rabbitTemplate;

    public KorisnikService(KorisnikRepository korisnikRepository, RabbitTemplate rabbitTemplate) {
        this.korisnikRepository = korisnikRepository;
        this.rabbitTemplate=rabbitTemplate;
    }

    public void createUser(Korisnik korisnik){
        korisnikRepository.save(korisnik);

    }
}
