package application;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GrupaKorisnikaRepository extends CrudRepository<GrupaKorisnika,Long>{

    List<GrupaKorisnika> findByGroupName(String groupName);
}
