package io;

import controller.Simulation;
import model.*;

import java.io.*;
import java.util.*;

/**
 * A class to collect and save data from the simulation to files
 * @author Herzog, Clauss, Hanselmann, Rietzler
 * @version 2017-12-03
 */
public class DataCollection{
    private static final String OUT_FOLDER_PATH = "DataOutput/";
    private static final String FILE_PATH_LEFT_EARLY = OUT_FOLDER_PATH +"DataLeftEarly.csv";
    private static final String FILE_PATH_ADDITIONAL_STATION = OUT_FOLDER_PATH +"DataAdditionalStation.csv";
    private static final String FILE_PATH_MONEY_LOSS = OUT_FOLDER_PATH +"DataMoneyLoss.csv";
    private static final String FILE_PATH_OPERATING_COSTS = OUT_FOLDER_PATH +"DataOperatingCosts.csv";
    private static final String FILE_PATH_NUMBER_CUSTOMERS = OUT_FOLDER_PATH +"DataNumberCustomers.csv";
    private static PrintWriter printWriter;
    private static double price;
    private static DataCollectionObserver dataCollectionObserver = new DataCollectionObserver();
    private static LiveDataProcessing liveDataProcessing;

    /**
     * no instance should be created
     */
    private DataCollection(){}

    /**
     * initialize the data files
     */
    public static void prepareDataCollection(){
        deleteFiles();
        prepareFiles();
        prepareLiveDataProcessing();
    }

    /**
     * getter method for the dataCollectionObserver
     * @return the dataCollectionObserver
     */
    public static DataCollectionObserver getDataCollectionObserver(){
        return dataCollectionObserver;
    }

    /**
     * @return the total weigth sold at Kasse
     */
    private static int totalAmountAtKasse(){
        return Kasse.getTotalWeightPaid();
    }

    /**
     * @return the total amount at the end station
     */
    private static int totalAmountAtEndStation(){
        return EndStation.getTotalAmountWantedFood();
    }

    /**
     * Setter for the price per gram
     * @param price price per kilogram
     */
    public static void setPrice(double price){
        DataCollection.price = price / 1000.0;
    }

    /**
     * getter for the out folder path
     * @return the OUT_FOLDER_PATH
     */
    public static String getOutFolderPath(){
        return OUT_FOLDER_PATH;
    }

    /**
     * getter for the file path for left early
     * @return the FILE_PATH_ADDITIONAL_STATION
     */
    public static String getFilePathLeftEarly(){
        return FILE_PATH_LEFT_EARLY;
    }

    /**
     * getter for the file path for additional station
     * @return the FILE_PATH_ADDITIONAL_STATION
     */
    public static String getFilePathAdditionalStation(){
        return FILE_PATH_ADDITIONAL_STATION;
    }

    /**
     * getter for the file path for money loss
     * @return the FILE_PATH_MONEY_LOSS
     */
    public static String getFilePathMoneyLoss(){
        return FILE_PATH_MONEY_LOSS;
    }

    /**
     * getter for the file path for operating costs
     * @return the FILE_PATH_OPERATING_COSTS
     */
    public static String getFilePathOperatingCosts(){
        return FILE_PATH_OPERATING_COSTS;
    }

    /**
     * getter for the file path for numberCustomer
     * @return the FILE_PATH_NUMBER_CUSTOMERS
     */
    public static String getFilePathNumberCustomers(){
        return FILE_PATH_NUMBER_CUSTOMERS;
    }

    /**
     * prepares the plotters for live data
     */
    private static void prepareLiveDataProcessing() {
        liveDataProcessing = LiveDataProcessing.create();
        LiveDataProcessing.setTimeInterval(4);
    }

    /**
     * deletes all files in the dataoutput folder
     */
    @SuppressWarnings("Duplicates")
    private static void deleteFiles(){
            File outPutFolder = new File(OUT_FOLDER_PATH);
            if (outPutFolder.exists()) {
                File[] files = outPutFolder.listFiles();
                for (File file : files) {
                    file.delete();//delete the files
                }
            } else{
                outPutFolder.mkdir();//create folder if not existing
            }
    }

