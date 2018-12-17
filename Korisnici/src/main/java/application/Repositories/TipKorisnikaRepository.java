package application.Repositories;

import application.Models.TipKorisnika;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TipKorisnikaRepository extends CrudRepository<TipKorisnika,Long> {

    Optional<TipKorisnika> findByTypeName(String typeName);

}
