package view;

import controller.Simulation;
import model.MensaStation;
import model.Station;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Class GraphPlotter draws points to panel.
 * 
 * @author Daniel Pawlowicz
 *
 */
public class GraphPlotter extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	/** Default frame size X for frame in pixels */
	private final int DEFAULT_FRAME_SIZE_X = 400;
	/** Default frame size Y for frame in pixels */
	private final int DEFAULT_FRAME_SIZE_Y = 400;
	/** Padding to Frame */
	private final int PAD = 30;
	/** Radius of dot */
	private final int DOT_RADIUS = 1;
	/** Padding of label */
	private final int LABEL_PAD = 10;
	/** Height of label */
	private final int LABEL_HEIGHT = 10;
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

	private Map<MensaStation, Color> stationColor = new HashMap<>();

	private Color color;

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
	 * 
	 */

	public GraphPlotter(int maxValueForX, int maxValueForY, String labelForY) {
		this.maxValueForX = maxValueForX;
		this.maxValueForY = maxValueForY;
		this.labelForY = labelForY;
		
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(this);
		f.setSize(this.DEFAULT_FRAME_SIZE_X + PAD, this.DEFAULT_FRAME_SIZE_Y + PAD);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		initialise();
	}



	/**
	 * 
	 * Constructor of this class
	 * 
	 * @param maxValueForX
	 *            max points to draw before the points overruns
	 * @param maxValueForY
	 *            the highest value for y
	 * 
	 */
	public GraphPlotter(int maxValueForX, int maxValueForY, String labelForY, String labelForX) {
		this.maxValueForX = maxValueForX;
		this.maxValueForY = maxValueForY;
		this.labelForY = labelForY;
		this.labelForX = labelForX;
		
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(this);
		f.setSize(this.DEFAULT_FRAME_SIZE_X + PAD, this.DEFAULT_FRAME_SIZE_Y + PAD);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		initialise();
	}

	private void initialise() {
		int r = 0, g = 0, b = 0;
		for(Station station : Station.getAllStations()){
			if(station instanceof MensaStation) {
				b += 64;
				if (b > 255) {
					b = 0;
					g += 64;
					if (g > 255) {
						g = 0;
						r += 64;
						if (r > 255) {
							r = 0;
						}
					}
				}
				stationColor.put((MensaStation)station, new Color(r,g,b));
			}

		}
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
		g2.setPaint(color);
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
	public void add(int p, MensaStation station) {
		if (getSizeOfDataPoints() > this.maxValueForX) {
			deleteLastDataPoint();
		}
		setDataPoint(p);
		color = stationColor.get(station);
		this.repaint();
	}


}
