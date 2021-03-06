package view;

import model.Customer;
import model.Station;

import java.awt.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * a wrapper class that handles multiple GraphPlotters
 * @author Hanselmann, Rietzler, Herzog, Clauss
 * @version 2017-12-04
 */
public class GraphPlotterArray implements LiveDataView {
    private Map<Station, GraphPlotter> graphPlotterMap;

    /**
     * construtor for class graphPlotterMap
     * @param desiredStations the stations that for which a plot shall be created
     */
    public GraphPlotterArray(List<Station> desiredStations) {

        graphPlotterMap = new HashMap<>();
        for(Station station : desiredStations){
            addStationPlotter(station);
        }
        positionPlotterWindows();
    }

    /**
     * method to add a new plotter for a specific station to the array
     * @param station the selected station
     */
    private void addStationPlotter(Station station){
        //for better visibility of graphPlotterPoints
        int yScaling = Customer.getAllCustomers().size()/2;
        if(yScaling <= 0){
            yScaling = 1;
        }
        //max value for x set to 100000, to have enough space for points
        GraphPlotter plotter = new GraphPlotter(100000, yScaling, "queue size", "time");
        plotter.getFrame().setTitle(station.getLabel());
        graphPlotterMap.put(station,plotter);
    }


    /**
     * positions the plotter windows on the screen
     */
    private void positionPlotterWindows(){
        int x = 0;
        int y = 0;
        final int SPACING = 40;
        for(GraphPlotter plotter : graphPlotterMap.values()){
            plotter.getFrame().setLocation(x,y);
            x += plotter.getDEFAULT_FRAME_SIZE_X() + SPACING;
            if(x > Toolkit.getDefaultToolkit().getScreenSize().width - plotter.getDEFAULT_FRAME_SIZE_X()){
                x = 0;
                y += plotter.getDEFAULT_FRAME_SIZE_Y() + SPACING;
                if(y > Toolkit.getDefaultToolkit().getScreenSize().height - plotter.getDEFAULT_FRAME_SIZE_Y()){
                    y = 0;
                }
            }
        }
    }

    /**
     * add a point to the Graph of the selected station
     * @param station the selected station
     * @param x currently unused
     * @param y the value of the point
     */
    @Override
    public void addPointToDiagramm(Station station, int x, int y) {
        GraphPlotter plotter = graphPlotterMap.get(station);
        plotter.add(y);
    }

}
