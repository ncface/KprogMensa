package io;

/**
 * Interface for the factories
 */
public interface FactoryInterface {
	/**
	 * creates the startscenario
	 */
	public void createStartScenario();

	/**
	 * Setter for the scenariopath
	 * @param scenario the scenario
	 */
	public void setScenario(String scenario);
}
