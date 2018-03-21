package application;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TipKorisnikaRepository extends CrudRepository<TipKorisnika,Long> {

    List<TipKorisnika> findByTypeName(String typeName);

}
