package nl.s63b.europeanintegration.service;

import com.S63B.domain.Entities.Car;
import com.S63B.domain.Entities.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin.
 */
@Service
@Transactional
public class EuropeanIntegrationService {
    CarService carService;
    SimulationService simulationService;
    InvoiceService invoiceService;
    List<String> activeCars = new ArrayList<>();

    @Autowired
    public EuropeanIntegrationService(CarService carService, SimulationService simulationService, InvoiceService invoiceService) {
        this.carService = carService;
        this.simulationService = simulationService;
        this.invoiceService = invoiceService;
    }

    /**
     * Make a car drive in our country
     *
     * @param car that will drive in your country
     */
    public void addReceivedDrivingCar(Car car) {
        carService.saveCar(car);
        simulationService.addCarToSimulation(car);
    }

    public Invoice getCarInvoice(Car foreignCar) {
         return invoiceService.getInvoiceForeignCar(foreignCar);
    }

    public List<Car> getAllNonActiveForeignCars(List<Car> foreignCars) {
        updateActiveCars();
        List<Car> nonActiveCars = new ArrayList<>();
        for (Car car : foreignCars) {
            if (!activeCars.contains(car.getLicensePlate().getLicense()))
                nonActiveCars.add(car);
        }

        return nonActiveCars;
    }

    private void updateActiveCars() {
        activeCars = simulationService.getActiveCars();
    }
}
