package com.north.socket.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import needle.Needle;

import static com.north.socket.client.R.id.profileImg;


public class ActiveTask extends Activity {

    private TextView custname;
    private TextView custinfo;
    private TextView custaddress;
    private TextView svcname;
    private TextView svcinfo;
    private TextView svcaddress;
    private TextView callinfo;
    private TextView countdown;
    private ImageView custcallone;
    private ImageView custcalltwo;
    private ImageView svccall;
    private Button taskButton;
    private ImageView ledbutton;
    private RelativeLayout relativeLayout;
    private Button btnback;

    TextView fileCount;
    private TextView tickettimer;

    private EditText data1txt;
    private EditText data2txt;
    private Button btndata;
    private int chatticketnumber;


    private String latlongtosubmit;

    private Handler overlayTimer = new Handler();
    private FrameLayout overlay;


    Application application;
    String[] typeofCommand;
    String[] queuestate;
    int totalamount;

    Timer timerdisable;
    Timer resetcalltimer;
    ConnectivityReceiver myReceiverpossn;
    int state = 0;
    int notify = 0;
    int taskbuttonactive = 0;

    private DrawerLayout mDrawerLayout;

    private final String CANCELABLE_REQUEST_TAG = "volleyImageRequest";

    ListView lv;
    static Context context;

    double pickupLat;
    double pickupLong;
    double dropLat;
    double dropLong;

    public static int[] prgmImages;
    public static String[][] prgmNameList;

    ArrayList<String> listofButtons = new ArrayList<>();
    ArrayList<Integer> buttonID = new ArrayList<>();
    ArrayList<String> innertext = new ArrayList<>();
    ArrayList<Integer> numberOfElements = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        myReceiverpossn = new ConnectivityReceiver();
        IntentFilter filter = new IntentFilter("com.north.socket.client.POSSNBroadcastReceiver");
        registerReceiver(myReceiverpossn, filter);
        setContentView(R.layout.activity_active_task);
        Bundle bundle = getIntent().getExtras();
        TextView topinfo = (TextView) findViewById(R.id.topinfo);
        TextView rating = (TextView) findViewById(R.id.rating);
        TextView licnumber = (TextView) findViewById(R.id.licnumber);
        TextView brand = (TextView) findViewById(R.id.brand);
        TextView typeofsvc = (TextView) findViewById(R.id.typeofsvc);
        custname = (TextView) findViewById(R.id.custname);
        custinfo = (TextView) findViewById(R.id.custinfo);
        callinfo = (TextView) findViewById(R.id.callinfo);
        custaddress = (TextView) findViewById(R.id.custaddress);
        svcname = (TextView) findViewById(R.id.svcname);
        svcinfo = (TextView) findViewById(R.id.svcinfo);
        countdown = (TextView) findViewById(R.id.countdown);
        svcaddress = (TextView) findViewById(R.id.svcaddress);
        custcallone = (ImageView) findViewById(R.id.custcallone);
        custcalltwo = (ImageView) findViewById(R.id.custcalltwo);
        svccall = (ImageView) findViewById(R.id.svccall);
        taskButton = (Button) findViewById(R.id.taskButton);
        ledbutton = (ImageView) findViewById(R.id.ledbutton);

        fileCount = (TextView)findViewById(R.id.fileCount);

