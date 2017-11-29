package io;
import model.*;
import org.json.JSONArray;
import org.json.JSONObject;
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

/**
 * This is an abstract factory that creates instances
 * of actor types like objects, stations and their queues by using JSON
 *
 * @author Patrick Hanselmann, Sebastian Herzog, Jeffrey Manuel Rietzler, Nils Clauss
 * @version 2017-11-28
 */
public class FactoryJSON implements FactoryInterface {
    private static final String FORMAT_DIRECTORY = "json/";

    private static String SCENARIO_DIRECTORY = "Szenario 1/";
    /** the objects JSON data file */
    private static String theObjectDataFile = FORMAT_DIRECTORY + SCENARIO_DIRECTORY + "customer.json";

    /** the stations JSON data file */
    private static String theStationDataFile = FORMAT_DIRECTORY + SCENARIO_DIRECTORY + "station.json";

    /** the start station JSON data file */
    private static String theStartStationDataFile = FORMAT_DIRECTORY + SCENARIO_DIRECTORY + "startstation.json";

    /** the end station JSON data file */
    private static String theEndStationDataFile = FORMAT_DIRECTORY + SCENARIO_DIRECTORY + "endstation.json";

    /** the end station XML data file */
    private static String theStatisticsDataFile = FORMAT_DIRECTORY + SCENARIO_DIRECTORY + "statistics.json";

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
     * Setter for the scenarioFolder.
     * updates the filepaths
     * @param scenario the name of the scenarioFolder
     */
    public static void setScenario(String scenario){
        SCENARIO_DIRECTORY = scenario;
        String path = FORMAT_DIRECTORY + SCENARIO_DIRECTORY;
        theObjectDataFile = path + "customer.json";
        theStationDataFile = path + "station.json";
        theStatisticsDataFile = path + "statistics.json";
        theStartStationDataFile = path + "startstation.json";
        theEndStationDataFile = path + "endstation.json";
    }

    /**
     * Singleton for a jsonObject.
     *      //If jsonObject is not initialized
     * 	- load json file
     * 	- create new instance of JSON Object
     *
     * @param theJSONEbayDataFile , json File in which the information is written in
     * @return the JSONObject
     */
    public static JSONObject loadJSONObject(String theJSONEbayDataFile){
        //if(jsonObject==null){
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
        //}
        return jsonObject;
    }

