package com.wallstft.zookeeper.zkEnum;

public enum zkAction {
    Add, Update, Delete, Unknown;


    public String toString(zkAction action) {
        switch (action) {
            case Add:
                return "Add";
            case Update:
                return "Update";
            case Delete:
                return "Delete";
            default:
                return "Unknown";
        }
    };

    public zkAction stringToEnum ( String val ){
        if ( val != null && val.equalsIgnoreCase("add"))
            return Add;
        else if ( val != null && val.equalsIgnoreCase("update"))
            return Update;
        else if ( val != null && val.equalsIgnoreCase("delete"))
            return Delete;
        else
           return Unknown;
    }
}


