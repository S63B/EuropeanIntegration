package nl.s63b.europeanintegration.jms;

import com.S63B.domain.Entities.*;
import com.gmail.guushamm.EuropeanIntegration.StolenCar;
import nl.s63b.europeanintegration.service.CarService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Date;

/**
 * Created by Kevin.
 */
public class DomainTranslator {
    public static Car jmsToCar(com.gmail.guushamm.EuropeanIntegration.Car car) {
        Car newCar = new Car();

        //Plate
        LicensePlate plate = new LicensePlate();
        plate.setLicense(car.getLicensePlate());
        newCar.setLicensePlate(plate);
        //Tracker
        Tracker tracker = new Tracker("", car.getOriginCountry().name());
        newCar.setTracker(tracker);
        tracker.setCar(newCar);
        //Status
        newCar.setStolen(car.getStolen());

        return newCar;
    }

    public static Car jmsToCar(StolenCar car){
        Car newCar = new Car();

        //Plate
        LicensePlate plate = new LicensePlate();
        plate.setLicense(car.getLicensePlate());
        newCar.setLicensePlate(plate);
        //Tracker
        Tracker tracker = new Tracker("", car.getCountryOfOrigin().name());
        newCar.setTracker(tracker);
        tracker.setCar(newCar);
        //Status
        newCar.setStolen(true);

        return newCar;
    }

    public static StolenCar carToStolenCar(Car car, Countries destinationCountry){
        com.gmail.guushamm.EuropeanIntegration.Countries destination = com.gmail.guushamm.EuropeanIntegration.Countries.NETHERLANDS;

        for (com.gmail.guushamm.EuropeanIntegration.Countries c : com.gmail.guushamm.EuropeanIntegration.Countries.values()) {
            if (destinationCountry.equals(c)) {
                destination = c;
            }
        }

        if(car.getLicensePlate() == null){
            car.setLicensePlate(new LicensePlate("NL-NO-PLATE", null));
        }

        StolenCar stolenCar = new StolenCar(
                car.getLicensePlate().getLicense(),
                destination,
                true
        );
        return stolenCar;
    }

    public static com.gmail.guushamm.EuropeanIntegration.Car carToJms(Car car, Countries destinationCountry) {
        com.gmail.guushamm.EuropeanIntegration.Countries destination = com.gmail.guushamm.EuropeanIntegration.Countries.NETHERLANDS;

        for (com.gmail.guushamm.EuropeanIntegration.Countries c : com.gmail.guushamm.EuropeanIntegration.Countries.values()) {
            if (destinationCountry.equals(c)) {
                destination = c;
            }
        }

        if(car.getLicensePlate() == null){
            car.setLicensePlate(new LicensePlate("NL-NO-PLATE", null));
        }

        com.gmail.guushamm.EuropeanIntegration.Car newCar = new com.gmail.guushamm.EuropeanIntegration.Car(
                car.getLicensePlate().getLicense(),
                destination,
                com.gmail.guushamm.EuropeanIntegration.Countries.NETHERLANDS,
                car.isStolen()
        );
        return newCar;
    }

    public static Invoice jmsToInvoice(com.gmail.guushamm.EuropeanIntegration.Invoice invoice){
        Invoice newInvoice = new Invoice();

        //Creating a temporarily owner with the licenseplate as name so the owner of that car can be added and persisted at a later time
        Owner tempOwner = new Owner();
        tempOwner.setName(invoice.getLicensePlate());
        newInvoice.setOwner(tempOwner);

        newInvoice.setTotalPrice(invoice.getPrice());
        newInvoice.setCountryOfOrigin(invoice.getOriginCountry().name());

        DateTime dateTime = new DateTime(invoice.getDate());
        newInvoice.setDate(dateTime);

        return newInvoice;
    }

    public static com.gmail.guushamm.EuropeanIntegration.Invoice invoiceToJms(Invoice invoice, Countries destinationCountry){
        com.gmail.guushamm.EuropeanIntegration.Countries destination = com.gmail.guushamm.EuropeanIntegration.Countries.NETHERLANDS;

        for (com.gmail.guushamm.EuropeanIntegration.Countries c : com.gmail.guushamm.EuropeanIntegration.Countries.values()) {
            if (destinationCountry.equals(c)) {
                destination = c;
            }
        }

        com.gmail.guushamm.EuropeanIntegration.Invoice newInvoice = new com.gmail.guushamm.EuropeanIntegration.Invoice(
                0,
                invoice.getTotalPrice(),
                invoice.getOwner().getOwnedCars().get(0).getCar().getLicensePlate().getLicense(),
                destination,
                com.gmail.guushamm.EuropeanIntegration.Countries.NETHERLANDS,
                Date.from(Instant.now())
        );
        return newInvoice;
    }
}
