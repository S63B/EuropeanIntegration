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

    public Owner getOwner(int ownerId) {
        return ownerDao.findOne(ownerId);
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

    public Owner getOrCreateOwnerByUsername(String username) {
        Owner returnOwner = ownerDao.findByUsername(username);
        if (returnOwner == null) {
            returnOwner = new Owner();
            returnOwner.setUsername(username);
            returnOwner = ownerDao.save(returnOwner);
        }
        return returnOwner;
    }
}
