package io;
import model.*;
import view.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import model.MensaStation;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * This is an abstract factory that creates instances
 * of actor types like objects, stations and their queues 
 * 
 * @author Jaeger, Schmidt; Patrick Hanselmann, Sebastian Herzog, Jeffrey Manuel Rietzler, Nils Clauss
 * @version 2017-11-28
 */
public class FactoryXML implements FactoryInterface{
	private static final String FORMAT_DIRECTORY = "xml/";

	private static String SCENARIO_DIRECTORY = "Szenario 1/";

	/** the customers XML data file */
	private static String theCustomerDataFile = FORMAT_DIRECTORY + SCENARIO_DIRECTORY + "customer.xml";
	
	/** the stations XML data file */
	private static String theStationDataFile = FORMAT_DIRECTORY + SCENARIO_DIRECTORY + "station.xml";
	
	/** the start station XML data file */
	private static String theStartStationDataFile = FORMAT_DIRECTORY + SCENARIO_DIRECTORY + "startstation.xml";
	
	/** the end station XML data file */
	private static String theEndStationDataFile = FORMAT_DIRECTORY + SCENARIO_DIRECTORY + "endstation.xml";
	
	/** the end station XML data file */
	private static String theStatisticsDataFile = FORMAT_DIRECTORY + SCENARIO_DIRECTORY + "statistics.xml";

	/** the x position of the starting station, also position for all starting objects */
	private static int XPOS_STARTSTATION;
	
	/** the y position of the starting station, also position for all starting objects */
	private static int YPOS_STARTSTATION;

	/** the spacing between Inqueue and Station (left side)*/
	private static int SPACING_LEFT;