        ProgressBar spinner = (ProgressBar) findViewById(R.id.progressBar);
        overlay = (FrameLayout) findViewById(R.id.overlay);
        ImageView imageview = (ImageView) findViewById(R.id.imageView);
        ImageView imageview2 = (ImageView) findViewById(R.id.imageView2);
        tickettimer = (TextView) findViewById(R.id.tickettimer);
        overlay.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.GONE);
        RatingBar ambRatingbar = (RatingBar) findViewById(R.id.ambRatingbar);
        ambRatingbar.setStepSize(0.1f);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        ambRatingbar.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                displayOverlay();
                String data;
                data = "AMBREPORT|5|";
                Intent intent = new Intent();
                intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                intent.putExtra("sendchat", data);
                sendBroadcast(intent);
                return true;
            }
        });

        callinfo.setText("");
        latlongtosubmit = "";
        // taskButton.setText("Awaiting Location Please Wait");

        String rootb = Environment.getExternalStorageDirectory().toString();
        File directory = new File(rootb + "/PhotoDir");
        File listOfFile[] = directory.listFiles();
        fileCount.setText(String.valueOf(listOfFile.length));

        taskButton.setAlpha(0.2f);
        taskButton.setEnabled(false);
        taskbuttonactive = 0;

        TextView loginIDMenu = (TextView) findViewById(R.id.ambName);
        loginIDMenu.setText(MenuClass.loginId);

        final ImageView profileimg = (ImageView) findViewById(profileImg);
        Timer imageCheckTimer;

        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
        String root1 = Environment.getExternalStorageDirectory().toString() + "/21NorthAmb";
        File file1 = new File(root1, formatter.format(today) + "-signin.jpg");
        Uri filepath1 = Uri.parse(root1 + "/" + formatter.format(today) + "-signin.jpg");

        if (file1.exists()) {
            profileimg.setImageURI(filepath1);
        } else {

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

                            // (2) create a date "formatter" (the date format we want)
                            SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");

                            ImageRequest imgRequest = new ImageRequest("http://m.21north.in:7410/images/" + MenuClass.ambId + "-" + formatter.format(today) + "-signin.jpg", new Response.Listener<Bitmap>() {
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

        prgmNameList = new String[MenuClass.menuList.size() + 4][2];
        prgmImages = new int[MenuClass.menuList.size() + 4];

        for (int i = 0; i < MenuClass.menuList.size(); i++) {
            prgmNameList[i][0] = MenuClass.menuList.get(i).menuItemID;
            prgmNameList[i][1] = MenuClass.menuList.get(i).menuTitle;
        }

        prgmNameList[MenuClass.menuList.size()][0] = "-2";
        prgmNameList[MenuClass.menuList.size()][1] = getResources().getString(R.string.complaints);

        prgmNameList[MenuClass.menuList.size() + 1][0] = "-3";
        prgmNameList[MenuClass.menuList.size() + 1][1] = getResources().getString(R.string.refer_and_earn);

        prgmNameList[MenuClass.menuList.size() + 2][0] = "-4";
        prgmNameList[MenuClass.menuList.size() + 2][1] = getResources().getString(R.string.news);

        prgmNameList[MenuClass.menuList.size() + 3][0] = "-5";
        prgmNameList[MenuClass.menuList.size() + 3][1] = getResources().getString(R.string.training);

        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(new CustomAdapter(this, prgmNameList, prgmImages));


        relativeLayout = (RelativeLayout) findViewById(R.id.relLayout);
        System.out.println("Displaying Window\n");
        if (bundle != null) {
            if (bundle.getString("qdata") != null) {
                System.out.println("Time Activetask: " + (System.currentTimeMillis()) / 1000);
                String sq = bundle.getString("qdata");
                //String s = bundle.getString("data");
                //typeofCommand = s.split("\\|");
                if (sq != null) {
                    queuestate = sq.split("\\$");

                    pickupLat = Double.parseDouble(queuestate[8]);
                    pickupLong = Double.parseDouble(queuestate[9]);
                    dropLat = Double.parseDouble(queuestate[11]);
                    dropLong = Double.parseDouble(queuestate[12]);

                    typeofsvc.setText(queuestate[50]);

                    Login.vehicleType = queuestate[49];

                    int mainamount = 0;
                    state = Integer.parseInt(queuestate[1]);
//                    if (state == 100 || state == 110 || state == 400 || state == 410 || state == 602 || state == 603 || state == 612 || state == 613 || state == 702 || state == 703 || state == 722 || state == 723 || state == 802 || state == 803 || state == 902 || state == 903 || state == 2100 || state == 2200 || state == 2300 || state == 2400 || state == 3100 || state == 3200 || state == 3300 || state == 5200 || state == 5300 || state == 5500) {

                        if (SocketService.timeDiff()) {
                            latlongtosubmit = SocketService.latitude + "|" + SocketService.longitude + "|";
                            if (state != 410 && state != 612 && state != 613 && state != 5300) {
                                taskButton.setEnabled(true);
                                taskButton.setAlpha(1.0f);
                                taskbuttonactive = 1;
                            } else {
                                if (SocketService.distanceBtwPoints()) {
                                    changeButtonText("1");
                                }
                            }
//                        }
                    }


                    if (isInteger(queuestate[18])) {
                        mainamount = Integer.parseInt(queuestate[18]);
                    }
                    int subamount = 0;
                    if (isInteger(queuestate[19])) {
                        subamount = Integer.parseInt(queuestate[19]);
                    }

                    totalamount = mainamount + subamount;

                    final int seccompleted = Integer.parseInt(queuestate[45]);

                    if (seccompleted == 0) {
                        tickettimer.setText("");
                    }
                    if (seccompleted > 0) {
                        new CountDownTimer(30000 * 1000, 1000) {

                            public void onTick(long millisUntilFinished) {

                                long minutes = ((30000 * 1000 - millisUntilFinished + seccompleted * 1000) / 1000) / 60;
                                long seconds = ((30000 * 1000 - millisUntilFinished + seccompleted * 1000) / 1000) % 60;
                                tickettimer.setText("Ticket Generated " + String.valueOf(minutes) + ":" + String.valueOf(seconds) + " minutes ago.");
                            }

                            public void onFinish() {
                                countdown.setText(R.string.banned);
                                taskButton.setVisibility(View.INVISIBLE);
                                finish();
                            }
                        }.start();
                    }

                    if (queuestate.length > 1) {

                        countdown.setVisibility(View.INVISIBLE);
                        state = Integer.parseInt(queuestate[1]);

                        Intent sisintent = new Intent();
                        sisintent.setAction("com.north.socket.client.LBBroadcastReceiverFile");
                        if(state == 110 || state == 410 || state == 612 || state == 613 || state == 722 || state == 723 || state == 2200 || state == 2400 || state == 4300 || state == 4500 || state == 5300 || state == 5500 || state == 6300 || state == 6500 || state == 7300 || state == 7500 || state == 7900){
                            sisintent.putExtra("shouldisend", "1");
                        }else{
                            sisintent.putExtra("shouldisend", "0");
                        }
                        sendBroadcast(sisintent);

                        if ((state == 100) || (state == 602) || (state == 603) || (state == 2100) || (state == 3100)) {
                            licnumber.setVisibility(View.INVISIBLE);
                            brand.setVisibility(View.INVISIBLE);
                            custname.setVisibility(View.INVISIBLE);
                            custaddress.setVisibility(View.INVISIBLE);
                            svcname.setVisibility(View.INVISIBLE);
                            svcaddress.setVisibility(View.INVISIBLE);
                            custcalltwo.setVisibility(View.INVISIBLE);
                            custcallone.setVisibility(View.INVISIBLE);
                            svccall.setVisibility(View.INVISIBLE);
                            typeofsvc.setVisibility(View.INVISIBLE);
                            topinfo.setVisibility(View.INVISIBLE);
                            custinfo.setVisibility(View.INVISIBLE);
                            svcinfo.setVisibility(View.INVISIBLE);
                            imageview.setVisibility(View.INVISIBLE);
                            imageview2.setVisibility(View.INVISIBLE);
                            ledbutton.setVisibility(View.INVISIBLE);
                            rating.setVisibility(View.INVISIBLE);
                            ambRatingbar.setVisibility(View.INVISIBLE);
                            countdown.setTextColor(Color.RED);
//                    countdown.setVisibility(View.VISIBLE);
                            if (!queuestate[42].equals("(null)")) {
                                new CountDownTimer(Integer.parseInt(queuestate[42]) * 1000, 1000) {

                                    public void onTick(long millisUntilFinished) {
                                        taskButton.setText("Start Task - " + millisUntilFinished / 1000);
                                    }

                                    public void onFinish() {
                                        countdown.setText(R.string.banned);
                                        Intent intentb = new Intent();
                                        intentb.setAction("com.north.socket.client.POSSNBroadcastReceiverawaiting");
                                        intentb.putExtra("Banned", "true");
                                        taskButton.setVisibility(View.INVISIBLE);
                                        sendBroadcast(intentb);
                                        finish();

                                    }
                                }.start();
                            }

                        }
                        licnumber.setText(queuestate[28]);
                        brand.setText(queuestate[30]);


                        custname.setText(queuestate[31]);
                        custaddress.setText(queuestate[7]);
                        svcname.setText(queuestate[13]);
                        svcaddress.setText(queuestate[35]);
                        if (!queuestate[43].equals("(null)")) {
                            ambRatingbar.setRating(Float.parseFloat(queuestate[43]));
                            rating.setText(queuestate[43]);
                        } else {
                            ambRatingbar.setRating(5.0f);
                            rating.setText("5.0");
                        }
                        if (queuestate[33].equals("0")) {
                            custcallone.setAlpha(0.2f);
                            custcallone.setEnabled(false);
                        }

                        if (queuestate[41].equals("2")) {

                            svccall.setVisibility(View.INVISIBLE);
                            svcinfo.setText("");
                        }

                        switch (state) {
                            case 110:
                                hideSvc();
                                taskButton.setText(R.string.stop_task);
                                stopAlarm();
                                break;
                            case 400:
                                hideCust();
                                startAlarm();
                                taskButton.setText(R.string.start_task);
                                break;
                            case 410:
                                hideCust();
                                stopAlarm();
                                taskButton.setText(R.string.stop_task);
                                break;
                            case 402:
                                startAlarm();
                                hideCust();
                                taskButton.setText(R.string.start_task);
                                break;
                            case 412:
                                hideCust();
                                stopAlarm();
                                taskButton.setText(R.string.stop_task);
                                break;
                            case 403:
                                startAlarm();
                                hideCust();
                                taskButton.setText(R.string.start_task);
                                break;
                            case 413:
                                stopAlarm();
                                hideCust();
                                taskButton.setText(R.string.stop_task);
                                break;

                            case 612:
                                stopAlarm();
                                hideCust();
                                topinfo.setText(R.string.go_to_svc);
                                typeofsvc.setText(R.string.unpaid);
                                typeofsvc.setTextColor(ContextCompat.getColor(this,R.color.unpaid));
                                typeofsvc.setTypeface(null, Typeface.BOLD);
                                taskButton.setText(R.string.stop_task);
                                break;
                            case 613:
                                hideCust();
                                stopAlarm();
                                topinfo.setText(R.string.go_to_svc);
                                taskButton.setText(R.string.stop_task);
                                typeofsvc.setText(R.string.paid);
                                typeofsvc.setTextColor(ContextCompat.getColor(this,R.color.paid));
                                typeofsvc.setTypeface(null, Typeface.BOLD);
                                break;
                            case 702:
                                hideSvc();
                                topinfo.setText(getString(R.string.go_to_customer) + " : Rs " + String.valueOf(Integer.parseInt(queuestate[18]) + Integer.parseInt(queuestate[19])));
                                taskButton.setText(R.string.start_task);
                                typeofsvc.setText(R.string.unpaid);
                                typeofsvc.setTextColor(ContextCompat.getColor(this,R.color.unpaid));
                                typeofsvc.setTypeface(null, Typeface.BOLD);
                                break;
                            case 703:
                                hideSvc();
                                topinfo.setText(getString(R.string.go_to_customer) + " " + getString(R.string.bill_paid));
                                taskButton.setText(R.string.start_task);
                                typeofsvc.setText(R.string.paid);
                                typeofsvc.setTextColor(ContextCompat.getColor(this,R.color.paid));
                                typeofsvc.setTypeface(null, Typeface.BOLD);
                                break;
                            case 722:
                                hideSvc();
                                topinfo.setText(getString(R.string.go_to_customer) + " : Rs " + String.valueOf(Integer.parseInt(queuestate[18]) + Integer.parseInt(queuestate[19])));
                                taskButton.setText(R.string.stop_task);
                                typeofsvc.setText(R.string.unpaid);
                                typeofsvc.setTextColor(ContextCompat.getColor(this,R.color.unpaid));
                                typeofsvc.setTypeface(null, Typeface.BOLD);
                                break;
                            case 723:
                                hideSvc();
                                topinfo.setText(getString(R.string.go_to_customer) + " " + getString(R.string.bill_paid));
                                taskButton.setText(R.string.stop_task);
                                typeofsvc.setText(R.string.paid);
                                typeofsvc.setTextColor(ContextCompat.getColor(this,R.color.paid));
                                typeofsvc.setTypeface(null, Typeface.BOLD);
                                break;
                            case 802:
                                hideSvc();
                                topinfo.setText(getString(R.string.go_to_customer) + " : Rs " + String.valueOf(Integer.parseInt(queuestate[18]) + Integer.parseInt(queuestate[19])));
                                taskButton.setText(R.string.customer_present_absent);
                                typeofsvc.setText(R.string.unpaid);
                                typeofsvc.setTextColor(ContextCompat.getColor(this,R.color.unpaid));
                                typeofsvc.setTypeface(null, Typeface.BOLD);
                                break;
                            case 803:
                                hideSvc();
                                topinfo.setText(getString(R.string.go_to_customer) + " " + getString(R.string.bill_paid));
                                taskButton.setText(R.string.customer_present_absent);
                                typeofsvc.setText(R.string.paid);
                                typeofsvc.setTextColor(ContextCompat.getColor(this,R.color.paid));
                                typeofsvc.setTypeface(null, Typeface.BOLD);
                                break;
                            case 902:
                                hideSvc();
                                topinfo.setText(getString(R.string.go_to_customer) + " : Rs " + String.valueOf(Integer.parseInt(queuestate[18]) + Integer.parseInt(queuestate[19])));
                                taskButton.setText(R.string.start_task);
                                typeofsvc.setText(R.string.unpaid);
                                typeofsvc.setTextColor(ContextCompat.getColor(this,R.color.unpaid));
                                typeofsvc.setTypeface(null, Typeface.BOLD);
                                break;
                            case 903:
                                hideSvc();
                                topinfo.setText(getString(R.string.go_to_customer) + " " + getString(R.string.bill_paid));
                                taskButton.setText(R.string.start_task);
                                typeofsvc.setText(R.string.paid);
                                typeofsvc.setTextColor(ContextCompat.getColor(this,R.color.paid));
                                typeofsvc.setTypeface(null, Typeface.BOLD);
                                break;
                            case 2100:
                                hideSvc();
                                topinfo.setText(R.string.chauffeur_accept_task);
                                taskButton.setText(R.string.start_task);
                                startAlarm();
                                break;
                            case 2200:
                                hideSvc();
                                topinfo.setText(R.string.chauffeur_go_to_cust);
                                taskButton.setText(R.string.stop_task);
                                stopAlarm();
                                break;
                            case 2300:
                                hideSvc();
                                topinfo.setText(R.string.chauffeur_start_service);
                                taskButton.setText(R.string.start_chauffeur_trip);
                                break;
                            case 2400:
                                hideSvc();
                                topinfo.setText(R.string.chauffeur_active_service);
                                taskButton.setText(R.string.stop_chauffeur_trip);
                                break;
                            case 3000:
                                hideSvc();
                                topinfo.setText(R.string.rsa_accept_task);
                                taskButton.setText(R.string.start_task);
                                break;
                            case 3110:
                                hideSvc();
                                topinfo.setText(R.string.rsa_go_to_cust);
                                taskButton.setText(R.string.awaiting_payment);
                                startAlarm();
                                break;
                            case 3200:
                                hideSvc();
                                topinfo.setText(R.string.rsa_go_to_cust);
                                taskButton.setText(R.string.stop_task_at_cust);
                                stopAlarm();
                                break;
                            case 3300:
                                hideSvc();
                                topinfo.setText(R.string.rsa_active_service);
                                taskButton.setText(R.string.start_task);
                                break;

                            case 3320:
                                hideCust();
                                topinfo.setText(R.string.rsa_active_service);
                                taskButton.setText(R.string.stop_task_at_svc);
                                break;

                            case 3500:
                                hideCust();
                                topinfo.setText(R.string.rsa_active_service);
                                taskButton.setText(R.string.stop_task);
                                break;
                            case 3503:
                                hideCust();
                                topinfo.setText(R.string.rsa_active_service);
                                taskButton.setText(R.string.stop_task);
                                break;

                            case 4200:
                                hideSvc();
                                topinfo.setText(R.string.internal_move_active);
                                taskButton.setText(R.string.start_task);
                                break;

                            case 4300:
                                hideSvc();
                                topinfo.setText(R.string.internal_move_active);
                                taskButton.setText(R.string.stop_task);
                                break;

                            case 4500:
                                hideCust();
                                topinfo.setText(R.string.internal_move_active);
                                taskButton.setText(R.string.at_svc_stop_task);
                                break;

                            case 5200:
                                hideCust();
                                topinfo.setText(R.string.home_delivery_active);
                                taskButton.setText(R.string.start_task_go_to_svc);
                                break;

                            case 5300:
                                hideCust();
                                topinfo.setText(R.string.home_delivery_active);
                                taskButton.setText(R.string.stop_task_at_svc);
                                break;

                            case 5500:
                                hideSvc();
                                topinfo.setText(R.string.home_delivery_active);
                                taskButton.setText(R.string.stop_task_at_cust);
                                break;

                            case 6200:
                                hideSvc();
                                topinfo.setText(R.string.stockyard_active);
                                taskButton.setText(R.string.start_task);
                                break;

                            case 6300:
                                hideSvc();
                                topinfo.setText(R.string.stockyard_active);
                                taskButton.setText(R.string.stop_task);
                                break;

                            case 6500:
                                hideCust();
                                topinfo.setText(R.string.stockyard_active);
                                taskButton.setText(R.string.stop_task);
                                break;

                            case 7200:
                                hideCust();
                                topinfo.setText(R.string.test_drive_active);
                                taskButton.setText(R.string.start_task_go_to_svc);
                                break;

                            case 7300:
                                hideCust();
                                topinfo.setText(R.string.test_drive_active);
                                taskButton.setText(R.string.stop_task_at_svc);
                                break;

                            case 7500:
                                hideSvc();
                                topinfo.setText(R.string.test_drive_active);
                                taskButton.setText(R.string.stop_task_at_cust);
                                break;

                            case 7600:
                                hideSvc();
                                topinfo.setText(R.string.test_drive_active);
                                taskButton.setText(R.string.start_test_drive);
                                break;

                            case 7700:
                                hideSvc();
                                topinfo.setText(R.string.test_drive_active);
                                taskButton.setText(R.string.stop_test_drive);
                                break;

                            case 7800:
                                hideCust();
                                topinfo.setText(R.string.test_drive_active);
                                taskButton.setText(R.string.start_task_go_to_svc);
                                break;

                            case 7900:
                                hideCust();
                                topinfo.setText(R.string.test_drive_active);
                                taskButton.setText(R.string.stop_task_at_svc);
                                break;
                        }
                        if (state < 500) {
                            try {
                                //hideSvc();
                                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                final Date dateObj = sdf.parse(queuestate[6]);
                                final Date dateObjeta = sdf.parse(queuestate[44]);
                                System.out.println(dateObj);
                                System.out.println("Check : " + new SimpleDateFormat("K:mm").format(dateObj));
                                topinfo.setText(getString(R.string.pickup) + new SimpleDateFormat("hh:mm a").format(dateObj) + " | " + getString(R.string.eta) + new SimpleDateFormat("hh:mm a").format(dateObjeta));
                            } catch (final ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        if ((state > 502) && (state < 1003)) {
                            //hideCust();
                            //topinfo.setText("Dropoff Vehicle : Rs "+typeofCommand[9]+"/-");
                            try {
                                //hideSvc();
                                if (!queuestate[39].equals("(null)")) {
                                    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                    final Date dateObj = sdf.parse(queuestate[39]);

                                    final Date dateObjeta = sdf.parse(queuestate[44]);
                                    System.out.println(dateObj);
                                    System.out.println(new SimpleDateFormat("K:mm").format(dateObj));
                                    topinfo.setText(getString(R.string.drop) + new SimpleDateFormat("hh:mm a").format(dateObj) + " | " + getString(R.string.amount) + totalamount + " | " + getString(R.string.eta) + new SimpleDateFormat("hh:mm a").format(dateObjeta));

                                }
                            } catch (final ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        if ((state > 1900) && (state < 2400)) {
                            //hideCust();
                            //topinfo.setText("Dropoff Vehicle : Rs "+typeofCommand[9]+"/-");
                            try {
                                //hideSvc();
                                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                final Date dateObj = sdf.parse(queuestate[6]);
                                final Date dateObjeta = sdf.parse(queuestate[44]);
                                System.out.println(dateObj);
                                System.out.println(new SimpleDateFormat("K:mm").format(dateObj));
                                //topinfo.setText("Chauffeur Service Needed Immediately");
                                topinfo.setText(getString(R.string.go_to_customer) + new SimpleDateFormat("K:mm a").format(dateObj) + " | ETA:" + new SimpleDateFormat("K:mm a").format(dateObjeta));
                            } catch (final ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        if ((state > 2900) && (state < 3400)) {
                            //hideCust();
                            //topinfo.setText("Dropoff Vehicle : Rs "+typeofCommand[9]+"/-");
                            try {
                                //hideSvc();
                                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                final Date dateObj = sdf.parse(queuestate[6]);
                                final Date dateObjeta = sdf.parse(queuestate[44]);
                                System.out.println(dateObj);
                                System.out.println(new SimpleDateFormat("K:mm").format(dateObj));
                                //topinfo.setText("Chauffeur Service Needed Immediately");
                                topinfo.setText("Drop:" + new SimpleDateFormat("K:mm a").format(dateObj) + " | ETA:" + new SimpleDateFormat("K:mm a").format(dateObjeta));
                            } catch (final ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        if ((state == 410) || (state == 612) || (state == 613)) {
                            taskButton.setText(R.string.not_at_svc_stop_task);
                            taskbuttonactive = 0;
                            taskButton.setEnabled(false);
                        }
                    }
                }
            }

            ViewGroup.MarginLayoutParams taskButtonparams = (ViewGroup.MarginLayoutParams) taskButton.getLayoutParams();
            taskButtonparams.bottomMargin = 30;

            listofButtons.clear();
            buttonID.clear();
            innertext.clear();
            numberOfElements.clear();
            for (int i = 0; i < TicketClass.list.size(); i++) {
                listofButtons.add(TicketClass.list.get(i).ticketText);
                buttonID.add(Integer.parseInt(TicketClass.list.get(i).ticketID));
                innertext.add(TicketClass.list.get(i).innerText);
                numberOfElements.add(Integer.parseInt(TicketClass.list.get(i).numberOfField));
            }

            LinearLayout layoutleft = (LinearLayout) findViewById(R.id.leftLinearlayout);
            LinearLayout layoutright = (LinearLayout) findViewById(R.id.rightLinearlayout);
            for (int i = 0; i < listofButtons.size(); i++) {
                final Button btn;
                btn = new Button(this);
                btn.setId(buttonID.get(i));
                btn.setOnClickListener(btnclick);
                btn.setText(listofButtons.get(i));
                btn.setTextSize(14);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.bottomMargin = 10;
                btn.setLayoutParams(params);

                if ((i % 2) == 0) {
                    layoutleft.addView(btn);
                } else {
                    layoutright.addView(btn);
                }
            }
            int enableid = Integer.parseInt(queuestate[38]);
            if (enableid != 0) {
                Button enablebutton = (Button) findViewById(enableid);
                //enablebutton.setEnabled(true);
                if (enablebutton != null) {
                    for (int z = 0; z < buttonID.size(); z++) {
                        Button disablebutton = (Button) findViewById(buttonID.get(z));
                        if (disablebutton != null) {
                            disablebutton.setEnabled(false);
                            disablebutton.setTextColor(ContextCompat.getColor(this,R.color.black));
                        }
                    }
                    enablebutton.setTextColor(ContextCompat.getColor(this,R.color.white));
                }
            }
//        }
            if ((state == 802) || (state == 803)) {
                enableid = Integer.parseInt(queuestate[38]);
                if (enableid == 37) {
                    taskButton.setText("Please wait.. Customer Support will call.");
                    taskButton.setEnabled(false);
                }
            }
            if ((state == 612) || (state == 613) || (state == 702) || (state == 703) || (state == 722) || (state == 723)) {


                enableid = Integer.parseInt(queuestate[38]);
                if (enableid != 0) {
                    for (int z = 0; z < listofButtons.size(); z++) {
                        Button disablebutton = (Button) findViewById(buttonID.get(z));
                        if (disablebutton != null) {
                            disablebutton.setEnabled(false);
                            disablebutton.setTextColor(ContextCompat.getColor(this,R.color.black));
                        }
                    }
                    Button enablebutton = (Button) findViewById(enableid);
                    //enablebutton.setEnabled(true);
                    if (enablebutton != null) {
                        enablebutton.setTextColor(ContextCompat.getColor(this,R.color.white));
                    }
                }
            }
        }
    }

    public void displayOverlay() {
        stopAlarm();
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
            } catch (Exception e) {
                // added try catch block to be sure of uninterupted execution
            }

        }
    };

    public void taskHistory(View v) {
        displayOverlay();
        String data = "";
        data = "AMBREPORT|16|";
        Intent intent = new Intent();
        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
        intent.putExtra("sendchat", data);
        sendBroadcast(intent);
    }


    public static boolean isInteger(String s) {
        return isInteger(s, 10);
    }

    public static boolean isInteger(String s, int radix) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(s.charAt(i), radix) < 0) return false;
        }
        return true;
    }

    void createTicketNoOpt(int ticketnumber) {
        displayOverlay();
        Intent intent = new Intent();
        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
        String data;// = "sendchat", "TICKET‰" + typeofCommand[1] + "‰" + ticketnumber + "‰‰‰";
        data = "GENTICKETNEW|" + queuestate[0] + "|" + ticketnumber + "|0|0|";
        intent.putExtra("sendchat", data);
        sendBroadcast(intent);
    }

    void createTicketOpt(int numdata, int typeofticket, String data1text, String data2text, int ticketnum, String buttontext) {
        btnback = new Button(ActiveTask.this);
        btnback.setId(100);
        btnback.setOnClickListener(btnclick);
        relativeLayout.addView(btnback);
        btnback.setWidth(relativeLayout.getWidth());
        btnback.setHeight(relativeLayout.getHeight());
        btnback.setBackgroundColor(Color.BLACK);
        data1txt = new EditText(ActiveTask.this);
        data1txt.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        data1txt.setText(data1text);
        data1txt.setSingleLine(false);
        data1txt.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        data1txt.setBackgroundResource(R.color.white);
        relativeLayout.addView(data1txt);
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) data1txt.getLayoutParams();
        lp.topMargin = 20;
        chatticketnumber = ticketnum;
        data1txt.setWidth(relativeLayout.getWidth());
        data1txt.setLayoutParams(lp);
        if (numdata == 2) {
            data2txt = new EditText(ActiveTask.this);
            //nametxt.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            data2txt.setText(data2text);
            data2txt.setSingleLine(true);
            //nametxt.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
            data2txt.setBackgroundResource(R.color.white);
            relativeLayout.addView(data2txt);
            ViewGroup.MarginLayoutParams lpconvreason = (ViewGroup.MarginLayoutParams) data2txt.getLayoutParams();
            lpconvreason.topMargin = 100;
            data2txt.setWidth(relativeLayout.getWidth());
            data2txt.setLayoutParams(lpconvreason);
        }

        btndata = new Button(ActiveTask.this);
        btndata.setId(typeofticket);
        btndata.setOnClickListener(btnclick);
        relativeLayout.addView(btndata);
        ViewGroup.MarginLayoutParams lpbut = (ViewGroup.MarginLayoutParams) btndata.getLayoutParams();
        lpbut.topMargin = 300;
        if (numdata == 2) {
            lpbut.topMargin = 360;
        }
        btndata.setBackgroundColor(Color.parseColor("#F28500"));
        btndata.setLayoutParams(lpbut);
        btndata.setWidth(relativeLayout.getWidth());
        btndata.setText(buttontext);

        ScrollView sv = (ScrollView) findViewById(R.id.scrolllayout);
        sv.scrollTo(0, sv.getTop());

        btndata.requestFocus();
        InputMethodManager immaddress = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        immaddress.showSoftInput(data1txt, InputMethodManager.SHOW_IMPLICIT);

    }

    View.OnClickListener btnclick = new View.OnClickListener() {

        @Override
        public void onClick(final View view) {


            stopAlarm();
            if (view.getId() == 100) {
                Log.i("TAG", "The index is" + view.getId());
                if (btnback != null) {
                    btnback.setVisibility(View.GONE);
                }
                if (data1txt != null) {
                    data1txt.setVisibility(View.GONE);
                }
                if (data2txt != null) {
                    data2txt.setVisibility(View.GONE);
                }
                if (btndata != null) {
                    btndata.setVisibility(View.GONE);
                }

            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActiveTask.this);
                alertDialogBuilder.setMessage(R.string.are_you_sure);
                alertDialogBuilder.setPositiveButton(R.string.OK,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                int indexNo = buttonID.indexOf(view.getId());
                                if (view.getId() < 99 && indexNo >= 0) {

                                    int numberOfData = numberOfElements.get(indexNo);
                                    switch (numberOfData) {
                                        case 0:
                                            createTicketNoOpt(buttonID.get(indexNo));
                                            break;
                                        case 1:
                                            createTicketOpt(numberOfData, 101, "", "", buttonID.get(indexNo), innertext.get(indexNo));
                                            break;
                                        case 2:
                                            createTicketOpt(numberOfData, 101, "", "", buttonID.get(indexNo), innertext.get(indexNo));
                                            break;
                                    }
                                } else {
                                    switch (view.getId()) {
                                        case 101:
                                            Log.i("TAG", "The index is" + view.getId());
                                            View viewthirteen = ActiveTask.this.getCurrentFocus();
                                            if (viewthirteen != null) {
                                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                imm.hideSoftInputFromWindow(viewthirteen.getWindowToken(), 0);
                                            }
                                            if (btnback != null) {
                                                btnback.setVisibility(View.GONE);
                                            }
                                            if (data1txt != null) {
                                                data1txt.setVisibility(View.GONE);
                                            }
                                            if (data2txt != null) {
                                                if (data1txt != null) {
                                                    data1txt.setVisibility(View.GONE);
                                                }
                                            }
                                            if (btndata != null) {
                                                btndata.setVisibility(View.GONE);
                                            }
                                            String data = "";
                                            if (data1txt != null) {
                                                //data = "TICKET‰" + typeofCommand[1] + "‰" + chatticketnumber + "‰" + data1txt.getText().toString() + "‰‰";
                                                data = "GENTICKETNEW|" + queuestate[0] + "|" + chatticketnumber + "|" + data1txt.getText().toString() + "|0|";
                                                if (data2txt != null) {
                                                    //  data = "TICKET‰" + typeofCommand[1] + "‰" + chatticketnumber + "‰" + data1txt.getText().toString() + "‰" + data2txt.getText().toString() + "‰";
                                                    data = "GENTICKETNEW|" + queuestate[0] + "|" + chatticketnumber + "|" + data1txt.getText().toString() + "|" + data2txt.getText().toString() + "|";
                                                }
                                            }
                                            displayOverlay();
                                            Intent intent = new Intent();
                                            intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                                            intent.putExtra("sendchat", data);
                                            sendBroadcast(intent);
                                            break;
                                        case 100:
                                            Log.i("TAG", "The index is" + view.getId());
                                            if (btnback != null) {
                                                btnback.setVisibility(View.GONE);
                                            }
                                            if (data1txt != null) {
                                                data1txt.setVisibility(View.GONE);
                                            }
                                            if (data2txt != null) {
                                                data2txt.setVisibility(View.GONE);
                                            }
                                            if (btndata != null) {
                                                btndata.setVisibility(View.GONE);
                                            }
                                            break;
                                        default:
                                            break;
                                    }
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

        }
    };

    public void openMenu(View view) {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    public void hideSvc() {
        svcname.setAlpha(0.2f);
        svcinfo.setAlpha(0.2f);
        svcaddress.setAlpha(0.2f);
        svccall.setAlpha(0.2f);

        custname.setAlpha(1f);
        custinfo.setAlpha(1f);
        custaddress.setAlpha(1f);
        custcallone.setEnabled(true);
        custcallone.setAlpha(1f);
        if (queuestate[33].equals("0")) {
            custcalltwo.setAlpha(0.2f);
            custcalltwo.setEnabled(false);
        }
    }


    public void startAlarm() {
        Intent intent = new Intent();
        intent.setAction("com.north.socket.client.BroadcastReceiver");
        intent.putExtra("senddata", "startalarm");
        sendBroadcast(intent);
    }


    public void stopAlarm() {
        Intent intentstop = new Intent();
        intentstop.setAction("com.north.socket.client.LBBroadcastReceiver");
        intentstop.putExtra("senddata", "stopalarm");
        sendBroadcast(intentstop);

        Intent intent = new Intent();
        intent.setAction("com.north.socket.client.BroadcastReceiver");
        intent.putExtra("senddata", "stopalarm");
        sendBroadcast(intent);


    }

    public void hideCust() {
        svcname.setAlpha(1f);
        svcinfo.setAlpha(1f);
        svcaddress.setAlpha(1f);
        svccall.setAlpha(1f);

        custname.setAlpha(0.2f);
        custinfo.setAlpha(0.2f);
        custaddress.setAlpha(0.2f);
        custcallone.setAlpha(0.2f);
        if ((queuestate[1].equals("410")) || (queuestate[1].equals("722")) || (queuestate[1].equals("723"))) {
            custcalltwo.setAlpha(0.2f);
            custcalltwo.setEnabled(false);
        }
    }

    public void custGoogle(View v) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + queuestate[8] + "," + queuestate[9]);
        if (state < 500) {
            gmmIntentUri = Uri.parse("google.navigation:q=" + queuestate[8] + "," + queuestate[9]);
        }
        if (state > 500) {
            gmmIntentUri = Uri.parse("google.navigation:q=" + queuestate[11] + "," + queuestate[12]);
        }
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    public void onResume() {
        super.onResume();
        if (myReceiverpossn == null) {
            myReceiverpossn = new ConnectivityReceiver();
            IntentFilter filter = new IntentFilter("com.north.socket.client.POSSNBroadcastReceiver");
            registerReceiver(myReceiverpossn, filter);
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

    public void svcGoogle(View v) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + queuestate[36] + "," + queuestate[37]);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }


    public void callCustone(View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.are_you_sure_cust_call);
        alertDialogBuilder.setPositiveButton(R.string.OK,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        displayOverlay();
                        String data;
                        data = "CUSTOMERCALL|1|";
                        Intent intent = new Intent();
                        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                        intent.putExtra("sendchat", data);
                        sendBroadcast(intent);

                        custcallone.setEnabled(false);
                        custcalltwo.setEnabled(false);
                        //callsupport.setEnabled(false);
                        svccall.setEnabled(false);
                        timeoutCalldisable();

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

    public void callCusttwo(View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.are_you_sure_cust_call);
        alertDialogBuilder.setPositiveButton(R.string.OK,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (!queuestate[33].equals("0")) {
                            displayOverlay();
                            String data;
                            data = "CUSTOMERCALL|2|";
                            Intent intent = new Intent();
                            intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                            intent.putExtra("sendchat", data);
                            sendBroadcast(intent);

                            custcallone.setEnabled(false);
                            custcalltwo.setEnabled(false);
                            //callsupport.setEnabled(false);
                            svccall.setEnabled(false);
                            timeoutCalldisable();
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

    public void svcManager(View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        if (Integer.parseInt(queuestate[1]) < 500) {
            alertDialogBuilder.setMessage(R.string.are_you_sure_SVC_M);

        } else {
            alertDialogBuilder.setMessage(R.string.are_you_sure_SVC_A);

        }
        alertDialogBuilder.setPositiveButton(R.string.OK,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        String data;
                        displayOverlay();
                        data = "CUSTOMERCALL|3|";
                        Intent intent = new Intent();
                        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                        intent.putExtra("sendchat", data);
                        sendBroadcast(intent);

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


    public void confTask(View v) {

        stopAlarm();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        int state = Integer.parseInt(queuestate[1]);
        if ((state == 802) || (state == 803)) {
            alertDialogBuilder.setMessage(R.string.is_customer_present_or_absent);
            alertDialogBuilder.setPositiveButton(R.string.customer_present,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            displayOverlay();

                            String data;
                            System.out.println(latlongtosubmit.length());
                            if (latlongtosubmit.length() > 1) {

                                data = "NEXTMOVE|" + latlongtosubmit + "1|" + queuestate[1] + "|########|####|";
                                Intent intent = new Intent();
                                intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                                intent.putExtra("sendchat", data);
                                sendBroadcast(intent);
                            }
                        }
                    });

            alertDialogBuilder.setNegativeButton(R.string.customer_absent,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            displayOverlay();
                            String data = "GENTICKETNEW|" + queuestate[0] + "|37|0|0|";
                            Intent intent = new Intent();
                            intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                            intent.putExtra("sendchat", data);
                            sendBroadcast(intent);
                            //finish();
                        }
                    });
        } else {

            alertDialogBuilder.setMessage(R.string.confirm_task);
            alertDialogBuilder.setPositiveButton(R.string.OK,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            displayOverlay();
                            String data;
                            if (latlongtosubmit.length() > 1) {
                                data = "NEXTMOVE|" + latlongtosubmit + "1|" + queuestate[1] + "|########|#####|";
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
        }
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void viewCash(View v) {

        displayOverlay();
        String data = "AMBREPORT|2|";
        Intent intent = new Intent();
        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
        intent.putExtra("sendchat", data);
        sendBroadcast(intent);
    }

    public void viewAttendance(View v) {
        displayOverlay();
        String data = "AMBREPORT|4|";
        Intent intent = new Intent();
        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
        intent.putExtra("sendchat", data);
        sendBroadcast(intent);
    }

    public void timeoutCallinfo() {

        resetcalltimer = new Timer();
        resetcalltimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setAction("com.north.socket.client.POSSNBroadcastReceiver");
                intent.putExtra("senddata", "resetcallinfo");
                sendBroadcast(intent);
            }
        }, 10 * 1000, 10 * 1000);
    }

    public void timeoutCalldisable() {

        timerdisable = new Timer();
        timerdisable.schedule(new TimerTask() {
            @Override
            public void run() {
                // Your database code here

                Intent intent = new Intent();
                intent.setAction("com.north.socket.client.POSSNBroadcastReceiver");
                intent.putExtra("senddata", "resetcalldisable");
                sendBroadcast(intent);
            }
        }, 20 * 1000, 20 * 1000);
    }

    @Override
    public void onBackPressed() {
        // do nothing.
        if (btnback != null) {
            btnback.setVisibility(View.GONE);
        }
        if (data1txt != null) {
            data1txt.setVisibility(View.GONE);
        }
        if (data2txt != null) {
            data2txt.setVisibility(View.GONE);
        }
        if (btndata != null) {
            btndata.setVisibility(View.GONE);
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

    public void changeButtonText(String atCheck) {
        if (state == 410 || state == 612 || state == 613) {
            if (atCheck.equals("1")) {
                taskButton.setText(R.string.at_svc_stop_task);
                taskbuttonactive = 1;
                taskButton.setAlpha(1.0f);
                taskButton.setEnabled(true);
            } else {
                taskButton.setText(R.string.not_at_svc_stop_task);
                taskbuttonactive = 0;
                taskButton.setAlpha(0.2f);
                taskButton.setEnabled(false);
            }
        }
    }

    public void address() {
        try {
            String stateview = "com.north.socket.client.AddressVerify";
            Intent activeview = new Intent(ActiveTask.this, Class.forName(stateview));
            activeview.putExtra("ambAddress", SocketService.ambAddress);
            startActivityForResult(activeview, 5);
        } catch (ClassNotFoundException ignored) {

        }
    }

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
                String displayLoadingScreen = intent.getStringExtra("DisplayOverlay");
                if (displayLoadingScreen != null) {
                    displayOverlay();
                }

                String ambreport = intent.getStringExtra("ambreport");
                if (ambreport != null) {
                    if (!ambreport.equals("none")) {
                        Intent activeview = new Intent(ActiveTask.this, ListHistory.class);
                        activeview.putExtra("data", ambreport);
                        startActivityForResult(activeview, 1);
                        overlay.setVisibility(View.INVISIBLE);
                    } else {

                        String notifyid = intent.getStringExtra("notifyID");
                        String check = "";

                        for (String[] aPrgmNameList : prgmNameList) {
                            if (notifyid.equals(aPrgmNameList[0])) {
                                check = aPrgmNameList[1];
                                break;
                            }
                        }

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActiveTask.this);
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
                String atSvc = intent.getStringExtra("AtSvc");
                if (atSvc != null) {
                    if (atSvc.equals("1")) {
                        System.out.println("At Service Station");
                        changeButtonText("1");
                    } else {
                        changeButtonText("0");
                    }
                }


                String callsuccess = intent.getStringExtra("callsuccess");
                if (callsuccess != null) {
                    callinfo.setText(callsuccess);
                    overlay.setVisibility(View.INVISIBLE);
                    if (resetcalltimer != null) {
                        resetcalltimer.cancel();
                    }
                    timeoutCallinfo();

                }

                String startclient = intent.getStringExtra("startclient");
                if (startclient != null) {
                    callinfo.setText("Network Error. Please wait. Trying to reconnect.");
                    if (resetcalltimer != null) {
                        resetcalltimer.cancel();
                    }
                    timeoutCallinfo();
                }

                String startedclient = intent.getStringExtra("startedclient");
                if (startedclient != null) {
                    callinfo.setText("Network Healthy.");
                    if (resetcalltimer != null) {
                        resetcalltimer.cancel();
                    }
                    timeoutCallinfo();
                }
                String pingreceived = intent.getStringExtra("pingreceived");
                if (pingreceived != null) {
                    if (!pingreceived.equals("1")) {
                        callinfo.setText("Network Healthy. Ping received in " + pingreceived + " seconds");
                    } else {
                        callinfo.setText("Network Healthy. Ping received in " + pingreceived + " second");
                    }
                    if (resetcalltimer != null) {
                        resetcalltimer.cancel();
                    }
                    timeoutCallinfo();
                }

                String checknetwork = intent.getStringExtra("checknetwork");
                if (checknetwork != null) {
                    callinfo.setText("Testing if Network is Healthy. Sending Ping");
                    if (resetcalltimer != null) {
                        resetcalltimer.cancel();
                    }
                    timeoutCallinfo();
                }
                String latlong;
                latlong = intent.getStringExtra("latlong");
                if (latlong != null) {
                    latlongtosubmit = latlong;

                    if (state == 410 || state == 612 || state == 613) {
                        if (SocketService.distanceBtwPoints()) {
                            changeButtonText("1");
                        } else {
                            changeButtonText("0");
                        }
                    } else if(state == 3110){
                        taskButton.setEnabled(false);
                    }else {
                        taskbuttonactive = 1;
                        taskButton.setAlpha(1.0f);
                        taskButton.setEnabled(true);
                    }
                }

                String data = intent.getStringExtra("senddata");
                if (data != null) {
                    String[] typeofCommand = data.split(":");
                    Log.d("LOG", "Received senddata : " + data);
                    if (typeofCommand[0].equals("NOTIFY")) {
                        notify = 1;
                        startAlarm();

                        Intent intentNotify = new Intent(context, ActiveTask.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intentNotify, PendingIntent.FLAG_ONE_SHOT);
                    }
                    switch (data) {
                        case "green":
                            //callinfo.setText("Position sent successfully.");
                            //timeoutCallinfo();
                            ledbutton.setImageResource(R.drawable.green);

                            break;
                        case "red":
                            callinfo.setText("Network Error. Please wait. Trying to reconnect.");
                            ledbutton.setImageResource(R.drawable.red);
                            if (resetcalltimer != null) {
                                resetcalltimer.cancel();
                            }
                            timeoutCallinfo();
                            break;
                        case "nolocation":
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ActiveTask.this);
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
                            break;
                        case "callsuccess":
                            callinfo.setText("Call Placed Successfully.");
                            break;
                        case "resetcallinfo":
                            callinfo.setText("");
                            if (resetcalltimer != null) {
                                resetcalltimer.cancel();
                            }
                            break;
                        case "resetcalldisable":
                            custcallone.setEnabled(true);
                            custcalltwo.setEnabled(true);
                            svccall.setEnabled(true);
                            //callsupport.setEnabled(true);
                            callinfo.setText("");
                            if (timerdisable != null) {
                                timerdisable.cancel();
                            }
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
