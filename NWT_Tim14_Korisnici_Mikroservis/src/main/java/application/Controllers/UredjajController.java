package application.Controllers;

import application.Application;
import application.Models.Uredjaj;
import application.Responses.ApiError;
import application.Exceptions.ItemNotFoundException;
import application.Responses.ApiSuccess;
import application.Repositories.UredjajRepository;
import application.Utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/devices")
public class UredjajController {

    private static final Logger log = LoggerFactory.getLogger(UredjajController.class);
    private final UredjajRepository uredjajRepository;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public UredjajController(UredjajRepository uredjajRepository, RabbitTemplate rabbitTemplate) {
        this.uredjajRepository = uredjajRepository;
        this.rabbitTemplate=rabbitTemplate;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Collection<Uredjaj> uredjaji() {
        log.info("/uredjaji GET");
        rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "getAllDevices","Get all devices");
        return (Collection<Uredjaj>) this.uredjajRepository.findAll();
    }

    @RequestMapping(value = "/{deviceId}", method = RequestMethod.GET)
    public Optional<Uredjaj> uredjajWithId(@PathVariable Long deviceId) {
        Optional<Uredjaj> postojeci= uredjajRepository.findById(deviceId);
        if(postojeci.isPresent()) {
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "getDeviceId", "Get device with Id: " + deviceId);
            return postojeci;
        }
        else {
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "errorNotFound", "Device with Id: " + deviceId + " not found");
            throw new ItemNotFoundException(deviceId, "uredjaj");
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public Optional<Uredjaj> uredjajWithDeviceName(@RequestParam("deviceName") String deviceName) {
        Optional<Uredjaj> postojeci= uredjajRepository.findByDeviceName(deviceName);
        if(postojeci.isPresent()) {
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "getDeviceName", "Get device with name: " + deviceName);
            return postojeci;
        }
        else {
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "errorNotFound","Device with name: " + deviceName + "not found");
            throw new ItemNotFoundException(deviceName , "device");
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<?> addDevice(@RequestParam String deviceName, @RequestParam long deviceTypeId) {
        if (!uredjajRepository.findByDeviceName(deviceName).isPresent()) {
            Uredjaj k = new Uredjaj(deviceName,deviceTypeId);

            uredjajRepository.save(k);
            ApiSuccess apiSuccess=new ApiSuccess(HttpStatus.OK.value(),"Device added",k);
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "addedDevice", "Device with name: " + deviceName + " created");
            return ResponseEntity.ok(apiSuccess);
        } else {
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "errorExists", "Device with that name already exists");
            ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), "Already Exists", "Device with that name already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
        }
    }

    @RequestMapping(value = "/update/{deviceId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateDeviceName(@PathVariable Long deviceId, @RequestParam String deviceName) {
        Uredjaj stariUredjaj = uredjajRepository.findById(deviceId).orElseThrow(
                () -> new ItemNotFoundException(deviceId,"device"));
        stariUredjaj.setDeviceName(deviceName);

        rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "updatedDevice", "Device with name: " + deviceName + " updated");
        uredjajRepository.save(stariUredjaj);
        ApiSuccess apiSuccess=new ApiSuccess(HttpStatus.OK.value(),"Device updated",stariUredjaj);
        return ResponseEntity.ok(apiSuccess);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteDevice(@PathVariable("id") long id) {
        log.info("Fetching & Deleting Device with id {}", id);

        Optional<Uredjaj> uredjaj = uredjajRepository.findById(id);
        if (!uredjaj.isPresent()) {
            log.error("Unable to delete. Device with id {} not found.", id);
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "errorDelete", "Unable to delete. Device with id: " + id + " not found.");
            ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(), "Unable to delete", "Unable to delete. Device with id: " + id + " not found." );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
        }
        uredjajRepository.delete(uredjaj.get());
        ApiSuccess apiSuccess=new ApiSuccess(HttpStatus.OK.value(),"Device deleted", uredjaj.get());
        rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "deletedDevice","Device with id: " + id + " deleted");
        return ResponseEntity.ok(apiSuccess);
    }
}
