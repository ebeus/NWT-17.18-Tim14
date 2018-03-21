package application;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UredjajRepository extends CrudRepository<Uredjaj,Long> {

    List<Uredjaj> findByDeviceName(String deviceName);
}
