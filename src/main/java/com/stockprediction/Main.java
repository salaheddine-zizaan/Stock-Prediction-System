package com.stockprediction;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("Stock Prediction App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);

        // Create a table to display stock data
        String[] columnNames = {"Timestamp", "Open", "High", "Low", "Close", "Volume"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);

        // Create a button to fetch stock data
        JButton fetchButton = new JButton("Fetch Stock Data");

        // Add an input field for stock symbol
        JTextField symbolField = new JTextField(10);
        symbolField.setText("AAPL"); // Default to "AAPL" (Apple stock)

        // Add components to the frame
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Stock Symbol:"));
        inputPanel.add(symbolField);
        inputPanel.add(fetchButton);
        frame.add(inputPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Create a dataset for the chart
        TimeSeries openSeries = new TimeSeries("Open Price");
        TimeSeries highSeries = new TimeSeries("High Price");
        TimeSeries lowSeries = new TimeSeries("Low Price");
        TimeSeries closeSeries = new TimeSeries("Close Price");

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(openSeries);
        dataset.addSeries(highSeries);
        dataset.addSeries(lowSeries);
        dataset.addSeries(closeSeries);

        // Create a chart
        JFreeChart lineChart = ChartFactory.createTimeSeriesChart(
                "Stock Prices Over Time", // Title
                "Time",                  // X-Axis Label
                "Price (USD)",           // Y-Axis Label
                dataset,                 // Dataset
                true,                    // Legend
                true,                    // Tooltips
                false                    // URLs
        );

        // Customize the chart
        XYPlot plot = lineChart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // Set custom colors for each series
        renderer.setSeriesPaint(0, Color.BLUE);   // Open Price
        renderer.setSeriesPaint(1, Color.GREEN); // High Price
        renderer.setSeriesPaint(2, Color.ORANGE); // Low Price
        renderer.setSeriesPaint(3, Color.RED);   // Close Price

        // Set line thickness
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesStroke(1, new BasicStroke(2.0f));
        renderer.setSeriesStroke(2, new BasicStroke(2.0f));
        renderer.setSeriesStroke(3, new BasicStroke(2.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.WHITE);

        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new Dimension(900, 400));
        chartPanel.setMouseWheelEnabled(true); // Enable zoom with mouse wheel
        frame.add(chartPanel, BorderLayout.SOUTH);

        // Action listener for the fetch button
        fetchButton.addActionListener(e -> {
            String symbol = symbolField.getText();
            String apiKey = "ct5u34pr01qp4ur8ebr0ct5u34pr01qp4ur8ebrg"; // Your API Key

            // Start fetching stock data every second and append it to previous data
            startFetchingStockData(symbol, apiKey, tableModel, openSeries, highSeries, lowSeries, closeSeries);
        });

        // Display the frame
        frame.setVisible(true);
    }

    // Method to start fetching stock data every second
    private static void startFetchingStockData(String symbol, String apiKey, DefaultTableModel tableModel, TimeSeries openSeries,
                                               TimeSeries highSeries, TimeSeries lowSeries, TimeSeries closeSeries) {
        StockScraper scraper = new StockScraper();

        // Set up a Timer to fetch stock data every second (1000 ms = 1 second)
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Fetch the latest stock data
                List<String[]> stockData = scraper.fetchLatestStockData(symbol, apiKey);

                // Update the table with the new stock data
                for (String[] row : stockData) {
                    tableModel.addRow(row); // Append the new data to the table
                }

                // Update the chart with the new stock data (without clearing previous data)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                for (String[] row : stockData) {
                    try {
                        // Convert the UNIX timestamp into a Date object
                        long timestamp = Long.parseLong(row[0]);
                        java.util.Date date = new java.util.Date(timestamp * 1000); // Convert to milliseconds

                        // Parse the timestamp into a Second object for the time-series chart
                        Second second = new Second(date);
                        openSeries.addOrUpdate(second, Double.parseDouble(row[1]));
                        highSeries.addOrUpdate(second, Double.parseDouble(row[2]));
                        lowSeries.addOrUpdate(second, Double.parseDouble(row[3]));
                        closeSeries.addOrUpdate(second, Double.parseDouble(row[4]));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }, 0, 10000); // 1000 ms = 1 second
    }
}
