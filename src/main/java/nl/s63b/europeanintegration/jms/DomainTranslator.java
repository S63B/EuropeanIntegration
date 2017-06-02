package nl.s63b.europeanintegration.jms;

import com.S63B.domain.Entities.Car;
import com.S63B.domain.Entities.Invoice;
import com.S63B.domain.Entities.LicensePlate;
import com.S63B.domain.Entities.Tracker;
import nl.s63b.europeanintegration.jms.kotlin.Countries;
import org.joda.time.DateTime;

import java.time.Instant;
import java.util.Date;

/**
 * Created by Kevin.
 */
public class DomainTranslator {
    private static DomainTranslator instance;

    private DomainTranslator() {
    }

    public static DomainTranslator getInstance() {
        if (instance == null) {
            instance = new DomainTranslator();
        }
        return instance;
    }

    public static Car jmsToCar(nl.s63b.europeanintegration.jms.kotlin.Car car) {
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
        newCar.setStolen(car.getStolen());

        return newCar;
    }

    public static nl.s63b.europeanintegration.jms.kotlin.Car carToJms(Car car, nl.s63b.europeanintegration.jms.Countries destinationCountry) {
        Countries destination = Countries.NETHERLANDS;

        for (Countries c : Countries.values()) {
            if (destinationCountry.equals(c)) {
                destination = c;
            }
        }

        if(car.getLicensePlate() == null){
            car.setLicensePlate(new LicensePlate("NL-NO-PLATE", null));
        }

        nl.s63b.europeanintegration.jms.kotlin.Car newCar = new nl.s63b.europeanintegration.jms.kotlin.Car(
                car.getLicensePlate().getLicense(),
                destination,
                car.isStolen()
        );
        return newCar;
    }

    //todo Ophalen van de user die in het buitenland gereden heeft om zo zijn invoice te koppelen en de juiste auto te selecteren
    public static Invoice jmsToInvoice(nl.s63b.europeanintegration.jms.kotlin.Invoice invoice){
        Invoice newInvoice = new Invoice();

        newInvoice.setTotalPrice(invoice.getPrice());
        newInvoice.setCountryOfOrigin(invoice.getOriginCountry().name());

        DateTime dateTime = new DateTime(invoice.getDate());
        newInvoice.setDate(dateTime);

        return newInvoice;
    }

    public static nl.s63b.europeanintegration.jms.kotlin.Invoice invoiceToJms(Invoice invoice, nl.s63b.europeanintegration.jms.Countries destinationCountry){
        Countries destination = Countries.NETHERLANDS;

        for (Countries c : Countries.values()) {
            if (destinationCountry.equals(c)) {
                destination = c;
            }
        }

        nl.s63b.europeanintegration.jms.kotlin.Invoice newInvoice = new nl.s63b.europeanintegration.jms.kotlin.Invoice(
                0,
                invoice.getTotalPrice(),
                invoice.getOwner().getOwnedCars().get(0).getCar().getLicensePlate().getLicense(),
                destination,
                Countries.NETHERLANDS,
                Date.from(Instant.now())
        );
        return newInvoice;
    }
}
