package application;

import application.Exceptions.ItemNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/grupe")
public class GrupaKorisnikaController {

    private static final Logger log = LoggerFactory.getLogger(GrupaKorisnikaController.class);
    private final GrupaKorisnikaRepository grupaKorisnikaRepository;

    @Autowired
    GrupaKorisnikaController(GrupaKorisnikaRepository grupaKorisnikaRepository) {
        this.grupaKorisnikaRepository = grupaKorisnikaRepository;
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    Collection<GrupaKorisnika> grupe() {
        log.info("/grupe GET");
        return (Collection<GrupaKorisnika>) this.grupaKorisnikaRepository.findAll();
    }


    @RequestMapping(value = "/{groupId}", method = RequestMethod.GET)
    Optional<GrupaKorisnika> grupaWithId(@PathVariable Long groupId) {
        Optional<GrupaKorisnika> postojeca=grupaKorisnikaRepository.findById(groupId);
        if(postojeca.isPresent())
            return postojeca;
        else {
            throw new ItemNotFoundException(groupId, "grupa");
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    Optional<GrupaKorisnika> grupaWithGroupName(@RequestParam("groupName") String groupName) {
        Optional<GrupaKorisnika> postojeca=grupaKorisnikaRepository.findByGroupName(groupName);
        if(postojeca.isPresent())
            return postojeca;
        else {
            throw new ItemNotFoundException(groupName , "grupa");
        }
    }


}
