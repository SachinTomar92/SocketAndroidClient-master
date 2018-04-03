package com.north.socket.client;

import android.app.Application;
import android.content.Context;

public class ApplicationData extends Application {


    /*The application context statically available to the entire application */
    private static Context mContext = null;

    private static ApplicationData mApplicationData = null;


    public static final int font_size_amount_small = 40;
    public static final int font_size_amount_normal = 60;

    public static final int REQUEST_THRESHOLD_TIME = 2000;


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mContext = getApplicationContext();


    }

    public static Context getApplicationContex() {
        return Login.context;

    }
}





