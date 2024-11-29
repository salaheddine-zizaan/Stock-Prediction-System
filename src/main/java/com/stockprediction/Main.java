package com.stockprediction;

public class Main {
    public static void main(String[] args) {
        String apiKey = "YOUR_API_KEY";  // Replace with your Alpha Vantage API key
        String symbol = "AAPL";         // Stock symbol (e.g., Apple Inc.)

        StockScraper scraper = new StockScraper();
        scraper.fetchStockData(symbol, apiKey);
    }
}
