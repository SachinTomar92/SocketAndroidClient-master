package com.north.socket.client;

import java.util.ArrayList;
import java.util.List;

public class MyStringPair {

    private String columnOne;
    private String columnTwo;
    private String columnThree;

    
    public MyStringPair(String columnOne, String columnTwo, String columnThree) {
        super();
        this.columnOne = columnOne;
        this.columnTwo = columnTwo;
        this.columnThree = columnThree;
    }
    
    public String getColumnOne() {
        return columnOne;
    }
    public String getColumnThree() {
        return columnThree;
    }
    public void setColumnOne(String columnOne) {
        this.columnOne = columnOne;
    }
    public void setColumnThree(String columnThree) {
        this.columnThree = columnThree;
    }
    public String getColumnTwo() {
        return columnTwo;
    }
    public void setColumnTwo(String columnTwo) {
        this.columnTwo = columnTwo;
    }
    
    public static List<MyStringPair> makeData(String inputString) {
        String[] typeofCommand;
        typeofCommand = inputString.split("\\|");
        int n = 10;
        List<MyStringPair> pair = new ArrayList<MyStringPair>();
        for (int i=0;i<typeofCommand.length;i++) {
            String[] internals;
            internals = typeofCommand[i].split("\\$");
            pair.add(new MyStringPair(internals[0], internals[1], internals[2] ));
        }
        return pair;
    }

}
