package application;

import application.Errors.ApiError;
import application.Exceptions.ItemNotFoundException;
import application.Exceptions.KorisnikNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/korisnici")
public class KorisnikController {

    private static final Logger log = LoggerFactory.getLogger(KorisnikController.class);
    private final KorisnikRepository korisnikRepository;

    @Autowired
    KorisnikController(KorisnikRepository korisnikRepository) {
        this.korisnikRepository = korisnikRepository;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    Collection<Korisnik> korisnici() {
        log.info("/korisnici GET");
        return (Collection<Korisnik>) this.korisnikRepository.findAll();
    }


    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    Optional<Korisnik> korisnikWithId(@PathVariable Long userId) {
        this.validateKorisnikId(userId);
        return this.korisnikRepository.findById(userId);
    }

    @RequestMapping(method = RequestMethod.GET)
    Optional<Korisnik> korisnikWithUserName(@RequestParam("userName") String userName) {
        this.validateKorisnikUserName(userName);
        return this.korisnikRepository.findByUsername(userName);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    ResponseEntity<?> add(@RequestParam String firstName,
                          @RequestParam String lastName,
                          @RequestParam String userName,
                          @RequestParam String password,
                          @RequestParam Long userTypeId,
                          @RequestParam Long userGroupId,
                          @RequestParam Long deviceId) {

        if (this.validateNewKorisnik(firstName, lastName, userName, password, userTypeId, userGroupId, deviceId)) {
            Korisnik k = new Korisnik(firstName, lastName, userName, password, userTypeId, userGroupId, deviceId);

            korisnikRepository.save(k);
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), "Already Exists", "User with that username already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
        }
    }

    @RequestMapping(value = "/update/{userId}", method = RequestMethod.PUT)
    ResponseEntity<?> updateUser(@PathVariable Long userId,
                                 @RequestParam String firstName,
                                 @RequestParam String lastName,
                                 @RequestParam String userName,
                                 @RequestParam String password,
                                 @RequestParam Long userTypeId,
                                 @RequestParam Long userGroupId,
                                 @RequestParam Long deviceId) {

        Korisnik stari = korisnikRepository.findById(userId).orElseThrow(
                () -> new ItemNotFoundException(userId,"user"));

        Korisnik k = new Korisnik(firstName, lastName, userName, password, userTypeId, userGroupId, deviceId);
        stari.updateFields(k);

        korisnikRepository.save(stari);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable("id") long id) {
        log.info("Fetching & Deleting User with id {}", id);

        Optional<Korisnik> korisnik = korisnikRepository.findById(id);
        if (!korisnik.isPresent()) {
            log.error("Unable to delete. User with id {} not found.", id);
            ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(), "Unable to delete", "Unable to delete. User with id: " + id + " not found." );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
        }
        korisnikRepository.delete(korisnik.get());
        return new ResponseEntity<Korisnik>(HttpStatus.NO_CONTENT);
    }

    private void validateKorisnikId(Long userId) {
        this.korisnikRepository.findById(userId).orElseThrow(
                () -> new ItemNotFoundException(userId,"user"));
    }

    private void validateKorisnikUserName(String userName) {
        this.korisnikRepository.findByUsername(userName).orElseThrow(
                () -> new ItemNotFoundException(userName,"user"));
    }

    private boolean validateNewKorisnik(String firstName, String lastName, String userName, String password, Long userTypeId, Long userGroupId, Long deviceId) {
        boolean b = korisnikRepository.findByUsername(userName).isPresent();
        return !b;
    }
}
