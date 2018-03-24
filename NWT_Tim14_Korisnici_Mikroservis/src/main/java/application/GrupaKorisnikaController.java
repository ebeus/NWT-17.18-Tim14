package application;

import application.Errors.ApiError;
import application.Exceptions.ItemNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    ResponseEntity<?> addGroup(@RequestParam String groupName) {
        if (!grupaKorisnikaRepository.findByGroupName(groupName).isPresent()) {
            GrupaKorisnika k = new GrupaKorisnika(groupName);

            grupaKorisnikaRepository.save(k);
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), "Already Exists", "Group with that name already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
        }
    }

    @RequestMapping(value = "/update/{groupId}", method = RequestMethod.PUT)
    ResponseEntity<?> updateGroupName(@PathVariable Long groupId, @RequestParam String groupName) {
        GrupaKorisnika staraGrupa = grupaKorisnikaRepository.findById(groupId).orElseThrow(
                () -> new ItemNotFoundException(groupId,"grupa"));
        staraGrupa.setGroupName(groupName);

        grupaKorisnikaRepository.save(staraGrupa);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteGroup(@PathVariable("id") long id) {
        log.info("Fetching & Deleting Group with id {}", id);

        Optional<GrupaKorisnika> grupa = grupaKorisnikaRepository.findById(id);
        if (!grupa.isPresent()) {
            log.error("Unable to delete. Group with id {} not found.", id);
            ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(), "Unable to delete", "Unable to delete. Group with id: " + id + " not found." );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
        }
        grupaKorisnikaRepository.delete(grupa.get());
        return new ResponseEntity<Korisnik>(HttpStatus.NO_CONTENT);
    }
}