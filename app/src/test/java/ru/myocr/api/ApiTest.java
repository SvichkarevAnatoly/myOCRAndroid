package ru.myocr.api;


import org.junit.Test;

import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import ru.myocr.model.City;

import static org.junit.Assert.assertTrue;

public class ApiTest {

    @Test
    public void getAllCities() throws Exception {
        // Create a MockWebServer. These are lean enough that you can create a new
        // instance for every unit test.
        MockWebServer server = new MockWebServer();

        // Schedule some responses.
        server.enqueue(new MockResponse().setBody("[{\"id\":1,\"name\":\"Nsk\"}]"));

        // Start the server.
        server.start();

        // Ask the server for its URL. You'll need this to make HTTP requests.
        HttpUrl baseUrl = server.url("/cities/all/");

        final List<City> cities = new ApiHelper(baseUrl.url().toString()).getAllCities(null);
        assertTrue(!cities.isEmpty());

        // Shut down the server. Instances cannot be reused.
        server.shutdown();
    }
}
