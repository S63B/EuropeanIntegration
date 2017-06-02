package nl.s63b.europeanintegration.jms;

import com.S63B.domain.Entities.Car;
import com.S63B.domain.Entities.Invoice;

/**
 * Created by Kevin.
 */
public interface TopicListener {
    void handleReceivedCar(Car car);
    void handleReceivedInvoice(Invoice invoice);
    // This gets called when other countries report a car for stolen
    void handleStolenCarAnnouncement(Car car);
    // This gets called when a car reported for stolen is present in a different car
    void handleStolenCarNotification(Car car);
}
