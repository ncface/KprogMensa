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
	private ArrayList<SynchronizedQueue> inComingQueues = new ArrayList<SynchronizedQueue>();
	
	/** a list of all outgoing queues*/
	private ArrayList<SynchronizedQueue> outGoingQueues = new ArrayList<SynchronizedQueue>();
		
	/** a parameter that affects the speed of the treatment for an object */
	private double troughPut;

	/**
	 * a parameter that describes the Type of this MensaStation
	 */
	private StationType type;
	
	/** the instance of our static inner Measurement class*/ 
	Measurement measurement = new Measurement();
				
	/** (private!) Constructor, creates a new process station 
	 * 
	 * @param label of the station 
	 * @param inQueues a list of all incoming queues
	 * @param outQueues a list of all outgoing queues
	 * @param troughPut a stations parameter that affects treatment of an object
	 * @param xPos x position of the station
	 * @param yPos y position of the station
	 * @param image image of the station 
	 */
	private MensaStation(String label, ArrayList<SynchronizedQueue> inQueues, ArrayList<SynchronizedQueue> outQueues , double troughPut, int xPos, int yPos, String image, StationType type){
		
		super(label, xPos, yPos, image);
		
		//the troughPut parameter 
		this.troughPut = troughPut;

		//the type
		this.type = type;
		
		//the stations queues
		this.inComingQueues = inQueues;
		this.outGoingQueues = outQueues;
		
	}
	
	/** create a new process station and add it to the station list
	 *
	 * @param label of the station 
	 * @param inQueues a list of all incoming queues
	 * @param outQueues a list of all outgoing queues
	 * @param troughPut a stations parameter that affects treatment of an object
	 * @param xPos x position of the station
	 * @param yPos y position of the station
	 * @param image image of the station 
	 */
	public static void create(String label, ArrayList<SynchronizedQueue> inQueues,ArrayList<SynchronizedQueue> outQueues , double troughPut, int xPos, int yPos, String image, StationType type){
	
		new MensaStation(label, inQueues,outQueues , troughPut, xPos, yPos, image,type);
		
	}

	public StationType getType() {
		return type;
	}

	@Override
	protected int numberOfInQueueCustomers(){
		
		int theNumber = 0;
		
		//We have more than one incoming queue -> get all incoming queues
		for (SynchronizedQueue inQueue : this.inComingQueues) {
						
			theNumber = theNumber + inQueue.size();
		}
		
		return theNumber;
		
	}
	
	
	@Override
	protected int numberOfOutQueueCustomers() {
		
		int theNumber = 0;
		
		//maybe we have more than one outgoing queue -> get all outgoing queues
		for (SynchronizedQueue outQueue : this.outGoingQueues) {
						
			theNumber = theNumber + outQueue.size();
		}
		
		return theNumber;
	
	}
	
	
	@Override
	protected Customer getNextInQueueCustomer(){
		
		//maybe we have more than one incoming queue -> get all incoming queues
		for (SynchronizedQueue inQueue : this.inComingQueues) {
							
			//We have to make a decision which queue we choose -> your turn 
			//I'll take the first possible I get
			if(inQueue.size() > 0){
				return (Customer) inQueue.poll();
			}
		}
		
		//nothing is found
		return null;
	}
	
	@Override
	protected Customer getNextOutQueueCustomer() {
		
		//maybe we have more than one outgoing queue -> get all outgoing queues
		for (SynchronizedQueue outQueue : this.outGoingQueues) {
									
		//We have to make a decision which queue we choose -> your turn 
		//I'll take the first possible I get
			if(outQueue.size() > 0){
				return (Customer) outQueue.poll();
			}
		}
				
		//nothing is found
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
	 * 
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
	
		
	@Override
	public ArrayList<SynchronizedQueue> getAllInQueues() {
		return inComingQueues;
	}

	@Override
	public ArrayList<SynchronizedQueue> getAllOutQueues() {
		return outGoingQueues;
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
