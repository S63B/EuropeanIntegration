package nl.s63b.europeanintegration.application;

import com.S63B.domain.Entities.Car;
import com.S63B.domain.Entities.Invoice;
import com.S63B.domain.Entities.Owner;
import nl.s63b.europeanintegration.jms.Countries;
import nl.s63b.europeanintegration.jms.TopicGateway;
import nl.s63b.europeanintegration.jms.TopicListener;
import nl.s63b.europeanintegration.jms.dao.CarDao;
import nl.s63b.europeanintegration.service.CarService;
import nl.s63b.europeanintegration.service.EuropeanIntegrationService;
import nl.s63b.europeanintegration.service.InvoiceService;
import nl.s63b.europeanintegration.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin.
 */
@Service
public class EUApplication implements TopicListener {
    private EuropeanIntegrationService europeanIntegrationService;
    private CarService carService;
    private OwnerService ownerService;
    private InvoiceService invoiceService;
    private List<Car> foreignCars;

    @Autowired
    public EUApplication(CarService carService, EuropeanIntegrationService europeanIntegrationService, OwnerService ownerService, InvoiceService invoiceService) {
        this.europeanIntegrationService = europeanIntegrationService;
        this.carService = carService;
        this.ownerService = ownerService;
        this.invoiceService = invoiceService;
    }

    /**
     * Used to initiate the JMS component
     */
    public void initiate() {
        TopicGateway.getInstance();
        TopicGateway.addListener(this);
        foreignCars = new ArrayList<>();
    }

    /**
     * Sends a car abroad to a different country
     *
     * @param carId              of the car send
     * @param destinationCountry country where the car will be driving in
     * @return the driving car
     */
    public Car sendCarAbroad(int carId, String destinationCountry) {
        Car sendCar = carService.getById(carId);
        Countries destination;
        try {
            destination = Countries.valueOf(destinationCountry);
        } catch (Exception e) {
            destination = null;
        }
        if (sendCar != null && destination != null) {
            TopicGateway.sendCar(sendCar, destination);
        } else {
            sendCar = null;
        }

        return sendCar;
    }

    /**
     * Sends an invoice abroad
     *
     * @param invoice            of the car
     * @param destinationCountry of the destination
     */
    private void sendInvoiceAbroad(Invoice invoice, Countries destinationCountry) {
        TopicGateway.sendInvoice(invoice, destinationCountry);
    }

    /**
     * Sends invoices to all the foreign cars that are done driving in our country
     *
     * @return list of all the invoices created and sent.
     */
    public List<Invoice> sendAllInvoices() {
        System.out.println("Foreign cars count: " + foreignCars.size());
        List<Car> nonActiveForeignCars = europeanIntegrationService.getAllNonActiveForeignCars(foreignCars);
        System.out.println("non active cars count" + nonActiveForeignCars.size());
        List<Invoice> createdInvoices = new ArrayList<>();

        for (Car car : nonActiveForeignCars) {
            if (foreignCars.contains(car)) {
                //Get the invoice from the car
                Invoice carInvoice = europeanIntegrationService.getCarInvoice(car);
                //Send the invoice abroad
                sendInvoiceAbroad(carInvoice, getCountryByString(car.getTracker().getCountry()));
                //Add the invoice to the list to return
                createdInvoices.add(carInvoice);
                //Remove the car from the list of foreign cars that are active in our simulation
                foreignCars.remove(car);
                System.out.println("car invoice made for: " + car.getLicensePlate().getLicense());
            }
        }
        return createdInvoices;
    }

    /**
     * Notifies the other countries in the EU that a car is stolen.
     *
     * @param carId of the car that is stolen
     */
    public Car reportCarAsStolen(int carId) {
        Car stolenCar = carService.getById(carId);
        carService.reportCarStolen(stolenCar);

        if (stolenCar != null) {
            TopicGateway.announceStolenCar(stolenCar);
        }
        return stolenCar;
    }

    /**
     * Notifies the country that their stolen car is driving in our country
     *
     * @param stolenCar that is stolen
     */
    public void notifyCountryOfDrivingStolenCar(Car stolenCar) {
        //   new NotImplementedException(); //todo implement when the JMS is improved
    }

    /**
     * This gets called when a car is received from the EU to drive in The Netherlands
     *
     * @param car that is received from a foreign country
     */
    @Override
    public void handleReceivedCar(Car car) {
        if (foreignCars.size() < 50) {
            car = europeanIntegrationService.addReceivedDrivingCar(car);

            if (car.isStolen()) {
                notifyCountryOfDrivingStolenCar(car);
            }

            foreignCars.add(car);
            System.out.println("Received a car from: " + car.getTracker().getCountry() + " PLATE: " + car.getLicensePlate().getLicense() + "Foreigncar count:" + foreignCars.size());
        } else {
            System.out.println("!!!Max foreign cars reached!!! Car from " + car.getTracker().getCountry() + "Not added");
        }
    }

    /**
     * This gets called when an invoice is received from a foreign country.
     * The invoice will be added to the correct owner. The received invoice has a temporarily owner with the car numberplate as name
     *
     * @param invoice that is received from a foreign country.
     */
    @Override
    public void handleReceivedInvoice(Invoice invoice) {
        System.out.println("Received an invoice from: " + invoice.getCountryOfOrigin());
        //Currently we only know the license of the car and not the owner. it is saved in the owner object though.
        String licensePlateLicense = invoice.getOwner().getName();
        Car car = carService.getBylicensePlateLicense(licensePlateLicense);

        if (car != null) {
            Owner realOwner = ownerService.getOwnerByCar(car);
            if (realOwner != null) {
                invoice.setOwner(realOwner);
                invoiceService.saveInvoice(invoice);
                System.out.println("Invoice saved to user: " + realOwner.getName());
            } else {
                System.out.println("The received car has no owner.");
            }
        } else {
            System.out.println("There is no known car with licenseplatenumber: " + invoice.getOwner().getName());
        }

    }

    @Override
    public void handleReceivedStolenCarAnnouncement(Car car) {
        System.out.println("Received a stolen car announcement from: " + car.getTracker().getCountry());
        carService.reportCarStolen(car);
    }

    @Override
    public void handleReceivedStolenCarNotification(Car car) {
        System.out.println("Received a stolen car notification about:" + car.getLicensePlate().getLicense());
    }

    /**
     * Gets a country enum value by string.
     *
     * @param countryName of the country
     * @return Country of string. If the string is not valid it will return Netherlands.
     */
    private Countries getCountryByString(String countryName) {
        Countries country;
        try {
            country = Countries.valueOf(countryName);
        } catch (Exception e) {
            country = Countries.NETHERLANDS;
        }
        return country;
    }
}
