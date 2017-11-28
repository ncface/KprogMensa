package model;

import java.util.Observable;
import java.util.Observer;

public class AdditionalMensaStation extends MensaStation implements Observer{

    /**
     * Constructor, creates a new additional mensa station
     *
     * @param label                      of the additional mensa station
     * @param inQueue                    a list of all incoming queues
     * @param outQueue                   a list of all outgoing queues
     * @param troughPut                  an additional mensa stations parameter that affects treatment of an object
     * @param xPos                       x position of the additional mensa station
     * @param yPos                       y position of the additional mensa station
     * @param image                      image of the additional mensa station
     * @param type                       the stationtype of the additional mensa station
     * @param operatingCostsPerClockbeat operating costs per clockbeat of the additional mensaStation
     */
    protected AdditionalMensaStation(String label, SynchronizedQueue inQueue, SynchronizedQueue outQueue, double troughPut, int xPos, int yPos, String image, StationType type, double operatingCostsPerClockbeat) {
        super(label, inQueue, outQueue, troughPut, xPos, yPos, image, type, operatingCostsPerClockbeat);
    }

    /** create a new process station and add it to the station list
     *
     * @param label of the station
     * @param inQueue a list of all incoming queues
     * @param outQueue a list of all outgoing queues
     * @param troughPut a stations parameter that affects treatment of an object
     * @param xPos x position of the station
     * @param yPos y position of the station
     * @param image image of the station
     * @param type the stationtype of the station
     * @param operatingCostsPerClockbeat operating costs per clockbeat of the mensaStation
     */
    public static void create(String label, SynchronizedQueue inQueue, SynchronizedQueue outQueue , double troughPut, int xPos, int yPos,
                              String image, StationType type, double operatingCostsPerClockbeat){

        new AdditionalMensaStation(label, inQueue,outQueue , troughPut, xPos, yPos, image,type, operatingCostsPerClockbeat);

    }

    @Override
    public void update(Observable o, Object arg) {
        MensaStation.MensaStationObservable mensaStationObservable = (MensaStation.MensaStationObservable) o;
        MensaStation mensaStation = mensaStationObservable.getOuterObject();
        StationType stationType = mensaStation.getStationType();
        //checks if there is one station which is ADDITIONAL and has a label that contains the stationType of the Observable
        for (Station station: Station.getAllStations()){
            if (station.getStationType() == StationType.ADDITIONAL &&
                    station.getLabel().toUpperCase().contains(stationType.toString())) {
                station.stationType = stationType;
                break;
            }
        }
        System.out.println("                                                       asdfasdfasdfasdfasdf");
    }
}
