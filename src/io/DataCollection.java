package io;

import model.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A class to collect and save data from the simulation to files
 */
public class DataCollection {
    private static final String outFolderPath = "DataOutput/";
    private static PrintWriter printWriter;
    private static double price;
    private static final String filePathLeftEarly = outFolderPath+"DataLeftEarly.csv";
    private static final String filePathAdditionalStation = outFolderPath+"DataAdditionalStation.csv";
    private static final String filePathMoneyLoss = outFolderPath+"DataMoneyLoss.csv";

    private DataCollection(){}

    public static void prepareDataCollection(){
        deleteFiles();
        prepareFiles();
    }

    /**
     * deletes all files in the dataoutput folder
     */
    private static void deleteFiles(){
            File outPutFolder = new File(outFolderPath);
            if (outPutFolder.exists()) {
                File[] files = outPutFolder.listFiles();
                for (File file : files) {
                    file.delete();
                }
            }else{
                outPutFolder.mkdir();
            }
    }

    private static void prepareFiles(){
        Map<String, String> headers = new HashMap<>();
        headers.put(filePathLeftEarly, "LeavingTime,Amount");
        headers.put(filePathAdditionalStation,"OpeningTime,StationType");
        headers.put(filePathMoneyLoss,"Einnahmen,moeglicheEinnahmen,Verlust");
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
            int amount = customer.getTotalAmount();
            String data = leavingTime + ", " + amount;
            printWriter.println(data);
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    private static int totalAmountAtKasse(){
        return Kasse.getTotalWeigth();
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

}
