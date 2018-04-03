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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ambassadorPresentdetail extends Activity {
    String[] typeofCommand;

    ListView historyview;
    RelativeLayout relativeLayout;
    Button historyButton;
    Button chequeButton;
    Button cashButton;
    EditText queueid;
    EditText mishapreason;
    EditText amount;
    Button addMishap;

    TextView queueidtext;
    TextView amounttext;
    TextView reasontext;

    Spinner reasonspinner;
    Spinner ambspinner;
    String [] reasonfunctions;
    String []  chequecom;
    String[] secondcommand;
//    ConnectivityReceiver myReceiverpossn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambassador_present_detail);
//        myReceiverpossn = new ambassadorPresentdetail.ConnectivityReceiver();
//        IntentFilter filter = new IntentFilter("com.north.socket.client.POSSNBroadcastReceiverimplantdetail");
//        registerReceiver(myReceiverpossn, filter);

        reasonspinner = (Spinner) findViewById(R.id.spinner);
        historyview = (ListView) findViewById(R.id.listView);
        historyButton = (Button) findViewById(R.id.historyButton);
        historyButton.setFocusable(true);
        historyButton.setActivated(true);

        chequeButton = (Button) findViewById(R.id.chequeButton);
        chequeButton.setFocusable(true);
        chequeButton.setActivated(true);
        cashButton = (Button) findViewById(R.id.cashButton);
        cashButton.setFocusable(true);
        cashButton.setActivated(true);

        addMishap = (Button) findViewById(R.id.addMishap);


        queueidtext = (TextView) findViewById(R.id.queueidtext);
        amounttext = (TextView) findViewById(R.id.amounttext);
        reasontext = (TextView) findViewById(R.id.reasontext);
        queueid = (EditText) findViewById(R.id.queueid);;
        mishapreason = (EditText) findViewById(R.id.mishapReason);;
        ambspinner = (Spinner) findViewById(R.id.ambspinner);
        amount = (EditText) findViewById(R.id.amount);;


        addMishap.setVisibility(View.INVISIBLE);
        queueidtext.setVisibility(View.INVISIBLE);
        amounttext.setVisibility(View.INVISIBLE);
        reasontext.setVisibility(View.INVISIBLE);
        historyButton.setVisibility(View.INVISIBLE);
        reasonspinner.setVisibility(View.INVISIBLE);
        ambspinner.setVisibility(View.INVISIBLE);
        chequeButton.setVisibility(View.INVISIBLE);
        cashButton.setVisibility(View.INVISIBLE);
        queueid.setVisibility(View.INVISIBLE);
        mishapreason.setVisibility(View.INVISIBLE);
        amount.setVisibility(View.INVISIBLE);

        Bundle bundle = getIntent().getExtras();
        String jammed;
        String tmp;
        final String[] typeofCommandHistory;

        tmp = bundle.getString("data");

        final String[] typeofCommand;
        typeofCommand = tmp.split("\\¤");
        //tmp = tmp.replace("$", "    ");
        if (typeofCommand[0].equals("mishap"))
        {
            addMishap.setVisibility(View.VISIBLE);
            queueidtext.setVisibility(View.VISIBLE);
            amounttext.setVisibility(View.VISIBLE);
            reasontext.setVisibility(View.VISIBLE);
            typeofCommandHistory = typeofCommand[1].split("\\^");
            secondcommand = typeofCommandHistory[2].split("\\|");
            queueid.setVisibility(View.VISIBLE);
            mishapreason.setVisibility(View.VISIBLE);
            reasonspinner.setVisibility(View.VISIBLE);
            ambspinner.setVisibility(View.VISIBLE);
            amount.setVisibility(View.VISIBLE);
            List<String> spinnerArrayamb = new ArrayList<String>();
            for (int i = 0; i < secondcommand.length; i++) {
                String[] reasonlist = secondcommand[i].split("\\$");

                spinnerArrayamb.add(reasonlist[1]);

            }

            ArrayAdapter<String> adapterspinneramb = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, spinnerArrayamb);

            adapterspinneramb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ambspinner.setAdapter(adapterspinneramb);

            List<String> spinnerArray = new ArrayList<String>();
//            for (int i = 0; i < reasons.length; i++) {
//                String[] reasonlist = reasons[i].split("\\$");
//
//                spinnerArray.add(reasonlist[1]);
//
//            }
            spinnerArray.add("Ambassador Mistake");
            spinnerArray.add("Third Party Mistake");

            ArrayAdapter<String> adapterspinner = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, spinnerArray);

            adapterspinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            reasonspinner.setAdapter(adapterspinner);
        }
        else {


            typeofCommandHistory = typeofCommand[1].split("\\^");
            secondcommand = typeofCommandHistory[0].split("\\|");
            secondcommand = secondcommand[0].split("\\$");
            final String[] reasons = typeofCommandHistory[1].split("\\|");
            reasonfunctions = reasons;
            if (typeofCommandHistory.length > 2) {
                final String[] chequecommand = typeofCommandHistory[2].split("\\|");
                chequecom = chequecommand;
                final String[] thirdcommand = secondcommand[0].split("\\!");

                if (thirdcommand[1].equals("1")) {
                    historyButton.setVisibility(View.VISIBLE);
                    reasonspinner.setVisibility(View.VISIBLE);

                }
                if (thirdcommand[1].equals("3")) {
                    chequeButton.setVisibility(View.VISIBLE);
                    reasonspinner.setVisibility(View.VISIBLE);
                }
                if (thirdcommand[1].equals("4")) {
                    reasonspinner.setVisibility(View.VISIBLE);
                    cashButton.setVisibility(View.VISIBLE);
                }

                relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout);
                historyview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {

//                int ab = position;
//                String [] inputlist;
//                typeofCommand = typeofCommandHistory[1].split("\\|");
//                String[] internals;
//                internals = typeofCommand[ab].split("\\$");
//                if (thirdcommand[1].equals("6"))
//                {
//                    String data = "";
//                    data = "AMBDETAILREPORT|"+internals[3]+"|1|";
//                    Intent intent = new Intent();
//                    intent.setAction("com.north.socket.client.LBBroadcastReceiver");
//                    intent.putExtra("sendchat", data);
//                    sendBroadcast(intent);
//                }


//                Intent intent = new Intent(MainActivity.this, SendMessage.class);
//                String message = "abc";
//                intent.putExtra(EXTRA_MESSAGE, message);
//                startActivity(intent);
                    }
                });
                if (bundle.getString("data") != null) {
                    //historyview.setAdapter(new ArrayAdapter<String>(this, R.layout.mytextview, typeofCommandHistory));
                    //if ((thirdcommand[1].equals("6"))||(thirdcommand[1].equals("7"))||(thirdcommand[1].equals("8"))||(thirdcommand[1].equals("9"))||(thirdcommand[1].equals("10"))||(thirdcommand[1].equals("11"))||(thirdcommand[1].equals("12"))||(thirdcommand[1].equals("13"))||(thirdcommand[1].equals("14"))||(thirdcommand[1].equals("15"))) {
                    List<String> spinnerArray = new ArrayList<String>();
                    for (int i = 0; i < reasons.length; i++) {
                        String[] reasonlist = reasons[i].split("\\$");

                        spinnerArray.add(reasonlist[1]);

                    }

                    ArrayAdapter<String> adapterspinner = new ArrayAdapter<String>(
                            this, android.R.layout.simple_spinner_item, spinnerArray);

                    adapterspinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    reasonspinner.setAdapter(adapterspinner);

                    if (typeofCommandHistory.length > 2) {
                        final List<MyStringPair> myStringPairList = MyStringPair.makeData(typeofCommandHistory[2]);
                        MyStringPairAdapter adapter = new MyStringPairAdapter(this, myStringPairList);

                        historyview.setAdapter(adapter);

                    }
                    // }
                }
            }
            else
            {
                finish();
            }
        }


    }

    public void addMishap(View v)
    {
        //String[] reasonlist = reasonfunctions[reasonspinner.getSelectedItemPosition()].split("\\$");
        int reason = reasonspinner.getSelectedItemPosition();
        reason = reason + 1;
        int ambreason = ambspinner.getSelectedItemPosition();
        //secondcommand[ambreason];
        String[] reasonlist = secondcommand[ambreason].split("\\$");
        String data = "";
        if ((queueid.getText().length() == 0)||(amount.getText().length() == 0)||(mishapreason.getText().length() == 0))
        {
            return;
        }
        data = "MISHAPCREATE|"+reasonlist[0]+"|"+String.valueOf(reason)+"|"+queueid.getText()+"|"+amount.getText()+"|"+mishapreason.getText()+"|";
        Intent intent = new Intent();
        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
        intent.putExtra("sendchat", data);
        sendBroadcast(intent);
        finish();
    }

    public void updateAttendance(View v)
    {
        String[] reasonlist = reasonfunctions[reasonspinner.getSelectedItemPosition()].split("\\$");
        String data = "";
                    data = "AMBABSENTREASON|"+reasonlist[2]+"|"+reasonlist[0]+"|1|";
                    Intent intent = new Intent();
                    intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                    intent.putExtra("sendchat", data);
                    sendBroadcast(intent);
                    finish();
    }

    public void updateCheque(View v)
    {
        String[] reasonlist = reasonfunctions[reasonspinner.getSelectedItemPosition()].split("\\$");
        String [] chequelist = chequecom[0].split("\\$");
        String data = "";
        data = "AMBABSENTREASON|"+chequelist[4]+"|"+reasonlist[0]+"|2|";
        Intent intent = new Intent();
        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
        intent.putExtra("sendchat", data);
        sendBroadcast(intent);
        finish();
    }

    public void updateCash(View v)
    {
        String[] reasonlist = reasonfunctions[reasonspinner.getSelectedItemPosition()].split("\\$");
        String [] chequelist = chequecom[0].split("\\$");
        String data = "";
        data = "AMBABSENTREASON|"+chequelist[4]+"|"+reasonlist[0]+"|3|";
        Intent intent = new Intent();
        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
        intent.putExtra("sendchat", data);
        sendBroadcast(intent);
        finish();
    }

    public void onBackPressed() {
        finish();
    }

    public void closeHistory(View v) {
        finish();
    }

}
