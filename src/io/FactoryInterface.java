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
	 * Setter for the scenario
	 * @param scenario the scenario
	 */
	static void setScenario(String scenario){};
}
