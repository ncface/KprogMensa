package view;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class BarChartView extends JFrame{
    private JTextArea jTextTextArea;
    public BarChartView(){
        jTextTextArea = new JTextArea();
        jTextTextArea.setLineWrap(false);
        jTextTextArea.setEditable(false);
        JScrollPane jScrollPane = new JScrollPane(jTextTextArea);
        jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.add(jScrollPane);
        this.setTitle("Bar Chart for the loss");
        this.setSize(300,300);
        this.setVisible(true);
    }

    private void showMoneyLoss(){
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
                int number = Integer.parseInt(money[i]);
                theData += headLines[i] +"\n";
                for (int j = 0; j<number; j+=10) {
                    theData += "|";
                }
                theData += " " + money[i];
                theData += "\n\n\n";
            }
            jTextTextArea.setText(theData);

            System.out.println(theData);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        BarChartView barChartView = new BarChartView();
        barChartView.showMoneyLoss();
    }
}
