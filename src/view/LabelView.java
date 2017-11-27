package view;

//import javax.swing.ImageIcon;
import javax.swing.JLabel;


/**
 * A JLabel class for LabelViews
 *
 * @author Herzog, Sebastian
 * @version 2017-11-27
 */
@SuppressWarnings("serial")
public abstract class LabelView extends JLabel {

    /** Creates a new view with an image (JLabel)
     *
     * @param text text of the label
     * @param xPos x position of the view
     * @param yPos y position of the view
     */
    // MEIN KONSTRUKTOR
    protected LabelView(String text, int xPos, int yPos){
        JLabel stationLabel = new JLabel();
        stationLabel.setSize(100,100);
        stationLabel.setLocation(xPos, yPos);
        stationLabel.setText(text);
        SimulationView.addActorView(stationLabel);
    }


}
