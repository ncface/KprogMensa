package model;

/**
 * defines several types of Stations with their InQueueLimit
 * @author Patrick Hanselmann, Sebastian Herzog, Jeffrey Manuel Rietzler, Nils Clauss
 * @version 2017-11-28
 */
public enum StationType {
    VORSPEISE(0), HAUPTSPEISE(0), NACHSPEISE(0), KASSE(0), DEFAULT(0), START(0), ENDE(0), ADDITIONAL(0);

    /** the limit for customers in the incoming queue before opening an additional station*/
    private int inQueueLimit;

    /**
     * set incoming queue limit for the station types
     * @param inQueueLimit the incoming queue limit
     */
    StationType(int inQueueLimit){
        this.inQueueLimit = inQueueLimit;
    }

    /**
     * parser for StationTypes
     * @param str the Name
     * @return the StationType corresponding to the InputString,
     * if no Type fits DEFAULT is returned
     */
    public static StationType parseStationType(String str){
        for(StationType type: StationType.values()){
            if(str.toUpperCase().equals(type.toString())){
                return type;
            }
        }
        //creates type even if there is no matching string
        return DEFAULT;
    }

    /**
     * setter for the InQueueLimit
     * @param inQueueLimit the new inQueueLimit
     */
    public void setInQueueLimit(int inQueueLimit) {
        this.inQueueLimit = inQueueLimit;
    }

    /**
     * getter for the inQueueLimit
     * @return the inQueueLimit
     */
    public int getInQueueLimit() {
        return inQueueLimit;
    }
}
