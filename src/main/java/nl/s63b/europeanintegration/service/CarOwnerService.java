package nl.s63b.europeanintegration.service;

import com.S63B.domain.Entities.Car;
import com.S63B.domain.Entities.Car_Ownership;
import com.S63B.domain.Entities.Owner;
import nl.s63b.europeanintegration.jms.dao.CarOwnerDao;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Kevin.
 */

@Service
@Transactional
public class CarOwnerService {
    private CarOwnerDao carOwnerDao;
    private CarService carService;

    @Autowired
    public CarOwnerService(CarOwnerDao carOwnerDao, CarService carService) {
        this.carOwnerDao = carOwnerDao;
        this.carService = carService;
    }

    public Car_Ownership addCarToOwner(Car car, Owner owner) {
        Car_Ownership car_ownership = new Car_Ownership(car, owner, DateTime.now());
        carOwnerDao.save(car_ownership);
        return car_ownership;
    }
}
