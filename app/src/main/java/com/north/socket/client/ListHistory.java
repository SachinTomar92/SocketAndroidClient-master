package com.north.socket.client;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListHistory extends Activity {
    String[] typeofCommand;

    ListView ambassadorPresent;
    RelativeLayout relativeLayout;
    ConnectivityReceiver myReceiverpossn;
    Button historyButton;
    Button addMishap;
    TextView topHeadingTextView;
    Context listContext;
    String[] headings;
    String[][] detailsArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myReceiverpossn = new ConnectivityReceiver();
        IntentFilter filter = new IntentFilter("com.north.socket.client.POSSNBroadcastListHistory");
        registerReceiver(myReceiverpossn, filter);
        setContentView(R.layout.activity_ambassador_present);

        listContext = this;

        Bundle bundle = getIntent().getExtras();
        String tmp;
        tmp = bundle.getString("data");
        if(tmp != null) {
            String[] multiplerecordset = tmp.split("\\^");
            if (multiplerecordset.length == 0) {
                finish();
            } else {
                final String[] newrecord = multiplerecordset[1].split("\\|");
                headings = newrecord[0].split("\\$");


                if(newrecord.length == 1){
                    detailsArray = new String[newrecord.length][headings.length];
                    String dummyDataArray[] = new String [headings.length];
                    for(int j = 0; j < headings.length; j++){
                        dummyDataArray[j] = "Not Available";

                    }
                    detailsArray[detailsArray.length-1] = dummyDataArray;
                }else{
                    detailsArray = new String[newrecord.length - 1][headings.length];
                }

                for (int i = 1; i < newrecord.length; i++) {
                    String[] details = newrecord[i].split("\\$");
                    detailsArray[i - 1] = details;
                }

                historyButton = (Button) findViewById(R.id.historyButton);
                historyButton.setFocusable(true);
                historyButton.setActivated(true);
                historyButton.setVisibility(View.INVISIBLE);
                addMishap = (Button) findViewById(R.id.AddMishap);
                topHeadingTextView = (TextView) findViewById(R.id.topHeading);
                addMishap.setVisibility(View.INVISIBLE);
                ambassadorPresent = (ListView) findViewById(R.id.listView);
                relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout);

                addMishap.setText(MenuClass.reportHeading);
                topHeadingTextView.setText(MenuClass.reportHeading);

                ambassadorPresent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {


                        if (MenuClass.reportHeading.equals("History")) {
                            String[] firstBrk = newrecord[position].split("\\$");
                            Intent intentWeb = new Intent(listContext, webPageScreen.class);
                            intentWeb.putExtra("Link", "5");
                            intentWeb.putExtra("queueID", firstBrk[2]);
                            listContext.startActivity(intentWeb);

                        }

                    }
                });
                if (bundle.getString("data") != null) {
                    HistoryListAdapter historyListAdapter = new HistoryListAdapter(this, headings, detailsArray);
                    ambassadorPresent.setAdapter(historyListAdapter);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (myReceiverpossn != null) {

            try {
                unregisterReceiver(myReceiverpossn);
                myReceiverpossn = null;
            } catch (IllegalArgumentException e) {
                // Check wether we are in debug mode

                e.printStackTrace();

            }
        }
        super.onDestroy();
    }


    public void onStop() {
        if (myReceiverpossn != null) {

            try {
                unregisterReceiver(myReceiverpossn);
                myReceiverpossn = null;
            } catch (IllegalArgumentException e) {
                // Check wether we are in debug mode

                e.printStackTrace();

            }
        }
        super.onStop();
    }

    public void onBackPressed() {
        finish();
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
                        Intent activeview = new Intent(ListHistory.this, ambassadorPresentdetail.class);
                        activeview.putExtra("data", "data¤" + ambreport);
                        startActivityForResult(activeview, 1);
                        //overlay.setVisibility(View.INVISIBLE);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
