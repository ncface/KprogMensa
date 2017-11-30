package model;

import controller.Simulation;
import io.DataCollection;

import java.util.*;

/**
 * Class for a Kasse
 * @author Patrick Hanselmann, Sebastian Herzog, Jeffrey Manuel Rietzler, Nils Clauss
 * @version 2017-11-28
 */
public class Kasse extends MensaStation {

    private static int totalWeightPaid;

    /**
     * Constructor, creates a new Kasse
     * @param label     of the station
     * @param inQueue   a list of all incoming queues
     * @param outQueue  a list of all outgoing queues
     * @param troughPut a stations parameter that affects treatment of an object
     * @param xPos      x position of the station
     * @param yPos      y position of the station
     * @param image     image of the station
     * @param type      the stationtype of the station
     * @param operatingCostsPerClockbeat the operating costs per clockbeat
     */
    protected Kasse(String label, SynchronizedQueue inQueue, SynchronizedQueue outQueue, double troughPut, int xPos, int yPos,
                    String image, StationType type, double operatingCostsPerClockbeat) {
        super(label, inQueue, outQueue, troughPut, xPos, yPos, image, type, operatingCostsPerClockbeat);
    }

    /**
     * Creates a new Kasse
     * @param label     of the station
     * @param inQueue   a list of all incoming queues
     * @param outQueue  a list of all outgoing queues
     * @param troughPut a stations parameter that affects treatment of an object
     * @param xPos      x position of the station
     * @param yPos      y position of the station
     * @param image     image of the station
     * @param type      the stationtype of the station
     * @param operatingCostsPerClockbeat the operating costs per clockbeat
     */
    public static void create(String label, SynchronizedQueue inQueue, SynchronizedQueue outQueue, double troughPut, int xPos, int yPos,
                              String image, StationType type, double operatingCostsPerClockbeat){
        new Kasse(label, inQueue, outQueue, troughPut, xPos, yPos, image, type, operatingCostsPerClockbeat);
    }

    /**
     * Getter method for the total amount of food
     * @return totalWeightPaid
     */
    public static int getTotalWeightPaid() {
        return totalWeightPaid;
    }

    /**
     * @Override handleCustomer of the super class (MensaStation)
     * calculates the total weight of food of the customer
     */
    @Override
    protected void handleCustomer(Customer customer) {
        super.handleCustomer(customer);
        // get all amounts of food the cusotmer buys
        Collection<Integer> amountFoodToPay = customer.getCustomerFoodAmountAtStationsWanted().values();
        synchronized (this) {
            for (int amountFood : amountFoodToPay) {
                totalWeightPaid += amountFood;
            }
        }
    }

    /**
     * checks if the number of in queue customers is too big
     * @return the number of in queue customers
     */
    @Override
    protected int numberOfInQueueCustomers(){
        int numberOfInQueueCustomers = this.inComingQueue.size();
        if (numberOfInQueueCustomers>this.stationType.getInQueueLimit()){
            //number of in queue customers is too big
            startAdditionalKasse();
        }
        return numberOfInQueueCustomers;
    }

    /**
     * starts an additional kasse if one additional kasse is available
     */
    private void startAdditionalKasse(){
        //iterates over all stations
        for (Station station: Station.getAllStations()){
            //checks if there is one station which is ADDITIONAL and has a label that contains the stationType Kasse
            if (station.getStationType() == StationType.ADDITIONAL &&
                    station.getLabel().toUpperCase().contains(stationType.toString())) {
                station.stationType = stationType;

                //set opening time for calculation of the operation costs
                ((MensaStation)station).setOpeningTime();
                DataCollection.additionalStationOpened(station, Simulation.getGlobalTime());
                //open only one station
                break;
            }
        }
    }
}
