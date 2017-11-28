package model;

/**
 * defines several types of Stations
 * @author Patrick Hanselmann, Sebastian Herzog, Jeffrey Manuel Rietzler, Nils Clauss
 * @version 2017-11-28
 */
public enum StationType {
    VORSPEISE(0), HAUPTSPEISE(0), NACHSPEISE(0), KASSE(0), DEFAULT(0), START(0), ENDE(0), EXTRA(0);

    /**
     * parser for StationTypes
     * @param str the Name
     * @return the StationType corresponding to the InputString,
     * if no Type fits DEFAULT is returned
     */
    private int inQueueLimit;

    StationType(int inQueueLimit){
        this.inQueueLimit = inQueueLimit;
    }

    public static StationType parseStationType(String str){
        for(StationType type: StationType.values()){
            if(str.toUpperCase().equals(type.toString())){
                return type;
            }
        }
        return DEFAULT;
    }

    public void setInQueueLimit(int inQueueLimit) {
        this.inQueueLimit = inQueueLimit;
    }

    public int getInQueueLimit() {
        return inQueueLimit;
    }
}
