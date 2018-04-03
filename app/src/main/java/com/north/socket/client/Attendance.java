package com.north.socket.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Attendance extends Activity {

    int phototaken = 0;
    String[] queuestate;
    private Switch shower;
    private Switch powerbank;
    private Switch charger;
    private Switch uniform;
    private ImageView attendance;
    private Switch backpack;
    private Button refreshbut;
    private Button signin;
    private Handler overlayTimer = new Handler();
    private FrameLayout overlay;
    ConnectivityReceiver myReceiverpossn;
    private String latlongtosubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        // getActionBar().setDisplayHomeAsUpEnabled(true);
        // getActionBar().hide();
        myReceiverpossn = new ConnectivityReceiver();
        IntentFilter filter = new IntentFilter("com.north.socket.client.attendanceBroadcastReceiver");
        registerReceiver(myReceiverpossn, filter);
        overlay = (FrameLayout) findViewById(R.id.overlay);
        overlay.setVisibility(View.INVISIBLE);

        shower = (Switch) findViewById(R.id.shower);
        powerbank = (Switch) findViewById(R.id.powerbank);
        charger = (Switch) findViewById(R.id.charger);
        uniform = (Switch) findViewById(R.id.uniform);
        backpack = (Switch) findViewById(R.id.bag);
        attendance = (ImageView) findViewById(R.id.attendancePhoto);
        signin = (Button) findViewById(R.id.signin);

        if (SocketService.timeDiff()) {
            signin.setAlpha(1.0f);
            signin.setEnabled(true);
            latlongtosubmit = SocketService.latitude + "|" + SocketService.longitude + "|";
        } else {
            signin.setAlpha(0.3f);
            signin.setEnabled(false);
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("qdata") != null) {
            String s = bundle.getString("qdata");
            queuestate = s.split("\\$");
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
            try {
                overlay.setVisibility(View.INVISIBLE);
                overlayTimer.removeCallbacksAndMessages(null);
            } catch (Exception e) {
                // added try catch block to be sure of uninterupted execution
            }

      /* and here comes the "trick" */
            //handler.postDelayed(this, 1000);
        }
    };

    public void callRefreshattend(View v) {
        System.exit(2);
    }

    public void onBackPressed() {
        // do nothing.
    }


    public void attendancePhoto(View v) {
        int ab = 10;
        //resize();

        Date today = Calendar.getInstance().getTime();

        // (2) create a date "formatter" (the date format we want)
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
        //pagercount = pagercount-8500;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        String root = Environment.getExternalStorageDirectory().toString();
        File directory = new File(root + "/PhotoDir");
        File f = new File(directory, queuestate[7] + "-" + formatter.format(today) + "-signin.jpg");

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 1);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    @Override
    protected void onDestroy() {
        if (myReceiverpossn != null) {
            try {
                unregisterReceiver(myReceiverpossn);
                myReceiverpossn = null;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {

            String root = Environment.getExternalStorageDirectory().toString();
            File directory = new File(root + "/PhotoDir");
            Date today = Calendar.getInstance().getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
            File f = new File(directory, queuestate[7] + "-" + formatter.format(today) + "-signin.jpg");
            Bitmap b = BitmapFactory.decodeFile(f.getPath().toString());
            int orientation = 0;
            if (b != null) {
                Bitmap rotated = null;
                Bitmap out = null;
                try {
                    ExifInterface ei = new ExifInterface(f.getPath().toString());
                    orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                } catch (Exception e) {
                }
                switch (orientation) {
                    case ExifInterface.ORIENTATION_NORMAL:
                        if (b != null) {
                            rotated = rotateImage(b, 0);
                            out = Bitmap.createScaledBitmap(rotated, 480, 800, true);
                        }
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        if (b != null) {
                            rotated = rotateImage(b, 90);
                            out = Bitmap.createScaledBitmap(rotated, 480, 800, true);
                        }
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        if (b != null) {
                            rotated = rotateImage(b, 180);
                            out = Bitmap.createScaledBitmap(rotated, 480, 800, true);
                        }
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        if (b != null) {
                            rotated = rotateImage(b, 270);
                            out = Bitmap.createScaledBitmap(rotated, 480, 800, true);
                        }
                        break;
                    case ExifInterface.ORIENTATION_UNDEFINED:
                        if (b != null) {
                            rotated = rotateImage(b, 0);
                            out = Bitmap.createScaledBitmap(rotated, 480, 800, true);
                        }
                        break;
                    // etc.
                }

                FileOutputStream fOut;
                try {
                    fOut = new FileOutputStream(f);
                    if (out == null) {
                        f.delete();
                    }
                    if (out.getWidth() > 0 && out.getHeight() > 0) {
                        out.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
                        fOut.flush();
                        fOut.close();
                        phototaken = 1;
                    }

                } catch (Exception e) {
                }
                createDirectoryAndSaveFile(out);
                attendance.setImageBitmap(out);
            }
        }
        if (requestCode == 0) {
            phototaken = 0;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createDirectoryAndSaveFile(Bitmap imageToSave) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/21NorthAmb");

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/21NorthAmb/");
            wallpaperDirectory.mkdirs();
        }

        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
        File file = new File(new File("/sdcard/21NorthAmb/"), formatter.format(today) + "-signin.jpg");
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SignIn(View v) {
        if (shower.isChecked() && powerbank.isChecked() && charger.isChecked() && uniform.isChecked() && backpack.isChecked() && phototaken == 1) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            //int state = Integer.parseInt(typeofCommand[0]);
            alertDialogBuilder.setMessage(R.string.want_to_sign_in);
            alertDialogBuilder.setPositiveButton(R.string.yes,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            displayOverlay();
                            String data = "ATTENAMB|1|" + latlongtosubmit;
                            Intent intent = new Intent();
                            intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                            intent.putExtra("sendchat", data);
//                                sendBroadcast(intent);
                            //finish();
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

        } else {
            if (phototaken != 1) {
                Toast toast = Toast.makeText(this, R.string.click_selfie, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 105, 50);
                toast.show();
            } else {
                Toast toast = Toast.makeText(this, R.string.please_check_essentials, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 105, 50);
                toast.show();
            }
        }
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

    public void onResume() {
        super.onResume();
        if (myReceiverpossn == null) {
            myReceiverpossn = new ConnectivityReceiver();
            IntentFilter filter = new IntentFilter("com.north.socket.client.attendanceBroadcastReceiver");
            registerReceiver(myReceiverpossn, filter);
        }
    }

    public class ConnectivityReceiver extends BroadcastReceiver {

        public ConnectivityReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                String latlong = intent.getStringExtra("latlong");
                if (latlong != null) {
                    latlongtosubmit = latlong;
                    signin.setAlpha(1.0f);
                    signin.setEnabled(true);
                }
            } catch (java.lang.NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
