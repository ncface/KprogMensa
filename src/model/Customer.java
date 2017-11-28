package model;

import io.Statistics;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import view.CustomerView;
import controller.Simulation;

/**
 * Class for the customers
 * 
 * @author Jaeger, Schmidt
 * @version 2016-07-08
 */
	
	public class Customer extends Actor {
							
		/** the view of the customer */
		public CustomerView theView;
		
		/** the process time of the customer*/
		private int processTime;

		/** the initial process time of the costumer*/
		private final int INITIALPROCESSTIME;
		
		/** the speed of the customer, the higher the lower */
		private int mySpeed;

		/** the frustration limit of the costumer (value between 1 and 10)*/
		private final int frustrationLimit;

		/** the waiting time of the costumer*/
		private int waitingTime;
				
		/** all the station (stationtype) where the customer have to go to*/
		private ArrayList<StationType> stationsToGo = new ArrayList<>();
		
		/** a pointer to the actual position of the stationsToGo list, start position is 0*/ 
		private int stationListPointer = 0;
		 				
		/** list of all customers */
		private static ArrayList<Customer> allCustomers = new ArrayList<Customer>();
		
		/** the actual station where this customer is in, null if it's not in a station or a stations queue */
		private Station actualStation = null;

		/** the different amount of food for the different stations*/
		private Map<StationType, Integer> foodAmountAtStations;
		
		/** the instance of our static inner Measurement class*/ 
		Measurement measurement = new Measurement();
		
				
		/** (private!) Constructor, creates a new customer model and send it to the start station
		 * 
		 * @param label of the customer
		 * @param stationsToGo the stations to go
		 * @param processtime the processing time of the customer, affects treatment by a station
		 * @param speed the moving speed of the customer
		 * @param xPos x position of the customer
		 * @param yPos y position of the customer
		 * @param image image of the customer
		 * @param foodAmountAtStations the amount of food at the different mensastations
		 */
		private Customer(String label, ArrayList<StationType> stationsToGo, int processtime, int speed, int xPos, int yPos, String image, Map<StationType,Integer> foodAmountAtStations, int frustrationLimit){
			super(label, xPos, yPos);
			
			waitingTime = 0;

			//create the view
			this.theView = CustomerView.create(label, image, xPos, yPos);
			
			Customer.allCustomers.add(this); //add customer to the static list
			
			this.stationsToGo = stationsToGo;
			this.processTime = processtime;
			this.INITIALPROCESSTIME = this.processTime;
			this.mySpeed = speed;
			this.frustrationLimit = frustrationLimit;
			
			this.foodAmountAtStations = foodAmountAtStations;
						
			//the first station to go to is the start station
			Station station = this.getNextStation();
			
			//enter the in queue of the start station
			this.enterInQueue(station);
						
		}
		
		/** Create a new customer model
		 *
		 * @param label of the customer
		 * @param stationsToGo the stations to go
		 * @param processtime the processing time of the customer, affects treatment by a station
		 * @param speed the moving speed of the customer
		 * @param xPos x position of the customer
		 * @param yPos y position of the customer
		 * @param image image of the customer
		 * @param foodAmountAtStation the amount of food at the different mensastations
		 */
		public static void create(String label, ArrayList<StationType> stationsToGo, int processtime, int speed , int xPos, int yPos, String image, Map<StationType,Integer> foodAmountAtStation, int frustrationLimit){
				
			new Customer(label, stationsToGo, processtime, speed, xPos, yPos, image, foodAmountAtStation, frustrationLimit);
				
		}

		/**
		 * a getter method for the foodAmountAtStations
		 * @return foodAmountAtStations
		 */
		public Map<StationType, Integer> getFoodAmountAtStations() {
			return foodAmountAtStations;
		}

		/** Choose the next station with the shortest inQueue to go to
		 * 
		 * @return the next station or null if no station was found
		 */
		private Station getNextStation(){
						
			//we are at the end of the list
			if(this.stationsToGo.size() < stationListPointer) return null;

			//get the mensastationtype of the next station from the list and increase the list pointer
			StationType stationType = this.stationsToGo.get(stationListPointer);
			stationListPointer++;

			//a list of possible stations with with a certain stationtype
			List<Station> possibleStations = new ArrayList<>();

			//looking for the matching station and return it
			for (Station station : Station.getAllStations()){
				if(stationType == station.getStationType()) possibleStations.add(station);
			}

			//the number of possiblestations
			int numberPossibleStations = possibleStations.size();

			//looking for the station with the shortes inQueue
			if (numberPossibleStations == 0)return null; //no station with the requested stationtype
			else if (numberPossibleStations == 1)return possibleStations.get(0); //only one station with the requested stationtype
			else{
				Station stationWithShortestInQueue = possibleStations.get(0);
				for (Station station: possibleStations) {
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
			inQueue.offer(this);
			//set actual station to the just entered station
			this.actualStation = station;
			updateProcessTime();
		}

		/**
		 * updates the processtime at the actual station according to the amount of food.
		 *
		 */
		private void updateProcessTime(){
			StationType stationType = actualStation.getStationType();
			if(foodAmountAtStations.containsKey(stationType)) {
				int amountFoodInGram = foodAmountAtStations.get(stationType);
				//convert the amountFoodInGram in amountFoodInKilogram
				double amountFoodInKilogram = amountFoodInGram / 1000.0;
				this.processTime = (int) (INITIALPROCESSTIME * (1 + amountFoodInKilogram));
			}
			else{
				processTime = INITIALPROCESSTIME;
			}
		}


		/** Chooses a suited outgoing queue of the given station and enter it
		 * 
		 * @param station the station from where the queue should be chosen
		 */
		protected void enterOutQueue(Station station){
			SynchronizedQueue outQueue = station.getOutQueue();
			outQueue.offer(this);
		}
		
			
		@Override		
		protected boolean work(){
			//the customer doesnt wait anymore
			waitingTime = 0;

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
				((Component) theView).setLocation(this.xPos, this.yPos);	
				
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
			
			//wake up the station
			station.wakeUp();
									
			//work is done
			return false;
			
			
					
		}
		
		/**
		 * A (static) inner class for measurement jobs. The class records specific values of the customer during the simulation.
		 * These values can be used for statistic evaluation.
		 */
		static class Measurement {
			
			/** the treated time by all processing stations, in seconds */
			int myTreatmentTime = 0;
			
		}
		
		/**Print some statistics
		 * 
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
			waitingTime++;
			int frustration = 0;
			if(waitingTime<=10)frustration = 0;
			else if (waitingTime>10 && waitingTime<=30)frustration = (waitingTime - 10) / 4;
			else if (waitingTime>30)frustration = 5;

			if (personsInFrontOfThis<=10)frustration = 0;
			else if (personsInFrontOfThis>10 && personsInFrontOfThis<=30)frustration *= ((personsInFrontOfThis - 10)/10);
			else if (personsInFrontOfThis>30)frustration *= 2;

			if (frustration>frustrationLimit){
				stationsToGo.set(stationListPointer, StationType.ENDE); //set the next station as endstation
				return true;
			}
			return false;
		}


		/** Get all customers
		 * 
		 * @return a list of all customers
		 */
		public static ArrayList<Customer> getAllCustomers() {
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
		
	}
	
