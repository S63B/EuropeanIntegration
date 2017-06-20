package nl.s63b.europeanintegration.jms.dao;

import com.S63B.domain.Entities.Car;
import com.S63B.domain.Entities.Car_Ownership;
import com.S63B.domain.Entities.Owner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Kevin.
 */
@Repository
public interface CarOwnerDao extends CrudRepository<Car_Ownership, Integer> {
    List<Car_Ownership> getAllByOwner(Owner owner);
    List<Car_Ownership> getAllByCar(Car car);
}
