package nl.s63b.europeanintegration.service;

import com.S63B.domain.Entities.Car;
import com.S63B.domain.Entities.Car_Ownership;
import com.S63B.domain.Entities.Invoice;
import com.S63B.domain.Entities.Owner;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created by Kevin.
 */
@Service
@Transactional
public class InvoiceService {

    CarOwnerService carOwnerService;
    OwnerService ownerService;
    int invoicecount = 0; //todo invoicecount should be removed once invoices are properly created.

    @Autowired
    public InvoiceService(CarOwnerService carOwnerService, OwnerService ownerService){
        this.carOwnerService = carOwnerService;
        this.ownerService = ownerService;
    }

    public Invoice getInvoiceForeignCar(Car car) {
        Owner carOwner = ownerService.getOrCreateOwnerByUsername(car.getLicensePlate().getLicense());
        Car_Ownership ownership = carOwnerService.addCarToOwner(car, carOwner);
        ownerService.addOwnership(carOwner, ownership);

        Invoice tempinvoice = new Invoice();
        tempinvoice.setId(invoicecount);
        tempinvoice.setOwner(carOwner);
        tempinvoice.setTotalPrice(invoicecount);

        tempinvoice.setCountryOfOrigin(car.getTracker().getCountry());
        invoicecount++;
        return tempinvoice;
    }
}
