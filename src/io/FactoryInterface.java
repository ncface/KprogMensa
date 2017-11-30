package io;

/**
 * Interface for the factories
 */
public interface FactoryInterface {
	/**
	 * creates the startscenario
	 */
	static void createStartScenario() {};

	/**
	 * Setter for the scenariopath
	 * @param scenario the scenario
	 */
	static void setScenario(String scenario){};
}
