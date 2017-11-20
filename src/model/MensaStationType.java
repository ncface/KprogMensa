package model;

/**
 * defines several types of MensaStations
 */
public enum MensaStationType {
    VORSPEISE, HAUPTSPEISE, NACHTISCH, KASSE, DEFAULT;

    /**
     * parser for MesaStationTypes
     * @param str the Name
     * @return the MensaStationType corresponding to the InputString,
     * if no Type fits DEFAULT is returned
     */
    public static MensaStationType parseMensaStationType(String str){
        for(MensaStationType type: MensaStationType.values()){
            if(str.toUpperCase().equals(type.toString())){
                return type;
            }
        }
        return DEFAULT;
    }
}
