package com.north.socket.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

class ExpandableListDataPump {
    public static HashMap<String, List<String>> getData() {
        LinkedHashMap<String, List<String>> expandableListDetail = new LinkedHashMap<String, List<String>>();

        for(int i = 0; i < TakingComplaints.category.length; i++){
            List<String> cricket = new ArrayList<String>();

            for(int j = 0;j < TakingComplaints.subCategry.length; j++ ){
                if(TakingComplaints.category[i][0].equals(TakingComplaints.subCategry[j][0])){
                    cricket.add(TakingComplaints.subCategry[j][2]);
                }
            }
            expandableListDetail.put(TakingComplaints.category[i][1], cricket);
        }
        return expandableListDetail;
    }
}