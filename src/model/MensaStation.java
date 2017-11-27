package model;

import io.Statistics;
import java.util.ArrayList;
import java.util.Collection;

import controller.Simulation;

/**
 * Class for a mensa station
 * 
 * @author Jaeger, Schmidt
 * @version 2017-10-29
 */
public class MensaStation extends Station {
	
	/** a list of all incoming queues*/
	private SynchronizedQueue inComingQueue;
	
	/** a list of all outgoing queues*/
	private SynchronizedQueue outGoingQueue;
		
	/** a parameter that affects the speed of the treatment for an object */
	private double troughPut;

	/** the instance of our static inner Measurement class*/
	Measurement measurement = new Measurement();
				
	/** (private!) Constructor, creates a new process station 
	 * 
	 * @param label of the station 
	 * @param inQueue a list of all incoming queues
	 * @param outQueue a list of all outgoing queues
	 * @param troughPut a stations parameter that affects treatment of an object
	 * @param xPos x position of the station
	 * @param yPos y position of the station
	 * @param image image of the station
	 * @param type the stationtype of the station
	 */
	private MensaStation(String label, SynchronizedQueue inQueue, SynchronizedQueue outQueue , double troughPut, int xPos, int yPos, String image, StationType type){
		
		super(label, xPos, yPos, image, type);
		
		//the troughPut parameter 
		this.troughPut = troughPut;

		//the stations queues
		this.inComingQueue = inQueue;
		this.outGoingQueue = outQueue;
		
	}
	
	/** create a new process station and add it to the station list
	 *
	 * @param label of the station 
	 * @param inQueue a list of all incoming queues
	 * @param outQueue a list of all outgoing queues
	 * @param troughPut a stations parameter that affects treatment of an object
	 * @param xPos x position of the station
	 * @param yPos y position of the station
	 * @param image image of the station
	 * @param type the stationtype of the station
	 */
	public static void create(String label, SynchronizedQueue inQueue, SynchronizedQueue outQueue , double troughPut, int xPos, int yPos, String image, StationType type){
	
		new MensaStation(label, inQueue,outQueue , troughPut, xPos, yPos, image,type);
		
	}

	/**
	 * executes the super method work of the super-class and checks if the customer has to leave early
	 */
	@Override
	protected boolean work() {
		boolean work = super.work();
		SynchronizedQueue inQueue = inComingQueue;
		Object [] allInQueueCostumer = inQueue.toArray();
		for(int i=0; i<inQueue.size(); i++){
			Customer waitingCostumer =  (Customer) allInQueueCostumer[i];
			if (waitingCostumer.leavesEarly(i)){
				waitingCostumer.wakeUp();
				inQueue.remove(waitingCostumer);
				Statistics.show(waitingCostumer.getLabel()+" geht entnervt");
			}
		}
		return work;
	}


	@Override
	protected int numberOfInQueueCustomers(){
		
		return this.inComingQueue.size();
		
	}
	
	
	@Override
	protected int numberOfOutQueueCustomers() {
		
		return this.outGoingQueue.size();
	
	}
	
	
	@Override
	protected Customer getNextInQueueCustomer(){
		
		if(this.inComingQueue.size() > 0){
			return (Customer) this.inComingQueue.poll();
		}
		
		//if there are no entries in the queue
		return null;

	}
	
	@Override
	protected Customer getNextOutQueueCustomer() {
		
		if(this.outGoingQueue.size() > 0){
			return (Customer) this.outGoingQueue.poll();
		}
		
		//if there are no entries in the queue
		return null;
		
	}
	
	
	@Override
	protected void handleCustomer(Customer customer){
										
		//count all the visiting objects
		measurement.numbOfVisitedObjects++; 
		
		Statistics.show(this.getLabel() + " behandelt: " + customer.getLabel());
		
		//the processing time of the object
		int processTime = customer.getProcessTime();
		
		//the time to handle the object
		int theCustomersTreatingTime = (int) (processTime/this.troughPut);
				
		//get the starting time of the treatment
		long startTime = Simulation.getGlobalTime(); 
				
		//the elapsed time of the treatment
		int elapsedTime = 0;
				
		//while treating time is not reached
		while (!(theCustomersTreatingTime <= elapsedTime)){
				
			//the elapsed time since the start of the treatment
			elapsedTime = (int) (Simulation.getGlobalTime() - startTime); 
									
			//let the thread sleep for the adjusted clock beat 
			//This is just needed to notice the different treatment duration in the view
			try {
				Thread.sleep(Simulation.CLOCKBEAT);
						
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
					
		}
		
		//increase the time the object was treated
		customer.measurement.myTreatmentTime = customer.measurement.myTreatmentTime + elapsedTime;
				
		//increase the stations in use time
		measurement.inUseTime = measurement.inUseTime + elapsedTime; 
						
		//the treatment is over, now the object chooses an outgoing queue and enter it
		customer.enterOutQueue(this);
			
		//just to see the view of the outgoing queue works
		try {
			Thread.sleep(500);
					
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * A (static) inner class for measurement jobs. The class records specific values of the station during the simulation.
	 * These values can be used for statistic evaluation.
	 */
	private static class Measurement {
		
		/** the total time the station is in use */
		private int inUseTime = 0;
		
		/** the number of all objects that visited this station*/ 
		private int numbOfVisitedObjects = 0;
		
		
		/**Get the average time for treatment
		 * 
		 * @return the average time for treatment
		 */
		private int avgTreatmentTime() {
			
			if(numbOfVisitedObjects == 0) return 0; //in case that a station wasn't visited
			else
			return inUseTime/numbOfVisitedObjects;
			
		}
		
	}
	
	
	/**
	 * get and print some statistics out of the Measurement class
	 */
	public void printStatistics() {
		
		String theString = "\nStation Typ: " + this.label;
		theString = theString + "\nAnzahl der behandelten Customer: " + measurement.numbOfVisitedObjects;
		theString = theString + "\nZeit zum Behandeln aller Customer: " + measurement.inUseTime;
		theString = theString + "\nDurchnittliche Behandlungsdauer: " + measurement.avgTreatmentTime();
		
		Statistics.show(theString);
		
	}
	
		
	/** Get all mensa stations
	 * 
	 * @return the allMensaStations
	 */
	public static ArrayList<MensaStation> getAllProcessStations() {
		
		// all the process station objects
		ArrayList<MensaStation> allMensaStations = new ArrayList<MensaStation>();
		
		//filter the process stations out of the station list
		for (Station station : Station.getAllStations()) {
			
			if(station instanceof MensaStation) allMensaStations.add((MensaStation) station);
			
		}
				
		return allMensaStations;
	}
	
		
	/**
	 * Returns the InQueue of the MensaStation Object
	 * @return inComingQue
	 */
	public SynchronizedQueue getInQueue() {
		return inComingQueue;
	}

	/**
	 * Returns the OutQueue of the MensaStation Object
	 * @return outComingQue
	 */
	public SynchronizedQueue getOutQueue() {
		return outGoingQueue;
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
