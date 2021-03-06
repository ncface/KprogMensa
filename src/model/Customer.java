package model;

import io.Statistics;

import java.util.*;

import view.CustomerView;
import controller.Simulation;

/**
 * Class for the customers
 * 
 * @author Jaeger, Schmidt, Rietzler, Clauss, Herzog, Hanselmann
 * @version 2017-12-03
 */
	public class Customer extends Actor {
							
		/** the view of the customer */
		public CustomerView theView;
		
		/** the process time of the customer*/
		private int processTime;

		/** the initial process time of the costumer*/
		private final int INITIALPROCESSTIME;
		
		/** the initial process time of the costumer*/
		public static final int MAXFRUSTRATIONLIMIT = 10;
		
		/** the speed of the customer, the higher the lower */
		private int mySpeed;

		/** the frustration limit of the costumer (value between 1 and 10)*/
		private final int FRUSTRATIONLIMIT;

		/** the global time the customer enters an inQueue*/
		private long enterInQueueTime;
				
		/** all the station (stationtype) where the customer have to go to*/
		private Queue<StationType> stationsToGo = new LinkedList<>();
		 				
		/** list of all customers */
		private static List<Customer> allCustomers = new ArrayList<>();
		
		/** the actual station where this customer is in, null if it's not in a station or a stations queue */
		private Station actualStation = null;

		/** the different amount of food for the different stations*/
		private Map<StationType, Integer> customerFoodAmountAtStationsWanted;

		/** a reference to the customer itself*/
		private Customer customer;

		/** a customerObservable for the customer*/
		private CustomerObservable customerObservable;

		/** the instance of our static inner Measurement class*/ 
		Measurement measurement = new Measurement();

		/** the amount of time the customer waits without frustration */
		private final int LOWERLIMITWAITINGTIME = 10;
		/** the amount of time the customer waits after which the frustration doesn´t increase anymore */
		private final int UPPERLIMITWAITINGTIME = 30;
		/** the number of persons in the waiting queue in front of the customer, the customer accepts with a frustration = 0 */
		private final int LOWERLIMITPERSONS = 10;
		/** the number of persons in the waiting queue in front of this customer, that don´t increase the frustration */
		private final int UPPERLIMITPERSONS = 30;
		
				
		/** (private!) Constructor, creates a new customer model and send it to the start station
		 * 
		 * @param label of the customer
		 * @param stationsToGo the stations to go
		 * @param processtime the processing time of the customer, affects treatment by a station
		 * @param speed the moving speed of the customer
		 * @param xPos x position of the customer
		 * @param yPos y position of the customer
		 * @param image image of the customer
		 * @param customerFoodAmountAtStationsWanted the amount of food at the different mensastations
		 */
		private Customer(String label, Queue<StationType> stationsToGo, int processtime, int speed, int xPos, int yPos,
						 String image, Map<StationType,Integer> customerFoodAmountAtStationsWanted, int FRUSTRATIONLIMIT){
			super(label, xPos, yPos);
			
			enterInQueueTime = 0;

			//create the view
			this.theView = CustomerView.create(label, image, xPos, yPos);
			
			Customer.allCustomers.add(this); //add customer to the static list
			
			this.stationsToGo = stationsToGo;
			this.processTime = processtime;
			this.INITIALPROCESSTIME = this.processTime;
			this.mySpeed = speed;
			this.FRUSTRATIONLIMIT = FRUSTRATIONLIMIT;
			
			this.customerFoodAmountAtStationsWanted = customerFoodAmountAtStationsWanted;
						
			//the first station to go to is the start station
			Station station = this.getNextStation();
			
			//enter the in queue of the start station
			this.enterInQueue(station);

			customerObservable = new CustomerObservable();

			//a reference to the customer itself
			this.customer = this;
		}
		
		/** Create a new customer model
		 *
		 * @param label of the customer
		 * @param stationsToGo the stations to go
		 * @param processTime the processing time of the customer, affects treatment by a station
		 * @param speed the moving speed of the customer
		 * @param xPos x position of the customer
		 * @param yPos y position of the customer
		 * @param image image of the customer
		 * @param foodAmountAtStation the amount of food at the different mensastations
		 */
		public static void create(String label, Queue<StationType> stationsToGo,
								  int processTime, int speed , int xPos, int yPos,
								  String image, Map<StationType,Integer> foodAmountAtStation,
								  int frustrationLimit){
				
			new Customer(label, stationsToGo, processTime, speed, xPos, yPos, image, foodAmountAtStation, frustrationLimit);
				
		}

		/** Choose the next station with the shortest inQueue to go to
		 * @return the next station or null if no station was found
		 */
		private Station getNextStation(){
			//we are at the end of the list
			if(this.stationsToGo.isEmpty()) return null;

			//get the stationtype of the next station from the list
			StationType stationType = this.stationsToGo.poll();

			//a list of possible stations with with a certain stationtype
			List<Station> possibleStations = new ArrayList<>();

			//looking for the matching station and add it to the possible stations
			for (Station station : Station.getAllStations()){
				if(stationType == station.getStationType()) possibleStations.add(station);
			}

			// the number of possible stations
			int numberPossibleStations = possibleStations.size();

			// looking for the station with the shortest InQueue
			if (numberPossibleStations == 0)return null; //no station with the requested stationtype
			else if (numberPossibleStations == 1)return possibleStations.get(0); //only one station with the requested stationtype
			else{
				Station stationWithShortestInQueue = possibleStations.get(0);
				for (Station station: possibleStations) {
					//choose the station with the shortest InQueue
					if (station.numberOfInQueueCustomers() < stationWithShortestInQueue.numberOfInQueueCustomers()) {
						stationWithShortestInQueue = station;
					}
				}
				return stationWithShortestInQueue;
			}
		}
		
		/** Chooses a suited incoming queue of the given station and enter it 
		 * 
		 * @param station the station from where the queue should be chosen
		 * 
		 */
		private void enterInQueue(Station station){
			SynchronizedQueue inQueue = station.getInQueue();
			if(inQueue.offer(this)) {
				//set actual station to the just entered station
				this.actualStation = station;
				updateProcessTime();
			}
		}

		/**
		 * updates the processtime at the actual station according to the amount of food.
		 */
		private void updateProcessTime(){
			StationType stationTypeWantedFood = actualStation.getStationType();
			if(customerFoodAmountAtStationsWanted.containsKey(stationTypeWantedFood)) {
				int amountFoodInGram = customerFoodAmountAtStationsWanted.get(stationTypeWantedFood);
				//convert the amountFoodInGram in amountFoodInKilogram
				double amountFoodInKilogram = amountFoodInGram / 1000.0;
				//calculate the process time
				this.processTime = (int) (INITIALPROCESSTIME * (1 + amountFoodInKilogram));
			}
			else{
				processTime = INITIALPROCESSTIME;
			}
		}

		/**
		 * Calculates the frustration caused by the waitingTime of the Customer
		 * the frustration can have values between 0 and 5
		 * @param waitingTime the waiting time of the customer
		 * @return a frustration value between 0 and 5
		 */
		private double calculateFrustrationByWaitingTime(long waitingTime){
			double frustration = 0;
			//normiert den frustrationswert auf maximal 5
			final double NORMIERUNG = UPPERLIMITWAITINGTIME/5;
			if(waitingTime <= LOWERLIMITWAITINGTIME){
				frustration = 0;
			}
			else if (waitingTime > LOWERLIMITWAITINGTIME && waitingTime <= UPPERLIMITWAITINGTIME){
				frustration = (waitingTime - LOWERLIMITWAITINGTIME)/NORMIERUNG;
			}
			else if (waitingTime > UPPERLIMITWAITINGTIME){
				frustration = 5;
			}
			return frustration;
		}

		/**
		 * calculates a frustration value based on the persons in front of this person in the waiting Queue
		 * the frustration can have values between 0 and 2
		 * @param personsInFrontOfThis the number of persons in front of this
		 * @return a frustration value based on the persons in front of this one between 0 and 2
		 */
		private double calculateFrustrationByPersonsInFrontOfThis(int personsInFrontOfThis){
			double frustration = 0;
			//normiert den Frustrationswert auf maximal 2
			final double NORMIERUNG = UPPERLIMITPERSONS / 2;
			if(personsInFrontOfThis <= LOWERLIMITPERSONS){
				frustration = 0;
			}
			else if(personsInFrontOfThis > LOWERLIMITPERSONS && personsInFrontOfThis <= UPPERLIMITPERSONS){
				frustration = (personsInFrontOfThis - LOWERLIMITPERSONS)/NORMIERUNG;
			}
			else if(personsInFrontOfThis > UPPERLIMITPERSONS){
				frustration = 2;
			}
			return frustration;
		}

		/**
		 * Chooses the outgoing queue of the given station and enter it
		 * @param station the station from where the queue should be chosen
		 */
		protected void enterOutQueue(Station station){
			SynchronizedQueue outQueue = station.getOutQueue();
			outQueue.offer(this);
		}

		@Override
		protected boolean work(){

			//the customer is leaving the station -> set actual station to null
			this.actualStation = null;
						
			//choose the next station to go to
			Station station = this.getNextStation();

			//only move if there is a next station found
			if(station == null)return false;
					
			//let the customer move to the chosen station
			Statistics.show(this.getLabel() + " geht zur " + station.getLabel());
			//while target is not achieved 
			while (!(station.getXPos() == this.xPos && station.getYPos() == this.yPos)) {
				
				//move to the station
	 			if(station.getXPos() > this.xPos) this.xPos++;
	 			if(station.getYPos() > this.yPos) this.yPos++;
	 			
	 			if(station.getXPos() < this.xPos) this.xPos--;
	 			if(station.getYPos() < this.yPos) this.yPos--;	
	 			
	 			//set our view to the new position
				theView.setLocation(this.xPos, this.yPos);
				
				//let the thread sleep for the sequence time
				try {
					Thread.sleep(Simulation.SPEEDFACTOR *mySpeed);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			
			Statistics.show(this.getLabel() + " erreicht " + station.getLabel());
			
			//the customer has reached the station, now the customer chooses an incoming queue and enter it
			this.enterInQueue(station);

			//the customer doesnt wait anymore
			enterInQueueTime = Simulation.getGlobalTime();
			
			//wake up the station
			station.wakeUp();
									
			//work is done
			return false;
		}

		/**
		 * a getter method for the customerFoodAmountAtStationsWanted
		 * @return customerFoodAmountAtStationsWanted
		 */
		public Map<StationType, Integer> getCustomerFoodAmountAtStationsWanted() {
			return customerFoodAmountAtStationsWanted;
		}

		/**
		 * Getter method for the total amount of food the customer would like to buy
		 * @return the total amount of food wanted
		 */
		public int getTotalAmountWantedFood(){
			Collection<Integer> amountsWantedFood = customerFoodAmountAtStationsWanted.values();
			int totalAmountWantedFood = 0;
			for (int amount: amountsWantedFood){
				totalAmountWantedFood += amount;
			}
			return totalAmountWantedFood;
		}

		/**
		 * Print some statistics
		 */
		public void printStatistics() {
			
			String theString = "\nCustomer: " + this.label;
			theString = theString + "\nZeit zum Behandeln des Customer: " + measurement.myTreatmentTime;
						
			Statistics.show(theString);
			
		}

		/**
		 * decides if the costumer leaves the mensa earlier
		 * @return true if he leaves early, else false
		 */
		public boolean leavesEarly(int personsInFrontOfThis){
			long waitingTime = Simulation.getGlobalTime() - enterInQueueTime;

			int frustration = (int)(calculateFrustrationByWaitingTime(waitingTime)*
					calculateFrustrationByPersonsInFrontOfThis(personsInFrontOfThis));

			if (frustration> FRUSTRATIONLIMIT){
				//set the next station as endstation
				stationsToGo.clear();
				stationsToGo.add(StationType.ENDE);
				//write in data collection
				customerObservable.notifyObservers(Simulation.getGlobalTime());
				return true;
			}
			return false;
		}



		/** Get all customers
		 * 
		 * @return a list of all customers
		 */
		public static List<Customer> getAllCustomers() {
			return allCustomers;
		}
		
		
		/** Get the actual station where this customer is in
		 * 
		 * @return the actual station where this customer is in, null if it's not in a station or a stations queue
		 */
		public Station getActualStation() {
			return actualStation;
		}
		
		
		/**Get the customers processing time
		 * 
		 * @return the processing time
		 */
		public int getProcessTime() {
			return processTime;
		}

		/**
		 * a getter method for the customerObservable
		 * @return the customerObservable
		 */
		public CustomerObservable getCustomerObservable(){
				return this.customerObservable;
			}

		/**
		 * A (static) inner class for measurement jobs. The class records specific values of the customer during the simulation.
		 * These values can be used for statistic evaluation.
		 */
		static class Measurement {

			/** the treated time by all processing stations, in seconds */
			int myTreatmentTime = 0;

		}

		/**
		 * an inner class which allows to observe the customer
		 */
		public class CustomerObservable extends Observable{
			/**
			 * notify the observers when this method gets called
			 * @param arg the time the customer left early
			 */
			@Override
			public void notifyObservers(Object arg) {
				setChanged();
				super.notifyObservers(arg);
			}
			/**
			 * getter for the customer
			 * @return the outer object
			 */
			public Customer getOuterObject(){
				return customer;
			}
		}
}

	
