package application.Repositories;

import application.Models.GrupaKorisnika;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GrupaKorisnikaRepository extends CrudRepository<GrupaKorisnika,Long>{

    Optional<GrupaKorisnika> findByGroupName(String groupName);
}
