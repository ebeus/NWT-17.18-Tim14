package application.Controllers;

import application.Application;
import application.FieldConstants;
import application.Models.KorisnikReturn;
import application.Repositories.GrupaKorisnikaRepository;
import application.Repositories.KorisnikRepository;
import application.Repositories.TipKorisnikaRepository;
import application.Models.GrupaKorisnika;
import application.Models.Korisnik;
import application.Models.TipKorisnika;
import application.Responses.ApiError;
import application.Exceptions.ItemNotFoundException;
import application.Responses.ApiSuccess;
import application.Responses.LogMessage;
import application.Utils.Constants;
import application.Utils.ConvertUsers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class KorisnikController {

    private static final Logger log = LoggerFactory.getLogger(KorisnikController.class);
    private final KorisnikRepository korisnikRepository;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GrupaKorisnikaRepository grupaRepo;
    
    @Autowired
    private TipKorisnikaRepository tipRepo;
    
    @Autowired
    public KorisnikController(KorisnikRepository korisnikRepository, RabbitTemplate rabbitTemplate) {
        this.korisnikRepository = korisnikRepository;
        this.rabbitTemplate = rabbitTemplate;
    }
    
	Authentication auth;


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<?> korisnici(@RequestHeader("Authorization") String token) {
        log.info("Get all users");
       
        return new ResponseEntity<Iterable<KorisnikReturn>>( ConvertUsers.ToKorisniciReturn(this.korisnikRepository.findAll()), HttpStatus.OK);
    }

    @PreAuthorize("#oauth2.hasScope('mobile') or hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/group/{userGroupId}", method = RequestMethod.GET)
    public ResponseEntity<?> korisniciWithGroupId(@PathVariable Long userGroupId,
    		@RequestHeader("Authorization") String token) {
    	
        log.info("Get users in group");
        if(!isUserThisType(FieldConstants.USER_TYPE_ADMIN)) {
        	Optional<KorisnikReturn> korisnik = ConvertUsers.ToKorisnikReturnOpt(korisnikRepository.findById(getUserIdFromAuth()));
        	if(!korisnik.isPresent()) {
        		ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(), Constants.MESSAGE_DOES_NOT_EXIST, Constants.MESSAGE_DOES_NOT_EXIST);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
        	}
        	if(userGroupId != korisnik.get().getGrupaKorisnika().getId()) {
        		ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), Constants.MESSAGE_UNAUTHORIZED, Constants.MESSAGE_UNAUTHORIZED);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);        		
        	}
        }
        return new ResponseEntity<Iterable<KorisnikReturn>>( ConvertUsers.ToKorisniciReturn(this.korisnikRepository.findByUserGroupId(userGroupId)), HttpStatus.OK);
    }

    @PreAuthorize("#oauth2.hasScope('mobile') or hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<?>  korisnikWithId(@PathVariable Long userId,
    		@RequestHeader("Authorization") String token) {
    	
        log.info("Get user with id: " + userId);
        if(!isUserThisType(FieldConstants.USER_TYPE_ADMIN)) {
        	Optional<KorisnikReturn> korisnik = ConvertUsers.ToKorisnikReturnOpt(korisnikRepository.findById(getUserIdFromAuth()));
        	if(!korisnik.isPresent()) {
        		ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(), Constants.MESSAGE_DOES_NOT_EXIST, Constants.MESSAGE_DOES_NOT_EXIST);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
        	}
        	if(!areUsersInSameGroup(korisnik.get().getId(), userId, token)) {
        		ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), Constants.MESSAGE_UNAUTHORIZED, Constants.MESSAGE_UNAUTHORIZED);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);        		
        	}
        }
        
        this.userWithIdExists(userId);
        return new ResponseEntity<Optional<KorisnikReturn>> (ConvertUsers.ToKorisnikReturnOpt(this.korisnikRepository.findById(userId)), HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(method = RequestMethod.GET)
    public Optional<KorisnikReturn> korisnikWithUserName(@RequestParam("userName") String userName) {
        this.userWithUserNameExists(userName);
        return ConvertUsers.ToKorisnikReturnOpt(this.korisnikRepository.findByUserName(userName));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<?> add(@RequestParam String firstName,
                          @RequestParam String lastName,
                          @RequestParam String userName,
                          @RequestParam String password,
                          @RequestParam String email,
                          @RequestParam Long userTypeId,
                          @RequestParam Long userGroupId,
                          @Valid Korisnik k1,BindingResult bindingResult,
                                 @RequestHeader("Authorization") String token) {

        if(bindingResult.hasErrors()){
            return validationErrorHandling(bindingResult);
        }else {

            LogMessage lm;
            if (this.checkExistingUsername(userName) && !korisnikRepository.findByEmail(email).isPresent()) {
            	GrupaKorisnika grupa = grupaRepo.findById(userGroupId).get();
            	TipKorisnika tipK = tipRepo.findById(userTypeId).get();
                Korisnik k = new Korisnik(firstName, lastName, userName, passwordEncoder.encode(password), email, tipK, grupa);

                lm=new LogMessage(Constants.MESSAGING_USER_ADD,Constants.MESSAGING_EVERYTHING_OK,Constants.USER_REGISTERED, Constants.MESSAGING_MICROSERVICE, k.getUserName());

                rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY, lm.toString());
                korisnikRepository.save(k);
                ApiSuccess apiSuccess = new ApiSuccess(HttpStatus.OK.value(), "User added", k);
                return ResponseEntity.ok(apiSuccess);
            } else {
                lm=new LogMessage(Constants.MESSAGING_USER_ADD, Constants.MESSAGING_SOMETHING_WRONG, Constants.USER_NOT_REGISTERED, Constants.MESSAGING_MICROSERVICE,Constants.MESSAGING_USER_EXISTS + ": "+ userName);
                rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY, lm.toString());
                ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), "Already Exists", "User with that username already exists");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
            }
        }
    }

    @PreAuthorize("#oauth2.hasScope('mobile')")
    @RequestMapping(value = "/update/{userId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateFullUser(@PathVariable Long userId,
                                 @RequestParam String firstName,
                                 @RequestParam String lastName,
                                 @RequestParam String userName,
                                 @RequestParam String password,
                                 @RequestParam String email,
                                 @Valid Korisnik k2,BindingResult bindingResult,
                                 @RequestHeader("Authorization") String token) {

    	if(userId != getUserIdFromAuth() || isUserThisType(FieldConstants.USER_TYPE_ADMIN)) {
    			return new ResponseEntity<String>(Constants.MESSAGE_UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
    	}

    	if(this.checkExistingUsername(userName) || korisnikRepository.findByEmail(email).isPresent()) {
            return new ResponseEntity<String>(Constants.MESSAGING_USER_EXISTS, HttpStatus.BAD_REQUEST);
        }
    	
        if(bindingResult.hasErrors()){
            return validationErrorHandling(bindingResult);
        }else {
            Korisnik stari = korisnikRepository.findById(userId).orElseThrow(
                    () -> new ItemNotFoundException(userId, "user"));

        	
            Korisnik k = new Korisnik(firstName, lastName, userName, passwordEncoder.encode(password), email, tipRepo.findById(stari.getUserType().getId()).get(),  grupaRepo.findById(stari.getUserGroup().getId()).get());
            LogMessage lm = new LogMessage(Constants.MESSAGING_USER_ADD, Constants.MESSAGING_EVERYTHING_OK, Constants.USER_CHANGED, Constants.MESSAGING_MICROSERVICE, whatHasChanged(stari,k));
            stari.updateFields(k);
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY ,lm.toString());
            korisnikRepository.save(stari);
            ApiSuccess apiSuccess = new ApiSuccess(HttpStatus.OK.value(), "User updated", stari);
            return ResponseEntity.ok(apiSuccess);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/admin/update/{userId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@PathVariable Long userId,
                                        @RequestParam String firstName,
                                        @RequestParam String lastName,
                                        @RequestParam Long userTypeId,
                                        @RequestParam Long userGroupId,
                                        @Valid Korisnik k2,BindingResult bindingResult,
                                        @RequestHeader("Authorization") String token) {

        if(!isUserThisType(FieldConstants.USER_TYPE_ADMIN)) {
            return new ResponseEntity<String>(Constants.MESSAGE_UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        if(bindingResult.hasErrors()){
            return validationErrorHandling(bindingResult);
        }else {
            Korisnik stari = korisnikRepository.findById(userId).orElseThrow(
                    () -> new ItemNotFoundException(userId, "user"));

            GrupaKorisnika grupa = grupaRepo.findById(userGroupId).get();
            TipKorisnika tipK = tipRepo.findById(userTypeId).get();

            Korisnik k = new Korisnik(firstName, lastName, stari.getUserName(), stari.getPassword(), stari.getEmail(), tipK, grupa);
            LogMessage lm = new LogMessage(Constants.MESSAGING_USER_ADD, Constants.MESSAGING_EVERYTHING_OK, Constants.USER_CHANGED, Constants.MESSAGING_MICROSERVICE, whatHasChanged(stari,k));
            stari.updateFields(k);
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY ,lm.toString());
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

        if(!k1.getUserGroup().getId().equals(k2.getUserGroup().getId())){
            changed+="userTypeId-";
        }

        if(!k1.getUserType().getId().equals(k2.getUserType().getId())){
            changed+="userGroupId-";
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

        rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY,"Validation error");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(HttpStatus.BAD_REQUEST.value(),"Validation error",message.toString()));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable("id") long id,
    		@RequestHeader("Authorization") String token) {
        log.info("Fetching & Deleting User with id {}", id);
        
        
        
        LogMessage lm;
        Optional<Korisnik> korisnik = korisnikRepository.findById(id);
        if (!korisnik.isPresent()) {
            log.error("Unable to delete. User with id {} not found.", id);
            lm = new LogMessage(Constants.MESSAGING_USER_DELETE, Constants.MESSAGING_SOMETHING_WRONG, Constants.USER_NOT_DELETED, Constants.MESSAGING_MICROSERVICE, "User id: " + id);
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY, lm.toString());
            ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(), "Unable to delete", "Unable to delete. User with id: " + id + " not found." );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
        }
        korisnikRepository.delete(korisnik.get());
        lm = new LogMessage(Constants.MESSAGING_USER_DELETE, Constants.MESSAGING_EVERYTHING_OK, Constants.USER_DELETED, Constants.MESSAGING_MICROSERVICE, korisnik.get().getUserName());
        rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY, lm.toString());
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
    
    private String getExtraInfo(String field) {
        auth = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationDetails oauthDetails = (OAuth2AuthenticationDetails) auth.getDetails();
        System.out.println(auth.getDetails().toString());
        System.out.println(oauthDetails.getDecodedDetails());
        Map<String, Object> details = (Map<String, Object>) oauthDetails.getDecodedDetails();
        return details.get(field).toString();
    }
    
    private boolean isUserThisType(String userType) {
    	String authType = getExtraInfo(FieldConstants.USER_TYPE_TOKEN_FIELD);
    	return authType.equals(userType);
    }
    
    private boolean areUsersInSameGroup(long UserId1, long UserId2, String token) {
    	Optional<Korisnik> korisnik1 = korisnikRepository.findById(UserId1);    
    	Optional<Korisnik> korisnik2 = korisnikRepository.findById(UserId2);
    	if(!korisnik1.isPresent() || !korisnik2.isPresent())
    		return false;
    	
    	if(korisnik1.get().getUserGroup().getId() == korisnik2.get().getUserGroup().getId())
    		return true;
    	
    	return false;
    }
    
    private long getUserIdFromAuth() {
		return Long.parseLong(getExtraInfo(FieldConstants.USER_ID_TOKEN_FIELD));
    }
}
