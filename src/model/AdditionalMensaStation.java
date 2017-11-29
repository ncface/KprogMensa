package model;

import controller.Simulation;
import io.DataCollection;

import java.util.Observable;
import java.util.Observer;

public class AdditionalMensaStation extends MensaStation implements Observer{

    /**
     * Constructor, creates a new additional mensa station
     *
     * @param label of the additional mensa station
     * @param inQueue a list of all incoming queues
     * @param outQueue a list of all outgoing queues
     * @param troughPut an additional mensa stations parameter that affects treatment of an object
     * @param xPos x position of the additional mensa station
     * @param yPos y position of the additional mensa station
     * @param image image of the additional mensa station
     * @param type the stationtype of the additional mensa station
     * @param operatingCostsPerClockbeat operating costs per clockbeat of the additional mensaStation
     */
    protected AdditionalMensaStation(String label, SynchronizedQueue inQueue, SynchronizedQueue outQueue, double troughPut, int xPos, int yPos, String image, StationType type, double operatingCostsPerClockbeat) {
        super(label, inQueue, outQueue, troughPut, xPos, yPos, image, type, operatingCostsPerClockbeat);
    }

    /** create a new process station and add it to the station list
     *
     * @param label of the additional mensa station
     * @param inQueue a list of all incoming queues
     * @param outQueue a list of all outgoing queues
     * @param troughPut an additional mensa stations parameter that affects treatment of an object
     * @param xPos x position of the additional mensa station
     * @param yPos y position of the additional mensa station
     * @param image image of the additional mensa station
     * @param type the stationtype of the additional mensa station
     * @param operatingCostsPerClockbeat operating costs per clockbeat of the additional mensaStation
     */
    public static void create(String label, SynchronizedQueue inQueue, SynchronizedQueue outQueue , double troughPut, int xPos, int yPos,
                              String image, StationType type, double operatingCostsPerClockbeat){

        new AdditionalMensaStation(label, inQueue,outQueue , troughPut, xPos, yPos, image,type, operatingCostsPerClockbeat);

    }

    /**
     * when notified, updates the stationtype of one additional station if there is one available
     */
    @Override
    public void update(Observable o, Object arg) {
        //get the observable object
        MensaStation.MensaStationObservable mensaStationObservable = (MensaStation.MensaStationObservable) o;
        //get the outer object and the station type
        MensaStation mensaStation = mensaStationObservable.getOuterObject();
        StationType stationType = mensaStation.getStationType();
        //iterate over all stations and choose station to open
        for (Station station: Station.getAllStations()){
            //checks if there is one station which is ADDITIONAL and has a label that contains the stationType of the Observable
            if (station.getStationType() == StationType.ADDITIONAL &&
                    station.getLabel().toUpperCase().contains(stationType.toString())) {
                station.stationType = stationType;

                //set opening time for calculation of the operation costs
                ((MensaStation)station).setOpeningTime();
                DataCollection.additionalStationOpened(station, Simulation.getGlobalTime());
                mensaStationObservable.deleteObserver((AdditionalMensaStation)station);
                //open only one station
                break;
            }
        }
    }
}
