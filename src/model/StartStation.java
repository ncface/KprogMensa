package model;

import java.util.Collection;

import controller.Simulation;

/**
 * Class for the beginning station, this is where all objects start
 * 
 * @author Jaeger, Schmidt
 * @version 2017-10-29
 */
public class StartStation extends SimpleStation {
						
	/** instance of the start station */
	private static StartStation theStartStation;

	/** the station type for every startstation */
	private static final StationType startStationType = StationType.START;

	/** the total number of available StartStationObjects*/
	private static final int TOTALNUMBERSTARTSTATIONOBJECTS = 1;

	/** counter for the number of created StartStations Objects*/
	private static int counterStartStation = 0;
	
	/** (private!) Constructor, creates a new start station
	 * 
	 * @param label of the station 
	 * @param inQueue the incoming queue
	 * @param outQueue the outgoing queue
	 * @param xPos x position of the station 
	 * @param yPos y position of the station 
	 * @param image image of the station 
	 */
	private StartStation(String label, SynchronizedQueue inQueue, SynchronizedQueue outQueue, int xPos, int yPos,
						 String image){
		
		super(label, inQueue, outQueue, xPos, yPos, image, startStationType);
		
	}
	
	
	/** creates a new start station
	 *
	 * @param label of the station 
	 * @param inQueue the incoming queue
	 * @param outQueue the outgoing queue
	 * @param xPos x position of the station 
	 * @param yPos y position of the station 
	 * @param image image of the station  
	 */
	public static synchronized void create(String label, SynchronizedQueue inQueue, SynchronizedQueue outQueue, int xPos, int yPos,
							  String image)throws Exception{
		if (counterStartStation<TOTALNUMBERSTARTSTATIONOBJECTS) {
			theStartStation = new StartStation(label, inQueue, outQueue, xPos, yPos, image);
			counterStartStation++;
		}else{
			throw new Exception();
		}
	}
	
			
	@Override
	protected void handleCustomer(Customer customer){
				
		//the customer enters the outgoing queue
		customer.enterOutQueue(this);
		
		//let the next customer start with a little delay
		try {
			Thread.sleep(Simulation.CLOCKBEAT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
		
	/**Get the start station
	 * 
	 * @return theStartStation
	 */
	public static StartStation getStartStation() {
		return theStartStation;
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
