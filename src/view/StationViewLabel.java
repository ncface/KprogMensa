package view;


/**
 * Class for the Label of the view of stations
 *
 * @author Patrick Hanselmann, Sebastian Herzog, Jeffrey Manuel Rietzler, Nils Clauss
 * @version 2017-11-27
 */
@SuppressWarnings("serial")
public class StationViewLabel extends LabelView {

    /** Creates a new view with an Label (JLabel)
     *
     * @param text text of the label (which will be the label of the xml)
     * @param xPos x position of the view
     * @param yPos y position of the view
     */
    private StationViewLabel(String text, int xPos, int yPos){
        super(text, xPos, yPos);
        this.setText(text);
    }


    /** Creates a new view of the stationLabel
     *
     * @param text text of the label (which will be the label of the xml)
     * @param xPos x position of the view
     * @param yPos y position of the view
     *
     * @return the StationView
     */
    public static StationViewLabel createLabel(String text, int xPos, int yPos){

        return new StationViewLabel(text, xPos, yPos);

    }

}
