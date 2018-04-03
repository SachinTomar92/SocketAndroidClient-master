package com.north.socket.client;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.north.socket.client.AmbassadorApp.getContext;
import static com.north.socket.client.R.color.historyColor;
import static com.north.socket.client.R.id.textView;
import static com.north.socket.client.R.id.time;

public class TransactionHistory extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        try {
            init();
        }
        catch (Exception e){}
    }

    public void init() throws ParseException {

        TableLayout stk = (TableLayout) findViewById(R.id.table_main);
        TableRow tbrow0 = new TableRow(this);

        TextView tv0 = new TextView(this);
        tv0.setText("  2017-09-04 ");
        tv0.setTextColor(Color.WHITE);
        tbrow0.addView(tv0);

        TextView tv1 = new TextView(this);
        tv1.setText(" **** **** ");
        tv1.setTextColor(Color.WHITE);
        tbrow0.addView(tv1);

        TextView tv2 = new TextView(this);
        tv2.setText(" Unit ");
        tv2.setTextColor(Color.WHITE);
        tbrow0.addView(tv2);

        TextView tv3 = new TextView(this);
        tv3.setText(" Remaining ");
        tv3.setTextColor(Color.WHITE);
        tbrow0.addView(tv3);

        TextView tv4 = new TextView(this);
        tv4.setText("2017-0-");
        tv4.setTextColor(Color.WHITE);
        tbrow0.addView(tv4);

        stk.addView(tbrow0);

        for (int i = 0; i < awaitPayment.authCodeList.size(); i++) {
            TableRow tbrow = new TableRow(this);

            final float scale = getContext().getResources().getDisplayMetrics().density;
            int pixels = (int) (60 * scale + 0.5f);

            TextView t1v = new TextView(this);
            t1v.setMinHeight(pixels);
            String txt1 = awaitPayment.receiptDateList.get(i);
            String[] text1Array = txt1.split("T");
            t1v.setText(text1Array[0]);
            t1v.setTypeface(null, Typeface.BOLD);
            t1v.setTextColor(Color.parseColor("#8B8B8B"));
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);

            TextView t2v = new TextView(this);
            String[] timeArray = text1Array[1].split("\\+");

            String result = timeArray[0].substring(0, timeArray[0].lastIndexOf(':', timeArray[0].lastIndexOf(':')));
            t2v.setText(result);
            t2v.setTypeface(null, Typeface.BOLD);
            t2v.setTextColor(Color.parseColor("#8B8B8B"));
            t2v.setGravity(Gravity.CENTER);
            tbrow.addView(t2v);

            TextView t3v = new TextView(this);
            String txt2 = awaitPayment.cardNumList.get(i);
            String[] text2Array = txt2.split("-");
            t3v.setText("**** " + text2Array[3]);
            t3v.setTypeface(null, Typeface.BOLD);
            t3v.setTextColor(Color.parseColor("#8B8B8B"));
            t3v.setGravity(Gravity.CENTER);
            tbrow.addView(t3v);

            TextView t4v = new TextView(this);
            String txt3 = awaitPayment.authCodeList.get(i);
            if(txt3.equals("N/A")){
                t4v.setText("Failed");
            }else{
                t4v.setText("Success");
            }
//            t4v.setText(awaitPayment.amountList.get(i));
            t4v.setTextColor(Color.parseColor("#8B8B8B"));
            t4v.setTypeface(null, Typeface.BOLD);
            t4v.setGravity(Gravity.CENTER);
            tbrow.addView(t4v);

            TextView t5v = new TextView(this);
            t5v.setText("\u20B9 " + awaitPayment.amountList.get(i));
            t5v.setTypeface(null, Typeface.BOLD);
            t5v.setTextColor(Color.parseColor("#8B8B8B"));
            t5v.setGravity(Gravity.CENTER);
            tbrow.addView(t5v);

            tbrow.setOnClickListener(this);
            tbrow.setId(i);

            View v = new View(this);
            v.setBackgroundColor(Color.parseColor("#8B8B8B"));
            v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
            stk.addView(v);
            stk.addView(tbrow);
        }
        for (int i = 0; i < awaitPayment.listData.size(); i++) {
            TableRow tbrow = new TableRow(this);

            HistoryDetails historyDetails = (HistoryDetails) awaitPayment.listData.get(i);
            final float scale = getContext().getResources().getDisplayMetrics().density;
            int pixels = (int) (60 * scale + 0.5f);

            TextView t1v = new TextView(this);
            t1v.setMinHeight(pixels);
            String txt1 = historyDetails.TrxDate;
            String[] timeArray = txt1.split("\\s+");
            String check1 = timeArray[0] + "/" + timeArray[1] + "/" + timeArray[2];
            t1v.setText(timeArray[0] + "/" + timeArray[1] + "/" + timeArray[2]);
            t1v.setTypeface(null, Typeface.BOLD);
            t1v.setTextColor(Color.parseColor("#8B8B8B"));
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);

            TextView t2v = new TextView(this);

            String check2 = timeArray[3] + " " + timeArray[4];
            t2v.setText(timeArray[3] + " " + timeArray[4]);
            t2v.setTypeface(null, Typeface.BOLD);
            t2v.setTextColor(Color.parseColor("#8B8B8B"));
            t2v.setGravity(Gravity.CENTER);
            tbrow.addView(t2v);

            TextView t3v = new TextView(this);
            String check3 = historyDetails.CardLastFourDigits;
            t3v.setText("**** " + historyDetails.CardLastFourDigits);
            t3v.setTypeface(null, Typeface.BOLD);
            t3v.setTextColor(Color.parseColor("#8B8B8B"));
            t3v.setGravity(Gravity.CENTER);
            tbrow.addView(t3v);

            TextView t4v = new TextView(this);

//            if(txt3.equals("N/A")){
//                t4v.setText("Failed");
//            }else{
//                t4v.setText("Success");
//            }
            String check4 = historyDetails.TerminalMessage;
            t4v.setText(historyDetails.TerminalMessage);
            t4v.setTextColor(Color.parseColor("#8B8B8B"));
            t4v.setTypeface(null, Typeface.BOLD);
            t4v.setGravity(Gravity.CENTER);
            tbrow.addView(t4v);

            TextView t5v = new TextView(this);
            String check5 = historyDetails.TrxAmount;
            t5v.setText("\u20B9 " + historyDetails.TrxAmount);
            t5v.setTypeface(null, Typeface.BOLD);
            t5v.setTextColor(Color.parseColor("#8B8B8B"));
            t5v.setGravity(Gravity.CENTER);
            tbrow.addView(t5v);

            tbrow.setOnClickListener(this);
            tbrow.setId(i);

            System.out.println("Row output: " + check1 + " " + check2 + " " + check3 + " " + check4 + " " + check5);

            View v = new View(this);
            v.setBackgroundColor(Color.parseColor("#8B8B8B"));
            v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
            stk.addView(v);
            stk.addView(tbrow);
        }
    }

    @Override
    public void onClick(View v) {
        int clickedID = v.getId();
        Intent intent = new Intent(this, TransHistoryDetails.class);
        intent.putExtra("indexNo", clickedID);
        startActivity(intent);
        System.out.println("ClickedID: " + clickedID);
    }
}

