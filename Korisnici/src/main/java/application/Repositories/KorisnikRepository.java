package application.Repositories;

import java.util.List;
import java.util.Optional;

import application.Models.Korisnik;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KorisnikRepository extends CrudRepository<Korisnik,Long> {

    List<Korisnik> findByLastName(String lastName);

    Optional<Korisnik> findByUserName(String userName);

    List<Korisnik> findByUserGroupId(Long userGroupId);
}
