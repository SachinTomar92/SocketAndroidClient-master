package com.north.socket.client;

import java.util.ArrayList;

/**
 * Created by sachintomar on 16/09/17.
 */

public class MenuReports {
    String buttonTitle;
    String buttonId;

    static ArrayList<MenuReports> reportList = new ArrayList<>();

    MenuReports(String buttonId, String buttonTitle){
        this.buttonId = buttonId;
        this.buttonTitle = buttonTitle;
    }

}
