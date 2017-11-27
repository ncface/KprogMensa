package model;

import io.Statistics;

import java.awt.Component;
import java.util.ArrayList;

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
		
		/** the speed of the customer, the higher the lower */
		private int mySpeed;
				
		/** all the station (mensastationtype) where the customer have to go to*/
		private ArrayList<StationType> stationsToGo = new ArrayList<>();
		
		/** a pointer to the actual position of the stationsToGo list, start position is 0*/ 
		private int stationListPointer = 0;
		 				
		/** list of all customers */
		private static ArrayList<Customer> allCustomers = new ArrayList<Customer>();
		
		/** the actual station where this customer is in, null if it's not in a station or a stations queue */
		private Station actualStation = null;
		
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
		 */
		private Customer(String label, ArrayList<StationType> stationsToGo, int processtime, int speed, int xPos, int yPos, String image){
			super(label, xPos, yPos);
			
			//create the view
			this.theView = CustomerView.create(label, image, xPos, yPos);
			
			Customer.allCustomers.add(this); //add customer to the static list
			
			this.stationsToGo = stationsToGo;
			this.processTime = processtime;
			this.mySpeed = speed;
						
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
		 */
		public static void create(String label, ArrayList<StationType> stationsToGo, int processtime, int speed , int xPos, int yPos, String image){
				
			new Customer(label, stationsToGo, processtime, speed, xPos, yPos, image);
				
		}
					
		/** Chose the next station to go to
		 * 
		 * @return the next station or null if no station was found
		 */
		private Station getNextStation(){
						
			//we are at the end of the list
			if(this.stationsToGo.size() < stationListPointer) return null;

			//get the mensastationtype of the next station from the list and increase the list pointer

			
			//get the label of the next station from the list and increase the list pointer
			//String stationLabel = this.stationsToGo.get(stationListPointer++);
					
			//looking for the matching station and return it
			for (Station station : Station.getAllStations()){
				
				//if(stationLabel.equals(station.getLabel())) return station;
					
			}
			
			return null; //the matching station wasn't found
		}
		
		/** Chooses a suited incoming queue of the given station and enter it 
		 * 
		 * @param station the station from where the queue should be chosen
		 * 
		 */
		private void enterInQueue(Station station){
			
			//get the stations incoming queues
			ArrayList<SynchronizedQueue> inQueues = station.getAllInQueues();
			
			//there is just one queue, enter it
			if(inQueues.size()==1) inQueues.get(0).offer(this);
			
			//Do we have more than one incoming queue?
			//We have to make a decision which queue we choose -> your turn 
			else{
				
				//get the first queue and it's size
				SynchronizedQueue queueBuffer = inQueues.get(0);
				int queueSize = queueBuffer.size();
								
				//Looking for the shortest queue (in a simple way)
				for (SynchronizedQueue inQueue : inQueues) {
						
					if(inQueue.size() < queueSize) {
						queueBuffer = inQueue;
						queueSize = inQueue.size();
					}
				}
				
				//enter the queue
				queueBuffer.offer(this);
				
				//set actual station to the just entered station
				this.actualStation = station;
							
								
			}
				
		}
		
	
		/** Chooses a suited outgoing queue of the given station and enter it
		 * 
		 * @param station the station from where the queue should be chosen
		 */
		void enterOutQueue(Station station){
			
			//get the stations outgoing queues
			ArrayList<SynchronizedQueue> outQueues = station.getAllOutQueues();
				
			
			//there is just one queue, enter it
			if(outQueues.size()==1) outQueues.get(0).offer(this);
			
			//Do we have more than one outgoing queue?
			//We have to make a decision which queue we choose -> your turn 
			else{
				
				//get the first queue and it's size
				SynchronizedQueue queueBuffer = outQueues.get(0);
				int queueSize = queueBuffer.size();
								
				//Looking for the shortest queue (in a simple way)
				for (SynchronizedQueue inQueue : outQueues) {
						
					if(inQueue.size() < queueSize) {
						queueBuffer = inQueue;
						queueSize = inQueue.size();
					}
				}
				
				//enter the queue
				queueBuffer.offer(this);
				
			}
		
		}
		
			
		@Override		
		protected boolean work(){
			
			//the customer is leaving the station -> set actual station to null
			this.actualStation = null;
						
			//choose the next station to go to
			Station station = this.getNextStation();
			
			//only move if there is a next station found
			if(station == null) return false;
					
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
	
