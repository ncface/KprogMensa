package model;

import java.util.Collection;

import io.Statistics;

/**
 * Class for the end station. This is the last station where all objects are collected
 * 
 * @author Jaeger, Schmidt
 * @version 2017-10-26
 */
public class EndStation extends SimpleStation {
	
	/** instance of the start station */
	@SuppressWarnings("unused")
	private static EndStation theEndStation;

	/** the station type for every endstation */
	private static final StationType endStationType = StationType.ENDE;
	
	/** (private!) Constructor, creates a new end station
	 * 
	 * @param label of the station 
	 * @param inQueue the incoming queue
	 * @param outQueue the outgoing queue
	 * @param xPos x position of the station 
	 * @param yPos y position of the station 
	 * @param image image of the station  
	 */
	private EndStation(String label, SynchronizedQueue inQueue, SynchronizedQueue outQueue, int xPos, int yPos, String image){
		super(label, inQueue, outQueue, xPos, yPos, image, endStationType);
	}
	
	/** creates a new end station
	 *
	 * @param label of the station 
	 * @param inQueue the incoming queue
	 * @param outQueue the outgoing queue
	 * @param xPos x position of the station 
	 * @param yPos y position of the station 
	 * @param image image of the station  
	 */
	public static void create(String label, SynchronizedQueue inQueue, SynchronizedQueue outQueue, int xPos, int yPos, String image){
	
		theEndStation = new EndStation(label, inQueue, outQueue, xPos, yPos, image);
		
	}
	
	@Override
	protected boolean work() {
		
		//let the thread wait only if there are no objects in the incoming queue
		if (numberOfInQueueCustomers() == 0 ) return false;
		
		//If there is an inqueue object found, handle it
		if (numberOfInQueueCustomers() > 0) this.handleCustomer(this.getNextInQueueCustomer());
						
		//maybe there is more work to do
		return true;
		
	}
	
	@Override
	protected void handleCustomer(Customer customer){
				
		
			// the object chooses the outgoing queue and enter it
			customer.enterOutQueue(this);
			
			//  this is a just for fun action, the object gets invisible
			customer.theView.setVisible(false);
					
			//End the simulation if the condition is met
			endSimulation();
		
				
	}
	
	
	/** End the simulation if the condition is met
	 *
	 * 
	 */
	private void endSimulation(){
		
		// Are all objects in the stations outgoing queue, then we are finish
		if(Customer.getAllCustomers().size() == numberOfOutQueueCustomers()){
											
		Statistics.show("\n--- Simulation beendet ----");
												
		//show some station statistics
		for (MensaStation station : MensaStation.getAllProcessStations()) {
				station.printStatistics();
		}
									
		//show some objects statistics
		for (Object object : this.outGoingQueue){
			((Customer) object).printStatistics();
		}
														
		// end simulation 
		// System.exit(0); 
												
		}
	}
	
	@Override
	protected void handleCustomers(Collection<Customer> customers) {
				
	}

	@Override
	protected Collection<Customer> getNextInQueueCustomers() {
		return null;
	}

	@Override
	protected Collection<Customer> getNextOutQueueCustomers() {
		return null;
	}

	
}
