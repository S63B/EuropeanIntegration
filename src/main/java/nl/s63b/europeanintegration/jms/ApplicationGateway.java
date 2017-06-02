package nl.s63b.europeanintegration.jms;

import com.S63B.domain.Entities.Car;
import com.S63B.domain.Entities.Invoice;
import com.S63B.domain.Entities.Tracker;
import kotlin.NotImplementedError;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by Kevin.
 */
public class ApplicationGateway implements TopicListener {

    public ApplicationGateway() {
        TopicGateway.getInstance();
        TopicGateway.addListener(this);
    }

    public void sendCarAbroad(Car car, Countries destinationCountry) {
        TopicGateway.sendCar(car, destinationCountry);
    }

    public void sendInvoiceAbroad(Invoice invoice, Countries destinationCountry) {
        TopicGateway.sendInvoice(invoice, destinationCountry);
    }

    /**
     * Notifies the other countries in the EU that a car is stolen.
     * @param stolenCar that is stolen
     */
    public void reportCarAsStolen(Car stolenCar){
        new NotImplementedException(); //todo implement when the JMS is improved
    }

    /**
     * Notifies the country that their stolen car is driving in our country
     * @param stolenCar
     */
    public void notifyCountryOfStolenCar(Car stolenCar){
        new NotImplementedException(); //todo implement when the JMS is improved
    }

    @Override
    public void handleReceivedCar(Car car) {
        System.out.println("Received a car from: " + car.getTracker().getCountry() + " PLATE: " + car.getLicensePlate().getLicense());
    }

    @Override
    public void handleReceivedInvoice(Invoice invoice) {
        System.out.println("Received an invoice from: " + invoice.getCountryOfOrigin());
    }

    @Override
    public void handleStolenCarAnnouncement(Car car) {

    }

    @Override
    public void handleStolenCarNotification(Car car) {

    }
}
