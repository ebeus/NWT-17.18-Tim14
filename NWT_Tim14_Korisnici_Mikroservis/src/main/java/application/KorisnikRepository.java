package application;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KorisnikRepository extends CrudRepository<Korisnik,Long> {

    List<Korisnik> findByLastName(String lastName);
}
