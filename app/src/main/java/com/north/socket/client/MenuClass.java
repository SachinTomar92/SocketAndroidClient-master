package com.north.socket.client;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by sachintomar on 27/09/17.
 */

public class MenuClass {

    static String ambId = "0";
    String menuItemID;
    String menuTitle;
    static String loginId = "";
    static float ratingAmb = 3;
    static Bitmap ambImage;
    static String MSwipeUserName;
    static String MSWipePassword;
    static int ezetap = 0;
    static ArrayList<MenuClass> menuList = new ArrayList<>();


    static String reportHeading = "";

    MenuClass(String menuItemID, String menuTitle){
        this.menuItemID = menuItemID;
        this.menuTitle = menuTitle;
    }
    public static void addMenu(String menuString){
        menuList.clear();
        System.out.println(menuString);
        String[] firstBrk = menuString.split("\\|");
        for(int i = 0; i < firstBrk.length; i++){
            String[] secBrk = firstBrk[i].split("\\$");
            menuList.add(new MenuClass(secBrk[0], secBrk[1]));
        }
        System.out.println(menuList);
    }
    public static boolean ambImageStatus(){
        if(ambImage == null){
            return false;
        }
        return true;
    }
}
