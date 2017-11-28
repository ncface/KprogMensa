package model;

import java.util.*;

/**
 * Class for a Kasse
 * @author Patrick Hanselmann, Sebastian Herzog, Jeffrey Manuel Rietzler, Nils Clauss
 * @version 2017-11-28
 */
public class Kasse extends MensaStation {

    private static int totalWeigth;

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
     */
    protected Kasse(String label, SynchronizedQueue inQueue, SynchronizedQueue outQueue, double troughPut, int xPos, int yPos, String image, StationType type, double operatingCostsPerClockbeat) {
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
     */
    public static void create(String label, SynchronizedQueue inQueue, SynchronizedQueue outQueue, double troughPut, int xPos, int yPos, String image, StationType type, double operatingCostsPerClockbeat){
        new Kasse(label, inQueue, outQueue, troughPut, xPos, yPos, image, type, operatingCostsPerClockbeat);
    }

    /**
     * Getter method for the total amount of food
     * @return totalWeigth
     */
    public static int getTotalWeigth() {
        return totalWeigth;
    }

    /**
     * @Override handleCustomer of the super class (MensaStation)
     * calculates the total weight of food of the customer
     */
    @Override
    protected void handleCustomer(Customer customer) {
        super.handleCustomer(customer);
        Collection<Integer> amountFood = customer.getFoodAmountAtStations().values();
        for(int i: amountFood){
            totalWeigth += i;
        }
    }
}
