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
import java.util.List;

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

    public Car saveCar(Car car) {
        licensePlateDao.save(car.getLicensePlate());
        trackerDao.save(car.getTracker());
        return carDao.save(car);
    }

    public boolean carIsStolen(Car car) {
        boolean isStolen = false;
        LicensePlate plate = licensePlateDao.findFirstByLicense(car.getLicensePlate().getLicense());
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

    public boolean licensePlateExists(String licensePlate) {
        return licensePlateDao.existsByLicense(licensePlate);
    }

    public Car getBylicensePlateLicense(String  licensePlateLicense){
        Car foundCar = null;
        LicensePlate licensePlate = licensePlateDao.findFirstByLicense(licensePlateLicense);

        if(licensePlate != null){
            foundCar = carDao.findByLicensePlate(licensePlate);
        }

        return foundCar;
    }

    public Car getOrSave(Car car) {
        Car returncar;

        LicensePlate plate = licensePlateDao.findFirstByLicense(car.getLicensePlate().getLicense());

        if(plate != null){
            returncar = carDao.findByLicensePlate(plate);
        }
        else {
            returncar = saveCar(car);
        }

        return returncar;
    }
}
