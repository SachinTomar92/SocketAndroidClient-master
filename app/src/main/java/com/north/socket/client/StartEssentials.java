package com.north.socket.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class StartEssentials extends Activity {

    static final String BROADCAST_ACTION = "com.north.socket.client";
    private Intent intent_broadcast;

    String[] queuestate;
    private Button startessential;

    private ImageView ledbutton;
    private Button banned;
    int varbanned = 0;
    int varbusy = 0;
    String logininfo;

    IntentFilter filter;
    ConnectivityReceiver myReceiver;

    static Context startEssContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_essentials);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().hide();

        startEssContext = this;

        myReceiver = new ConnectivityReceiver();
        filter = new IntentFilter("com.north.socket.client.EssentialsNet");
        registerReceiver(myReceiver, filter);

        System.out.println("SocketService.startEssentialsCheck" + SocketService.startEssentialsCheck);

        Bundle bundle = getIntent().getExtras();
        startessential = (Button) findViewById(R.id.startessential);

        if(bundle.getString("qdata")!= null) {
            String s = bundle.getString("qdata");

            startessential.setText(s);
        }
    }





    public void onBackPressed() {
        // do nothing.
    }
    @Override
    public void onPause() {

        if (myReceiver != null) {

            try  {
                unregisterReceiver(myReceiver);
                myReceiver = null;
            }
            catch (IllegalArgumentException e) {
                // Check wether we are in debug mode

                e.printStackTrace();

            }
        }
        super.onPause();
    }

    protected void onDestroy()
    {
        if (myReceiver != null) {

            try  {
                unregisterReceiver(myReceiver);
                myReceiver = null;
            }
            catch (IllegalArgumentException e) {
                // Check wether we are in debug mode

                e.printStackTrace();

            }
        }
        super.onDestroy();
    }



    public class ConnectivityReceiver extends BroadcastReceiver {

        public ConnectivityReceiver() {
        }
        @Override
        public void onReceive(Context context, Intent intent) {
        try {

            String ambreport = intent.getStringExtra("ambreport");
            if (ambreport != null)
            {
                Intent activeview = new Intent(StartEssentials.this, ListHistory.class);
                activeview.putExtra("data", ambreport);
                startActivityForResult(activeview, 1);
            }
            String wifiEnabled = intent.getStringExtra("wifiEnabled");
            if(wifiEnabled != null){
                SocketService.startEssentialsCheck = 0;
                finish();
            }
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        }

    }
}