	/** the spacing between Outqueue and Station (right side)*/
	private static int SPACING_RIGHT;
		
	
	/**
     * create the actors for the starting scenario
     */
	@SuppressWarnings("Duplicates")
	public static void createStartScenario(){
		
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
	 * Setter for the scenarioFolder.
	 * updates the filepaths
	 * @param scenario the name of the scenarioFolder
	 */
	public static void setScenario(String scenario){
			SCENARIO_DIRECTORY = scenario;
			String path = FORMAT_DIRECTORY + SCENARIO_DIRECTORY;
			theCustomerDataFile = path + "customer.xml";
			theStationDataFile = path + "station.xml";
			theStatisticsDataFile = path + "statistics.xml";
			theStartStationDataFile = path + "startstation.xml";
			theEndStationDataFile = path + "endstation.xml";
	}

	/**
	 * reads the values for the DataCollection
	 */
	private static void createDataCollection() {
		try {
    		
    		//read the information from the XML file into a JDOM Document
    		Document theXMLDoc = new SAXBuilder().build(theStatisticsDataFile);
    		
    		//the <settings> ... </settings> node
    		Element root = theXMLDoc.getRootElement();
    		
    		//get the start_station into a List object
    		Element startStation = root.getChild("values");
    		
    		//get the label
    		Double pricePerKilo = Double.parseDouble(startStation.getChildText("pricePerKilo"));
    		    		    		
    		DataCollection.setPrice(pricePerKilo);

    	} catch (JDOMException e) {
				e.printStackTrace();
		} catch (IOException e) {
				e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This Method adds the AdditionalMensaStations to the Observable other MensaStations.
	 */
	private static void addObserverToObservable() {
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
     * create the start station
     */
     private static void createStartStation(){
    	
    	try {
    		
    		//read the information from the XML file into a JDOM Document
    		Document theXMLDoc = new SAXBuilder().build(theStartStationDataFile);
    		
    		//the <settings> ... </settings> node
    		Element root = theXMLDoc.getRootElement();
    		
    		//get the start_station into a List object
    		Element startStation = root.getChild("start_station");
    		
    		//get the label
    		String label = startStation.getChildText("label");
    		    		    		
    		//get the position
    		XPOS_STARTSTATION = Integer.parseInt(startStation.getChildText("x_position"));
    		YPOS_STARTSTATION = Integer.parseInt(startStation.getChildText("y_position"));
    		
    		//the <view> ... </view> node
    		Element viewGroup = startStation.getChild("view");
    		// the image
    		String image = viewGroup.getChildText("image");

			//reads spacing and gets later the left and right side spacing from it
			Element spacingGroup = startStation.getChild("spacing");
			SPACING_LEFT = Integer.parseInt(spacingGroup.getChildText("left"));
			SPACING_RIGHT = Integer.parseInt(spacingGroup.getChildText("right"));

    		//CREATE THE INQUEUE
    		// the positions-
    		int xPosInQueue = XPOS_STARTSTATION - SPACING_LEFT;
    		int yPosInQueue = YPOS_STARTSTATION;
    		
    		//create the inqueue
    		SynchronizedQueue theInQueue = SynchronizedQueue.createQueue(QueueViewText.class, xPosInQueue, yPosInQueue);
    		
    		//CREATE THE OUTQUEUE
    		// the positions
    		int xPosOutQueue = XPOS_STARTSTATION + SPACING_RIGHT;
    		int yPosOutQueue = YPOS_STARTSTATION;
    		
    		//create the outqueue
    		SynchronizedQueue theOutQueue = SynchronizedQueue.createQueue(QueueViewText.class, xPosOutQueue, yPosOutQueue);

    		//creating a new StartStation object
    		StartStation.create(label, theInQueue, theOutQueue, XPOS_STARTSTATION, YPOS_STARTSTATION, image);


    	} catch (JDOMException e) {
				e.printStackTrace();
		} catch (IOException e) {
				e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
     }
	
	/**
     * create some customers out of the XML file
     */
     private static void createCustomers(){
    	
    	try {
		
    		//read the information from the XML file into a JDOM Document
    		Document theXMLDoc = new SAXBuilder().build(theCustomerDataFile);
    		
    		//the <settings> ... </settings> node, this is the files root Element
    		Element root = theXMLDoc.getRootElement();

    		//get anzahl_durchlaeufe
			int amountOfToGeneratingCustomersPerType = Integer.parseInt(root.getChildText("anzahl_durchlaeufe"));

			//get the <generalStationsBefore> ... </generalStationsBefore> node
			Element generalStationsBefore = root.getChild("generalStationsBefore");

			//get every name element of the generalStationsBefore
			List<Element> generalStationsBeforeNames = generalStationsBefore.getChildren("name");

			//get the <generalStationsAfter> ... </generalStationsAfter> node
			Element generalStationsAfter = root.getChild("generalStationsAfter");

			//get every name element of the generalStationsAfter
			List<Element> generalStationsAfterNames = generalStationsAfter.getChildren("name");

			//get all the customers into a List customer
    		List <Element> allCustomers = root.getChildren("customer");

    		//the counter for created Customer
			int counterCustomer = 0;

    		for(int iterations = 0 ; iterations < amountOfToGeneratingCustomersPerType ; iterations++) {
				//separate every JDOM "object" Element from the list and create Java Customer objects
				for (Element customer : allCustomers) {

					//variables for customer generating
					String label = null;
					int processtime = 0;
					int speed = 0;
					String image = null;

					// read data
					label = customer.getChildText("label");
					//add an unique identifier to the label of the customer
					label += ("_"+counterCustomer);
					counterCustomer++;
					processtime = Integer.parseInt(customer.getChildText("processtime"));
					speed = Integer.parseInt(customer.getChildText("speed"));

					//the <view> ... </view> node
					Element viewGroup = customer.getChild("view");
					// read data
					image = viewGroup.getChildText("image");

					//get all the stations, where the object wants to go to
					//the <sequence> ... </sequence> node
					Element sequenceGroup = customer.getChild("sequence");

					List<Element> allStations = sequenceGroup.getChildren("station");

					//get the elements into a list
					ArrayList<StationType> stationsToGo = new ArrayList<StationType>();

					//add the stations every customers goes before going to the customer-specific stations
					for (Element element: generalStationsBeforeNames){
						stationsToGo.add(StationType.parseStationType(element.getText()));
					}

					//adds all stations where the customer wants to go and
					// reads the <name>,<min>,<max> for the wanted food of the customer
					HashMap<StationType, Integer> weights= new HashMap<StationType, Integer>();
					for (Element theStation : allStations) {
						StationType theStationType = StationType.parseStationType(theStation.getChild("name").getText());
						int theStationMinWeight = Integer.valueOf(theStation.getChild("min").getText());
						int theStationMaxWeight = Integer.valueOf(theStation.getChild("max").getText());
						//add the station to the list where the customer should go
						stationsToGo.add(theStationType);
						//add the weight of the food, the customer should take in the stations
						weights.put(theStationType,newRandom(theStationMinWeight,theStationMaxWeight));
					}
					//add the stations every customer goes after going to the customer-specific stations
					for (Element element: generalStationsAfterNames){
						stationsToGo.add(StationType.parseStationType(element.getText()));
					}

					//get the gaussian standard deviance (deviation)
					double stdDeviance = Integer.parseInt(root.getChildText("stdDeviance"));
					Random rand = new Random();
					
					//limit gets calculated
					int frustrationLimit;
					//generate new gauss limit until value is in range
					do{
						//move of the mean value 2/3 to the right
						frustrationLimit = (int) (rand.nextGaussian()* stdDeviance + (Customer.MAXFRUSTRATIONLIMIT*2/3));
					}while(frustrationLimit <= 1 || frustrationLimit >= Customer.MAXFRUSTRATIONLIMIT);

					//creating a new Customer object
					Customer.create(label, stationsToGo, processtime, speed, XPOS_STARTSTATION, YPOS_STARTSTATION, image, weights, frustrationLimit);
				}
			}
    	
    	} catch (JDOMException e) {
				e.printStackTrace();
		} catch (IOException e) {
				e.printStackTrace();
		}
    }

	/**
	 * creates a random value in the given range
	 * @param min the minimum value for the random Generator
	 * @param max the maximum value for the random Generator
	 * @return the random integer
	 */
	private static Integer newRandom(int min, int max) {
		return (int)(Math.random() * (max - min) + min);
	}

	/**
     * create some mensa stations out of the XML file
     */
	@SuppressWarnings("Duplicates")
     private static void createMensaStation(){
    	
    	try {

			//read the information from the XML file into a JDOM Document
			Document theXMLDoc = new SAXBuilder().build(theStationDataFile);

			//the <settings> ... </settings> node
			Element root = theXMLDoc.getRootElement();

			//add the inQueueLimits to the types
			List<Element> allLimits = root.getChildren("type_limits");
			for(Element element:allLimits){
				StationType type = StationType.parseStationType(element.getChildText("type"));
				int limit = Integer.parseInt(element.getChildText("limit"));
				type.setInQueueLimit(limit);
			}

			//get all the stations into a List object
			List <Element> stations = root.getChildren("station");

			//separate every JDOM "station" Element from the list and create Java Station objects
			for (Element mensaStation : stations) {

				// data variables:
				String label;
				StationType type;
				double troughPut;
				int xPos ;
				int yPos ;
				String image;
				double operatingCostsPerClockbeat;

				// read data
				label = mensaStation.getChildText("label");
				type = StationType.parseStationType(mensaStation.getChildText("type"));
				troughPut = Double.parseDouble(mensaStation.getChildText("troughput"));
				xPos = Integer.parseInt(mensaStation.getChildText("x_position"));
				yPos = Integer.parseInt(mensaStation.getChildText("y_position"));
				operatingCostsPerClockbeat = Double.parseDouble(mensaStation.getChildText("operating_costs"));

				//the <view> ... </view> node
				Element viewGroup = mensaStation.getChild("view");
				// read data
				image = viewGroup.getChildText("image");

				//reads spacing and gets later the left and right side spacing from it
				Element spacing = mensaStation.getChild("spacing");

				//CREATE THE INQUEUE
				//get the inqueue into a List object
				int xPosInQueue = xPos - Integer.parseInt(spacing.getChildText("left"));
				int yPosInQueue = yPos;

				//create a Synchronized Queue for the station inqueue
				SynchronizedQueue theInqueue = SynchronizedQueue.createQueue(QueueViewJPanel.class, xPosInQueue, yPosInQueue);;

				//CREATE THE OUTQUEUE
				//get the outqueue into a List object
				int xPosOutQueue = xPos + Integer.parseInt(spacing.getChildText("right"));
				int yPosOutQueue = yPos;

				//create a Synchronized Queue for the station inqueue
				SynchronizedQueue theOutqueue = SynchronizedQueue.createQueue(QueueViewText.class, xPosOutQueue, yPosOutQueue);

				//creating a new Kasse, AdditionalMensaStation or MensaStation Object depending on the station type
                if (label.toUpperCase().contains(StationType.KASSE.toString())) {
                    Kasse.create(label, theInqueue, theOutqueue, troughPut, xPos, yPos, image, type, operatingCostsPerClockbeat);
                }
                else if(type == StationType.ADDITIONAL){
					AdditionalMensaStation.create(label, theInqueue, theOutqueue, troughPut, xPos, yPos, image, type, operatingCostsPerClockbeat);
				}
                else{
                    MensaStation.create(label, theInqueue, theOutqueue, troughPut, xPos, yPos, image,type, operatingCostsPerClockbeat);
                }
			}
    	} catch (JDOMException e) {
				e.printStackTrace();
		} catch (IOException e) {
				e.printStackTrace();
		}
    	
    }
    
     /**
     * create the end station
     */
     private static void createEndStation(){
    	
    	try {
    		
    		//read the information from the XML file into a JDOM Document
    		Document theXMLDoc = new SAXBuilder().build(theEndStationDataFile);
    		
    		//the <settings> ... </settings> node
    		Element root = theXMLDoc.getRootElement();
    		
    		//get the end_station into a List object
    		Element endStation = root.getChild("end_station");
    		
    		//get label
    		String label = endStation.getChildText("label");
    		    		    		
    		//position
    		int xPos = Integer.parseInt(endStation.getChildText("x_position"));
    		int yPos = Integer.parseInt(endStation.getChildText("y_position"));
    		
    		//the <view> ... </view> node
    		Element viewGroup = endStation.getChild("view");
    		// the image
    		String image = viewGroup.getChildText("image");

			//reads spacing and gets later the left and right side spacing from it
			Element spacingGroup = endStation.getChild("spacing");
			SPACING_LEFT = Integer.parseInt(spacingGroup.getChildText("left"));
			SPACING_RIGHT = Integer.parseInt(spacingGroup.getChildText("right"));

    		//CREATE THE INQUEUE
    		// the positions
			int xPosInQueue = xPos - SPACING_LEFT;
			int yPosInQueue = yPos;
    		
    		//create the inqueue
    		SynchronizedQueue theInQueue = SynchronizedQueue.createQueue(QueueViewText.class, xPosInQueue, yPosInQueue);
    		
    		//CREATE THE OUTQUEUE
    		// the positions
    		int xPosOutQueue = xPos + SPACING_RIGHT;
    		int yPosOutQueue = yPos;
    		
    		//create the outqueue
    		SynchronizedQueue theOutQueue = SynchronizedQueue.createQueue(QueueViewText.class, xPosOutQueue, yPosOutQueue);
    		
    		//creating a new EndStation object
    		EndStation.create(label, theInQueue, theOutQueue, xPos, yPos, image);
    	    
    	
    	} catch (JDOMException e) {
				e.printStackTrace();
		} catch (IOException e) {
				e.printStackTrace();
		} catch (Exception e){
    		e.printStackTrace();
		}
     }
        
}
