package io;

import controller.Simulation;
import model.*;
import view.GraphPlotterArray;
import view.LiveDataView;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class to collect and save data from the simulation to files
 */
public class DataCollection{
    private static final String outFolderPath = "DataOutput/";
    private static PrintWriter printWriter;
    private static double price;
    private static LiveDataView plotterArray;
    //Counts the method aktivations of method updateNumderCustomersInQueue
    private static int numberCalled = 0;
    //when numberCalled reaches this value a point in the plotter is plotted
    private static int plotWhenReached = 4;
    private static final String filePathLeftEarly = outFolderPath+"DataLeftEarly.csv";
    private static final String filePathAdditionalStation = outFolderPath+"DataAdditionalStation.csv";
    private static final String filePathMoneyLoss = outFolderPath+"DataMoneyLoss.csv";
    private static final String filePathOperatingCosts = outFolderPath+"DataOperatingCosts.csv";
    private static final String filePathNumberCustomers = outFolderPath+"DataNumberCustomers.csv";
    private static DataCollectionObserver dataCollectionObserver = new DataCollectionObserver();

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
        preparePlotters();
    }

    /**
     * getter method for the dataCollectionObserver
     * @return the dataCollectionObserver
     */
    public static DataCollectionObserver getDataCollectionObserver(){
        return dataCollectionObserver;
    }

    /**
     * prepares the plotters for live data
     */
    private static void preparePlotters() {
        List<Station> possibleStations = new ArrayList<>();
        for(Station station : Station.getAllStations()){
            if(station instanceof MensaStation){
                possibleStations.add(station);
            }
        }
        DataCollection.plotterArray = new GraphPlotterArray(possibleStations);
    }

    /**
     * deletes all files in the dataoutput folder
     */
    @SuppressWarnings("Duplicates")
    private static void deleteFiles(){
            File outPutFolder = new File(outFolderPath);
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
        headers.put(filePathLeftEarly, "LeavingTime,Amount");
        headers.put(filePathAdditionalStation,"OpeningTime,StationType");
        headers.put(filePathMoneyLoss,"earnings,possibleEarnings,loss");
        headers.put(filePathOperatingCosts,"Station,OperatingCosts");
        String headerNumberCustomer = "Time";
        for (Station station: Station.getAllStations()){
            if (station instanceof MensaStation){
                headerNumberCustomer += "," + station.getLabel();
            }
        }
        headers.put(filePathNumberCustomers,headerNumberCustomer);
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
            File outPutFile = new File(filePathLeftEarly);
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
            File outPutFile = new File(filePathAdditionalStation);
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
            File outPutFile = new File(filePathMoneyLoss);
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
            File outPutFile = new File(filePathOperatingCosts);
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
                File outPutFile = new File(filePathNumberCustomers);
                DataCollection.printWriter = new PrintWriter(new BufferedWriter(new FileWriter(outPutFile, true)));
                List<Station> stations = Station.getAllStations();
                String info = "" + Simulation.getGlobalTime();
                for (Station station : stations) {
                    if (station instanceof MensaStation) {
                        MensaStation mensaStation = (MensaStation) station;
                        if(numberCalled % plotWhenReached == 0) {
                            plotterArray.addPointToDiagramm(mensaStation, 0,mensaStation.getNumberOfInQueueCustomers());
                        }
                        info += "," + mensaStation.getNumberOfInQueueCustomers();
                    }
                }
                numberCalled++;
                printWriter.println(info);
                printWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
     * @return the outFolderPath
     */
    public static String getOutFolderPath(){
    	return outFolderPath;
    }

    /**
     * getter for the file path for left early
     * @return the filePathAdditionalStation
     */
    public static String getFilePathLeftEarly(){
    	return filePathLeftEarly;
    }

    /**
     * getter for the file path for additional station
     * @return the filePathAdditionalStation
     */
    public static String getFilePathAdditionalStation(){
    	return filePathAdditionalStation;
    }

    /**
     * getter for the file path for money loss
     * @return the filePathMoneyLoss
     */
    public static String getFilePathMoneyLoss(){
    	return filePathMoneyLoss;
    }

    /**
     * getter for the file path for operating costs
     * @return the filePathOperatingCosts
     */
    public static String getFilePathOperatingCosts(){
    	return filePathOperatingCosts;
    }

    /**
     * getter for the file path for numberCustomer
     * @return the filePathNumberCustomers
     */
    public static String getFilePathNumberCustomers(){
    	return filePathNumberCustomers;
    }

    /**
     * an inner class that gets notified when a customer leaves early
     */
    private static class DataCollectionObserver implements Observer{
        @Override
        public void update(Observable o, Object arg) {
            Customer.CustomerObservable customerObservable = (Customer.CustomerObservable) o;
            Customer customer = customerObservable.getOuterObject();
            DataCollection.customerLeftEarly(customer,(long)arg);
        }
    }
}
