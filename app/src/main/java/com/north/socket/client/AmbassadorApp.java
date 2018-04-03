package com.north.socket.client;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/*
 * Created by sree on 12/02/16.
 */
public class AmbassadorApp extends Application {
    /**
     * Keeps a reference of the application context
     */
    private static Context sContext;
    ConnectivityReceiver myReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        //sContext = getApplicationContext();
        this.sContext = this;

        myReceiver = new ConnectivityReceiver();
        IntentFilter gpsFilter = new IntentFilter("android.location.PROVIDERS_CHANGED");
        registerReceiver(myReceiver, gpsFilter);
    }

    /**
     * Returns the application context
     *
     * @return application context
     */
    public static Context getContext() {
        return sContext;
    }

    public class ConnectivityReceiver extends BroadcastReceiver {
        public ConnectivityReceiver() {
            System.out.println("ConnectivityReceiver Ambassador File");
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                System.out.println("State changed Ambassador File");
            }
        }
    }
}
