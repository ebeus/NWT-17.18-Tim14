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
@RequestMapping("/tipovi")
public class TipKorisnikaController {

    private static final Logger log = LoggerFactory.getLogger(TipKorisnikaController.class);
    private final TipKorisnikaRepository tipKorisnikaRepository;

    @Autowired
    TipKorisnikaController(TipKorisnikaRepository tipKorisnikaRepository) {
        this.tipKorisnikaRepository = tipKorisnikaRepository;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    Collection<TipKorisnika> tipovi() {
        log.info("/tipovi GET");
        return (Collection<TipKorisnika>) this.tipKorisnikaRepository.findAll();
    }


    @RequestMapping(value = "/{typeId}", method = RequestMethod.GET)
    Optional<TipKorisnika> tipWithId(@PathVariable Long typeId) {
        Optional<TipKorisnika> postojeci=tipKorisnikaRepository.findById(typeId);
        if(postojeci.isPresent())
            return postojeci;
        else {
            throw new ItemNotFoundException(typeId, "tip");
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    Optional<TipKorisnika> tipWithTypeName(@RequestParam("typeName") String typeName) {
        Optional<TipKorisnika> postojeci=tipKorisnikaRepository.findByTypeName(typeName);
        if(postojeci.isPresent())
            return postojeci;
        else {
            throw new ItemNotFoundException(typeName , "tip");
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    ResponseEntity<?> addType(@RequestParam String typeName) {
        if (!tipKorisnikaRepository.findByTypeName(typeName).isPresent()) {
            TipKorisnika k = new TipKorisnika(typeName);

            tipKorisnikaRepository.save(k);
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), "Already Exists", "Type with that name already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
        }
    }

    @RequestMapping(value = "/update/{typeId}", method = RequestMethod.PUT)
    ResponseEntity<?> updateTypeName(@PathVariable Long typeId, @RequestParam String typeName) {
        TipKorisnika stariTip = tipKorisnikaRepository.findById(typeId).orElseThrow(
                () -> new ItemNotFoundException(typeId,"tip"));
        stariTip.setTypeName(typeName);

        tipKorisnikaRepository.save(stariTip);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteType(@PathVariable("id") long id) {
        log.info("Fetching & Deleting Type with id {}", id);

        Optional<TipKorisnika> tip = tipKorisnikaRepository.findById(id);
        if (!tip.isPresent()) {
            log.error("Unable to delete. Type with id {} not found.", id);
            ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(), "Unable to delete", "Unable to delete. Type with id: " + id + " not found." );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
        }
        tipKorisnikaRepository.delete(tip.get());
        return new ResponseEntity<Korisnik>(HttpStatus.NO_CONTENT);
    }

}