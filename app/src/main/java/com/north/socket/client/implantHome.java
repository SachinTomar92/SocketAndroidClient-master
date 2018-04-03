package com.north.socket.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class implantHome extends Activity {

    String[] typeofCommand;
    String[] queuestate;
    String s;
    private Button refreshbut;
    private Button button;
    private TextView info;
    int mswipe = 0;
    int ezetap = 0;
    private Button callsupport;
    private Button btnback;

    private EditText data1txt;
    private EditText data2txt;
    private Button btndata;
    private int chatticketnumber;
    private int totalamount;

    private EditText addresstxt;
    private Button btnaddress;

    private EditText nametxt;
    private Button btnname;

    private EditText cartxt;
    private Button btncar;
    private String latlongtosubmit;
    private EditText convtxt;
    private EditText convreasontxt;
    private TextView ambPresent;
    private TextView ambRating;
    private Button btnconv;
    private RelativeLayout relativeLayout;
    ConnectivityReceiver myReceiverpossn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        latlongtosubmit="";
        myReceiverpossn = new ConnectivityReceiver();
        IntentFilter filter = new IntentFilter("com.north.socket.client.POSSNBroadcastReceiverimplant");
        registerReceiver(myReceiverpossn, filter);
        ezetap = 1;
        setContentView(R.layout.activity_implant_home);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().hide();
        button = (Button) findViewById(R.id.button);
        info = (TextView) findViewById(R.id.info);
        ambPresent = (TextView)  findViewById(R.id.ambPresent);
        ambRating = (TextView) findViewById(R.id.ambRating);
        //callsupport = (Button) findViewById(R.id.button11);

        Bundle bundle = getIntent().getExtras();

        relativeLayout = (RelativeLayout) findViewById(R.id.relLayout);
        int state=0;
        if(bundle.getString("qdata")!= null) {
            s = bundle.getString("qdata");
            typeofCommand = s.split("\\|");
            queuestate = typeofCommand[0].split("\\$");
            int totalamb = Integer.parseInt(queuestate[0])+Integer.parseInt(queuestate[1]);
            ambPresent.setText(String.valueOf(queuestate[0])+" of "+String.valueOf(totalamb));
            ambRating.setText(queuestate[2]);
        }
    }

    public void viewAttendance(View v) {
       // displayOverlay();
        String data = "";
        data = "AMBREPORT|6|";
        Intent intent = new Intent();
        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
        intent.putExtra("sendchat", data);
        sendBroadcast(intent);
    }

    public void viewRating(View v) {
        // displayOverlay();
        String data = "";
        data = "AMBREPORT|7|";
        Intent intent = new Intent();
        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
        intent.putExtra("sendchat", data);
        sendBroadcast(intent);
    }

    public void viewCheque(View v) {
        // displayOverlay();
        String data = "";
        data = "AMBREPORT|8|";
        Intent intent = new Intent();
        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
        intent.putExtra("sendchat", data);
        sendBroadcast(intent);
    }

    public void viewCash(View v) {
        // displayOverlay();
        String data = "";
        data = "AMBREPORT|9|";
        Intent intent = new Intent();
        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
        intent.putExtra("sendchat", data);
        sendBroadcast(intent);
    }

    public void viewMishap(View v) {
        // displayOverlay();
        String data = "";
        data = "AMBREPORT|10|";
        Intent intent = new Intent();
        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
        intent.putExtra("sendchat", data);
        sendBroadcast(intent);
    }

    public void viewComplaints(View v) {
        // displayOverlay();
        String data = "";
        data = "AMBREPORT|11|";
        Intent intent = new Intent();
        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
        intent.putExtra("sendchat", data);
        sendBroadcast(intent);
    }

    public void viewCashDeposit(View v) {
        // displayOverlay();
        String data = "";
        data = "AMBREPORT|12|";
        Intent intent = new Intent();
        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
        intent.putExtra("sendchat", data);
        sendBroadcast(intent);
    }

    public void viewPhotos(View v) {
        // displayOverlay();
        String data = "";
        data = "AMBREPORT|13|";
        Intent intent = new Intent();
        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
        intent.putExtra("sendchat", data);
        sendBroadcast(intent);
    }

    public void viewMissed(View v) {
        // displayOverlay();
        String data = "";
        data = "AMBREPORT|14|";
        Intent intent = new Intent();
        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
        intent.putExtra("sendchat", data);
        sendBroadcast(intent);
    }

    public void viewEquipment(View v) {
        // displayOverlay();
        String data = "";
        data = "AMBREPORT|15|";
        Intent intent = new Intent();
        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
        intent.putExtra("sendchat", data);
        sendBroadcast(intent);
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


    public class ConnectivityReceiver extends BroadcastReceiver {

        public ConnectivityReceiver() {
        }
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                String ambreport = intent.getStringExtra("ambreport");
                if (ambreport != null) {
                    if (!ambreport.equals("none")) {
                        Intent activeview = new Intent(implantHome.this, ambassadorPresent.class);
                        activeview.putExtra("data", ambreport);
                        startActivityForResult(activeview, 1);
                        //overlay.setVisibility(View.INVISIBLE);
                    } else {
                        //overlay.setVisibility(View.INVISIBLE);
                    }
                }
                String latlong;
                latlong = intent.getStringExtra("latlong");
                if (latlong != null) {
                    latlongtosubmit = latlong;
//                    button.setAlpha(1.0f);
  //                  button.setEnabled(true);
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
