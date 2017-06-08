package nl.s63b.europeanintegration.service;

import com.S63B.domain.Entities.Car;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * Created by Kevin.
 */
@Service
@Transactional
public class SimulationService {

    @Autowired
    public SimulationService() {
    }

    public void addCarToSimulation(Car car) {
        String httpPost = "http://192.168.24.125:8081/vehicle?licensePlate=" + car.getLicensePlate().getLicense();
        try {
            HttpUriRequest request = new HttpPost(httpPost);
            HttpClientBuilder.create().build().execute(request);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    public List<String> getActiveCars() {
        String httpGet = "http://192.168.24.125:8081/vehicles";
        List<String> activeCars = new ArrayList<>();

        // Connect to the URL using java's native library
        URL url = null;
        try {
            url = new URL(httpGet);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
            JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.

            JsonArray cars = rootobj.getAsJsonArray("entity");
            for(JsonElement object : cars){
                activeCars.add(object.getAsJsonObject().get("licensePlate").getAsString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return activeCars;
    }
}
