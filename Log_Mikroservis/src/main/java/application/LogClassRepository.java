package application;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogClassRepository extends CrudRepository<LogClass,Long> {

    List<LogClass> findByLogSource(String logSource);

}