package model;

/**
 * defines several types of Stations
 */
public enum StationType {
    VORSPEISE, HAUPTSPEISE, NACHSPEISE, KASSE, DEFAULT, START, ENDE;

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
