package io;
import model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import view.*;
import java.io.IOException;
import java.util.*;

import model.MensaStation;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * This is an abstract factory that creates instances
 * of actor types like customers, stations and their queues by using JSON
 *
 * @author Patrick Hanselmann, Sebastian Herzog, Jeffrey Manuel Rietzler, Nils Clauss
 * @version 2017-11-28
 */
public class FactoryJSON extends AbstractFactory {
    /**the FactoryJSON Object*/
    private static Factory factoryJSON;

    /** an empty jsonObject to load in the jsonObjects temporarly*/
    private JSONObject jsonObject;

    /** The directory with all json scenarios */
    public static final String FORMAT_DIRECTORY = "json/";

    /** The ending of all json files */
    private static final String FILE_ENDING = ".json";


    /**
     * private Constructor for FactoryJSON
     * @param scenario the selected scenario
     */
    private FactoryJSON(String scenario){
        super(FORMAT_DIRECTORY, scenario, FILE_ENDING);
    }

    /**
     * method that returns a reference for the FactoryJSON Object
     * @return the FactoryJSON Object
     */
    public static Factory createFactory(String scenarioPath){
        factoryJSON = new FactoryJSON(scenarioPath);
        return factoryJSON;
    }

    /**
     * Singleton for a jsonObject.
     * 	- load json file
     * 	- create new instance of JSON Object
     *
     * @param theJSONDataFile , json File in which the information is written in
     * @return the JSONObject
     */
    private JSONObject loadJSONObject(String theJSONDataFile){
        try {
            // load the JSON-File into a String
            FileReader fr = new FileReader(theJSONDataFile);
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
        return jsonObject;
    }

    /**
     * reads the values for the DataCollection
     */
    protected void createDataCollection() {
        try {
            //read the information from the JSON file into the jsonObject
            jsonObject = loadJSONObject(theStatisticsDataFile).getJSONObject("values");

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
     * create the start station of the JSON file
     */
    protected void createStartStation() {
        try {
        //read the information from the JSON file into the jsonObject
        jsonObject = loadJSONObject(theStartStationDataFile).getJSONObject("start_station");

        //get the label
        String label = jsonObject.getString("label");

        //get the position
        xPosStartStation = jsonObject.getInt("x_position");
        yPosStartStation = jsonObject.getInt("y_position");

        //the <view> ... </view> node
        JSONObject viewJO = jsonObject.getJSONObject("view");
        String image = viewJO.getString("image");

        //the <spacing> ... </spacing> node
        JSONObject spacingJO = jsonObject.getJSONObject("spacing");
        spacingLeft = spacingJO.getInt("left");
        spacingRight = spacingJO.getInt("right");

        //CREATE THE INQUEUE
        // the positions-
        int xPosInQueue = xPosStartStation - spacingLeft;
        int yPosInQueue = yPosStartStation;

        //create the inqueue
        SynchronizedQueue theInQueue = SynchronizedQueue.createQueue(QueueViewText.class, xPosInQueue, yPosInQueue);

        //CREATE THE OUTQUEUE
        // the positions
        int xPosOutQueue = xPosStartStation + spacingRight;
        int yPosOutQueue = yPosStartStation;

        //create the outqueue
        SynchronizedQueue theOutQueue = SynchronizedQueue.createQueue(QueueViewText.class, xPosOutQueue, yPosOutQueue);

        //creating a new StartStation object
        StartStation.create(label, theInQueue, theOutQueue, xPosStartStation, yPosStartStation, image);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * create some customers out of the JSON file
     */
    protected void createCustomers() {
        try {

            //read the information from the JSON file into the jsonObject
            jsonObject = loadJSONObject(theCustomersDataFile);

            //get amountOfToGeneratingCustomersPerType from Json
            int amountOfToGeneratingCustomersPerType = jsonObject.getInt("anzahl_durchlaeufe");

            //get generalStationsBefore from Json
            JSONObject generalStationsBefore = jsonObject.getJSONObject("generalStationsBefore");

            //get the JsonArray with the names of the stations before the customer-specific stations
            JSONArray generalStationsBeforeNames = generalStationsBefore.getJSONArray("names");

            //get generalStationsAfter from Json
            JSONObject generalStationsAfter = jsonObject.getJSONObject("generalStationsAfter");

            //get the JsonArray with the names of the stations after the customer-specific stations
            JSONArray generalStationsAfterNames = generalStationsAfter.getJSONArray("names");

            //iterate through the JSONArray to load single JSON objects into a list
            JSONArray allCustomers = jsonObject.getJSONArray("customer");

            //the counter for created Customer
            int counterCustomer = 0;

            for(int iterations = 0 ; iterations < amountOfToGeneratingCustomersPerType ; iterations++) {
            	//iterate over all CustomerTypes
                for (Object customerType : allCustomers) {
                    //cast Object (customerType) to JSONObject(customer)
                    JSONObject customer = (JSONObject) customerType;

                    //variables for customer generating
                    String label;
                    int processtime;
                    int speed;
                    String imagePath;

                    // read data
                    label = customer.getString("label");
                    //add an unique identifier to the label of the customer
                    label += ("_"+counterCustomer++);
                    processtime = customer.getInt("processtime");
                    speed = customer.getInt("speed");

                    //the <view> ... </view> node
                    JSONObject viewTag = customer.getJSONObject("view");
                    imagePath = viewTag.getString("image");

                    //get all the stations, where the object wants to go to
                    JSONArray allStations = customer.getJSONArray("sequence");

                    //get the elements into a list
                    Queue<StationType> stationsToGo = new LinkedList<>();

                    //add the stations every customers goes before going to the customer-specific stations
                    for (Object stationName: generalStationsBeforeNames){
                        stationsToGo.add(StationType.parseStationType(stationName.toString()));
                    }

                    HashMap<StationType, Integer> weights= new HashMap<StationType, Integer>();
                    for (Object station : allStations) {
                        //cast Object (station) to JSONObject(theStation)
                        JSONObject theStation = (JSONObject) station;

                        StationType theStationType = StationType.parseStationType(theStation.getString("name"));
                        int theStationMinWeight = theStation.getInt("min");
                        int theStationMaxWeight = theStation.getInt("max");
                        //add the station to the list where the customer should go
                        stationsToGo.add(theStationType);
                        //add the weight of the food, the customer should take in the stations
                        weights.put(theStationType,newRandom(theStationMinWeight,theStationMaxWeight));
                    }
                    //add the stations every customers goes after going to the customer-specific stations
                    for (Object stationName: generalStationsAfterNames){
                        stationsToGo.add(StationType.parseStationType(stationName.toString()));
                    }

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
                    Customer.create(label, stationsToGo, processtime, speed, xPosStartStation, yPosStartStation, imagePath, weights, frustrationLimit);

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Integer newRandom(int min, int max) {
        return (int)(Math.random() * (max - min) + min);
    }

    /**
     * create some process stations out of the JSON file
     */
    @SuppressWarnings("Duplicates")
    protected void createMensaStation() {
        try {

            //read the information from the JSON file into the jsonObject
            jsonObject = loadJSONObject(theStationDataFile);

            //load all JSONObjects from "station" into a JSONArray
            JSONArray typeLimits = jsonObject.getJSONArray("type_limits");


            //add the inQueueLimits to the types
            for(Object typeLimit : typeLimits){
                //cast Object (typeLimit) to JSONObject(station)
                JSONObject station = (JSONObject) typeLimit;

                //set the limit to of maximum queue size
                StationType stationType = StationType.parseStationType(station.getString("type"));
                int limit = station.getInt("limit");
                stationType.setInQueueLimit(limit);
            }

            JSONArray allStations = jsonObject.getJSONArray("station");

            //separate every JDOM "station" Element from the list and create Java Station objects
            for (Object station : allStations) {
            	//cast Object (station) to JSONObject(mensaStation)
                JSONObject mensaStation = (JSONObject) station;

                //variables for station generating
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
                SynchronizedQueue theInqueue = SynchronizedQueue.createQueue(QueueViewJPanel.class, xPosInQueue, yPosInQueue);

                //CREATE THE OUTQUEUE
                //get the outqueue into a List object
                int xPosOutQueue = xPos + spacingJO.getInt("right");
                int yPosOutQueue = yPos;

                //create a Synchronized Queue for the station inqueue
                SynchronizedQueue theOutqueue = SynchronizedQueue.createQueue(QueueViewText.class, xPosOutQueue, yPosOutQueue);

                //creating a new Kasse, AdditionalMensaStation or MensaStation Object depending on the station type
                if (label.toUpperCase().contains(StationType.KASSE.toString()))  {
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
    protected void createEndStation() {
        try {

            //read the information from the JSON file into the jsonObject
            jsonObject = loadJSONObject(theEndStationDataFile).getJSONObject("end_station");

            //get label
            String label = jsonObject.getString("label");

            //get position
            int xPos = Integer.parseInt(jsonObject.getString("x_position"));
            int yPos = Integer.parseInt(jsonObject.getString("y_position"));

            //the <view> ... </view> node
            JSONObject viewNode = jsonObject.getJSONObject("view");
            // the image
            String imagePath = viewNode.getString("image");

            //reads spacing and gets later the left and right side spacing from it
            //the <view> ... </view> node
            JSONObject spacingJO = jsonObject.getJSONObject("spacing");

            spacingLeft = spacingJO.getInt("left");
            spacingRight = spacingJO.getInt("right");

            //CREATE THE INQUEUE
            // the positions
            int xPosInQueue = xPos - spacingLeft;
            int yPosInQueue = yPos;

            //create the inqueue
            SynchronizedQueue theInQueue = SynchronizedQueue.createQueue(QueueViewText.class, xPosInQueue, yPosInQueue);

            //CREATE THE OUTQUEUE
            // the positions
            int xPosOutQueue = xPos + spacingRight;
            int yPosOutQueue = yPos;

            //create the outqueue
            SynchronizedQueue theOutQueue = SynchronizedQueue.createQueue(QueueViewText.class, xPosOutQueue, yPosOutQueue);

            //creating a new EndStation object
            EndStation.create(label, theInQueue, theOutQueue, xPos, yPos, imagePath);


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
