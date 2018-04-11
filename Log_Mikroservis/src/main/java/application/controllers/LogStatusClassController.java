package application.controllers;

import application.model.LogStatusClass;
import application.repository.LogStatusClassRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/status")
public class LogStatusClassController {

    private static final Logger logController = LoggerFactory.getLogger(LogStatusClassController.class);
    private final LogStatusClassRepository logStatusClassRepository;

    @Autowired
    public LogStatusClassController(LogStatusClassRepository logStatusClassRepository) {
        this.logStatusClassRepository = logStatusClassRepository;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Collection<LogStatusClass> logs(){
        logController.info("LogStatusClassController: findAll()");
        return (Collection<LogStatusClass>) this.logStatusClassRepository.findAll();
    }

    @RequestMapping(value = "/{id}" , method = RequestMethod.GET)
    public Optional<LogStatusClass> statusWithId(@PathVariable Long id){
        logController.info("LogStatusClassController: StatusWithType() "+ id);
        return this.logStatusClassRepository.findById(id);
    }


}
