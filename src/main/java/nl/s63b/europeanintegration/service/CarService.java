package nl.s63b.europeanintegration.service;

import com.S63B.domain.Entities.Car;
import com.S63B.domain.Entities.LicensePlate;
import nl.s63b.europeanintegration.jms.dao.CarDao;
import nl.s63b.europeanintegration.jms.dao.LicensePlateDao;
import nl.s63b.europeanintegration.jms.dao.TrackerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created by Kevin.
 */
@Service
@Transactional
public class CarService {
    CarDao carDao;
    LicensePlateDao licensePlateDao;
    TrackerDao trackerDao;

    @Autowired
    public CarService(CarDao carDao, LicensePlateDao licensePlateDao, TrackerDao trackerDao) {
        this.licensePlateDao = licensePlateDao;
        this.carDao = carDao;
        this.trackerDao = trackerDao;
    }

    public void saveCar(Car car) {
        licensePlateDao.save(car.getLicensePlate());
        trackerDao.save(car.getTracker());
        carDao.save(car);
    }

    public boolean carIsStolen(Car car) {
        boolean isStolen = false;
        LicensePlate plate = licensePlateDao.findByLicense(car.getLicensePlate().getLicense());
        if (plate != null) {
            Car foundCar = carDao.findByLicensePlate(plate);
            isStolen = foundCar.isStolen();
        }
        return isStolen;
    }

    public Car getById(int carId) {
        return carDao.findOne(carId);
    }

    public void reportCarStolen(Car car) {
        car.setStolen(true);
        saveCar(car);
    }
}
