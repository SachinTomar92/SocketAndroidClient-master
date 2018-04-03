package com.north.socket.client;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.attr.data;

public class Feedback extends Activity {

    EditText feedbackEditText;
    String queueID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Bundle bundle = getIntent().getExtras();
        if(MenuClass.reportHeading.equals("History")){
            queueID = bundle.getString("queueID");
        }

        feedbackEditText = (EditText)findViewById(R.id.feedbackEditText);
    }
    public void submitFeedback(View v){
        String feedbackText = String.valueOf(feedbackEditText.getText());
        if(feedbackText.length() > 1){
            String finalFeedback;
            if(MenuClass.reportHeading.equals("History")){
                finalFeedback = "QIDFEEDBACK|" + queueID + "|" + feedbackText + "|";
            }else {
                finalFeedback = "ACCTFEEDBACK|" + feedbackText + "|";
            }
            Intent intent = new Intent();
            intent.setAction("com.north.socket.client.LBBroadcastReceiver");
            intent.putExtra("sendchat", finalFeedback);
            sendBroadcast(intent);
            finish();
        }else{
            Toast.makeText(this, "Enter Feedback", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
