package com.north.socket.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DrivingTest extends Activity {

    int phototaken = 0;
    String[] queuestate;
    private TextView drivingAddress;
    private Button refreshbut;
    private Handler overlayTimer = new Handler();
    private FrameLayout overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivingtest);
        // getActionBar().setDisplayHomeAsUpEnabled(true);
        // getActionBar().hide();
        overlay = (FrameLayout) findViewById(R.id.overlay);
        overlay.setVisibility(View.INVISIBLE);

        drivingAddress = (TextView) findViewById(R.id.newAddress);
        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("qdata")!= null) {
            String s = bundle.getString("qdata");
            queuestate = s.split("\\$");
            drivingAddress.setText(queuestate[12]);
//            drivingAddress.setText("This is a really big addres. It reads like this, would you belive it. And it is so long and it has a phone number 0803131313 and more information is neeeed");
            drivingAddress.setTextColor(Color.WHITE);
        }
    }

    public void displayOverlay() {
        overlay.setVisibility(View.VISIBLE);
        overlayTimer.removeCallbacksAndMessages(null);
        overlayTimer.postDelayed(overlayTimeout, 8000);
    }

    private Runnable overlayTimeout = new Runnable() {
        @Override
        public void run() {
            try{
                overlay.setVisibility(View.INVISIBLE);
                overlayTimer.removeCallbacksAndMessages(null);
            }
            catch(Exception e){
                // added try catch block to be sure of uninterupted execution
            }

      /* and here comes the "trick" */
            //handler.postDelayed(this, 1000);
        }
    };

    public void callRefreshattend(View v)
    {
        System.exit(2);
    }

    public void onBackPressed() {
        // do nothing.
    }






    protected void onDestroy()
    {
        try
        {
            // Intent intent = new Intent();
            // intent.setAction("com.north.socket.client.BroadcastReceiver");
            // intent.putExtra("senddata", "LGOUT");
            // sendBroadcast(intent);
            // System.out.println("onDestroy.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onDestroy();
    }




}
