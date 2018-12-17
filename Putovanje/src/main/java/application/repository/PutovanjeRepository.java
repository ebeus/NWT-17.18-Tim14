package application.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import application.models.Putovanje;

@Repository
public interface PutovanjeRepository extends CrudRepository<Putovanje, Long> {
	List<Putovanje> findAll();
	List<Putovanje> findAllByidKorisnika(long idKorisnika);
	Putovanje findById(long id);
	Putovanje findByNaziv(String tripName);
	boolean existsBynaziv(String tripName);
}
