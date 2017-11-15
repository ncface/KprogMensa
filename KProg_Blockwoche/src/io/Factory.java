package io;
import model.*;
import view.*;
import java.io.IOException;
import java.util.ArrayList;
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
	private static String theObjectDataFile = "xml/object.xml"; 
	
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
    		
    		//CREATE THE INQUEUE
    		//the <inqueue> ... </inqueue> node
    		Element inqueueGroup = startStation.getChild("inqueue");
    		
    		// the positions
    		int xPosInQueue = Integer.parseInt(inqueueGroup.getChildText("x_position"));
    		int yPosInQueue = Integer.parseInt(inqueueGroup.getChildText("y_position"));
    		
    		//create the inqueue
    		SynchronizedQueue theInQueue = SynchronizedQueue.createQueue(QueueViewText.class, xPosInQueue, yPosInQueue);
    		
    		//CREATE THE OUTQUEUE
    		//the <outqueue> ... </outqueue> node
    		Element outqueueGroup = startStation.getChild("outqueue");
    		
    		// the positions
    		int xPosOutQueue = Integer.parseInt(outqueueGroup.getChildText("x_position"));
    		int yPosOutQueue = Integer.parseInt(outqueueGroup.getChildText("y_position"));
    		
    		//create the outqueue
    		SynchronizedQueue theOutQueue = SynchronizedQueue.createQueue(QueueViewText.class, xPosOutQueue, yPosOutQueue);
    		    		
    		//creating a new StartStation object
    		StartStation.create(label, theInQueue, theOutQueue, XPOS_STARTSTATION, YPOS_STARTSTATION, image);
    	    
    	
    	} catch (JDOMException e) {
				e.printStackTrace();
		} catch (IOException e) {
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
					ArrayList<String> stationsToGo = new ArrayList<String>();

					for (Element theStation : allStations) {

						stationsToGo.add(theStation.getText());

					}

					//creating a new Customer object
					Customer.create(label, stationsToGo, processtime, speed, XPOS_STARTSTATION, YPOS_STARTSTATION, image);
        		
        		
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
    			MensaStationType type;
    			double troughPut;
    			int xPos ;
    			int yPos ;
    			String image;
    			    			
    			// read data
    			label = mensaStation.getChildText("label");
    			type = MensaStationType.parseMensaStationType(mensaStation.getChildText("type"));
    			troughPut = Double.parseDouble(mensaStation.getChildText("troughput"));
        		xPos = Integer.parseInt(mensaStation.getChildText("x_position"));
        		yPos = Integer.parseInt(mensaStation.getChildText("y_position"));
        		        		
        		//the <view> ... </view> node
        		Element viewGroup = mensaStation.getChild("view");
        		// read data
        		image = viewGroup.getChildText("image");
        		        		
        		//CREATE THE INQUEUES
        		
        		//get all the inqueues into a List object
        		List <Element> inqueues = mensaStation.getChildren("inqueue");
        		
        		//create a list of the stations inqueues 
        		ArrayList<SynchronizedQueue> theInqueues = new ArrayList<SynchronizedQueue>(); //ArrayList for the created inqueues
        		
        		for (Element inqueue : inqueues) {
        			
        			int xPosInQueue = Integer.parseInt(inqueue.getChildText("x_position"));
            		int yPosInQueue = Integer.parseInt(inqueue.getChildText("y_position"));
            		
            		//create the actual inqueue an add it to the list
            		theInqueues.add(SynchronizedQueue.createQueue(QueueViewJPanel.class, xPosInQueue, yPosInQueue));
            	}
        		        		
        		//CREATE THE OUTQUEUES
        		
        		//get all the outqueues into a List object
        		List <Element> outqueues = mensaStation.getChildren("outqueue");
        		
        		//create a list of the stations outqueues 
        		ArrayList<SynchronizedQueue> theOutqueues = new ArrayList<SynchronizedQueue>(); //ArrayList for the created outqueues
        		
        		for (Element outqueue : outqueues) {
        			
        			int xPosOutQueue = Integer.parseInt(outqueue.getChildText("x_position"));
            		int yPosOutQueue = Integer.parseInt(outqueue.getChildText("y_position"));
            		
            		//create the actual outqueue an add it to the list
            		theOutqueues.add(SynchronizedQueue.createQueue(QueueViewText.class, xPosOutQueue, yPosOutQueue));
            	}
        		
        		//creating a new Station object
        		MensaStation.create(label, theInqueues, theOutqueues, troughPut, xPos, yPos, image,type);
        		
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
    		
    		//CREATE THE INQUEUE
    		//the <inqueue> ... </inqueue> node
    		Element inqueueGroup = endStation.getChild("inqueue");
    		
    		// the positions
    		int xPosInQueue = Integer.parseInt(inqueueGroup.getChildText("x_position"));
    		int yPosInQueue = Integer.parseInt(inqueueGroup.getChildText("y_position"));
    		
    		//create the inqueue
    		SynchronizedQueue theInQueue = SynchronizedQueue.createQueue(QueueViewText.class, xPosInQueue, yPosInQueue);
    		
    		//CREATE THE OUTQUEUE
    		//the <outqueue> ... </outqueue> node
    		Element outqueueGroup = endStation.getChild("outqueue");
    		
    		// the positions
    		int xPosOutQueue = Integer.parseInt(outqueueGroup.getChildText("x_position"));
    		int yPosOutQueue = Integer.parseInt(outqueueGroup.getChildText("y_position"));
    		
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
