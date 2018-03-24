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
@RequestMapping("/uredjaji")
public class UredjajController {

    private static final Logger log = LoggerFactory.getLogger(UredjajController.class);
    private final UredjajRepository uredjajRepository;

    @Autowired
    UredjajController(UredjajRepository uredjajRepository) {
        this.uredjajRepository = uredjajRepository;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    Collection<Uredjaj> uredjaji() {
        log.info("/uredjaji GET");
        return (Collection<Uredjaj>) this.uredjajRepository.findAll();
    }

    @RequestMapping(value = "/{deviceId}", method = RequestMethod.GET)
    Optional<Uredjaj> uredjajWithId(@PathVariable Long deviceId) {
        Optional<Uredjaj> postojeci= uredjajRepository.findById(deviceId);
        if(postojeci.isPresent())
            return postojeci;
        else {
            throw new ItemNotFoundException(deviceId, "uredjaj");
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    Optional<Uredjaj> uredjajWithDeviceName(@RequestParam("deviceName") String deviceName) {
        Optional<Uredjaj> postojeci= uredjajRepository.findByDeviceName(deviceName);
        if(postojeci.isPresent())
            return postojeci;
        else {
            throw new ItemNotFoundException(deviceName , "uredjaj");
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    ResponseEntity<?> addDevice(@RequestParam String deviceName, @RequestParam long deviceTypeId) {
        if (!uredjajRepository.findByDeviceName(deviceName).isPresent()) {
            Uredjaj k = new Uredjaj(deviceName,deviceTypeId);

            uredjajRepository.save(k);
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), "Already Exists", "Device with that name already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
        }
    }

    @RequestMapping(value = "/update/{deviceId}", method = RequestMethod.PUT)
    ResponseEntity<?> updateDeviceName(@PathVariable Long deviceId, @RequestParam String deviceName) {
        Uredjaj stariTip = uredjajRepository.findById(deviceId).orElseThrow(
                () -> new ItemNotFoundException(deviceId,"uredjaj"));
        stariTip.setDeviceName(deviceName);

        uredjajRepository.save(stariTip);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteDevice(@PathVariable("id") long id) {
        log.info("Fetching & Deleting Device with id {}", id);

        Optional<Uredjaj> uredjaj = uredjajRepository.findById(id);
        if (!uredjaj.isPresent()) {
            log.error("Unable to delete. Device with id {} not found.", id);
            ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(), "Unable to delete", "Unable to delete. Device with id: " + id + " not found." );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
        }
        uredjajRepository.delete(uredjaj.get());
        return new ResponseEntity<Uredjaj>(HttpStatus.NO_CONTENT);
    }
}
