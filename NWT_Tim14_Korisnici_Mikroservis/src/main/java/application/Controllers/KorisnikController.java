package application.Controllers;

import application.Application;
import application.Repositories.KorisnikRepository;
import application.Models.Korisnik;
import application.Responses.ApiError;
import application.Exceptions.ItemNotFoundException;
import application.Responses.ApiSuccess;
import application.Responses.LogMessage;
import application.Utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class KorisnikController {

    private static final Logger log = LoggerFactory.getLogger(KorisnikController.class);
    private final KorisnikRepository korisnikRepository;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public KorisnikController(KorisnikRepository korisnikRepository, RabbitTemplate rabbitTemplate) {
        this.korisnikRepository = korisnikRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Collection<Korisnik> korisnici() {
        log.info("Get all users");
        rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "getAll","Get all users");
        return (Collection<Korisnik>) this.korisnikRepository.findAll();
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public Optional<Korisnik> korisnikWithId(@PathVariable Long userId) {
        log.info("Get user with id: " + userId);
        rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "getId","Get user with id: " + userId);
        this.userWithIdExists(userId);
        return this.korisnikRepository.findById(userId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Optional<Korisnik> korisnikWithUserName(@RequestParam("userName") String userName) {
        rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "getUsername","Get user with username: " + userName);
        this.userWithUserNameExists(userName);
        return this.korisnikRepository.findByUserName(userName);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<?> add(@RequestParam String firstName,
                          @RequestParam String lastName,
                          @RequestParam String userName,
                          @RequestParam String password,
                          @RequestParam Long userTypeId,
                          @RequestParam Long userGroupId,
                          @RequestParam Long deviceId, @Valid Korisnik k1,BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return validationErrorHandling(bindingResult);
        }else {

            LogMessage lm;
            if (this.checkExistingUsername(userName)) {
                Korisnik k = new Korisnik(firstName, lastName, userName, password, userTypeId, userGroupId, deviceId);

                lm=new LogMessage(Constants.MESSAGING_USER_ADD,Constants.MESSAGING_EVERYTHING_OK,Constants.USER_REGISTERED, Constants.MESSAGING_MICROSERVICE, k.getUserName());

                rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "added",lm.toString());
                korisnikRepository.save(k);
                ApiSuccess apiSuccess = new ApiSuccess(HttpStatus.OK.value(), "User added", k);
                return ResponseEntity.ok(apiSuccess);
            } else {
                lm=new LogMessage(Constants.MESSAGING_USER_ADD, Constants.MESSAGING_SOMETHING_WRONG, Constants.USER_NOT_REGISTERED, Constants.MESSAGING_MICROSERVICE,Constants.MESSAGING_USER_EXISTS + ": "+ userName);
                rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "errorExists",lm.toString());
                ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), "Already Exists", "User with that username already exists");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
            }
        }
    }

    @RequestMapping(value = "/update/{userId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@PathVariable Long userId,
                                 @RequestParam String firstName,
                                 @RequestParam String lastName,
                                 @RequestParam String userName,
                                 @RequestParam String password,
                                 @RequestParam Long userTypeId,
                                 @RequestParam Long userGroupId,
                                 @RequestParam Long deviceId, @Valid Korisnik k2,BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return validationErrorHandling(bindingResult);
        }else {
            Korisnik stari = korisnikRepository.findById(userId).orElseThrow(
                    () -> new ItemNotFoundException(userId, "user"));

            Korisnik k = new Korisnik(firstName, lastName, userName, password, userTypeId, userGroupId, deviceId);
            LogMessage lm = new LogMessage(Constants.MESSAGING_USER_ADD, Constants.MESSAGING_EVERYTHING_OK, Constants.USER_CHANGED, Constants.MESSAGING_MICROSERVICE, whatHasChanged(stari,k));
            stari.updateFields(k);
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "updated",lm.toString());
            korisnikRepository.save(stari);
            ApiSuccess apiSuccess = new ApiSuccess(HttpStatus.OK.value(), "User updated", stari);
            return ResponseEntity.ok(apiSuccess);
        }
    }

    private String whatHasChanged(Korisnik k1, Korisnik k2){

        String changed="";

        System.out.println(k1.toString());
        System.out.println(k2.toString());

        if(!k1.getFirstName().equals(k2.getFirstName())){
            changed+="firstName-";
        }

        if(!k1.getLastName().equals(k2.getLastName())){
            changed+="lastName-";
        }

        if(!k1.getUserName().equals(k2.getUserName())){
            changed+=k2.getUserName()+"-";
        }

        if(!k1.getPassword().equals(k2.getPassword())){
            changed+="password-";
        }

        if(!k1.getUserTypeId().equals(k2.getUserTypeId())){
            changed+="userTypeId-";
        }

        if(!k1.getUserGroupId().equals(k2.getUserGroupId())){
            changed+="userGroupId-";
        }

        if(!k1.getDeviceId().equals(k2.getDeviceId())){
            changed+="deviceId-";
        }

        return  changed;
    }

    private ResponseEntity<?> validationErrorHandling(BindingResult bindingResult){
        log.error("Validation error");

        List<FieldError> errors = bindingResult.getFieldErrors();
        StringBuilder message= new StringBuilder();
        for (FieldError e : errors){
            message.append("@").append(e.getField().toUpperCase()).append(":").append(e.getDefaultMessage());
        }

        rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "errorValidation","Validation error");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(HttpStatus.BAD_REQUEST.value(),"Validation error",message.toString()));
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable("id") long id) {
        log.info("Fetching & Deleting User with id {}", id);
        LogMessage lm;
        Optional<Korisnik> korisnik = korisnikRepository.findById(id);
        if (!korisnik.isPresent()) {
            log.error("Unable to delete. User with id {} not found.", id);
            lm = new LogMessage(Constants.MESSAGING_USER_DELETE, Constants.MESSAGING_SOMETHING_WRONG, Constants.USER_NOT_DELETED, Constants.MESSAGING_MICROSERVICE, "User id: " + id);
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "errorDelete",lm.toString());
            ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(), "Unable to delete", "Unable to delete. User with id: " + id + " not found." );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
        }
        korisnikRepository.delete(korisnik.get());
        lm = new LogMessage(Constants.MESSAGING_USER_DELETE, Constants.MESSAGING_EVERYTHING_OK, Constants.USER_DELETED, Constants.MESSAGING_MICROSERVICE, korisnik.get().getUserName());
        rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "deleted",lm.toString());
        ApiSuccess apiSuccess=new ApiSuccess(HttpStatus.OK.value(),"User deleted",korisnik.get());
        return ResponseEntity.ok(apiSuccess);
    }

    private void userWithIdExists(Long userId) {
        this.korisnikRepository.findById(userId).orElseThrow(
                () -> new ItemNotFoundException(userId,"user"));
    }

    private void userWithUserNameExists(String userName) {
        this.korisnikRepository.findByUserName(userName).orElseThrow(
                () -> new ItemNotFoundException(userName,"user"));
    }

    private boolean checkExistingUsername(String userName) {
        boolean b = korisnikRepository.findByUserName(userName).isPresent();
        return !b;
    }
}
