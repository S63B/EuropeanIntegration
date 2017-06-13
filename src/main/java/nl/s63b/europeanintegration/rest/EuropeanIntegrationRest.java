package nl.s63b.europeanintegration.rest;

import com.S63B.domain.Entities.Car;
import com.S63B.domain.Entities.Invoice;
import nl.s63b.europeanintegration.application.EUApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin.
 */
@RestController
@RequestMapping("/EU")
@CrossOrigin(origins = "*")
public class EuropeanIntegrationRest {
    EUApplication applicationGateway;

    @Autowired
    public EuropeanIntegrationRest(EUApplication applicationGateway) {
        this.applicationGateway = applicationGateway;
    }

    @RequestMapping(value = "/car/{id}/{country}", method = RequestMethod.POST)
    public ResponseEntity<Car> sendCarAbroad(@PathVariable("id") int carId, @PathVariable("country") String destination) {

        Car sendCar = applicationGateway.sendCarAbroad(carId, destination);
        HttpStatus status = (sendCar != null) ? HttpStatus.OK : HttpStatus.NO_CONTENT;

        return new ResponseEntity<>(sendCar, status);
    }

    @RequestMapping(value = "/car/{id}/stolen", method = RequestMethod.POST)
    public ResponseEntity<Car> reportCarStolen(@PathVariable("id") int carId) {
        Car reportCar = applicationGateway.reportCarAsStolen(carId);

        HttpStatus status = (reportCar != null) ? HttpStatus.OK : HttpStatus.NO_CONTENT;

        return new ResponseEntity<>(reportCar, status);
    }

    @RequestMapping(value = "/invoice/sendAll", method = RequestMethod.POST)
    public ResponseEntity<List<Invoice>> sendAllInvoices(){
        List invoices;
        HttpStatus status = HttpStatus.OK;

        invoices = applicationGateway.sendAllInvoices();

        return new ResponseEntity<List<Invoice>>(invoices, status);
    }

}
