package com.north.socket.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static com.north.socket.client.R.id.connStatus;
import static com.north.socket.client.R.id.mobileNo;
import static com.north.socket.client.R.id.username;

public class ForgotPassword extends Activity {

    EditText mobileNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mobileNo = (EditText)findViewById(R.id.mobileNo);

        Intent intent = getIntent();
        if(!intent.getStringExtra("phnNo").equals("1")){
            mobileNo.setText(intent.getStringExtra("phnNo"));
        }
    }
    public void next_button_clicked(View v){
        if (mobileNo.getText().length() < 10)
        {
            Toast.makeText(this, R.string.mobile_no_request, Toast.LENGTH_SHORT).show();

            return;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.are_you_sure);
        alertDialogBuilder.setPositiveButton(R.string.click_to_rcv_sms,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        String logininfo = "NEWAMBFORGOT"+"|"+mobileNo.getText()+"|";

                        Intent intent = new Intent();
                        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                        intent.putExtra("sendchat", logininfo);
                        sendBroadcast(intent);
                        Intent dataToLogin = new Intent();
                        dataToLogin.putExtra("checkNo", mobileNo.getText().toString());
                        setResult(RESULT_OK, dataToLogin);
                        finish();
                    }
                });

        alertDialogBuilder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();



    }

}
