package com.github.blaxk3.converter;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CurrencyRateAPI {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyRateAPI.class);

    protected CurrencyRateAPI (String foreignCurrency1, String foreignCurrency2, double amount) {

        String apiKey = "";

        try {
            URL url = new java.net.URI("https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/" + foreignCurrency1).toURL();

            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setRequestMethod("GET");

            int responseCode = request.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                JsonElement root = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent()));
//                JsonObject jsonObj = root.getAsJsonObject();
//                JsonObject conversionRates = root.getAsJsonObject().getAsJsonObject("conversion_rates");
                double exchangeRate = root.getAsJsonObject().getAsJsonObject("conversion_rates").get(foreignCurrency2).getAsDouble();
                double convertedCurrency = amount * exchangeRate;

            } else {
                logger.error("GET request failed with response code: {}", responseCode);
            }
        } catch (Exception e) {
            logger.error("An error occurred during the API request", e);
        }
    }
}
