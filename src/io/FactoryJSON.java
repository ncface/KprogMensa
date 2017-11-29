package io;
import model.*;
import org.json.JSONArray;
import org.json.JSONException;
import view.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.MensaStation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

import org.json.JSONObject;

/**
 * This is an abstract factory that creates instances
 * of actor types like objects, stations and their queues by using JSON
 *
 * @author Patrick Hanselmann, Sebastian Herzog, Jeffrey Manuel Rietzler, Nils Clauss
 * @version 2017-11-28
 */
public class FactoryJSON implements FactoryInterface {

    /** the objects JSON data file */
    private static String theObjectDataFile = "json/customer.json";

    /** the stations JSON data file */
    private static String theStationDataFile = "json/station.json";

    /** the start station JSON data file */
    private static String theStartStationDataFile = "json/startstation.json";

    /** the end station JSON data file */
    private static String theEndStationDataFile = "json/endstation.json";

    /** the end station XML data file */
    private static String theStatisticsDataFile = "json/statistics.xml";

    /** an empty jsonObject to load in the jsonObjects temporarly*/
    private static JSONObject jsonObject;

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
        addObserverToObservable();
        createDataCollection();

    }

    /**
     * Singleton for a jsonObject.
     * If jsonObject is not initialized
     * 	- load json file
     * 	- create new instance of JSON Object
     *
     * @param theJSONEbayDataFile , json File the information is written in
     * @return the JSONObject
     */
    public static JSONObject loadJSONObject(String theJSONEbayDataFile){
        if(jsonObject==null){
            try {
                // load the JSON-File into a String
                FileReader fr = new FileReader(theJSONEbayDataFile);
                BufferedReader br = new BufferedReader(fr);
                String json = "";
                for(String line=""; line!=null; line = br.readLine())
                    json+=line;
                br.close();

                // create a new JSON Object with the
                jsonObject = new JSONObject(json);

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return jsonObject;
    }

    /**
     * reads the values for the DataCollection
     */
    private static void createDataCollection() {
        try {
            //read the information from the JSON file into the jsonObject
            jsonObject = loadJSONObject(theStatisticsDataFile);

            //the <settings> ... </settings> node
            //Element root = theXMLDoc.getRootElement();

            //get the start_station into a List object
           // Element startStation = root.getChild("values");

            //get the label
            Double pricePerKilo = jsonObject.getDouble("pricePerKilo");

            DataCollection.setPrice(pricePerKilo);

        } catch (JSONException e) {
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
     * create the start station of the JSON file
     */
    private static void createStartStation() {
        try {
        //read the information from the JSON file into the jsonObject
        JSONObject jsonObject2 = loadJSONObject(theStartStationDataFile).getJSONObject("start_station");

        //read the information from the XML file into a JDOM Document
        // Document theXMLDoc = new SAXBuilder().build(theStartStationDataFile);

        //the <settings> ... </settings> node
        //Element root = theXMLDoc.getRootElement();

        //get the start_station into a List object
        //Element startStation = root.getChild("start_station");

        //get the label
        String label = jsonObject2.getString("label");

        //get the position
        XPOS_STARTSTATION = jsonObject2.getInt("x_position");
        YPOS_STARTSTATION = jsonObject2.getInt("y_position");

        //the <view> ... </view> node
        JSONObject viewJO = jsonObject2.getJSONObject("view");
        String image = viewJO.getString("image"); //"startstation.png"
                                               System.out.println( image + " test");

        //the <spacing> ... </spacing> node
        JSONObject spacingJO = jsonObject2.getJSONObject("spacing");
        SPACING_LEFT = spacingJO.getInt("left");
                                                System.out.println(SPACING_LEFT + " test2");
        SPACING_RIGHT = spacingJO.getInt("right");

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



        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * create some objects out of the JSON file
     */
    private static void createObjects() {
        try {

            //read the information from the JSON file into the jsonObject
            JSONObject jsonObject2 = loadJSONObject(theObjectDataFile);
            System.out.println(jsonObject2 + " test3");

            //read the information from the XML file into a JDOM Document
            //Document theXMLDoc = new SAXBuilder().build(theObjectDataFile);

            //the <settings> ... </settings> node, this is the files root Element
            //Element root = theXMLDoc.getRootElement();

            //get anzalhDurchlaeufe
            int anzahlDurchlaeufe = jsonObject2.getInt("anzahl_durchlaeufe");
                    System.out.println(anzahlDurchlaeufe);

            //get all the JSONobjects into a ArrayList object
            List <JSONObject> allObjects = new ArrayList<>();

            //iterate through the JSONArray to load single JSON objects into a list
            JSONArray objects = jsonObject2.getJSONArray("object");
            for(Object objectIterator : objects)
            {
                JSONObject jo = (JSONObject) objectIterator;
                allObjects.add(jo);
            }

            //the counter for created Customer
            int counterCustomer = 0;

            for(int durchlaeufe = 0 ; durchlaeufe < anzahlDurchlaeufe ; durchlaeufe++) {
                //separate every JDOM "object" Element from the list and create Java Customer objects
                for (JSONObject customer : allObjects) {

                    // data variables:
                    String label = null;
                    int processtime = 0;
                    int speed = 0;
                    String image = null;

                    // read data
                    label = customer.getString("label");
                    label += ("_"+counterCustomer); //add an unique identifier to the label of the customer
                    counterCustomer++;
                    processtime = Integer.parseInt(customer.getString("processtime"));
                    speed = Integer.parseInt(customer.getString("speed"));

                    //the <view> ... </view> node
                    //Element viewGroup = customer.getChild("view");
                    // read data
                    image = customer.getString("image");

                    //get all the stations, where the object wants to go to
                    //the <sequence> ... </sequence> node
                    //Element sequenceGroup = customer.getChild("sequence");

                    //get all the JSONobjects into a ArrayList object
                    List <JSONObject> allStations = new ArrayList<>();
                    //List<Element> allStations = sequenceGroup.getChildren("station");
                    //iterate through the JSONArray to load single JSON objects into a list
                    JSONArray stations = jsonObject.getJSONArray("sequence");
                    for(Object objectIterator : stations)
                    {
                        JSONObject jo = (JSONObject) objectIterator;
                        allStations.add(jo);
                    }

                    //get the elements into a list
                    ArrayList<StationType> stationsToGo = new ArrayList<StationType>();

                    //add always StartStation as first Station (every customer goes through startStation first)
                    stationsToGo.add(StationType.START);

                    HashMap<StationType, Integer> weights= new HashMap<StationType, Integer>();
                    for (JSONObject theStation : allStations) {
                        StationType theStationType = StationType.parseStationType(theStation.getString("name")); //.getText()); weg da schon string erhalten?
                        int theStationMinWeight = Integer.valueOf(theStation.getString("min")); //.getText());
                        int theStationMaxWeight = Integer.valueOf(theStation.getString("max")); //.getText());
                        stationsToGo.add(theStationType);
                        weights.put(theStationType,newRandom(theStationMinWeight,theStationMaxWeight));
                    }
                    //add always EndStation and Kasse as last Station (every customer goes through EndStation last)
                    stationsToGo.add(StationType.KASSE);
                    stationsToGo.add(StationType.ENDE);

                    //get the gaussian standard deviance (deviation)
                    double stdDeviance = Integer.parseInt(jsonObject.getString("stdDeviance"));
                    Random rand = new Random();

                    //limit gets calculated
                    int frustrationLimit;
                    //generate new gauss limit until value is in range
                    do{
                        frustrationLimit = (int) (rand.nextGaussian()* stdDeviance + Customer.MAXFRUSTRATIONLIMIT);
                    }while(frustrationLimit <= 1 || frustrationLimit >= Customer.MAXFRUSTRATIONLIMIT);

                    //creating a new Customer object
                    Customer.create(label, stationsToGo, processtime, speed, XPOS_STARTSTATION, YPOS_STARTSTATION, image, weights, frustrationLimit);


        		/*
        		 * TIP: Make copies of the object like this (e.g. 5)

        		for (int i = 0; i < 5; i++) {

        			Customer.create(label, stationsToGo, processtime, speed, XPOS_STARTSTATION, YPOS_STARTSTATION, image);

        		}
        		*/
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static Integer newRandom(int min, int max) {
        return (int)(Math.random() * (max - min) + min);
    }

    /**
     * create some process stations out of the JSON file
     */
    private static void createMensaStation() {
        try {

            //read the information from the JSON file into the jsonObject
            jsonObject = loadJSONObject(theStationDataFile);

            //the <settings> ... </settings> node
           // Element root = theXMLDoc.getRootElement();

            //add the inQueueLimits to the types
            //following block replaces: List<Element> allLimits = root.getChildren("type_limit");
            List <JSONObject> allLimits = new ArrayList<>();
            JSONArray limits = jsonObject.getJSONArray("station");
            for(Object objectIterator : limits)
            {
                JSONObject jo = (JSONObject) objectIterator;
                allLimits.add(jo);
            }

            for(JSONObject jo :allLimits){
                StationType type = StationType.parseStationType(jo.getString("type"));
                int limit = Integer.parseInt(jo.getString("limit"));
                type.setInQueueLimit(limit);
            }

            List <JSONObject> allStations = new ArrayList<>();

            JSONArray stations = jsonObject.getJSONArray("station");
            for(Object objectIterator : stations)
            {
                JSONObject jo = (JSONObject) objectIterator;
                allStations.add(jo);
            }

            //separate every JDOM "station" Element from the list and create Java Station objects
            for (JSONObject mensaStation : allStations) {

                // data variables:
                String label;
                StationType type;
                double troughPut;
                int xPos ;
                int yPos ;
                String image;
                double operatingCostsPerClockbeat;

                // read data
                label = mensaStation.getString("label");
                type = StationType.parseStationType(mensaStation.getString("type"));
                troughPut = Double.parseDouble(mensaStation.getString("troughput"));
                xPos = Integer.parseInt(mensaStation.getString("x_position"));
                yPos = Integer.parseInt(mensaStation.getString("y_position"));
                operatingCostsPerClockbeat = Double.parseDouble(mensaStation.getString("operating_costs"));

                //the <view> ... </view> node
                //Element viewGroup = mensaStation.getString("view");
                // read data
                image = mensaStation.getString("image");

                //reads spacing and gets later the left and right side spacing from it
                //Element spacing = mensaStation.getChild("spacing");

                //CREATE THE INQUEUE
                //get the inqueue into a List object
                int xPosInQueue = xPos - Integer.parseInt(mensaStation.getString("left"));
                int yPosInQueue = yPos;

                //create a Synchronized Queue for the station inqueue
                SynchronizedQueue theInqueue = SynchronizedQueue.createQueue(QueueViewJPanel.class, xPosInQueue, yPosInQueue);;

                //CREATE THE OUTQUEUE
                //get the outqueue into a List object
                int xPosOutQueue = xPos + Integer.parseInt(mensaStation.getString("right"));
                int yPosOutQueue = yPos;

                //create a Synchronized Queue for the station inqueue
                SynchronizedQueue theOutqueue = SynchronizedQueue.createQueue(QueueViewText.class, xPosOutQueue, yPosOutQueue);

                //creating a new Kasse, AdditionalMensaStation or MensaStation Object depending on the station type
                if (type == StationType.KASSE)  {
                    Kasse.create(label, theInqueue, theOutqueue, troughPut, xPos, yPos, image, type, operatingCostsPerClockbeat);
                }
                else if(type == StationType.ADDITIONAL){
                    AdditionalMensaStation.create(label, theInqueue, theOutqueue, troughPut, xPos, yPos, image, type, operatingCostsPerClockbeat);
                }
                else{
                    MensaStation.create(label, theInqueue, theOutqueue, troughPut, xPos, yPos, image,type, operatingCostsPerClockbeat);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * create the end station
     */
    private static void createEndStation() {
        try {

            //read the information from the JSON file into the jsonObject
            jsonObject = loadJSONObject(theEndStationDataFile);

            //the <settings> ... </settings> node
            //Element root = theXMLDoc.getRootElement();

            //get the end_station into a List object
           // Element endStation = root.getChild("end_station");

            //get label
            String label = jsonObject.getString("label");

            //position
            int xPos = Integer.parseInt(jsonObject.getString("x_position"));
            int yPos = Integer.parseInt(jsonObject.getString("y_position"));

            //the <view> ... </view> node
            //Element viewGroup = endStation.getChild("view");
            // the image
            String image = jsonObject.getString("image");

            //reads spacing and gets later the left and right side spacing from it
            //Element spacingGroup = endStation.getChild("spacing");
            SPACING_LEFT = Integer.parseInt(jsonObject.getString("left"));
            SPACING_RIGHT = Integer.parseInt(jsonObject.getString("right"));

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


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
