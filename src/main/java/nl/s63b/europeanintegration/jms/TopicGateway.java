package nl.s63b.europeanintegration.jms;

import com.google.gson.Gson;
import nl.s63b.europeanintegration.jms.kotlin.Car;
import nl.s63b.europeanintegration.jms.kotlin.Connector;
import nl.s63b.europeanintegration.jms.kotlin.Countries;
import nl.s63b.europeanintegration.jms.kotlin.Invoice;

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

    public static void sendCar(com.S63B.domain.Entities.Car car, nl.s63b.europeanintegration.jms.Countries destinationCountry) {
        connector.publishCar(DomainTranslator.carToJms(car, destinationCountry));
    }

    public static void sendInvoice(com.S63B.domain.Entities.Invoice invoice, nl.s63b.europeanintegration.jms.Countries destinationCountry) {
        connector.publishInvoice(DomainTranslator.invoiceToJms(invoice, destinationCountry));
    }

    private void subscribeToQueues() {
        connector.subscribeToQueue(
                Countries.NETHERLANDS,
                Car.class,
                (String message) -> {
                    Car car = gson.fromJson(message, Car.class);
                    handlerReceivedCar(car);

                    // Lambdas in java always have to have a return value
                    return null;
                }
        );
        connector.subscribeToQueue(
                Countries.NETHERLANDS,
                Invoice.class,
                (String message) -> {
                    Invoice invoice = gson.fromJson(message, Invoice.class);
                    handleReceivedInvoice(invoice);

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
        for (TopicListener listener : listeners) {
            listener.handleReceivedInvoice(DomainTranslator.jmsToInvoice(invoice));
        }
    }

    private void handlerReceivedCar(Car car) {
        for (TopicListener listener : listeners) {
            listener.handleReceivedCar(DomainTranslator.jmsToCar(car));
        }
    }
}
