package application.controllers;

import application.Responses.ApiError;
import application.Responses.ApiSuccess;
import application.model.LogClass;
import application.model.LogStatusClass;
import application.model.LogTypeClass;
import application.repositories.LogClassRepository;
import application.repositories.LogStatusClassRepository;
import application.repositories.LogTypeClassRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/logs")
public class LogClassController {

    private static final Logger logController = LoggerFactory.getLogger(LogClassController.class);
    private final LogClassRepository logClassRepository;
    private final LogTypeClassRepository logTypeClassRepository;
    private final LogStatusClassRepository logStatusClassRepository;

    @Autowired
    public LogClassController(LogClassRepository logClassRepository, LogTypeClassRepository logTypeClassRepository, LogStatusClassRepository logStatusClassRepository) {
        this.logClassRepository = logClassRepository;
        this.logTypeClassRepository = logTypeClassRepository;
        this.logStatusClassRepository = logStatusClassRepository;
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    @PreAuthorize("#oauth2.hasScope('admin') and hasRole('ROLE_ADMIN')")
    public Collection<LogClass> logs(){
        logController.info("LogClassRestController: findAll()");
        return (Collection<LogClass>) this.logClassRepository.findAll();
    }

   /* //Pretraga po tipu (1-5)
    @RequestMapping(value = "type/{typeId}" , method = RequestMethod.GET)
    public Collection<LogClass> logsWithType(@PathVariable Long typeId){
        logController.info("LogClassRestController: logsWithType() "+ typeId);
        return this.logClassRepository.findByLogTypeId(typeId);
    }*/

    //Pretraga po tipu (string)
    @PreAuthorize("#oauth2.hasScope('admin') and hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "type/{typeName}" , method = RequestMethod.GET)
    public Collection<LogClass> logsWithType(@PathVariable String typeName){
        logController.info("LogClassRestController: logsWithType() "+ typeName);
        final LogTypeClass[] logTypeClass = {new LogTypeClass()};
        logTypeClassRepository.findByTypeName(typeName).ifPresent(logTypeElement -> {
            logController.info("LogClassRestController: findByTypeName() "+ logTypeElement.getId());
            logTypeClass[0] = logTypeElement;
        });
        return this.logClassRepository.findByLogTypeId(logTypeClass[0].getId());
    }

    // - statusu (1, 0)
  /*  @RequestMapping(value = "status/{status}" , method = RequestMethod.GET)
    public Collection<LogClass> logsWithStatus(@PathVariable Long status){
        logController.info("LogClassRestController: logsWithType() "+ status);
        return this.logClassRepository.findByStatus(status);
    } */

    // - statusu (String)
    @PreAuthorize("#oauth2.hasScope('admin') and hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "status/{statusName}" , method = RequestMethod.GET)
    public Collection<LogClass> logsWithStatus(@PathVariable String statusName){
        logController.info("LogClassRestController: logsWithType() "+ statusName);
        final LogStatusClass[] logStatusClass = {new LogStatusClass()};
        logStatusClassRepository.findByStatusName(statusName).ifPresent(logStatusElement -> {
            logController.info("LogClassRestController: findByTypeName() "+ logStatusElement.getId());
            logStatusClass[0] = logStatusElement;
        });
        return this.logClassRepository.findByStatusId(logStatusClass[0].getId());
    }

    // - mikroservisu
    @PreAuthorize("#oauth2.hasScope('admin') and hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "source/{source}" , method = RequestMethod.GET)
    public Collection<LogClass> losgWithSource(@PathVariable String source){
        logController.info("LogClassRestController: logWithLogSource() "+ source);
        return this.logClassRepository.findBySource(source);
    }

    // - korisnickom imenu
    @PreAuthorize("#oauth2.hasScope('admin')")
    @RequestMapping(value = "/user/{user}", method = RequestMethod.GET)
    public Collection<LogClass> logsWithUser(@PathVariable String user){
        logController.info("LogClassRestController: logWithUser() "+ user);
        return this.logClassRepository.findByUser(user);
    }

    // - imenu putovanja
    @PreAuthorize("#oauth2.hasScope('admin') and hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "trip/{tripName}" , method = RequestMethod.GET)
    public Collection<LogClass> logsWithTripName(@PathVariable String tripName){
        logController.info("LogClassRestController: logsWithTripName() "+ tripName);
        return this.logClassRepository.findByTripName(tripName);
    }

    @PreAuthorize("#oauth2.hasScope('admin') and hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<?> add(@RequestParam Long logTypeId,
                          @RequestParam Long statusId,
                          @RequestParam String message,
                          @RequestParam String logSource,
                          @RequestParam String user,
                          @RequestParam String tripName) {

        String validation = this.validateNewLogClass(logTypeId, statusId, message, logSource, user, tripName);
        if (("").equals(validation)) {
            LogClass logic = new LogClass(logTypeId, statusId, message, logSource, user, tripName);

            logClassRepository.save(logic);
            ApiSuccess apiSuccess=new ApiSuccess(HttpStatus.OK.value(),"Log added: ",logic);
            return ResponseEntity.ok(apiSuccess);
        } else {
            ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), "Wrong params", "Log params error: " + validation);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
        }
    }

    private String validateNewLogClass(Long logTypeId, Long status, String message, String logSource, String user, String tripName) {
        if(logTypeId <= 0 || logTypeId > 6) return "logTypeId";
        else if(status <= 0 || status >2 ) return "status";
        else if (logSource.equals("")) return "logSource";
        else return "";
    }

}
