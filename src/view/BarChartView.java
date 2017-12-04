package view;

import javax.swing.*;

/**
 * This class represents a BarChart
 * @author Rietzler, Hanselmann, Herzog, Clauss
 * @version 4.12.17
 */
public class BarChartView extends JFrame implements ChartView {
	// the JTextArea that shows the data
	private JTextArea jTextTextArea;

	private static final String SYMBOL = "|";

	private String chart;

	private String heading;

	/**
	 * Constructor for a BarChartFrame
	 * @param title the title of the frame
	 */
	public BarChartView(String title){
		setUp(title);
		chart = "";
		heading = "";
	}

	private void setUp(String title) {
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

	@Override
	public void add(String label, int value, int scaling) {
		chart += label + "\n";
		StringBuilder builder = new StringBuilder(chart);
		if(value < 0||scaling <= 0){
			throw new IllegalArgumentException();
		}
		for(int i = 0 ; i < value/scaling ;i++){
			builder.append(SYMBOL);
		}
		chart = builder.toString();
		chart += " " + value + "\n\n\n";
	}

	@Override
	public void setChartHeading(String title) {
		this.heading = title;
	}

	@Override
	public void showChart() {
		this.setVisible(false);
		jTextTextArea.setText(heading + "\n\n" + chart);
		this.setVisible(true);
	}
}
