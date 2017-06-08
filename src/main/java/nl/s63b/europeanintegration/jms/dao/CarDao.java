package nl.s63b.europeanintegration.jms.dao;

import com.S63B.domain.Entities.Car;
import com.S63B.domain.Entities.LicensePlate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Kevin.
 */
@Repository
public interface CarDao extends CrudRepository<Car, Integer> {
    Car findByLicensePlate(LicensePlate licensePlate);
}
