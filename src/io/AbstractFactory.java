package io;

import model.AdditionalMensaStation;
import model.MensaStation;
import model.Station;

import java.util.ArrayList;
import java.util.List;

/**
 * an abstract class for the factory
 * @author Rietzler, Hanselmann, Herzog, Clauss
 * @version 2017-12-03
 */
public abstract class AbstractFactory implements Factory {

	/** the customers data file */
	protected String theCustomersDataFile;

	/** the stations data file */
	protected String theStationDataFile;

	/** the start station data file */
	protected String theStartStationDataFile;

	/** the end station data file */
	protected String theEndStationDataFile;

	/** the statistics data file */
	protected String theStatisticsDataFile;

	/** the x position of the starting station, also position for all starting customers */
	protected int xPosStartStation;

	/** the y position of the starting station, also position for all starting customers */
	protected int yPosStartStation;

	/** the spacing between Inqueue and Station (left side)*/
	protected int spacingLeft;

	/** the spacing between Outqueue and Station (right side)*/
	protected int spacingRight;

	/**
	 * private Constructor for the abstractFactory
	 * @param scenario the selected scenario
	 */
	protected AbstractFactory(String format, String scenario, String fileEnding){
		if(!format.endsWith("/")){
			format += "/";
		}
		if(!scenario.endsWith("/")){
			scenario += "/";
		}
		if(!fileEnding.startsWith(".")){
			fileEnding = "." + fileEnding;
		}
		String prePath = format + scenario;
		theCustomersDataFile = prePath + "customer" + fileEnding;
		theStationDataFile = prePath + "station" + fileEnding;
		theStartStationDataFile = prePath + "startstation" + fileEnding;
		theEndStationDataFile = prePath + "endstation" + fileEnding;
		theStatisticsDataFile = prePath + "statistics" + fileEnding;
	}

	/**
	 * creates a random value in the given range
	 * @param min the minimum value for the random Generator
	 * @param max the maximum value for the random Generator
	 * @return the random integer
	 */
	protected Integer newRandom(int min, int max) {
		return (int)(Math.random() * (max - min) + min);
	}

	/**
	 * creates the start-scenario
	 */
	@Override
	public void createStartScenario(){
		/*NOTE: The start station must be created first,
		* because the objects constructor puts the objects into the start stations outgoing queue
		*/
		createStartStation();
		createCustomers();
		createMensaStation();
		createEndStation();
		addObserverToObservable();
		setUpDataCollection();
	}

	/**
	 * creates the startStation
	 */
	protected abstract void createStartStation();

	/**
	 * creates all customers
	 */
	protected abstract void createCustomers();

	/**
	 * creates all MensaStations
	 */
	protected abstract void createMensaStation();

	/**
	 * creates the EndStation
	 */
	protected abstract void createEndStation();

	/**
     * This Method adds the AdditionalMensaStations to the Observable other MensaStations.
     */
    protected void addObserverToObservable() {
        //make list and add all additionalMensaStations
        List<AdditionalMensaStation> additionalMensaStations = new ArrayList<>();
        for(Station station: Station.getAllStations()){
            if(station instanceof AdditionalMensaStation) {
                additionalMensaStations.add((AdditionalMensaStation) station);
            }
        }
        //add observers to the observable MensaStations when StationTypes matches
        for(Station station: Station.getAllStations()){
            for (Station additionalMensaStation: additionalMensaStations){
                if (additionalMensaStation.getLabel().toUpperCase().contains(station.getStationType().toString())){
                    ((MensaStation)station).setObserver((AdditionalMensaStation)additionalMensaStation);
                }
            }
        }
    }

	/**
	 * initialize the DataCollection
	 */
	protected abstract void setUpDataCollection();
}
