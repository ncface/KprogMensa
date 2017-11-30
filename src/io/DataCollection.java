package io;

import controller.Simulation;
import model.*;
import view.GraphPlotter;
import view.GraphPlotterArray;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class to collect and save data from the simulation to files
 */
public class DataCollection {
    private static final String outFolderPath = "DataOutput/";
    private static PrintWriter printWriter;
    private static double price;
    private static GraphPlotterArray plotterArray = new GraphPlotterArray();
    private static int numberCalled = 0;
    //plot when this number is reached so only every 4th point will get visualized
    private static int plotWhenReached = 4;
    private static final String filePathLeftEarly = outFolderPath+"DataLeftEarly.csv";
    private static final String filePathAdditionalStation = outFolderPath+"DataAdditionalStation.csv";
    private static final String filePathMoneyLoss = outFolderPath+"DataMoneyLoss.csv";
    private static final String filePathOperatingCosts = outFolderPath+"DataOperatingCosts.csv";
    private static final String filePathNumberCustomers = outFolderPath+"DataNumberCustomers.csv";
    
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
     * prepares the plotters for live data
     */
    private static void preparePlotters() {
        for(Station station:Station.getAllStations()){
            if(station instanceof MensaStation){
                plotterArray.addStationPlotter(station);
            }
        }
        plotterArray.positionPlotterWindows();
    }

    /**
     * deletes all files in the dataoutput folder
     */
    private static void deleteFiles(){
            File outPutFolder = new File(outFolderPath);
            if (outPutFolder.exists()) {
                File[] files = outPutFolder.listFiles();
                for (File file : files) {
                    file.delete();//delete the files
                }
            }else{
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
        Station station = stationOpened;
        long openingTime = openingTimeStation;
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
            printWriter.println(""+moneyEarnedAtKasse+","+totalAmountPossibleMoney+","+loss);
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
                    printWriter.println(mensaStation.getLabel()+","+mensaStation.calculateOperatingCosts());
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
                            plotterArray.addPoint(mensaStation,mensaStation.getNumberOfInQueueCustomers());
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

    private static int totalAmountAtEndStation(){
        return EndStation.getTotalAmountWantedFood();
    }

    /**
     * Setter for the price per gram
     * @param price price per kilo
     */
    public static void setPrice(double price){
        DataCollection.price = price / 100;
    }
    
    public static String getOutFolderPath(){
    	return outFolderPath;
    }
    public static String getFilePathLeftEarly(){
    	return filePathLeftEarly;
    }
    public static String getFilePathAdditionalStation(){
    	return filePathAdditionalStation;
    }
    public static String getFilePathMoneyLoss(){
    	return filePathMoneyLoss;
    }
    public static String getFilePathOperatingCosts(){
    	return filePathOperatingCosts;
    }
    public static String getFilePathNumberCustomers(){
    	return filePathNumberCustomers;
    }
}
