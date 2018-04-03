package com.north.socket.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class AcceptTask extends Activity {

    Bundle bundle;
    String[] detailsArray;
    ImageView led;
    AcceptTaskConnectivityReceiver myReceiverAccept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_task);

        final TextView timerAcc = (TextView)findViewById(R.id.timerAcc);
        led = (ImageView)findViewById(R.id.led);

        bundle = getIntent().getExtras();
        detailsArray = (String[]) bundle.get("detailsInfo");

        overlay = (FrameLayout) findViewById(R.id.overlay);
        overlay.setVisibility(View.INVISIBLE);

        if(myReceiverAccept == null) {
            myReceiverAccept = new AcceptTaskConnectivityReceiver();
            IntentFilter filter = new IntentFilter("com.north.socket.client.AcceptTaskReceiver");
            registerReceiver(myReceiverAccept, filter);
        }

        TextView serviceCentreLabel = (TextView)findViewById(R.id.svcinfo);
        TextView svcnameTopInfo = (TextView) findViewById(R.id.svcnameTopInfo);
        TextView svcaddressTopInfo = (TextView) findViewById(R.id.svcaddressTopInfo);

        TextView customerNameTopInfo = (TextView) findViewById(R.id.custnameTopInfo);
        TextView custInfoLabel = (TextView)findViewById(R.id.custinfo);
        TextView custaddressTopInfo = (TextView) findViewById(R.id.custaddressTopInfo);

        TextView topinfoActivity = (TextView) findViewById(R.id.topinfoActivity);
        topinfoActivity.setText(detailsArray[6]);

        TextView typeOfService = (TextView) findViewById(R.id.typeofsvc);
        typeOfService.setText(detailsArray[9]);

        Intent sendPhotos = new Intent();
        sendPhotos.setAction("com.north.socket.client.LBBroadcastReceiverFile");
        sendPhotos.putExtra("shouldisend", "0");
        sendBroadcast(sendPhotos);

        if(detailsArray[6].equals(getString(R.string.pickup))){
            serviceCentreLabel.setAlpha(0.2f);
            svcnameTopInfo.setAlpha(0.2f);
            svcaddressTopInfo.setAlpha(0.2f);
        }else if(detailsArray[6].equals(getString(R.string.dropoff))){
            customerNameTopInfo.setAlpha(0.2f);
            custInfoLabel.setAlpha(0.2f);
            custaddressTopInfo.setAlpha(0.2f);
        }else if(detailsArray[6].equals(getString(R.string.chauffeur))){
            serviceCentreLabel.setVisibility(View.INVISIBLE);
            svcnameTopInfo.setVisibility(View.INVISIBLE);
            svcaddressTopInfo.setVisibility(View.INVISIBLE);
        }

        TextView vehicleNumber = (TextView) findViewById(R.id.vehicleNumber);
        vehicleNumber.setText(detailsArray[0]);

        TextView brandTopInfo = (TextView) findViewById(R.id.brandTopInfo);
        brandTopInfo.setText(detailsArray[1]);

        ImageView imageView = (ImageView) findViewById(R.id.vehIcon);

        if(Login.vehicleType.equals(Login.twoWheelerCheck)){
            imageView.setImageResource(R.drawable.bike);
        }else{
            imageView.setImageResource(R.drawable.car);
        }

// Load the animation like this

        Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide);
        animSlide.setRepeatCount(Animation.INFINITE);

// Start the animation like this
        imageView.startAnimation(animSlide);

        if (!detailsArray[3].equals("0")) {
            customerNameTopInfo.setText(detailsArray[2]);
            custaddressTopInfo.setText(detailsArray[3]);
        }
        if (!detailsArray[5].equals("0")) {
            svcnameTopInfo.setText(detailsArray[4]);
            svcaddressTopInfo.setText(detailsArray[5]);
        }
        if (!detailsArray[7].equals("(null)")) {
            new CountDownTimer(Integer.parseInt(detailsArray[7]) * 1000, 1000) {

                public void onTick(long millisUntilFinished) {
                    timerAcc.setText(("" + millisUntilFinished / 1000));
                }

                public void onFinish() {
                    timerAcc.setText(R.string.banned);
                    Intent intentb = new Intent();
                    intentb.setAction("com.north.socket.client.POSSNBroadcastReceiverawaiting");
                    intentb.putExtra("Banned", "true");
                    sendBroadcast(intentb);
                    finish();

                }
            }.start();
        }

        PulsatorLayout pulsator = (PulsatorLayout) findViewById(R.id.pulsator);
        pulsator.start();
    }
    public void acceptTaskClicked(View view){


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.confirm_task);
        alertDialogBuilder.setPositiveButton(R.string.OK,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        displayOverlay();
                        String latlongtosubmit = SocketService.latitude + "|" + SocketService.longitude + "|";
                        if (latlongtosubmit.length() > 1) {
                            String data = "NEXTMOVE|" + latlongtosubmit + "1|" + detailsArray[8] + "|########|#####|";
                            Intent intent = new Intent();
                            intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                            intent.putExtra("sendchat", data);
                            sendBroadcast(intent);
                        }
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

    public void onResume() {
        super.onResume();
        if (myReceiverAccept == null) {
            myReceiverAccept = new AcceptTaskConnectivityReceiver();
            IntentFilter filter = new IntentFilter("com.north.socket.client.AcceptTaskReceiver");
            registerReceiver(myReceiverAccept, filter);
        }
    }

    protected void onDestroy() {
        if (myReceiverAccept != null) {

            try {
                unregisterReceiver(myReceiverAccept);
                myReceiverAccept = null;
            } catch (IllegalArgumentException e) {
                // Check wether we are in debug mode

                e.printStackTrace();

            }
        }
        super.onDestroy();
    }

    public void onStop() {
        if (myReceiverAccept != null) {

            try {
                unregisterReceiver(myReceiverAccept);
                myReceiverAccept = null;
            } catch (IllegalArgumentException e) {
                // Check wether we are in debug mode

                e.printStackTrace();

            }
        }
        super.onStop();
    }

    private FrameLayout overlay;
    private Handler overlayTimer = new Handler();
    public void displayOverlay() {
        overlay.setVisibility(View.VISIBLE);
        overlayTimer.removeCallbacksAndMessages(null);
        overlayTimer.postDelayed(overlayTimeout, 10000);
    }

    private Runnable overlayTimeout = new Runnable() {
        @Override
        public void run() {
            try {
                overlay.setVisibility(View.INVISIBLE);
                overlayTimer.removeCallbacksAndMessages(null);
                showAlert();
            } catch (Exception ignored) {
                // added try catch block to be sure of uninterupted execution
            }

        }
    };

    @Override
    public void onBackPressed() {

    }
    public void showAlert(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Network not available. Please try again.");
        alertDialogBuilder.setPositiveButton(R.string.OK,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public class AcceptTaskConnectivityReceiver extends BroadcastReceiver {

        public AcceptTaskConnectivityReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {


            try {
                String data = intent.getStringExtra("senddata");
                if (data != null) {
                    String[] typeofCommand = data.split(":");
                    Log.d("LOG", "Received senddata : " + data);
                    if (data.equals("green")) {
                        //callinfo.setText("Position sent successfully.");
                        //timeoutCallinfo();

                        led.setImageResource(R.drawable.green);

                    } else if (data.equals("red")) {
                        led.setImageResource(R.drawable.red);
                    }
                }
            } catch (Exception ignored) {

            }
        }
    }
}
