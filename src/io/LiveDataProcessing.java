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
	private static LiveDataProcessing liveDataProcessing;
	private  int counterMethodCalls;
	private  LiveDataView liveDataView;
	private  List<Station> stations;

	/**
	 * constructor of class LiveDataProcessing
	 */
	private LiveDataProcessing(){
		stations = new ArrayList<>();
		setUp();
		counterMethodCalls = 0;
	}

	/**
	 * create method to implement singleton pattern on class LiveDataProcessing
	 * @return the only LiveDataProcessing object
	 */
	public static LiveDataProcessing create(){
		liveDataProcessing = new LiveDataProcessing();
		return liveDataProcessing;
	}

	/**
	 * brings the LiveDataProcessing into a valid state
	 */
	private void setUp(){
		for(Station station : Station.getAllStations()){
			if(station instanceof MensaStation){
				stations.add(station);
			}
		}
		liveDataView = new GraphPlotterArray(stations);
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
	public void plotWaitingQueueLength(){
		if(counterMethodCalls % timeInterval == 0) {
			for (Station station : stations) {
				liveDataView.addPointToDiagramm(station, 0, station.getInQueue().size());
			}
		}
		counterMethodCalls++;
	}
}
