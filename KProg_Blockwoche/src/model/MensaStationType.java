package model;

public enum MensaStationType {
    VORSPEISE, HAUPTGERICHT, NACHTISCH, KASSE, DEFAULT;

    public static MensaStationType parseMensaStationType(String str){
        for(MensaStationType type: MensaStationType.values()){
            if(str.toUpperCase().equals(type.toString())){
                return type;
            }
        }
        return DEFAULT;
    }
}
