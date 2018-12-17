package application.repository;

import java.util.List;

import application.models.Putovanje;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import application.models.Lokacija;

@Repository
public interface LokacijaRepository extends CrudRepository<Lokacija, Long> {
	List<Lokacija> findByPutovanje(Putovanje p);
	List<Lokacija> findAll();
}
