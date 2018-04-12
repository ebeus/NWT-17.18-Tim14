package application.repositories;

import application.model.LogStatusClass;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LogStatusClassRepository extends CrudRepository<LogStatusClass,Long> {

    Optional<LogStatusClass> findByStatusName(String statusName);

}