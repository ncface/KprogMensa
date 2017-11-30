package view;

import com.sun.deploy.uitoolkit.Window;
import model.Customer;
import model.Station;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * a wrapper class that handles multiple GraphPlotters
 * @author Hanselmann, Rietzler, Herzog, Clauss
 * @version 30.11.17
 */
public class GraphPlotterArray {
    Map<Station, GraphPlotter> GraphPlotterArray;

    /**
     * construtor for class GraphPlotterArray
     */
    public GraphPlotterArray() {
        GraphPlotterArray = new HashMap<>();
    }

    /**
     * method to add a new plotter for a specific station to the array
     * @param station the selected station
     */
    public void addStationPlotter(Station station){
        GraphPlotter plotter = new GraphPlotter(100000, Customer.getAllCustomers().size()/2, "queue size", "time");
        plotter.getFrame().setTitle(station.getLabel());
        GraphPlotterArray.put(station,plotter);
    }

    /**
     * add a point to the Graph of the selected station
     * @param station the selected station
     * @param pointValue the point value
     */
    public void addPoint(Station station, int pointValue){
        GraphPlotter plotter = GraphPlotterArray.get(station);
        plotter.add(pointValue);
    }

    /**
     * positions the plotter windows on the screen
     */
    public void positionPlotterWindows(){
        int x = 0;
        int y = 0;
        final int SPACING = 40;
        for(GraphPlotter plotter : GraphPlotterArray.values()){
            plotter.getFrame().setLocation(x,y);
            x += plotter.getDEFAULT_FRAME_SIZE_X() + SPACING;
            if(x > Toolkit.getDefaultToolkit().getScreenSize().width){
                x = 0;
                y += plotter.getDEFAULT_FRAME_SIZE_Y() + SPACING;
            }
        }
    }
}
