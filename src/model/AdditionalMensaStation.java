package model;

public class AdditionalMensaStation extends MensaStation {

    /**
     * Constructor, creates a new process station
     *
     * @param label                      of the station
     * @param inQueue                    a list of all incoming queues
     * @param outQueue                   a list of all outgoing queues
     * @param troughPut                  a stations parameter that affects treatment of an object
     * @param xPos                       x position of the station
     * @param yPos                       y position of the station
     * @param image                      image of the station
     * @param type                       the stationtype of the station
     * @param operatingCostsPerClockbeat operating costs per clockbeat of the mensaStation
     */
    protected AdditionalMensaStation(String label, SynchronizedQueue inQueue, SynchronizedQueue outQueue, double troughPut, int xPos, int yPos, String image, StationType type, double operatingCostsPerClockbeat) {
        super(label, inQueue, outQueue, troughPut, xPos, yPos, image, type, operatingCostsPerClockbeat);
    }
}
