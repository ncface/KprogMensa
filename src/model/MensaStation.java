package model;

import io.Statistics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;

import controller.Simulation;

/**
 * Class for a mensa station
 * 
 * @author Jaeger, Schmidt; Patrick Hanselmann, Sebastian Herzog, Jeffrey Manuel Rietzler, Nils Clauss
 * @version 2017-11-28
 */
public class MensaStation extends Station {
	
	/** the incoming queue*/
	protected SynchronizedQueue inComingQueue;
	
	/** the outgoing queue*/
	protected SynchronizedQueue outGoingQueue;
		
	/** a parameter that affects the speed of the treatment for an customer */
	protected double troughPut;

	/** the instance of our static inner Measurement class*/
	Measurement measurement = new Measurement();

	/** the operating costs of the MensaStation per clockbeat*/
	private double operatingCostsPerClockbeat;

	/** the opening time of the MensaStation*/
	private long openingTime;

	/** a reference to the mensaStaiton itself*/
	private MensaStation mensaStation;

	/** a mensaStationObservable for this mensaStation*/
	private MensaStationObservable mensaStationObservable;

	/** Constructor, creates a new process station
	 * 
	 * @param label of the station 
	 * @param inQueue a list of all incoming queues
	 * @param outQueue a list of all outgoing queues
	 * @param troughPut a stations parameter that affects treatment of an object
	 * @param xPos x position of the station
	 * @param yPos y position of the station
	 * @param image image of the station
	 * @param type the stationtype of the station
	 * @param operatingCostsPerClockbeat operating costs per clockbeat of the mensaStation
	 */
	protected MensaStation(String label, SynchronizedQueue inQueue, SynchronizedQueue outQueue , double troughPut, int xPos, int yPos,
						   String image, StationType type, double operatingCostsPerClockbeat){
		
		super(label, xPos, yPos, image, type);
		
		//the troughPut parameter 
		this.troughPut = troughPut;

		//the operating costs per clockbeat of the MensaStation
		this.operatingCostsPerClockbeat = operatingCostsPerClockbeat;

		//the stations queues
		this.inComingQueue = inQueue;
		this.outGoingQueue = outQueue;

		//creates a MensaStationObservable for this mensaStation
		this.mensaStationObservable = new MensaStationObservable();

		this.mensaStation = this;
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
	 * @param operatingCostsPerClockbeat operating costs per clockbeat of the mensaStation
	 */
	public static void create(String label, SynchronizedQueue inQueue, SynchronizedQueue outQueue , double troughPut, int xPos, int yPos,
							  String image, StationType type, double operatingCostsPerClockbeat){
	
		new MensaStation(label, inQueue,outQueue , troughPut, xPos, yPos, image,type, operatingCostsPerClockbeat);
		
	}

	/**
	 * executes the super method work of the super-class and checks if the customer has to leave early
	 *
	 * @return true if the MensaStation has more work to do, and
	 * false if the MensaStation has no more work to do for the moment, so the thread can fall into the wait() mode
	 */
	@Override
	protected boolean work() {
		boolean work = super.work();
		Object [] allInQueueCostumer = inComingQueue.toArray();
		// search for customers who want to leave early
		for(int i=0; i<allInQueueCostumer.length; i++){
			Customer waitingCostumer =  (Customer) allInQueueCostumer[i];
			if (waitingCostumer.leavesEarly(i)){
				//when customer leaves early
				waitingCostumer.wakeUp();
				inComingQueue.remove(waitingCostumer);
				Statistics.show(waitingCostumer.getLabel()+" geht entnervt");
			}
		}
		return work;
	}


	@Override
	protected int numberOfInQueueCustomers(){
		int numberOfInQueueCustomers = this.inComingQueue.size();
		//notify additionalMensaStations when inQueue too long
		if (numberOfInQueueCustomers>this.stationType.getInQueueLimit()){
			mensaStationObservable.notifyObservers();
		}
		return numberOfInQueueCustomers;
		
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
			return inUseTime/numbOfVisitedObjects;
			
		}
		
	}

	/**
	 * an inner class which allows to observe the mensa station
	 */
	protected class MensaStationObservable extends Observable{
		@Override
		public void notifyObservers() {
			setChanged();
			super.notifyObservers();
		}

		/**
		 * getter for the mensaStation
		 * @return the outer object
		 */
		protected MensaStation getOuterObject(){
			return mensaStation;
		}
	}

	/**
	 * Setter method to add an Observer object
	 * @param additionalMensaStation the observer object
	 */
	public void setObserver(AdditionalMensaStation additionalMensaStation){
		this.mensaStationObservable.addObserver(additionalMensaStation);
	}

	/**
	 * calculates the operating costs of the MensaStation
	 * @return	the operating costs
	 */
	public double calculateOperatingCosts(){
		long endTime = Simulation.getGlobalTime();
		long operatingTime = endTime - this.openingTime;
		return operatingTime * this.operatingCostsPerClockbeat;
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
	 * Setter method for the opening time of the MensaStation
	 * only sets the opening time when opening time wasnt set before
	 */
	public void setOpeningTime() {
		if (openingTime == 0) {
			this.openingTime = Simulation.getGlobalTime();
		}
	}

	/**
	 * Returns the inQueue of the mensaStation object
	 * @return the in coming queue
	 */
	public SynchronizedQueue getInQueue() {
		return inComingQueue;
	}

	/**
	 * Returns the outQueue of the mensaStation object
	 * @return the out goining queue
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
