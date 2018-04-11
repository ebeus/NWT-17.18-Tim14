package application.repository;

import application.model.LogTypeClass;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LogTypeClassRepository extends CrudRepository<LogTypeClass,Long> {

    Optional<LogTypeClass> findByTypeName(String typeName);

}
