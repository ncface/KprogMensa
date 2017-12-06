package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Class GraphPlotter draws points to panel.
 * 
 * @author Daniel Pawlowicz, Clauss, Herzog, Rietzler, Hanselmann
 * @version 2017-12-03
 */
public class GraphPlotter extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	/** Default frame size X for frame in pixels */
	private final int DEFAULT_FRAME_SIZE_X = 300;
	/** Default frame size Y for frame in pixels */
	private final int DEFAULT_FRAME_SIZE_Y = 300;
	/** Padding to Frame */
	private final int PAD = 30;
	/** Radius of dot */
	private final int DOT_RADIUS = 2;
	/** Padding of label */
	private final int LABEL_PAD = 10;
	/** Height of label */
	private final int LABEL_HEIGHT = 50;
	/** Width of label */
	private final int LABEL_WIDTH = 100;
	
	/** Max value for x to print */
	private int maxValueForX;
	/** Scale factor depending to y*/
	private int maxValueForY;
	/** Label for the x axis */
	private String labelForX = "time";
	/** Label for the y axis */
	private String labelForY;
	/** Frame for the plotter */
	private JFrame f;

	/**
	 * List with points to draw. It holds the y coordinates of points. x
	 * coordinates are spaced
	 */
	private List<Integer> dataPoints = new ArrayList<Integer>();

	/**
	 * 
	 * Constructor of this class
	 * 
	 * @param maxValueForX
	 *            max points to draw before the points overruns
	 * @param maxValueForY
	 *            the highest value for y
	 * @param labelForY the label for y
	 * 
	 */
	public GraphPlotter(int maxValueForX, int maxValueForY, String labelForY) {
		this.maxValueForX = maxValueForX;
		this.maxValueForY = maxValueForY;
		this.labelForY = labelForY;
		
		f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(this);
		f.setSize(this.DEFAULT_FRAME_SIZE_X + PAD, this.DEFAULT_FRAME_SIZE_Y + PAD);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}
	
	/**
	 * 
	 * Constructor of this class
	 * 
	 * @param maxValueForX
	 *            max points to draw before the points overruns
	 * @param maxValueForY
	 *            the highest value for y
	 * @param labelForY the label for y
	 * @param labelForX the label for x
	 * 
	 */
	public GraphPlotter(int maxValueForX, int maxValueForY, String labelForY, String labelForX) {
		this.maxValueForX = maxValueForX;
		this.maxValueForY = maxValueForY;
		this.labelForY = labelForY;
		this.labelForX = labelForX;
		
		f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(this);
		f.setSize(this.DEFAULT_FRAME_SIZE_X + PAD, this.DEFAULT_FRAME_SIZE_Y + PAD);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}

	/** method that draws the points and lines between the points */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		int w = getWidth();
		int h = getHeight();
		
		// add labels
		JLabel jLabelY = new JLabel(labelForY);
		JLabel jLabelX = new JLabel(labelForX);
		
		jLabelY.setSize(LABEL_WIDTH, LABEL_HEIGHT);
		jLabelY.setLocation(LABEL_PAD, LABEL_PAD);
		this.add(jLabelY);
		
		jLabelX.setSize(LABEL_WIDTH, LABEL_HEIGHT);
		jLabelX.setLocation(w - LABEL_WIDTH - LABEL_PAD, h - LABEL_HEIGHT - LABEL_PAD);
		jLabelX.setHorizontalAlignment(SwingConstants.RIGHT);
		this.add(jLabelX);

		// add axis
		g2.drawLine(PAD, PAD, PAD, h - PAD);
		g2.drawLine(PAD, h - PAD, w - PAD, h - PAD);

		double xScale = (w - 2 * PAD) / (dataPoints.size() + 1);
		double yScale = (h - 2 * PAD) / this.maxValueForY;

		int x0 = PAD;
		int y0 = h - PAD;

		// draw the points as little circles
		g2.setPaint(Color.blue);
		for (int i = 0; i < dataPoints.size(); i++) {
			int x = x0 + (int) (xScale * (i + 1));
			int y = y0 - (int) (yScale * dataPoints.get(i));
			g2.fillOval(x - DOT_RADIUS, y - DOT_RADIUS, 2 * DOT_RADIUS,
					2 * DOT_RADIUS);
		}
	}

	/** Setter for List with points for y-coords to draws */
	private void setDataPoint(int addPoint) {
		dataPoints.add(addPoint);
	}

	/** Size of List */
	private int getSizeOfDataPoints() {
		return dataPoints.size();
	}
	
	/** Deletes last DataPoint in List */
	private void deleteLastDataPoint() {
		dataPoints.remove(0);
	}
	
	/**
	 * Ad point and repaint graph.
	 * 
	 * @param p point to draw (y-coord)
	 */
	public void add(int p) {
		if (getSizeOfDataPoints() > this.maxValueForX) {
			deleteLastDataPoint();
		}
		setDataPoint(p);
		this.repaint();
	}

	/**
	 * returns the related frame of the plotter
	 * @return the frame of the plotter
	 */
	public JFrame getFrame() {
		return f;
	}

	/**
	 * getter for the default frame size x
	 * @return the default frame size x
	 */
	public int getDEFAULT_FRAME_SIZE_X() {
		return DEFAULT_FRAME_SIZE_X;
	}

	/**
	 * getter for the default frame size y
	 * @return the default frame size y
	 */
	public int getDEFAULT_FRAME_SIZE_Y() {
		return DEFAULT_FRAME_SIZE_Y;
	}
}