    /**
     * creates the datafiles
     */
    private static void prepareFiles(){
        Map<String, String> headers = new HashMap<>();
        headers.put(FILE_PATH_LEFT_EARLY, "LeavingTime,Amount");
        headers.put(FILE_PATH_ADDITIONAL_STATION,"OpeningTime,StationType");
        headers.put(FILE_PATH_MONEY_LOSS,"earnings,possibleEarnings,loss");
        headers.put(FILE_PATH_OPERATING_COSTS,"Station,OperatingCosts");
        String headerNumberCustomer = "Time";
        for (Station station: Station.getAllStations()){
            if (station instanceof MensaStation){
                headerNumberCustomer += "," + station.getLabel();
            }
        }
        headers.put(FILE_PATH_NUMBER_CUSTOMERS,headerNumberCustomer);
        try {
            for (String filePath: headers.keySet()) {
                File outPutFile = new File(filePath);
                DataCollection.printWriter = new PrintWriter(new BufferedWriter(new FileWriter(outPutFile, true)));
                printWriter.println(headers.get(filePath));
                printWriter.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * records when a customer leaves the Mensa early
     * @param customer the customer
     * @param leavingTime the time the customer left
     */
    public static synchronized void customerLeftEarly(Customer customer, long leavingTime){
        try {
            File outPutFile = new File(FILE_PATH_LEFT_EARLY);
            DataCollection.printWriter = new PrintWriter(new BufferedWriter(new FileWriter(outPutFile, true)));
            int amount = customer.getTotalAmountWantedFood();
            String data = leavingTime + ", " + amount;
            printWriter.println(data);
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * records when an additional station opened
     * @param stationOpened the station that opened
     * @param openingTimeStation the time when station was opened
     */
    public static void additionalStationOpened(Station stationOpened, long openingTimeStation){
        try {
            File outPutFile = new File(FILE_PATH_ADDITIONAL_STATION);
            DataCollection.printWriter = new PrintWriter(new BufferedWriter(new FileWriter(outPutFile, true)));
            printWriter.println(openingTimeStation+","+stationOpened.getStationType().toString());
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * records the loss of the simulation
     */
    public static void calculateLoss(){
        int totalAmountAtKasse = totalAmountAtKasse();
        double moneyEarnedAtKasse = totalAmountAtKasse * price;

        int totalAmountAtEndStation = totalAmountAtEndStation();
        double totalAmountPossibleMoney = totalAmountAtEndStation * price;

        double loss = totalAmountPossibleMoney - moneyEarnedAtKasse;

        try {
            File outPutFile = new File(FILE_PATH_MONEY_LOSS);
            DataCollection.printWriter = new PrintWriter(new BufferedWriter(new FileWriter(outPutFile, true)));
            printWriter.println(Math.round(moneyEarnedAtKasse)+","+Math.round(totalAmountPossibleMoney)+","+Math.round(loss));
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * records the operating costs of every mensa station
     */
    public static void processOperatingCosts(){
        try {
            File outPutFile = new File(FILE_PATH_OPERATING_COSTS);
            DataCollection.printWriter = new PrintWriter(new BufferedWriter(new FileWriter(outPutFile, true)));
            List<Station> stations = Station.getAllStations();
            for (Station station: stations){
                if (station instanceof MensaStation){
                    MensaStation mensaStation = (MensaStation) station;
                    printWriter.println(mensaStation.getLabel()+","+Math.round(mensaStation.calculateOperatingCosts()));
                }
            }
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * collects the numbers of incoming queue customers of every mensa station
     * displays the waiting queue length for every station in a separate window
     */
    public static void updateNumberCustomersInQueue(){
        try {
            if (Simulation.isRunning) {
                File outPutFile = new File(FILE_PATH_NUMBER_CUSTOMERS);
                DataCollection.printWriter = new PrintWriter(new BufferedWriter(new FileWriter(outPutFile, true)));
                List<Station> stations = Station.getAllStations();
                String info = "" + Simulation.getGlobalTime();
                for (Station station : stations) {
                    if (station instanceof MensaStation) {
                        MensaStation mensaStation = (MensaStation) station;
                        info += "," + mensaStation.getNumberOfInQueueCustomers();
                    }
                }
                liveDataProcessing.plotWaitingQueueLength();
                printWriter.println(info);
                printWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * an inner class that gets notified when a customer leaves early
     */
    private static class DataCollectionObserver implements Observer{
        /**
         * this method gets called when a observable notify DataCollectionObservable object
         * @param o the observable object
         * @param arg the time the customer left
         */
        @Override
        public void update(Observable o, Object arg) {
            Customer.CustomerObservable customerObservable = (Customer.CustomerObservable) o;
            Customer customer = customerObservable.getOuterObject();
            DataCollection.customerLeftEarly(customer,(long)arg);
        }
    }
}
