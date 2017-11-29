package model;

import java.util.ArrayList;
import java.util.Collection;

import view.StationView;
import view.StationViewLabel;

/**
 * Superclass for all Stations
 * 
 * @author Jaeger, Schmidt
 * @version 2016-02-11
 */
public abstract class Station extends Actor {
		
	/** list of all stations */
	protected static ArrayList<Station> allStations = new ArrayList<Station>();

     /** the Type of this Station */
    protected StationType stationType;

    /** the view of the station */
	@SuppressWarnings("unused")
	private StationView theView;
	private StationViewLabel theViewLabel;

			
	/** Constructor for all stations
	 * 
	 * @param label of the station 
	 * @param xPos x position of the station 
	 * @param yPos y position of the station 
	 * @param image image of the station
	 * @param type the stationtype of the station
	 */
	protected Station(String label, int xPos, int yPos, String image, StationType type) {
		super(label, xPos, yPos);
		
		//create the view
		this.theView = StationView.create(label, image, xPos, yPos);
		this.theViewLabel = StationViewLabel.createLabel(label,xPos,yPos+30);

		
		 //add this station to the all stations list
		allStations.add(this);
        this.stationType = type;
    }
	
	@Override
	protected boolean work() {
		
		//let the thread wait only if there are no customers in the incoming and outgoing queues
		if (numberOfInQueueCustomers() == 0 && numberOfOutQueueCustomers() == 0) return false;
		
		//If there is an inqueue customer found, handle it
		if (numberOfInQueueCustomers() > 0) this.handleCustomer(this.getNextInQueueCustomer());
				
		//If there is an customer in the out queue -> wake it up
		if(numberOfOutQueueCustomers() > 0){

			//get the customer
			Customer myCustomer = (Customer) this.getNextOutQueueCustomer();
			
			//instruct the customer to move to the next station
			myCustomer.wakeUp();
				
		}
				
		//maybe there is more work to do
		return true;
		
	}
	
	/** Handle the given customer. NOTE: Use this method if the station should handle only one customer
	 *
	 * @param customer the customer that should be treated
	 */
	protected abstract void handleCustomer(Customer customer);
	
	
	/** Handle the given customers. NOTE: Use this method if the station should handle more than one customer at the same time
	 *
	 * @param customers the collection of customers that should be treated
	 */
	protected abstract void handleCustomers(Collection<Customer> customers);
		
	
	/** Get all Stations
	 * 
	 * @return the allStations
	 */
	public static ArrayList<Station> getAllStations() {
		return allStations;
	}

	/**
	 * Get the StationType
	 * @return the stationType
	 */
    public StationType getStationType() {
        return stationType;
    }

    /** Get the number of all waiting customers in the incoming queues
	 *
	 * @return the number
	 */
	protected abstract int numberOfInQueueCustomers();
	
	
	/** Get the number of all waiting customers in the outgoing queues
	 *
	 * @return the number
	 */
	protected abstract int numberOfOutQueueCustomers();
		
	
	/** Get the next (suited) customer out of one of the stations incoming queues. NOTE: Use this method if you want to handle only one customer
	 *
	 * @return the next customer or null if there's nothing found
	 */
	protected abstract Customer getNextInQueueCustomer();
	
	
	/** Get a number of (suited) customers out of the stations incoming queues. NOTE: Use this method if you want to handle more than one customer at the same time
	 *
	 * @return a collection of customers or null if there's nothing found
	 */
	protected abstract Collection<Customer> getNextInQueueCustomers();
		
	
	/** Get the next (suited) customer out of one of the stations outgoing queues. NOTE: Use this method if you want to leave only one customer
	 *
	 * @return the next customer or null if there's nothing found
	 */
	protected abstract Customer getNextOutQueueCustomer();
	
	
	/** Get a number of (suited) customers out of the stations outgoing queues. NOTE: Use this method if you want to leave more than one customer at the same time
	 *
	 * @return a collection of customers or null if there's nothing found
	 */
	protected abstract Collection<Customer> getNextOutQueueCustomers();
	
		
	/** Get the incoming queue
	 * 
	 */
	public abstract SynchronizedQueue getInQueue();
	
	
	/** Get the outgoing queue
	 * 
	 */
	public abstract SynchronizedQueue getOutQueue();
	
}
