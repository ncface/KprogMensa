package io;
import model.*;
import view.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.MensaStation;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * This is an abstract factory that creates instances
 * of actor types like objects, stations and their queues 
 * 
 * @author Jaeger, Schmidt
 * @version 2017-10-29
 */
public class Factory {
	
	/** the objects XML data file */
	private static String theObjectDataFile = "xml/customer.xml";
	
	/** the stations XML data file */
	private static String theStationDataFile = "xml/station.xml"; 
	
	/** the start station XML data file */
	private static String theStartStationDataFile = "xml/startstation.xml"; 
	
	/** the end station XML data file */
	private static String theEndStationDataFile = "xml/endstation.xml"; 
	
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
     * 
     */
	public static void createStartScenario(){
		
		/*NOTE: The start station must be created first,
		* because the objects constructor puts the objects into the start stations outgoing queue
		*/ 
		createStartStation(); 
		createObjects();
		createMensaStation();
		createEndStation();
	}
	
	/**
     * create the start station
     * 
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

    		//Spacing aus XML holen. Das ist der Abstand zwischen der visualisierten In- bzw Outqueue und der Station.
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
     * create some objects out of the XML file
     * 
     */
     private static void createObjects(){
    	
    	try {
		
    		//read the information from the XML file into a JDOM Document
    		Document theXMLDoc = new SAXBuilder().build(theObjectDataFile);
    		
    		//the <settings> ... </settings> node, this is the files root Element
    		Element root = theXMLDoc.getRootElement();

    		//get anzalhDurchlaeufe
			int anzahlDurchlaeufe = Integer.parseInt(root.getChildText("anzahl_durchlaeufe"));

    		//get all the objects into a List object
    		List <Element> allObjects = root.getChildren("object");
    		for(int durchlaeufe = 0 ; durchlaeufe < anzahlDurchlaeufe ; durchlaeufe++) {
				//separate every JDOM "object" Element from the list and create Java Customer objects
				for (Element customer : allObjects) {

					// data variables:
					String label = null;
					int processtime = 0;
					int speed = 0;
					String image = null;

					// read data
					label = customer.getChildText("label");
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

					//add always StartStation as first Station (every customer goes through startStation first)
					stationsToGo.add(StationType.START);
					
					HashMap<StationType, Integer> weights= new HashMap<StationType, Integer>();
					for (Element theStation : allStations) {
						StationType theStationType = StationType.parseStationType(theStation.getChild("name").getText());
						int theStationMinWeight = Integer.valueOf(theStation.getChild("min").getText());
						int theStationMaxWeight = Integer.valueOf(theStation.getChild("max").getText());
						stationsToGo.add(theStationType);
						weights.put(theStationType,newRandom(theStationMinWeight,theStationMaxWeight));
					}
					//add always EndStation and Kasse as last Station (every customer goes through EndStation last)
					stationsToGo.add(StationType.KASSE);
					stationsToGo.add(StationType.ENDE);

					//creating a new Customer object
					Customer.create(label, stationsToGo, processtime, speed, XPOS_STARTSTATION, YPOS_STARTSTATION, image, weights);
        		
        		
        		/*
        		 * TIP: Make copies of the object like this (e.g. 5)

        		for (int i = 0; i < 5; i++) {

        			Customer.create(label, stationsToGo, processtime, speed, XPOS_STARTSTATION, YPOS_STARTSTATION, image);

        		}
        		*/
				}
			}
    	
    	} catch (JDOMException e) {
				e.printStackTrace();
		} catch (IOException e) {
				e.printStackTrace();
		}
    }
    
    private static Integer newRandom(int min, int max) {
		return (int)(Math.random() * (max - min) + min);
	}

	/**
     * create some process stations out of the XML file
     * 
     */
     private static void createMensaStation(){
    	
    	try {

			//read the information from the XML file into a JDOM Document
			Document theXMLDoc = new SAXBuilder().build(theStationDataFile);

			//the <settings> ... </settings> node
			Element root = theXMLDoc.getRootElement();

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

				// read data
				label = mensaStation.getChildText("label");
				type = StationType.parseStationType(mensaStation.getChildText("type"));
				troughPut = Double.parseDouble(mensaStation.getChildText("troughput"));
				xPos = Integer.parseInt(mensaStation.getChildText("x_position"));
				yPos = Integer.parseInt(mensaStation.getChildText("y_position"));

				//the <view> ... </view> node
				Element viewGroup = mensaStation.getChild("view");
				// read data
				image = viewGroup.getChildText("image");

				//spacing einlesen um left und right davon einzulesen
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

				//creating a new Station object or Kasse
                if (type == StationType.KASSE)
                {
                    Kasse.create(label, theInqueue, theOutqueue, troughPut, xPos, yPos, image, type);
                }
                else
                {
                    MensaStation.create(label, theInqueue, theOutqueue, troughPut, xPos, yPos, image,type);
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
     * 
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

			//Spacing aus XML holen. Das ist der Abstand zwischen der visualisierten In- bzw Outqueue und der Station.
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
		}
     }
        
}
