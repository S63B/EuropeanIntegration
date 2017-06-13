package nl.s63b.europeanintegration.service;

import com.S63B.domain.Entities.Car;
import com.S63B.domain.Entities.Car_Ownership;
import com.S63B.domain.Entities.Owner;
import com.google.common.collect.Lists;
import nl.s63b.europeanintegration.jms.dao.OwnerDao;
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
public class OwnerService {
    private OwnerDao ownerDao;
    private CarOwnerService carOwnerService;

    @Autowired
    public OwnerService(OwnerDao ownerDao, CarOwnerService carOwnerService) {
        this.ownerDao = ownerDao;
        this.carOwnerService = carOwnerService;
    }

    public List<Owner> getAllOwners() {
        return Lists.newArrayList(ownerDao.findAll());
    }

    public Owner addOwnership(Owner owner, Car_Ownership ownership) {
        if(owner.getOwnedCars() == null){
            owner.setOwnedCars(new ArrayList<>());
        }
        owner.getOwnedCars().add(ownership);
        return ownerDao.save(owner);
    }

    public Owner createOwner(Owner owner) {
        Owner returnOwner = null;
        if (ownerDao.findByUsername(owner.getUsername()) == null) {
            returnOwner = ownerDao.save(owner);
        }
        return returnOwner;
    }

    public Owner getOrCreateOwnerByCar(Car car) {
        Owner carOwner = getOwnerByCar(car);
        if (carOwner == null) {
            carOwner = new Owner();
            carOwner.setUsername(car.getLicensePlate().getLicense());
            carOwner = ownerDao.save(carOwner);
        }
        return carOwner;
    }

    public Owner getOwnerByCar(Car car){
        List<Car_Ownership> ownerships = carOwnerService.getAllByCar(car);
        Owner currentCarOwner = null;
        if(ownerships != null){
            currentCarOwner = ownerships.stream().max(Comparator.comparing(Car_Ownership::getPurchaseDate)).get().getOwner();
        }
        return currentCarOwner;
    }
}
