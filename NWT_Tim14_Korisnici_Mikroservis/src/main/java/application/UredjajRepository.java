package application;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UredjajRepository extends CrudRepository<Uredjaj,Long> {

    Optional<Uredjaj> findByDeviceName(String deviceName);
}
