package application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;


@RestController
public class LogClassRestController {

    @RequestMapping("/logs")
    Collection<LogClass> logs(){
        return (Collection<LogClass>) this.logClassRepository.findAll();
    }

    @Autowired LogClassRepository logClassRepository;
}
