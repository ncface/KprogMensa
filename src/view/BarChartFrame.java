package view;

import javax.swing.*;
import java.io.*;

/**
 * a class that shows bar charts
 * @author Jeffrey Manuel Rietzler, Patrick Hanselmann, Sebastian Herzog, Nils Clauss
 * @version
 */
public class BarChartFrame extends JFrame{
    // the JTextArea that shows the data
    private JTextArea jTextTextArea;

    /**
     * Constructor for a BarChartFrame
     * @param title the title of the frame
     */
    public BarChartFrame(String title){
        setUpFrame(title);
    }

    /**
     * this method sets up the frame
     * @param title
     */
    private void setUpFrame(String title){
        // set up the frame
        jTextTextArea = new JTextArea();
        jTextTextArea.setLineWrap(false);
        jTextTextArea.setEditable(false);
        JScrollPane jScrollPane = new JScrollPane(jTextTextArea);
        jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.add(jScrollPane);
        this.setTitle(title);
        this.setSize(400,400);
        this.setVisible(true);
    }

    /**
     * this method reads data from the file DataMoneyLoss.csv and shows them in the frame
     */
    public void showMoneyLoss(){
        try {
            //collects data about earnings, possible earnings and loss
            String theData = "";

            File file = new File("DataOutput/DataMoneyLoss.csv");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String [] headLines;
            String[] money;

            //get the headlines
            headLines = bufferedReader.readLine().split(",");
            //get the values
            money = bufferedReader.readLine().split(",");

            for (int i=0; i<money.length; i++) {
                int number = (int) Double.parseDouble(money[i]);
                //add a description
                theData += headLines[i] +"\n";
                //add a bar
                //the length of the bar depends on the money
                for (int j = 0; j<number; j+=10) {
                    theData += "|";
                }
                //add the value of money
                theData += " " + money[i];
                theData += "\n\n\n";
            }

            //set the data in the jTextArea
            jTextTextArea.setText(theData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * this method reads data from the file DataOperatingCosts.csv and shows them in the frame
     */
    public void showOperatingCosts(){
        try {
            //collects data about the operating costs
            String theData = "";

            File file = new File("DataOutput/DataOperatingCosts.csv");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = "";

            String [] headLine = bufferedReader.readLine().split(",");

            //add a description for the bar chart
            theData += headLine[0] + headLine[1] + "\n\n";

            String [] stationNameOperatingCosts;

            while(((line = bufferedReader.readLine()) != null) && !(line.isEmpty())){
                stationNameOperatingCosts = line.split(",");
                String stationName = stationNameOperatingCosts[0];
                int operatingCost = Integer.parseInt(stationNameOperatingCosts[1]);
                theData += stationName;
                theData += "\n";
                //add a bar
                //the length of the bar depends on the money
                for (int i=0; i<operatingCost; i+=5){
                    theData += "|";
                }
                //add the operating cost
                theData += operatingCost;
                theData += "\n\n";
            }

            jTextTextArea.setText(theData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
