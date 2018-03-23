package application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class KorisnikController {


    @RequestMapping("/korisnici")
    Collection<Korisnik> korisnici(){
        return (Collection<Korisnik>) this.korisnikRepository.findAll();
    }

    @Autowired
    KorisnikRepository korisnikRepository;

}
