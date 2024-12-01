package com.stockprediction;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class StockScraper {

    // Finnhub API URL for real-time stock quote data
    private final String apiUrl = "https://finnhub.io/api/v1/quote?symbol=%s&token=%s"; 

    // Fetch the latest stock data (latest price, high, low, open, close, volume)
    public List<String[]> fetchLatestStockData(String symbol, String apiKey) {
        List<String[]> stockData = new ArrayList<>();

        try {
            // Construct the API URL with the stock symbol and API key
            String urlString = String.format(apiUrl, symbol, apiKey);
            URL url = new URL(urlString);

            // Create a connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());

            // Extract the stock data (Finnhub provides real-time data)
            double currentPrice = jsonResponse.getDouble("c");  // Current price
            double highPrice = jsonResponse.getDouble("h");     // High price
            double lowPrice = jsonResponse.getDouble("l");      // Low price
            double openPrice = jsonResponse.getDouble("o");     // Open price
            double volume = jsonResponse.has("v") ? jsonResponse.getDouble("v") : 0.0; // Volume, default to 0 if missing

            // For the purpose of this example, we'll only fetch the latest data
            long timestamp = System.currentTimeMillis() / 1000L;  // Current timestamp in seconds
            String[] row = {
                String.valueOf(timestamp),
                String.valueOf(openPrice),
                String.valueOf(highPrice),
                String.valueOf(lowPrice),
                String.valueOf(currentPrice),
                String.valueOf(volume)
            };
            stockData.add(row);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stockData;  // Return the latest stock data
    }
}
