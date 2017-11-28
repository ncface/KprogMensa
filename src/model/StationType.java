package model;

/**
 * defines several types of Stations
 * @author Patrick Hanselmann, Sebastian Herzog, Jeffrey Manuel Rietzler, Nils Clauss
 * @version 2017-11-28
 */
public enum StationType {
    VORSPEISE, HAUPTSPEISE, NACHSPEISE, KASSE, DEFAULT, START, ENDE, EXTRA;

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
        return DEFAULT;
    }
}
