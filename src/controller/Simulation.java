package controller;

import io.DataCollection;
import view.SelectionDialog;
import io.FactoryJSON;
import view.SimulationView;
import io.Factory;
import io.Statistics;
import java.util.concurrent.atomic.AtomicLong;
import model.Actor;


/**
 * The main class, controls the flow of the simulation
 * 
 * @author Jaeger, Schmidt
 * @version 2016-07-07
 */
public class Simulation {
	
	/** is the simulation running*/
	public static boolean isRunning = false;  
	
	/** a speed factor for the clock to vary the speed of the clock in a simple way*/
	public static int SPEEDFACTOR = 1;  
	
	/**the beat or speed of the clock, e.g. 300 means one beat every 300 milli seconds*/
	public static final int CLOCKBEAT = 250 * SPEEDFACTOR;
	
	/**the global clock */
	//the clock must be thread safe -> AtomicLong. The primitive type long isn't, even if synchronized
	private static AtomicLong clock = new AtomicLong(0); 
	
	
	/**
	 * Die Main-Methode der Anwendung.
	 */
	public static void main(String[] args){
		
		//a new simulation
		Simulation theSimulation = new Simulation();
		theSimulation.init();
		
	}
	
	/**
	 * initialize the simulation
	 * 
	 */
	private void init(){
		SelectionDialog selectionDialog = SelectionDialog.create();
		String[] selectedFormatAndScenario = selectionDialog.getSelected();
		//create all stations and customers for the starting scenario out of XML or JSON
		if(selectedFormatAndScenario[0].contains("xml")){
			Factory.setScenario(selectedFormatAndScenario[1]);
			Factory.createStartScenario();
		}
		else if(selectedFormatAndScenario[0].contains("json")){
			FactoryJSON.setScenario(selectedFormatAndScenario[1]);
			FactoryJSON.createStartScenario();
		}

		//reset all data files in DataOutput
		DataCollection.prepareDataCollection();
				
		//the view of our simulation
		new SimulationView();
					
		// set up the the heartbeat (clock) of the simulation
		new HeartBeat().start();
		 		
		Statistics.show("---- Simulation gestartet ---\n");
				
		// start all the actor threads
		for (Actor actor : Actor.getAllActors()) {
			actor.start();		
						
		}
		
		/*
		 * Hinweis: wenn nicht �ber den Startbutton gestartet werden soll oder die Simulation ohne View laufen soll,
		 * den auskommentierten Code unten verwenden 
		 */
				
		/*
		//Zeitpuffer vor Start -> sonst l�uft der letzte manchmal nicht los
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		//wake up the start station -> lets the simulation run
		StartStation.getStartStation().wakeUp();
		
		*/
		
	}
			
	
	/**
	 * The heartbeat (the pulse) of the simulation, controls the clock.
	 * 
	 */
	private class HeartBeat extends Thread {
		
		@Override
		public void run() {
			
			while(true){
				
				try {
				
					Thread.sleep(CLOCKBEAT);

					//updates the data collection
					DataCollection.updateNumberCustomersInQueue();
					
					//Increase the global clock
					clock.incrementAndGet();
					
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	
	/** Get the global time
	 * 
	 * @return the global time
	 */
	public static long getGlobalTime() {
		return clock.get();
	}
	
}
