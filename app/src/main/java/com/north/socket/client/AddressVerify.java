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

public class AddressVerify extends Activity {

    int phototaken = 0;
    String[] queuestate;
    private EditText pin;
    private TextView oldAddress;
    private TextView newAddress;
    private Button refreshbut;
    private Handler overlayTimer = new Handler();
    private FrameLayout overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addressverify);
       // getActionBar().setDisplayHomeAsUpEnabled(true);
       // getActionBar().hide();
        overlay = (FrameLayout) findViewById(R.id.overlay);
        overlay.setVisibility(View.INVISIBLE);

        pin = (EditText) findViewById(R.id.PIN);
        oldAddress = (TextView) findViewById(R.id.oldAddress);
        newAddress = (TextView) findViewById(R.id.newAddress);
        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("qdata")!= null) {
            String s = bundle.getString("qdata");
            queuestate = s.split("\\$");
            oldAddress.setText(queuestate[8]);
            newAddress.setText(queuestate[10]);
            pin.setText("");
            pin.setFilters(new InputFilter[] {new InputFilter.AllCaps()});


            pin.setTextColor(Color.WHITE);
            pin.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            //imm.showSoftInput(pin, InputMethodManager.SHOW_IMPLICIT);

            oldAddress.setTextColor(Color.WHITE);
            newAddress.setTextColor(Color.WHITE);
        }
        if(bundle.getString("ambAddress")!=null){
            String s = bundle.getString("ambAddress");

            oldAddress.setText(s);
            newAddress.setText(s);
            pin.setText("");
            pin.setFilters(new InputFilter[] {new InputFilter.AllCaps()});


            pin.setTextColor(Color.WHITE);
            pin.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            //imm.showSoftInput(pin, InputMethodManager.SHOW_IMPLICIT);

            oldAddress.setTextColor(Color.WHITE);
            newAddress.setTextColor(Color.WHITE);
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
        finish();
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


    public void SignIn(View v) {
        if (pin.getText().length()>5) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            //int state = Integer.parseInt(typeofCommand[0]);

            alertDialogBuilder.setMessage("Are you sure you want to Verify your Address?");
            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            displayOverlay();
                            String data = "AMBVERIFYADDRESS|"+pin.getText()+"|";
                            Intent intent = new Intent();
                            intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                            intent.putExtra("sendchat", data);
                            sendBroadcast(intent);
                            //finish();
                        }
                    });

            alertDialogBuilder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });


            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

}
