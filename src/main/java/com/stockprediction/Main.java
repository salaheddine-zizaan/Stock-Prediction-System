package com.stockprediction;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("Stock Prediction App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);

        // Create a table to display stock data
        String[] columnNames = {"Date", "Open", "High", "Low", "Close", "Volume"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);

        // Create a button to fetch stock data
        JButton fetchButton = new JButton("Fetch Stock Data");

        // Add an input field for stock symbol
        JTextField symbolField = new JTextField(10);
        symbolField.setText("AAPL");

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
                "Date",                  // X-Axis Label
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
        fetchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String symbol = symbolField.getText();
                String apiKey = "YOUR_API_KEY"; // Replace with your actual API key

                StockScraper scraper = new StockScraper();
                List<String[]> stockData = scraper.fetchStockData(symbol, apiKey);

                // Update the table with the fetched data
                tableModel.setRowCount(0); // Clear previous rows
                openSeries.clear();
                highSeries.clear();
                lowSeries.clear();
                closeSeries.clear();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                for (String[] row : stockData) {
                    tableModel.addRow(row);

                    try {
                        Day day = new Day(sdf.parse(row[0]));
                        openSeries.addOrUpdate(day, Double.parseDouble(row[1]));
                        highSeries.addOrUpdate(day, Double.parseDouble(row[2]));
                        lowSeries.addOrUpdate(day, Double.parseDouble(row[3]));
                        closeSeries.addOrUpdate(day, Double.parseDouble(row[4]));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // Display the frame
        frame.setVisible(true);
    }
}
