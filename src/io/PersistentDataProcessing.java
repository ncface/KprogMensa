package io;

import view.BarChartView;
import view.ChartView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * A Class for all persistent data processing operations
 * @author Rietzler, Clauss, Herzog, Hanselmann
 * @version 2017-12-05
 */
public class PersistentDataProcessing {
	private static final int SCALING = 5;

	/**
	 * private constructor for the PersistentDataProcessing
	 */
	private PersistentDataProcessing(){}

	/**
	 * this method shows the Operating costs from the file "DataOutput/DataOperatingCosts.csv"
	 */
	public static void showOperatingCosts(){
		try {
			ChartView chartView = new BarChartView("Operating Costs");
			//collects data about the operating costs
			String theData = "";

			BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("DataOutput/DataOperatingCosts.csv")));

			chartView.setChartHeading(bufferedReader.readLine());

			String [] stationNameOperatingCosts;

			for(String line = bufferedReader.readLine();line != null; line = bufferedReader.readLine()) {
				stationNameOperatingCosts = line.split(",");
				String stationName = stationNameOperatingCosts[0];
				int operatingCost = Integer.parseInt(stationNameOperatingCosts[1]);
				chartView.add(stationName,operatingCost,SCALING);
			}
			chartView.showChart();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * this method reads data from the file DataMoneyLoss.csv and displays them
	 */
	public static void showMoneyloss(){
		try {
			ChartView chartView = new BarChartView("Money Loss");
			String theData = "";

			BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("DataOutput/DataMoneyLoss.csv")));

			String [] headLines;
			String[] money;

			//get the headlines
			headLines = bufferedReader.readLine().split(",");
			//get the values
			money = bufferedReader.readLine().split(",");

			for (int i=0; i<money.length; i++) {
				int value = (int) Double.parseDouble(money[i]);
				chartView.add(headLines[i], value, SCALING);
			}
			chartView.showChart();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
