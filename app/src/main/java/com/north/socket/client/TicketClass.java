package com.north.socket.client;

import java.util.ArrayList;

/**
 * Created by sachintomar on 26/08/17.
 */

public class TicketClass {

    String ticketID;
    String ticketText;
    String innerText;
    String numberOfField;

    static ArrayList<TicketClass> list = list = new ArrayList<TicketClass>();

    TicketClass(String ticketID, String ticketText, String innerText, String numberOfField) {
        this.ticketID = ticketID;
        this.ticketText = ticketText;
        this.innerText = innerText;
        this.numberOfField = numberOfField;
    }


    public static void addTicket(String ticketDetails){
        list.clear();
        String[] firstBrk = ticketDetails.split("\\|");
        for(int i = 0; i<firstBrk.length; i++){
            String[] secondBrk = firstBrk[i].split("\\$");
            if(secondBrk.length>3) {
                list.add(new TicketClass(secondBrk[0], secondBrk[1], secondBrk[2], secondBrk[3]));
            }
        }
        System.out.println(list);
    }
}
