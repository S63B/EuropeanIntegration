package nl.s63b.europeanintegration.jms;

import com.gmail.guushamm.EuropeanIntegration.*;
import com.gmail.guushamm.EuropeanIntegration.Car;
import com.gmail.guushamm.EuropeanIntegration.Invoice;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin.
 */
public class TopicGateway {
    private static TopicGateway instance;
    private static Connector connector;
    private Gson gson;
    private static List<TopicListener> listeners;

    private TopicGateway() {
        listeners = new ArrayList<>();
        DomainTranslator.getInstance();
        gson = new Gson();

        connector = new Connector();
        connector.prepare();
        subscribeToQueues();
    }

    public static TopicGateway getInstance() {
        if (instance == null) {
            instance = new TopicGateway();
        }
        return instance;
    }

    public static void sendCar(com.S63B.domain.Entities.Car car, Countries destinationCountry) {
        System.out.println("Car:" + car.getLicensePlate().getLicense() + " published");
        connector.publishCar(DomainTranslator.carToJms(car, destinationCountry));
    }

    public static void sendInvoice(com.S63B.domain.Entities.Invoice invoice, Countries destinationCountry) {
        System.out.println("Invoice:" + invoice.getId() + " published");
        connector.publishInvoice(DomainTranslator.invoiceToJms(invoice, destinationCountry));
    }

    public static void announceStolenCar(com.S63B.domain.Entities.Car stolenCar) {
        System.out.println("StolenCar:" + stolenCar.getLicensePlate().getLicense() + " published");
        connector.publishStolenCar(DomainTranslator.carToStolenCar(stolenCar, Countries.NETHERLANDS));
    }

    public static void alertCountryOfStolenCar(com.S63B.domain.Entities.Car stolenCar, Countries destinationCountry){
        System.out.println("AlertCar:"+ stolenCar.getLicensePlate().getLicense() + "published NOT YET CUS OF IMPLEMENTATION");
        //todo when jms supports it
    }

    private void subscribeToQueues() {

        connector.subscribeToQueue(
                com.gmail.guushamm.EuropeanIntegration.Countries.NETHERLANDS,
                Car.class,
                (String message) -> {
                    Car car = gson.fromJson(message, Car.class);
                    handlerReceivedCar(car);

                    // Lambdas in java always have to have a return value
                    return null;
                }
        );
        connector.subscribeToQueue(
                com.gmail.guushamm.EuropeanIntegration.Countries.NETHERLANDS,
                Invoice.class,
                (String message) -> {
                    Invoice invoice = gson.fromJson(message, Invoice.class);
                    handleReceivedInvoice(invoice);

                    // Lambdas in java always have to have a return value
                    return null;
                }
        );
        connector.subscribeToQueue(
                com.gmail.guushamm.EuropeanIntegration.Countries.NETHERLANDS,
                StolenCar.class,
                (String message) -> {
                    StolenCar stolenCar = gson.fromJson(message, StolenCar.class);
                    handleReceivedAnnouncement(stolenCar);

                    // Lambdas in java always have to have a return value
                    return null;
                }
        );
    }

    public static void addListener(TopicListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    private void handleReceivedInvoice(Invoice invoice) {
        System.out.println("JMS:Received invoice from: " + invoice.getOriginCountry());
        for (TopicListener listener : listeners) {
            listener.handleReceivedInvoice(DomainTranslator.jmsToInvoice(invoice));
        }
    }

    private void handlerReceivedCar(Car car) {
        System.out.println("JMS Received car from: " + car.getOriginCountry());
        for (TopicListener listener : listeners) {
            listener.handleReceivedCar(DomainTranslator.jmsToCar(car));
        }
    }

    //todo when JMS is complete
    private void handleReceivedStolencar(StolenCar stolenCar) {
        System.out.println("JMS:Received stolen car from: " + stolenCar.getCountryOfOrigin());

        for (TopicListener listener : listeners) {
            listener.handleReceivedStolenCarNotification(DomainTranslator.jmsToCar(stolenCar));
        }
    }

    private void handleReceivedAnnouncement(StolenCar stolenCar){
        System.out.println("JMS:Announcement stolen car from" + stolenCar.getCountryOfOrigin());

        for (TopicListener listener : listeners) {
            listener.handleReceivedStolenCarAnnouncement(DomainTranslator.jmsToCar(stolenCar));
        }
    }
}
