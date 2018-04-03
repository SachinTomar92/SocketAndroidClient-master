package com.north.socket.client;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import needle.Needle;

import static com.north.socket.client.R.id.profileImg;

public class AwaitingTask extends Activity {
    String[] typeofCommand;
    String[] queuestate;
    String[] loginCommand;
    private static FrameLayout overlay;

    private final String CANCELABLE_REQUEST_TAG = "volleyImageRequest";

    TextView dailyIncome;
    TextView cashInHand;

    String stateview = "";

    private RelativeLayout relativeLayout;
    ConnectivityReceiver myReceiverawaiting;
    private ImageView ledbutton;
    private Button banned;
    int varbanned = 0;
    int varbusy = 0;
    String logininfo = "";
    private static Handler overlayTimer = new Handler();
    private Button btnback;
    private Button btndata;
    private EditText data1txt;
    Application application;
    private DrawerLayout mDrawerLayout;

    TextView fileCount;

    ListView lv;
    static Context context;

    public static int[] prgmImages;
    public static String[][] prgmNameList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awaiting_task);

        myReceiverawaiting = new ConnectivityReceiver();
        IntentFilter filter = new IntentFilter("com.north.socket.client.POSSNBroadcastReceiverawaiting");
        registerReceiver(myReceiverawaiting, filter);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        overlay = (FrameLayout) findViewById(R.id.overlay);
        ledbutton = (ImageView) findViewById(R.id.ledbutton);
        TextView totalamount = (TextView) findViewById(R.id.totalamount);
        fileCount = (TextView)findViewById(R.id.fileRemaining);
        dailyIncome = (TextView) findViewById(R.id.dailyIncome);
        cashInHand = (TextView) findViewById(R.id.cashInHand);
        banned = (Button) findViewById(R.id.button7);

        relativeLayout = (RelativeLayout) findViewById(R.id.relLayout);
        overlay.setVisibility(View.INVISIBLE);
        RatingBar ambRatingbar = (RatingBar) findViewById(R.id.rating);
        ambRatingbar.setVisibility(View.INVISIBLE);
        ambRatingbar.setStepSize(0.1f);

        Bundle bundle = getIntent().getExtras();
        String rootb = Environment.getExternalStorageDirectory().toString();
        File file = new File(rootb, "config.txt");
        String filepath = rootb + "/config.txt";

        File directory = new File(rootb + "/PhotoDir");
        File listOfFile[] = directory.listFiles();
        fileCount.setText(String.valueOf(listOfFile.length));

        if (!isMyServiceRunning(SocketServiceFile.class)) {
            Intent isocketstart = new Intent(this, SocketServiceFile.class);
            startService(isocketstart);
        }

        Intent sendPhotos = new Intent();
        sendPhotos.setAction("com.north.socket.client.LBBroadcastReceiverFile");
        sendPhotos.putExtra("shouldisend", "1");
        sendBroadcast(sendPhotos);

        TextView loginIDMenu = (TextView) findViewById(R.id.ambName);

        final ImageView profileimg = (ImageView) findViewById(profileImg);
        Timer imageCheckTimer;

        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
        String root1 = Environment.getExternalStorageDirectory().toString() + "/21NorthAmb";
        File file1 = new File(root1, formatter.format(today) + "-signin.jpg");
        Uri filepath1 = Uri.parse(root1+"/" +  formatter.format(today) + "-signin.jpg");

        if(file1.exists()){
            profileimg.setImageURI(filepath1);
        }else {

            imageCheckTimer = new Timer();
            final Timer finalImageCheckTimer = imageCheckTimer;
            imageCheckTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // do your task here
                    Needle.onBackgroundThread().withThreadPoolSize(20).execute(new Runnable() {
                        @Override
                        public void run() {


                            Date today = Calendar.getInstance().getTime();
                            SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
                            // (2) create a date "formatter" (the date format we want)


                            ImageRequest imgRequest = new ImageRequest("http://m.21north.in:7410/images/" + MenuClass.ambId + "-" +formatter.format(today) + "-signin.jpg", new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap response) {
                                    profileimg.setImageBitmap(response);
                                    profileimg.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
                                }
                            }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
//                                    profileimg.setImageResource(RES_ERROR);

                                }
                            });
                            imgRequest.setTag(CANCELABLE_REQUEST_TAG);

                /* start the download */
                            VolleyManager.getInstance(context).getRequestQ().add(imgRequest);

                        }
                    });
                    finalImageCheckTimer.cancel();

                }
            }, 0, 20);
        }

        RatingBar navrating = (RatingBar) findViewById(R.id.nav_rating);
        navrating.setRating((float) 5);

        context = this;

        prgmImages = new int[MenuClass.menuList.size() + 4];
        prgmNameList = new String[MenuClass.menuList.size() + 4][2];

        for (int i = 0; i < MenuClass.menuList.size(); i++) {
            prgmNameList[i][0] = MenuClass.menuList.get(i).menuItemID;
            prgmNameList[i][1] = MenuClass.menuList.get(i).menuTitle;
        }

        prgmNameList[MenuClass.menuList.size() + 0][0] = "-2";
        prgmNameList[MenuClass.menuList.size() + 0][1] = getResources().getString(R.string.complaints);

        prgmNameList[MenuClass.menuList.size() + 1][0] = "-3";
        prgmNameList[MenuClass.menuList.size() + 1][1] = getResources().getString(R.string.refer_and_earn);

        prgmNameList[MenuClass.menuList.size() + 2][0] = "-4";
        prgmNameList[MenuClass.menuList.size() + 2][1] = getResources().getString(R.string.news);

        prgmNameList[MenuClass.menuList.size() + 3][0] = "-5";
        prgmNameList[MenuClass.menuList.size() + 3][1] = getResources().getString(R.string.training);

        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(new CustomAdapter(this, prgmNameList, prgmImages));


        // connect to the server
        application = (Application) AmbassadorApp.getContext();

        if (file.exists()) {
            BufferedReader br;
            try {


                String sCurrentLine;

                br = new BufferedReader(new FileReader(filepath));

                while ((sCurrentLine = br.readLine()) != null) {
                    logininfo = logininfo + sCurrentLine;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (!logininfo.equals("")) {
            loginCommand = logininfo.split(",");

            if (bundle.getString("qdata") != null) {
                String s = bundle.getString("qdata");
                if (s != null) {
                    typeofCommand = s.split("\\|");
                }
                if (s != null) {
                    queuestate = s.split("\\$");
                    MenuClass.ratingAmb = Float.parseFloat(queuestate[14]);
                    loginIDMenu.setText(MenuClass.loginId);
                    navrating.setRating(MenuClass.ratingAmb);

                    ambRatingbar.setRating(Float.parseFloat(queuestate[14]));

                    String totalAmtText = getResources().getString(R.string.monthly_income);
                    totalamount.setText(totalAmtText + " :  \u20B9 " + queuestate[0]);
//queuestate[17] - cash in hand
                    dailyIncome.setText(getResources().getString(R.string.daily_income) + " :  \u20B9 " + queuestate[16]);

                    cashInHand.setText(getResources().getString(R.string.cash_in_hand) + " : \u20B9 " + queuestate[17]);

                    varbusy = Integer.parseInt(queuestate[1]);
                    varbanned = Integer.parseInt(queuestate[2]);

                    //typeofReport = typeofCommand[5].split("Æ");

                    if (varbusy == 1) {
                        banned.setText(R.string.amb_status_busy);
                        banned.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                    }
                    if (varbanned == 1) {
                        banned.setEnabled(false);
                        banned.setText(R.string.amb_status_banned);
                        banned.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                    }

                    if (varbusy == 0) {
                        banned.setEnabled(false);
                        banned.setText(R.string.amb_status_free);
                        banned.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                    }
                    if (SocketService.disabled == 1) {
                        banned.setEnabled(false);
                        banned.setText(R.string.disabled_ambassador);
                        banned.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                    }
                }
            }
        }
    }
    public void stopAlarmButton(View v){
        stopAlarm();
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void stopAlarm() {
        Intent intentstop = new Intent();
        intentstop.setAction("com.north.socket.client.LBBroadcastReceiver");
        intentstop.putExtra("senddata", "stopalarm");
        context.sendBroadcast(intentstop);

        Intent intent = new Intent();
        intent.setAction("com.north.socket.client.BroadcastReceiver");
        intent.putExtra("senddata", "stopalarm");
        context.sendBroadcast(intent);


    }

    public void onResume() {
        super.onResume();
        if (myReceiverawaiting == null) {
            myReceiverawaiting = new ConnectivityReceiver();
            IntentFilter filter = new IntentFilter("com.north.socket.client.POSSNBroadcastReceiverawaiting");
            registerReceiver(myReceiverawaiting, filter);
        }
    }

    public void onStop() {
        if (myReceiverawaiting != null) {

            try {
                unregisterReceiver(myReceiverawaiting);
                myReceiverawaiting = null;
            } catch (IllegalArgumentException e) {
                // Check wether we are in debug mode

                e.printStackTrace();

            }
        }
        super.onStop();
    }

    @Override
    public void onPause() {

        if (myReceiverawaiting != null) {

            try {
                unregisterReceiver(myReceiverawaiting);
                myReceiverawaiting = null;
            } catch (IllegalArgumentException e) {
                // Check wether we are in debug mode

                e.printStackTrace();

            }
        }
        super.onPause();
    }

    protected void onDestroy() {
        if (myReceiverawaiting != null) {

            try {
                unregisterReceiver(myReceiverawaiting);
                myReceiverawaiting = null;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    public void openDrawer(View view) {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }


    View.OnClickListener btnclick = new View.OnClickListener() {

        @Override
        public void onClick(final View view) {

            stopAlarm();
            if (view.getId() == 100) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AwaitingTask.this);
                alertDialogBuilder.setMessage("Are you sure you want to modify your address. A new address verification ID will be sent to this new address. Do not modify this unless you are sure? Please call office for clarifications. If you do not enter the Verification ID within 5 days you will get disabled from the system.");
                alertDialogBuilder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                displayOverlay();
                                String data = "EDITADDRESS|" + data1txt.getText().toString() + "|";
                                Intent intent = new Intent();
                                intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                                intent.putExtra("sendchat", data);
                                sendBroadcast(intent);

                            }
                        });

                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (btnback != null) {
                            btnback.setVisibility(View.GONE);
                        }
                        if (data1txt != null) {
                            data1txt.setVisibility(View.GONE);
                        }
                        if (btndata != null) {
                            btndata.setVisibility(View.GONE);
                        }

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }

        }
    };

    public void address() {
        if (Integer.parseInt(queuestate[13]) > 0) {
            try {
                stateview = "com.north.socket.client.AddressVerify";
                Intent activeview = new Intent(AwaitingTask.this, Class.forName(stateview));
                activeview.putExtra("qdata", typeofCommand[0]);
                startActivityForResult(activeview, 5);
            } catch (ClassNotFoundException ignored) {

            }
        } else {
            btnback = new Button(AwaitingTask.this);
            btnback.setId(100);
            btnback.setOnClickListener(btnclick);
            relativeLayout.addView(btnback);
            btnback.setWidth(relativeLayout.getWidth());
            btnback.setHeight(relativeLayout.getHeight());
            btnback.setBackgroundColor(Color.BLACK);
            data1txt = new EditText(AwaitingTask.this);
            String adress1 = queuestate[8];
            data1txt.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            data1txt.setText(adress1);
            data1txt.setSingleLine(false);
            data1txt.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
            data1txt.setBackgroundResource(R.color.white);
            relativeLayout.addView(data1txt);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) data1txt.getLayoutParams();
            lp.topMargin = 50;

            data1txt.setWidth(relativeLayout.getWidth());
            data1txt.setLayoutParams(lp);


            btndata = new Button(AwaitingTask.this);
            btndata.setId(100);
            btndata.setOnClickListener(btnclick);
            relativeLayout.addView(btndata);
            ViewGroup.MarginLayoutParams lpbut = (ViewGroup.MarginLayoutParams) btndata.getLayoutParams();
            lpbut.topMargin = 400;
            btndata.setBackgroundColor(Color.parseColor("#F28500"));
            btndata.setLayoutParams(lpbut);
            btndata.setWidth(relativeLayout.getWidth());
            btndata.setText("Modify Address.");

            // ScrollView sv = (ScrollView)findViewById(R.id.scrolllayout);
            // sv.scrollTo(0, sv.getTop());

            btndata.requestFocus();
            InputMethodManager immaddress = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            immaddress.showSoftInput(data1txt, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void freeMe(View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to be marked Free?");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        displayOverlay();

                        String data = "AMBFREEME|1|";
                        Intent intent = new Intent();
                        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                        intent.putExtra("sendchat", data);
                        sendBroadcast(intent);
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void onBackPressed() {
        // do nothing.
    }

    public static void displayOverlay() {
        stopAlarm();
        overlay.setVisibility(View.VISIBLE);
        overlayTimer.removeCallbacksAndMessages(null);
        overlayTimer.postDelayed(overlayTimeout, 10000);
    }

    private static Runnable overlayTimeout = new Runnable() {
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
    public class ConnectivityReceiver extends BroadcastReceiver {

        public ConnectivityReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String fileCountNo = intent.getStringExtra("fileCount");
                if(fileCountNo != null){
                    String rootb = Environment.getExternalStorageDirectory().toString();
                    File directory = new File(rootb + "/PhotoDir");
                    File listOfFile[] = directory.listFiles();
                    fileCount.setText(String.valueOf(listOfFile.length));
                }

                String showVerifyAdd = intent.getStringExtra("ShowVerify");
                if (showVerifyAdd != null) {
                    address();
                }
                String bannedString = intent.getStringExtra("Banned");
                if (bannedString != null) {
                    if (bannedString.equals("true")) {
                        banned.setEnabled(false);
                        banned.setText(R.string.banned);
                        banned.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                        System.out.println("Banned From OnRec");
                    }
                }

                String startclient = intent.getStringExtra("startclient");
                if (startclient != null) {
                    ledbutton.setImageResource(R.drawable.red);
                }
                if (startclient != null) {
                    ledbutton.setImageResource(R.drawable.green);
                }

                String ambreport = intent.getStringExtra("ambreport");
                if (ambreport != null) {
                    if (!ambreport.equals("none")) {
                        Intent activeview = new Intent(AwaitingTask.this, ListHistory.class);
                        activeview.putExtra("data", ambreport);
                        startActivityForResult(activeview, 1);
                    } else {
                        String notifyid = intent.getStringExtra("notifyID");
                        String check = "";

                        for (String[] aPrgmNameList : prgmNameList) {
                            if (notifyid.equals(aPrgmNameList[0])) {
                                check = aPrgmNameList[1];
                                break;
                            }
                        }

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AwaitingTask.this);
                        //int state = Integer.parseInt(typeofCommand[0]);

                        alertDialogBuilder.setMessage("No " + check);
                        alertDialogBuilder.setPositiveButton(R.string.OK,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

                                        //finish();
                                    }
                                });


                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                        overlay.setVisibility(View.INVISIBLE);
                    }
                }
                String data = intent.getStringExtra("senddata");
                if (data != null) {
                    if (data.equals("green")) {
                        ledbutton.setImageResource(R.drawable.green);
                    }

                    if (data.equals("red")) {
                        ledbutton.setImageResource(R.drawable.red);
                    } else if (data.equals("nolocation")) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AwaitingTask.this);
                        alertDialogBuilder.setMessage(R.string.location_off);
                        alertDialogBuilder.setPositiveButton(R.string.OK,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
