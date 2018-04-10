package application.Controllers;

import application.Application;
import application.Models.TipKorisnika;
import application.Responses.ApiError;
import application.Exceptions.ItemNotFoundException;
import application.Responses.ApiSuccess;
import application.Repositories.TipKorisnikaRepository;
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
@RequestMapping("/userTypes")
public class TipKorisnikaController {

    private static final Logger log = LoggerFactory.getLogger(TipKorisnikaController.class);
    private final TipKorisnikaRepository tipKorisnikaRepository;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    TipKorisnikaController(TipKorisnikaRepository tipKorisnikaRepository, RabbitTemplate rabbitTemplate) {
        this.tipKorisnikaRepository = tipKorisnikaRepository;
        this.rabbitTemplate=rabbitTemplate;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    Collection<TipKorisnika> tipovi() {
        log.info("/tipovi GET");
        rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "getAllTypes","Get all types");
        return (Collection<TipKorisnika>) this.tipKorisnikaRepository.findAll();
    }


    @RequestMapping(value = "/{typeId}", method = RequestMethod.GET)
    Optional<TipKorisnika> tipWithId(@PathVariable Long typeId) {
        Optional<TipKorisnika> postojeci=tipKorisnikaRepository.findById(typeId);
        if(postojeci.isPresent()) {
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "getTypeId", "Get type with Id: " + typeId);
            return postojeci;
        }
        else {
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "errorNotFound","Type with Id: " + typeId + " not found");
            throw new ItemNotFoundException(typeId, "userType");
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    Optional<TipKorisnika> tipWithTypeName(@RequestParam("typeName") String typeName) {
        Optional<TipKorisnika> postojeci=tipKorisnikaRepository.findByTypeName(typeName);
        if(postojeci.isPresent()) {
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "getTypeName", "Get type with name: " + typeName);
            return postojeci;
        }
        else {
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "errorNotFound","Type with name: " + typeName + "not found");
            throw new ItemNotFoundException(typeName , "userType");
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    ResponseEntity<?> addType(@RequestParam String typeName) {
        if (!tipKorisnikaRepository.findByTypeName(typeName).isPresent()) {
            TipKorisnika k = new TipKorisnika(typeName);

            tipKorisnikaRepository.save(k);
            ApiSuccess apiSuccess=new ApiSuccess(HttpStatus.OK.value(),"User Type added",k);
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "addedType", "Type with name: " + typeName + " created");
            return ResponseEntity.ok(apiSuccess);
        } else {
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "errorExists", "Type with that name already exists");
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
        rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "updatedType", "Type with name: " + typeName + " updated");
        ApiSuccess apiSuccess=new ApiSuccess(HttpStatus.OK.value(),"User Type updated",stariTip);
        return ResponseEntity.ok(apiSuccess);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteType(@PathVariable("id") long id) {
        log.info("Fetching & Deleting Type with id {}", id);

        Optional<TipKorisnika> tip = tipKorisnikaRepository.findById(id);
        if (!tip.isPresent()) {
            log.error("Unable to delete. Type with id {} not found.", id);
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "errorDelete", "Unable to delete. Type with id: " + id + " not found.");
            ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(), "Unable to delete", "Unable to delete. Type with id: " + id + " not found." );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
        }
        tipKorisnikaRepository.delete(tip.get());
        rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "deletedType","Type with id: " + id + " deleted");
        ApiSuccess apiSuccess=new ApiSuccess(HttpStatus.OK.value(),"User Type deleted", tip.get());
        return ResponseEntity.ok(apiSuccess);
    }
}
