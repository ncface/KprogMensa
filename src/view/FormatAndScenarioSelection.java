package view;

/**
 * This Interface offers a method to get the desired input format and the selected scenario
 * @author Hanselmann, Rietzler, Clauss, Herzog
 * @version 4.12.17
 */
public interface FormatAndScenarioSelection {

    /**
     * this method return the selected input format and the selected scenario in an String array
     * @return at String[0] the selected format and at String[1] the selected scenario
     */
    String[] getSelectedFormatAndScenarion();
}
