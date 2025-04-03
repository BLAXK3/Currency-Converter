package com.github.blaxk3.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

public class CurrencyRateAPI {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyRateAPI.class);

    public String getApiKeyService() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                logger.error("Unable to find config.properties");
                return null;
            }
            properties.load(input);
            return properties.getProperty("API_KEY");
        } catch (IOException e) {
            logger.error("Error loading config.properties", e);
            return null;
        }
    }

    public String getURL() {
        return "https://v6.exchangerate-api.com/v6/" + getApiKeyService();
    }

    public JsonObject getJsonObject(URL url) {
        try {
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setRequestMethod("GET");

            int responseCode = request.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                JsonElement root = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent()));
                return root.getAsJsonObject();
            } else {
                logger.error("GET request failed with response code: {}", responseCode);
            }
        } catch (Exception e) {
            logger.error("An error occurred during the API request", e);
        }
        return null;
    }


    public String[] getCurrencyCode() throws MalformedURLException, URISyntaxException {
        JsonObject jsonObject = getJsonObject(new java.net.URI(getURL() + "/latest/" + "USD").toURL());
        if (jsonObject != null && jsonObject.has("conversion_rates")) {
            JsonObject code = jsonObject.getAsJsonObject("conversion_rates");
            if (code != null && !code.keySet().isEmpty()) {
                return code.keySet().toArray(new String[0]);
            }
        }
        return null;
    }

    public String convert(String foreignCurrency1, String foreignCurrency2, BigDecimal amount) throws MalformedURLException, URISyntaxException {
        return getJsonObject(new java.net.URI(getURL() + "/pair/" + foreignCurrency1 + "/" + foreignCurrency2 + "/" + amount).toURL()).get("conversion_result").toString();
    }
}
