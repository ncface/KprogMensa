package view;

import javax.swing.*;
import java.io.*;

/**
 * a class that shows bar charts
 * @author
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
        // set up the frame
        jTextTextArea = new JTextArea();
        jTextTextArea.setLineWrap(false);
        jTextTextArea.setEditable(false);
        JScrollPane jScrollPane = new JScrollPane(jTextTextArea);
        jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.add(jScrollPane);
        this.setTitle(title);
        this.setSize(300,300);
        this.setVisible(true);
    }

    /**
     * this method reads data from the file DataMoneyLoss.csv and shows them in the frame
     */
    public void showMoneyLoss(){
        try {
            String theData = "";

            File file = new File("DataOutput/DataMoneyLoss.csv");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String [] headLines;
            String[] money;

            headLines = bufferedReader.readLine().split(",");
            money = bufferedReader.readLine().split(",");

            for (int i=0; i<money.length; i++) {
                int number = (int) Double.parseDouble(money[i]);
                theData += headLines[i] +"\n";
                for (int j = 0; j<number; j+=10) {
                    theData += "|";
                }
                theData += " " + money[i];
                theData += "\n\n\n";
            }
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
            String theData = "";

            File file = new File("DataOutput/DataOperatingCosts.csv");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;

            String [] headLine = bufferedReader.readLine().split(",");

            String [] stationOperatingCosts;

            while((line = bufferedReader.readLine()) != null){
                stationOperatingCosts = line.split(",");
                String stationName = stationOperatingCosts[0];
            }

            jTextTextArea.setText(theData);

            System.out.println(theData);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
