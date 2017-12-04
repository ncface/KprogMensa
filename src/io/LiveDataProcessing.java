package io;

import model.MensaStation;
import model.Station;
import view.GraphPlotterArray;
import view.LiveDataView;

import java.util.ArrayList;
import java.util.List;

/**
 * A Class for all live data processing operations
 * @author Rietzler, Hanselmann, Herzog, Clauss
 * @version 4.12.17
 */
public class LiveDataProcessing {
	private static int timeInterval = 1;
	private static int counterMethodCalls = 0;
	private static LiveDataView liveDataView;
	private static List<Station> stations = new ArrayList<>();

	private LiveDataProcessing(){}

	/**
	 * brings the LiveDataProcessing into a valid state
	 * should be called before the other methods are used
	 */
	public static void setUp(){
		for(Station station : Station.getAllStations()){
			if(station instanceof MensaStation){
				stations.add(station);
			}
		}
		LiveDataProcessing.liveDataView = new GraphPlotterArray(stations);
	}

	/**
	 * setter to specify a time interval for the live data visualisation
	 * @param timeInterval the desired time interval
	 */
	public static void setTimeInterval(int timeInterval){
		if(timeInterval > 0){
			LiveDataProcessing.timeInterval = timeInterval;
		}
	}

	/**
	 * plots the waiting queue length of all MensaStations
	 */
	public static void plotWaitingQueueLength(){
		if(counterMethodCalls % timeInterval == 0) {
			for (Station station : stations) {
				liveDataView.addPointToDiagramm(station, 0, station.getInQueue().size());
			}
		}
		counterMethodCalls++;
	}
}
