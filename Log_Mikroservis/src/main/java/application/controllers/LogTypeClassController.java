package application.controllers;

import application.model.LogTypeClass;
import application.repository.LogTypeClassRepository;
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
@RequestMapping("/type")
public class LogTypeClassController {

    private static final Logger logController = LoggerFactory.getLogger(LogTypeClassController.class);
    private final LogTypeClassRepository logTypeClassRepository;

    @Autowired
    public LogTypeClassController(LogTypeClassRepository logTypeClassRepository) {
        this.logTypeClassRepository = logTypeClassRepository;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Collection<LogTypeClass> logs(){
        logController.info("LogTypeClassController: findAll()");
        return (Collection<LogTypeClass>) this.logTypeClassRepository.findAll();
    }

    @RequestMapping(value = "/{id}" , method = RequestMethod.GET)
    public Optional<LogTypeClass> statusWithId(@PathVariable Long id){
        logController.info("LogTypeClassController: StatusWithType() "+ id);
        return this.logTypeClassRepository.findById(id);
    }

}
