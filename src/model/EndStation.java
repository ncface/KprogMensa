package model;

import java.util.Collection;

import controller.Simulation;
import io.DataCollection;
import io.PersistentDataProcessing;
import io.Statistics;

/**
 * Class for the end station. This is the last station where all customers are collected
 * 
 * @author Jaeger, Schmidt
 * @version 2017-10-26
 */
public final class EndStation extends SimpleStation {
	
	/** instance of the start station */
	@SuppressWarnings("unused")
	private static EndStation theEndStation;

	/** the station type for every endstation */
	private static final StationType ENDSTATIONTYPE = StationType.ENDE;

	/** the total amount of food wanted of all customers*/
	private static int totalAmountWantedFood;

	/** the total number of available EndStation objects */
	private static final int TOTALNUMBERENDSTATIONOBJECTS = 1;

	/** counter for the number of created EndStation objects */
	private static int counterEndStation = 0;
	
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
		super(label, inQueue, outQueue, xPos, yPos, image, ENDSTATIONTYPE);
	}
	
	/** creates a new end station
	 *	only one instance of EndStation can be created
	 * @param label of the station 
	 * @param inQueue the incoming queue
	 * @param outQueue the outgoing queue
	 * @param xPos x position of the station 
	 * @param yPos y position of the station 
	 * @param image image of the station  
	 */
	public static synchronized void create(String label, SynchronizedQueue inQueue, SynchronizedQueue outQueue, int xPos, int yPos,
										   String image) throws Exception{
		if(counterEndStation<TOTALNUMBERENDSTATIONOBJECTS) {
			theEndStation = new EndStation(label, inQueue, outQueue, xPos, yPos, image);
			counterEndStation++;
		}
		else{
			throw new Exception();
		}
		
	}

	/**
	 * Getter method for the total amount of wanted food
	 * @return totalAmountWantedFood
	 */
	public static int getTotalAmountWantedFood(){
		return totalAmountWantedFood;
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
			
			//  this is a just for fun action, the customer gets invisible
			customer.theView.setVisible(false);

			// add the amount of wanted food of the customer to the total, global amount of wanted food
			Collection<Integer> amountFood = customer.getCustomerFoodAmountAtStationsWanted().values();
			for(int amount: amountFood){
				totalAmountWantedFood += amount;
			}

			//End the simulation if the condition is met
			endSimulation();
	}
	
	
	/**
	 * End the simulation if the condition is met
	 */
	private void endSimulation(){
		// Are all customers in the stations outgoing queue, then we are finish
		if(Customer.getAllCustomers().size() == numberOfOutQueueCustomers()){

			//Set the static variable isRunning to false
			Simulation.isRunning = false;

			// calculate data collection
			DataCollection.calculateLoss();
			DataCollection.processOperatingCosts();

			//show a bar chart of the money loss
			PersistentDataProcessing.showMoneyloss();
			//show a bar chart of the operating costs
			PersistentDataProcessing.showOperatingCosts();

			Statistics.show("\n--- Simulation beendet ----");

			//show some station statistics
			for (MensaStation station : MensaStation.getAllMensaStations()) {
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
