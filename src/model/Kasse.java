package model;

import java.util.*;


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
    protected Kasse(String label, SynchronizedQueue inQueue, SynchronizedQueue outQueue, double troughPut, int xPos, int yPos, String image, StationType type) {
        super(label, inQueue, outQueue, troughPut, xPos, yPos, image, type);
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
    public static void create(String label, SynchronizedQueue inQueue, SynchronizedQueue outQueue, double troughPut, int xPos, int yPos, String image, StationType type){
        new Kasse(label, inQueue, outQueue, troughPut, xPos, yPos, image, type);
    }

    /**
     * @Override handleCustomer of the super class
     * calculates the total weight of food of the customer
     */
    @Override
    protected void handleCustomer(Customer customer) {
        super.handleCustomer(customer);
        System.out.println("                                    it works");
        Collection<Integer> amountFood = customer.getFoodAmountAtStations().values();
        for(int i: amountFood){
            totalWeigth += i;
        }
    }
}
