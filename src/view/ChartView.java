package view;

/**
 * An interface that represents a chart to display information
 * @author Hanselmann, Herzog, Clauss, Rietzler
 * @version 4.12.17
 */
public interface ChartView {

    /**
     * A method to add data to the chart
     * @param label the label for the data
     * @param value the exact value
     * @param scaling the desired scaling. For example a scaling of 5 means that one unit in the chart represents 5 real units
     */
    void add(String label, int value, int scaling);

    /**
     * A method to display the chart on screen
     */
    void showChart();

    /**
     * A method to set the heading of a chart
     * @param title the new heading
     */
    void setChartHeading(String title);
}