    /**
     * reads the values for the DataCollection
     */
    private static void createDataCollection() {
        try {
            //read the information from the JSON file into the jsonObject
            jsonObject = loadJSONObject(theStatisticsDataFile).getJSONObject("values");

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
        jsonObject = loadJSONObject(theStartStationDataFile).getJSONObject("start_station");

        //read the information from the XML file into a JDOM Document
        // Document theXMLDoc = new SAXBuilder().build(theStartStationDataFile);

        //the <settings> ... </settings> node
        //Element root = theXMLDoc.getRootElement();

        //get the start_station into a List object
        //Element startStation = root.getChild("start_station");

        //get the label
        String label = jsonObject.getString("label");

        //get the position
        XPOS_STARTSTATION = jsonObject.getInt("x_position");
        YPOS_STARTSTATION = jsonObject.getInt("y_position");

        //the <view> ... </view> node
        JSONObject viewJO = jsonObject.getJSONObject("view");
        String image = viewJO.getString("image");

        //the <spacing> ... </spacing> node
        JSONObject spacingJO = jsonObject.getJSONObject("spacing");
        SPACING_LEFT = spacingJO.getInt("left");
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
            jsonObject = loadJSONObject(theObjectDataFile);

            //get anzalhDurchlaeufe
            int anzahlDurchlaeufe = jsonObject.getInt("anzahl_durchlaeufe");

            //iterate through the JSONArray to load single JSON objects into a list
            JSONArray objects = jsonObject.getJSONArray("object");

            //the counter for created Customer
            int counterCustomer = 0;

            for(int durchlaeufe = 0 ; durchlaeufe < anzahlDurchlaeufe ; durchlaeufe++) {

                for (Object customerJOA : objects) {
                    //das einzelne JSONObject aus dem JSONArray ziehen

                    JSONObject customer = (JSONObject) customerJOA;


                    // data variables:
                    String label = null;
                    int processtime = 0;
                    int speed = 0;
                    String image = null;

                    // read data
                    label = customer.getString("label");
                    label += ("_"+counterCustomer); //add an unique identifier to the label of the customer
                    counterCustomer++;
                    processtime = customer.getInt("processtime");
                    speed = customer.getInt("speed");

                    //the <view> ... </view> node
                    JSONObject viewJO = customer.getJSONObject("view");
                    image = viewJO.getString("image");

                    JSONArray stations = customer.getJSONArray("sequence");

                    //get the elements into a list
                    ArrayList<StationType> stationsToGo = new ArrayList<StationType>();

                    //add always StartStation as first Station (every customer goes through startStation first)
                    stationsToGo.add(StationType.START);

                    HashMap<StationType, Integer> weights= new HashMap<StationType, Integer>();
                    for (Object theStationJOA : stations) {
                        JSONObject theStation = (JSONObject) theStationJOA;

                        StationType theStationType = StationType.parseStationType(theStation.getString("name")); //.getText()); weg da schon string erhalten?
                        int theStationMinWeight = theStation.getInt("min"); //.getText());
                        int theStationMaxWeight = theStation.getInt("max"); //.getText());
                        stationsToGo.add(theStationType);
                        weights.put(theStationType,newRandom(theStationMinWeight,theStationMaxWeight));
                    }
                    //add always EndStation and Kasse as last Station (every customer goes through EndStation last)
                    stationsToGo.add(StationType.KASSE);
                    stationsToGo.add(StationType.ENDE);

                    //get the gaussian standard deviance (deviation)
                    double stdDeviance = jsonObject.getInt("stdDeviance");

                    Random rand = new Random();

                    //limit gets calculated
                    int frustrationLimit;
                    //generate new gauss limit until value is in range
                    do{
                        frustrationLimit = (int) (rand.nextGaussian()* stdDeviance + Customer.MAXFRUSTRATIONLIMIT);
                    }while(frustrationLimit <= 1 || frustrationLimit >= Customer.MAXFRUSTRATIONLIMIT);

                    //creating a new Customer object
                    Customer.create(label, stationsToGo, processtime, speed, XPOS_STARTSTATION, YPOS_STARTSTATION, image, weights, frustrationLimit);

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

            //load all JSONObjects from "station" into a JSONArray
            JSONArray limits = jsonObject.getJSONArray("type_limits");


            //add the inQueueLimits to the types
            for(Object stationJOA : limits){
                JSONObject station = (JSONObject) stationJOA;

                StationType type = StationType.parseStationType(station.getString("type"));
                int limit = station.getInt("limit");
                type.setInQueueLimit(limit);
            }

            //List <JSONObject> allStations = new ArrayList<>();

            JSONArray allStations = jsonObject.getJSONArray("station");
            /**for(Object objectIterator : stations)
            {
                JSONObject jo = (JSONObject) objectIterator;
                allStations.add(jo);
            }*/

            //separate every JDOM "station" Element from the list and create Java Station objects
            for (Object mensaStationJOA : allStations) {

                JSONObject mensaStation = (JSONObject) mensaStationJOA;
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
                troughPut = mensaStation.getDouble("troughput");
                xPos = mensaStation.getInt("x_position");
                yPos = mensaStation.getInt("y_position");
                operatingCostsPerClockbeat = mensaStation.getDouble("operating_costs");

                //the <view> ... </view> node
                JSONObject viewJO = mensaStation.getJSONObject("view");
                // read data
                image = viewJO.getString("image");

                //reads spacing and gets later the left and right side spacing from it
                //Element spacing = mensaStation.getChild("spacing");

                //the <spacing> ... </spacing> node
                JSONObject spacingJO = mensaStation.getJSONObject("spacing");

                //CREATE THE INQUEUE
                //get the inqueue into a List object
                int xPosInQueue = xPos - spacingJO.getInt("left");
                int yPosInQueue = yPos;

                //create a Synchronized Queue for the station inqueue
                SynchronizedQueue theInqueue = SynchronizedQueue.createQueue(QueueViewJPanel.class, xPosInQueue, yPosInQueue);;

                //CREATE THE OUTQUEUE
                //get the outqueue into a List object
                int xPosOutQueue = xPos + spacingJO.getInt("right");
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
            jsonObject = loadJSONObject(theEndStationDataFile).getJSONObject("end_station");

            //get label
            String label = jsonObject.getString("label");

            //position
            int xPos = Integer.parseInt(jsonObject.getString("x_position"));
            int yPos = Integer.parseInt(jsonObject.getString("y_position"));

            //the <view> ... </view> node
            JSONObject viewJO = jsonObject.getJSONObject("view");
            // the image
            String image = viewJO.getString("image");

            //reads spacing and gets later the left and right side spacing from it
            //the <view> ... </view> node
            JSONObject spacingJO = jsonObject.getJSONObject("spacing");

            SPACING_LEFT = spacingJO.getInt("left");
            SPACING_RIGHT = spacingJO.getInt("right");

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
