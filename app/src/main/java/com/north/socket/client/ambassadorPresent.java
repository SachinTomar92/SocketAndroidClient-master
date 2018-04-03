package com.north.socket.client;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.List;

public class ambassadorPresent extends Activity {
    String[] typeofCommand;

    ListView historyview;
    RelativeLayout relativeLayout;
    Button historyButton;
    Button AddMishap;
    ConnectivityReceiver myReceiverpossn;
    String tmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambassador_present);
        myReceiverpossn = new ambassadorPresent.ConnectivityReceiver();
        IntentFilter filter = new IntentFilter("com.north.socket.client.POSSNBroadcastReceiverimplantdetail");
        registerReceiver(myReceiverpossn, filter);
        Bundle bundle = getIntent().getExtras();
        String jammed;

        final String[] typeofCommandHistory;
        String[] secondcommand;
        tmp = bundle.getString("data");
        //tmp = tmp.replace("$", "    ");
        historyButton = (Button) findViewById(R.id.historyButton);
        historyButton.setFocusable(true);
        historyButton.setActivated(true);
        AddMishap = (Button) findViewById(R.id.AddMishap);
        AddMishap.setVisibility(View.INVISIBLE);
        typeofCommandHistory = tmp.split("\\^");
        secondcommand = typeofCommandHistory[0] .split("\\|");
        secondcommand = secondcommand[0].split("\\$");
        final String[] thirdcommand = secondcommand[0].split("\\!");

        historyview = (ListView) findViewById(R.id.listView);
        if (thirdcommand[1].equals("10"))
        {
            AddMishap.setVisibility(View.VISIBLE);
        }
        relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout);
        historyview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                int ab = position;
                String [] inputlist;
                typeofCommand = typeofCommandHistory[1].split("\\|");
                String[] internals;
                internals = typeofCommand[ab].split("\\$");
                if (thirdcommand[1].equals("6"))
                {
                    String data = "";
                    data = "AMBDETAILREPORT|"+internals[3]+"|1|"+"a|";
                    Intent intent = new Intent();
                    intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                    intent.putExtra("sendchat", data);
                    sendBroadcast(intent);
                }
                if (thirdcommand[1].equals("7"))
                {
                    String data = "";
                    data = "AMBDETAILREPORT|"+internals[3]+"|2|"+"a|";
                    Intent intent = new Intent();
                    intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                    intent.putExtra("sendchat", data);
                    sendBroadcast(intent);
                }
                if (thirdcommand[1].equals("8"))
                {
                    String data = "";
                    data = "AMBDETAILREPORT|"+internals[3]+"|3|"+internals[4]+"|";
                    Intent intent = new Intent();
                    intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                    intent.putExtra("sendchat", data);
                    sendBroadcast(intent);
                }
                if (thirdcommand[1].equals("9"))
                {
                    String data = "";
                    data = "AMBDETAILREPORT|"+internals[3]+"|4|"+internals[4]+"|";
                    Intent intent = new Intent();
                    intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                    intent.putExtra("sendchat", data);
                    sendBroadcast(intent);
                }
                if (thirdcommand[1].equals("10"))
                {
                    String data = "";
                    data = "AMBDETAILREPORT|"+internals[0]+"|5|"+internals[1]+"|";
                    Intent intent = new Intent();
                    intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                    intent.putExtra("sendchat", data);
                    sendBroadcast(intent);
                }
                if (thirdcommand[1].equals("11"))
                {
                    String data = "";
                    data = "AMBDETAILREPORT|"+internals[0]+"|5|"+internals[1]+"|";
                    Intent intent = new Intent();
                    intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                    intent.putExtra("sendchat", data);
                    sendBroadcast(intent);
                }
//                Intent intent = new Intent(MainActivity.this, SendMessage.class);
//                String message = "abc";
//                intent.putExtra(EXTRA_MESSAGE, message);
//                startActivity(intent);
            }
        });
        if (bundle.getString("data") != null) {
            //historyview.setAdapter(new ArrayAdapter<String>(this, R.layout.mytextview, typeofCommandHistory));
            if ((thirdcommand[1].equals("6"))||(thirdcommand[1].equals("7"))||(thirdcommand[1].equals("8"))||(thirdcommand[1].equals("9"))||(thirdcommand[1].equals("10"))||(thirdcommand[1].equals("11"))||(thirdcommand[1].equals("12"))||(thirdcommand[1].equals("13"))||(thirdcommand[1].equals("14"))||(thirdcommand[1].equals("15"))) {
                final List<MyStringPair> myStringPairList = MyStringPair.makeData(typeofCommandHistory[1]);
                MyStringPairAdapter adapter = new MyStringPairAdapter(this, myStringPairList);

                historyview.setAdapter(adapter);
            }
        }


    }

    public void onResume()
    {
        super.onResume();
        if (myReceiverpossn == null) {
            myReceiverpossn = new ambassadorPresent.ConnectivityReceiver();
            IntentFilter filter = new IntentFilter("com.north.socket.client.POSSNBroadcastReceiverimplantdetail");
            registerReceiver(myReceiverpossn, filter);
        }
    }

    public void onStop()
    {
        if (myReceiverpossn != null) {

            try  {
                unregisterReceiver(myReceiverpossn);
                myReceiverpossn = null;
            }
            catch (IllegalArgumentException e) {
                // Check wether we are in debug mode

                e.printStackTrace();

            }
        }
        super.onStop();
    }
    public void onBackPressed() {
        finish();
    }

    public void addMishap(View v) {
        Intent activeview = new Intent(ambassadorPresent.this, ambassadorPresentdetail.class);
        activeview.putExtra("data", "mishap¤"+tmp);
        startActivityForResult(activeview, 1);
    }

    public void closeHistory(View v) {
        finish();
    }

    public class ConnectivityReceiver extends BroadcastReceiver {

        public ConnectivityReceiver() {
        }
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                String ambreport = intent.getStringExtra("ambdetailreport");
                if (ambreport != null) {
                    if (!ambreport.equals("none")) {
                        Intent activeview = new Intent(ambassadorPresent.this, ambassadorPresentdetail.class);
                        activeview.putExtra("data", "data¤"+ambreport);
                        startActivityForResult(activeview, 1);
                        //overlay.setVisibility(View.INVISIBLE);
                    } else {
                        //overlay.setVisibility(View.INVISIBLE);
                    }
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
