package com.stockprediction;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class StockScraper {
    public void fetchStockData(String symbol, String apiKey) {
        String function = "TIME_SERIES_DAILY";
        try {
            // Construct the API URL
            String urlString = String.format(
                "https://www.alphavantage.co/query?function=%s&symbol=%s&apikey=%s",
                function, symbol, apiKey
            );
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
            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonObject timeSeries = jsonResponse.getAsJsonObject("Time Series (Daily)");

            // Print the stock prices
            System.out.println("Date\t\tOpen\tHigh\tLow\tClose\tVolume");
            timeSeries.entrySet().forEach(entry -> {
                String date = entry.getKey();
                JsonObject dailyData = entry.getValue().getAsJsonObject();
                System.out.printf("%s\t%s\t%s\t%s\t%s\t%s%n",
                        date,
                        dailyData.get("1. open").getAsString(),
                        dailyData.get("2. high").getAsString(),
                        dailyData.get("3. low").getAsString(),
                        dailyData.get("4. close").getAsString(),
                        dailyData.get("5. volume").getAsString()
                );
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
