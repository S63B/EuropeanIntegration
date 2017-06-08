package nl.s63b.europeanintegration.application;

import com.S63B.domain.Entities.Car;
import com.S63B.domain.Entities.Invoice;
import nl.s63b.europeanintegration.jms.Countries;
import nl.s63b.europeanintegration.jms.TopicGateway;
import nl.s63b.europeanintegration.jms.TopicListener;
import nl.s63b.europeanintegration.service.CarService;
import nl.s63b.europeanintegration.service.EuropeanIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin.
 */
@Service
public class EUApplication implements TopicListener {
    private EuropeanIntegrationService europeanIntegrationService;
    private CarService carService;
    private List<Car> foreignCars;

    @Autowired
    public EUApplication(CarService carService, EuropeanIntegrationService europeanIntegrationService) {
        this.europeanIntegrationService = europeanIntegrationService;
        this.carService = carService;
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
        List<Car> nonActiveForeignCars = europeanIntegrationService.getAllNonActiveForeignCars(foreignCars);
        List<Invoice> createdInvoices = europeanIntegrationService.getAllCarInvoices(nonActiveForeignCars);

        for (Car nonactive : nonActiveForeignCars) {
            if (foreignCars.contains(nonactive)) {
                foreignCars.remove(nonactive);
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
     * @param car
     */
    @Override
    public void handleReceivedCar(Car car) {
        if (foreignCars.size() < 50) {
            europeanIntegrationService.addReceivedDrivingCar(car);
            if (car.isStolen()) {
                notifyCountryOfDrivingStolenCar(car);
            }
            foreignCars.add(car);
            System.out.println("Received a car from: " + car.getTracker().getCountry() + " PLATE: " + car.getLicensePlate().getLicense() + "Foreigncar count:" + foreignCars.size());
        } else {
            System.out.println("!!!Max foreign cars reached!!! Car from " + car.getTracker().getCountry() + "Not added");
        }
    }

    @Override
    public void handleReceivedInvoice(Invoice invoice) {
        System.out.println("Received an invoice from: " + invoice.getCountryOfOrigin());
    }

    @Override
    public void handleReceivedStolenCarAnnouncement(Car car) {
        carService.reportCarStolen(car);
    }

    @Override
    public void handleReceivedStolenCarNotification(Car car) {

    }
}
