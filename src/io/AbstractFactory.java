package io;

public abstract class AbstractFactory implements Factory {

	/** the customers data file */
	protected String theCustomersDataFile;

	/** the stations data file */
	protected String theStationDataFile;

	/** the start station XML data file */
	protected String theStartStationDataFile;

	/** the end station data file */
	protected String theEndStationDataFile;

	/** the end station data file */
	protected String theStatisticsDataFile;

	/** the x position of the starting station, also position for all starting objects */
	protected int XPOS_STARTSTATION;

	/** the y position of the starting station, also position for all starting objects */
	protected int YPOS_STARTSTATION;

	/** the spacing between Inqueue and Station (left side)*/
	protected int SPACING_LEFT;

	/** the spacing between Outqueue and Station (right side)*/
	protected int SPACING_RIGHT;

	/**
	 * private Constructor for the abstractFactory
	 * only one FactoryXML Object should be created
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
		createDataCollection();

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
	 * adds the Observers to the related Observables
	 */
	protected abstract void addObserverToObservable();

	/**
	 * creates the DataCollection
	 */
	protected abstract void createDataCollection();
}
