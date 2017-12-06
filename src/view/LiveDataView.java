package view;

import model.Station;

/**
 * An interface that represents a Graph that displays live data while the simulation works
 * @author Hanselmann, Clauss, Rietzler, Herzog
 * @version 2017-12-04
 */
public interface LiveDataView {

    /**
     * A Method to add another point to the live data diagram
     * @param station the station to which the data point shall be assigned
     * @param x the x value of the data point
     * @param y the y value of the data point
     */
    void addPointToDiagramm(Station station, int x, int y);
}
