package com.north.socket.client;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.widget.FrameLayout;

public class LoadingPage extends Activity {

    private Handler overlayTimer = new Handler();
    LoadingPageConnectivityReceiver myReceiverAccept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_page);

        myReceiverAccept = new LoadingPageConnectivityReceiver();
        IntentFilter filter = new IntentFilter("com.north.socket.client.LoadingPageReceiver");
        registerReceiver(myReceiverAccept, filter);


        overlayTimer.removeCallbacksAndMessages(null);
        overlayTimer.postDelayed(overlayTimeout, 10000);
    }

    private Runnable overlayTimeout = new Runnable() {
        @Override
        public void run() {
            try {
                overlayTimer.removeCallbacksAndMessages(null);
                finish();
            } catch (Exception e) {
                // added try catch block to be sure of uninterupted execution
            }

      /* and here comes the "trick" */
            //handler.postDelayed(this, 1000);
        }
    };

    public void onResume() {
        super.onResume();
        if (myReceiverAccept == null) {
            myReceiverAccept = new LoadingPageConnectivityReceiver();
            IntentFilter filter = new IntentFilter("com.north.socket.client.LoadingPageReceiver");
            registerReceiver(myReceiverAccept, filter);
        }
    }

    public void onStop() {
        if (myReceiverAccept != null) {

            try {
                unregisterReceiver(myReceiverAccept);
                myReceiverAccept = null;
            } catch (IllegalArgumentException e) {
                // Check wether we are in debug mode

                e.printStackTrace();

            }
        }
        super.onStop();
    }

    private class LoadingPageConnectivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String stopLoading = intent.getStringExtra("stopOverLay");
            if(stopLoading != null){
                finish();
            }

        }
    }
}
