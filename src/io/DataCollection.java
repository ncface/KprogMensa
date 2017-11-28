package io;

import model.*;

import java.io.*;

/**
 * A class to collect and save data from the simulation to files
 */
public class DataCollection {
    private static String outFolderPath = "DataOutput/";
    private static PrintWriter printWriter;
    private static int price;

    private DataCollection(){}

    /**
     * deletes all files in the dataoutput folder
     */
    public static void deleteFiles(){
            File outPutFolder = new File(outFolderPath);
            File[] files = outPutFolder.listFiles();
            for(File file: files){
                file.delete();
            }
    }

    /**
     * records when a customer leaves the Mensa early
     * @param customer the customer
     * @param leavingTime the time the customer left
     */
    public static synchronized void customerLeftEarly(Customer customer, long leavingTime){
        try {
            String filePath = outFolderPath+"DataLeftEarly.csv";
            File outPutFile = new File(filePath);
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
    }

    public static void calculateLoss(){
        int totalAmountAtKasse = totalAmountAtKasse();
        double moneyEarnedAtKasse = totalAmountAtKasse * price;

        int totalAmountAtEndStation = totalAmountAtEndStation();
        double totalAmountPossibleMoney = totalAmountAtEndStation * price;

        double loss = moneyEarnedAtKasse - totalAmountPossibleMoney;

        try {
            String filePath = outFolderPath+"DataLoss.csv";
            File outPutFile = new File(filePath);
            DataCollection.printWriter = new PrintWriter(new BufferedWriter(new FileWriter(outPutFile, true)));
            printWriter.println("Einnahmen,moeglicheEinnahmen,Verlust");
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
    public static void setPrice(int price){
        DataCollection.price = price / 1000;
    }

}
