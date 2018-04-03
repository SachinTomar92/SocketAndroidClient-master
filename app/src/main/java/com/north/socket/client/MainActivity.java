package com.north.socket.client;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends MswipeView {

    static TextView mswipeInfo;
    static Context mainContext;
    static Activity activity = null;
    static TextView amountTextView;
    static TextView cardNoTextView;
    static TextView validThruTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mswipeInfo = (TextView)findViewById(R.id.mswipeInfo);
        amountTextView = (TextView)findViewById(R.id.amountTextView);
        cardNoTextView = (TextView)findViewById(R.id.cardNo);
        validThruTextView = (TextView)findViewById(R.id.validThru);

        amountTextView.setText(totalAmount);
        MswipeView.activityCheck = 1;
        mainContext = this;
        activity = this;
    }
    public void reinitMain(View v){
        reinit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public static void closeActivity(){
        activity.finish();
    }
}
