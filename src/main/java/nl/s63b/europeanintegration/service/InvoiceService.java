package nl.s63b.europeanintegration.service;

import com.S63B.domain.Entities.Car;
import com.S63B.domain.Entities.Car_Ownership;
import com.S63B.domain.Entities.Invoice;
import com.S63B.domain.Entities.Owner;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import nl.s63b.europeanintegration.jms.Countries;
import nl.s63b.europeanintegration.jms.dao.InvoiceDao;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Created by Kevin.
 */
@Service
@Transactional
public class InvoiceService {

    private InvoiceDao invoiceDao;
    CarOwnerService carOwnerService;
    OwnerService ownerService;
    Gson gson;

    @Autowired
    public InvoiceService(CarOwnerService carOwnerService, OwnerService ownerService, InvoiceDao invoiceDao) {
        this.carOwnerService = carOwnerService;
        this.ownerService = ownerService;
        this.invoiceDao = invoiceDao;
        this.gson = new GsonBuilder().create();
    }

    /**
     * todo This method should work with the correct data!!!!
     *
     * @param car
     * @return
     */
    public Invoice getInvoiceForeignCar(Car car) {
        Owner carOwner = ownerService.getOrCreateOwnerByCar(car);
        Car_Ownership ownership = carOwnerService.addCarToOwner(car, carOwner);
        ownerService.addOwnership(carOwner, ownership);

        Invoice tempinvoice = new Invoice();
        tempinvoice.setId(0);
        tempinvoice.setOwner(carOwner);
        tempinvoice.setTotalPrice(1337);
        tempinvoice.setDate(DateTime.now());
        tempinvoice.setStartDate(DateTime.now().minusDays(1));
        tempinvoice.setEndDate(DateTime.now().plusDays(1));

        tempinvoice = getInvoiceFromAdministration(tempinvoice ,carOwner, DateTime.now().minusDays(1), DateTime.now().plusDays(1));

        tempinvoice.setCountryOfOrigin(String.valueOf(Countries.NETHERLANDS));


        return tempinvoice;
    }

    public void saveInvoice(Invoice invoice) {
        invoiceDao.save(invoice);
    }

    public Invoice getInvoiceFromAdministration(Invoice tempinvoice, Owner owner, DateTime fromdate, DateTime tillDate) {
        String httpGet = "http://192.168.24.120:8082/invoice/generate?ownerId=" + owner.getId() + "&start_date=" + fromdate.getMillis() + "&end_date=" + tillDate.getMillis();
        URL url = null;
        try {

            url = new URL(httpGet);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
            JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
            JsonElement object = rootobj.get("entity");

            JsonObject invoiceObject = object.getAsJsonObject();
            tempinvoice.setId(invoiceObject.get("id").getAsInt());
            tempinvoice.setTotalPrice(invoiceObject.get("totalPrice").getAsDouble());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return tempinvoice;
    }
}
