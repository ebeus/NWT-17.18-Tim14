package application.Controllers;

import application.Application;
import application.Repositories.GrupaKorisnikaRepository;
import application.Models.GrupaKorisnika;
import application.Responses.ApiError;
import application.Exceptions.ItemNotFoundException;
import application.Responses.ApiSuccess;
import application.Utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/groups")
public class GrupaKorisnikaController {

    private static final Logger log = LoggerFactory.getLogger(GrupaKorisnikaController.class);
    private final GrupaKorisnikaRepository grupaKorisnikaRepository;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public GrupaKorisnikaController(GrupaKorisnikaRepository grupaKorisnikaRepository, RabbitTemplate rabbitTemplate) {
        this.grupaKorisnikaRepository = grupaKorisnikaRepository;
        this.rabbitTemplate=rabbitTemplate;
    }

    @PreAuthorize("#oauth2.hasScope('mobile') or #oauth2.hasScope('admin')")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Collection<GrupaKorisnika> grupe() {
        log.info("/grupe GET");
        rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "getAllGroups","Get all groups");
        return (Collection<GrupaKorisnika>) this.grupaKorisnikaRepository.findAll();
    }

    @PreAuthorize("#oauth2.hasScope('mobile') or #oauth2.hasScope('admin')")
    @RequestMapping(value = "/{groupId}", method = RequestMethod.GET)
    public Optional<GrupaKorisnika> grupaWithId(@PathVariable Long groupId) {
        Optional<GrupaKorisnika> existing=grupaKorisnikaRepository.findById(groupId);
        if(existing.isPresent()) {
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "getGroupId", "Get group with Id: " + groupId);
            return existing;
        }
        else {
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "errorNotFound", "Group with Id: " + groupId + " not found");
            throw new ItemNotFoundException(groupId, "group");
        }
    }

    @PreAuthorize("#oauth2.hasScope('admin')")
    @RequestMapping(method = RequestMethod.GET)
    public Optional<GrupaKorisnika> grupaWithGroupName(@RequestParam("groupName") String groupName) {
        Optional<GrupaKorisnika> postojeca=grupaKorisnikaRepository.findByGroupName(groupName);
        if(postojeca.isPresent()) {
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "getGroupName", "Get group with name: " + groupName);
            return postojeca;
        }
        else {
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "errorNotFound", "Group with name: " + groupName + " not found");
            throw new ItemNotFoundException(groupName , "group");
        }
    }

    @PreAuthorize("#oauth2.hasScope('admin') and hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<?> addGroup(@RequestParam String groupName) {
        if (!grupaKorisnikaRepository.findByGroupName(groupName).isPresent()) {
            GrupaKorisnika k = new GrupaKorisnika(groupName);

            grupaKorisnikaRepository.save(k);
            ApiSuccess apiSuccess=new ApiSuccess(HttpStatus.OK.value(),"Group added",k);
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "addedGroup", "Group with name: " + groupName + " created");
            return ResponseEntity.ok(apiSuccess);
        } else {
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "errorExists", "Group with that name already exists");
            ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), "Already Exists", "Group with that name already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
        }
    }

    @PreAuthorize("#oauth2.hasScope('admin') and hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/update/{groupId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateGroupName(@PathVariable Long groupId, @RequestParam String groupName) {
        GrupaKorisnika staraGrupa = grupaKorisnikaRepository.findById(groupId).orElseThrow(
                () -> new ItemNotFoundException(groupId,"grupa"));
        staraGrupa.setGroupName(groupName);

        grupaKorisnikaRepository.save(staraGrupa);
        rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "updatedGroup", "Group with name: " + groupName + " updated");
        ApiSuccess apiSuccess=new ApiSuccess(HttpStatus.OK.value(),"Group updated",staraGrupa);
        return ResponseEntity.ok(apiSuccess);
    }

    @PreAuthorize("#oauth2.hasScope('admin') and hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteGroup(@PathVariable("id") long id) {
        log.info("Fetching & Deleting Group with id {}", id);

        Optional<GrupaKorisnika> grupa = grupaKorisnikaRepository.findById(id);
        if (!grupa.isPresent()) {
            log.error("Unable to delete. Group with id {} not found.", id);
            rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "errorDelete", "Unable to delete. Group with id: " + id + " not found." );
            ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(), "Unable to delete", "Unable to delete. Group with id: " + id + " not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
        }
        grupaKorisnikaRepository.delete(grupa.get());
        rabbitTemplate.convertAndSend(Application.topicExchangeName, Constants.USERS_ROUTING_KEY + "deletedGroup","Group with id: " + id + " deleted");
        ApiSuccess apiSuccess=new ApiSuccess(HttpStatus.OK.value(),"Group deleted", grupa.get());
        return ResponseEntity.ok(apiSuccess);
    }
}
