package com.north.socket.client;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.channels.FileChannel;

import needle.Needle;

import static android.content.ContentValues.TAG;
import static com.north.socket.client.AmbassadorApp.getContext;
import static com.north.socket.client.Login.takephoto;
import static com.north.socket.client.Login.vehicleType;

/*
 * Created by sree on 11/02/16.
 */
public class SocketService extends Service implements LocationListener {
    public static int qState;
    private static TCPClient mTcpClient = null;
    Application application;
    Reachability reachability;
    static Location templocation;
    static MediaPlayer mp;
    ConnectivityReceiver myReceiver;
    float version = 2.9f;
    String androidversion = "androidv030";
    float newversion = 0f;
    public static int disconnected = 0;
    int isLogged = 0;
    private PowerManager.WakeLock mWakeLock;

    Time t1;
    Time t2;
    String[] typeofCommand;

    static int disabled = 0;


    int simtest = 0;

    static String ambAddress;

    private Handler pingServer = new Handler();
    private Handler pingOpen = new Handler();
    private Handler retryServer = new Handler();

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 30; // 20 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60; // 30 seconds

    private static Context context;

    static double latitude;
    static double longitude;
    String logininfo = "";
    static int loginDone = 0;
    Location location = null;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    protected static LocationManager locationManager;
    static int state = 0;
    int dbopen = 0;
    String queueid = "0";
    static String svcLat = "0";
    static String svcLong = "0";
    static String range = "0";
    String prevTicketRaised = "0";

    static int startEssentialsCheck = 0;
    static long seconds = 0;
    static boolean permissionCheck = false;
    SQLiteDatabase mydatabase;
    String[] loginstate;

    int deviceInfoCheck = 0;

    static int signaturePage = 0;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, START_STICKY, startId);

        this.context = this;
        get_current_location();
        System.out.println("onStartCommand");
        return Service.START_STICKY;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }

        sendToast("Starting Service");
        Log.v("Service", "Starting Service");
        Intent intentb = new Intent();
        intentb.setAction("com.north.socket.client.POSSNBroadcastReceiver");
        intentb.putExtra("sendinfo", "Initializing PowerManager");
        sendBroadcast(intentb);


        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (powerManager != null) {
            mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "MyWakelockTag");
        }
        mWakeLock.acquire();
        mTcpClient = null;

        application = (Application) getContext();
        myReceiver = new ConnectivityReceiver();
        IntentFilter filter = new IntentFilter("com.north.socket.client.LBBroadcastReceiver");
        registerReceiver(myReceiver, filter);

        String deviceMan = android.os.Build.MANUFACTURER;


        Intent notificationIntent = new Intent(this, Login.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext());

        builder
                .setSmallIcon(R.drawable.launcher)
                .setContentTitle("Ambassador")
                .setContentText("Doing some work...")
                .setContentIntent(pendingIntent).build();

        Notification notification = builder.build();
        notification.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startForeground(1337, notification);

        String manufacturer = "xiaomi";
        if (manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
            //this will open auto start screen where user can enable permission for your app
            Intent intent1 = new Intent();
            intent1.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            startActivity(intent1);
        }

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();
        sendToast("Disabling Statusbar");

        seconds = System.currentTimeMillis() / 1000;
        sendToast("Checking if Network is Available.");
        if (Reachability.isNetworkingAvailable(application)) {

            intentb.putExtra("sendinfo", "Connecting to Socket Server...");
            sendBroadcast(intentb);
            Needle.onBackgroundThread().withThreadPoolSize(20).execute(new Runnable() {
                @Override
                public void run() {
                    runServer();
                    // something that we want to do serially but not on the main thread
                }
            });
        } else {
            sendToast("Networking Not Available.");
            intentb.putExtra("sendinfo", "Networking Not Available.");
            sendBroadcast(intentb);
        }

        locationManager = (LocationManager) application.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            get_current_location();
        }

    }
    public void sendLedNotification(String data) {
        Intent intentb = new Intent();
        intentb.setAction("com.north.socket.client.POSSNBroadcastReceiver");
        intentb.putExtra("senddata", data);
        sendBroadcast(intentb);

        if (data.equals("green")) {

            intentb.putExtra("startedclient", "true");
            sendBroadcast(intentb);
        } else {
            intentb.putExtra("startclient", "true");
            sendBroadcast(intentb);
        }

        intentb.setAction("com.north.socket.client.BroadcastReceiver");
        intentb.putExtra("senddata", data);
        sendBroadcast(intentb);

        if (data.equals("green")) {
            intentb.putExtra("startedclient", "true");
            sendBroadcast(intentb);
        } else {
            intentb.putExtra("startclient", "true");
            sendBroadcast(intentb);
        }

        intentb.setAction("com.north.socket.client.POSSNBroadcastReceiverawaiting");
        intentb.putExtra("senddata", data);
        sendBroadcast(intentb);

        Intent photoIntent = new Intent();
        photoIntent.setAction("com.north.socket.client.PhotoGalleryReceiver");
        photoIntent.putExtra("senddata", data);
        sendBroadcast(photoIntent);

        Intent acceptIntent = new Intent();
        acceptIntent.setAction("com.north.socket.client.AcceptTaskReceiver");
        acceptIntent.putExtra("senddata", data);
        sendBroadcast(acceptIntent);

        intentb.setAction("com.north.socket.client.PhotoBroadcastReceiver");
        intentb.putExtra("senddata", data);
        sendBroadcast(intentb);

        intentb.setAction("com.north.socket.client.SignBroadcastReceiver");
        intentb.putExtra("senddata", data);
        sendBroadcast(intentb);

        if (data.equals("green")) {

            intentb.putExtra("startedclient", "true");
            sendBroadcast(intentb);
            intentb.putExtra("senddata", "Received Ping success. Starting Ping Timer again.");
            sendBroadcast(intentb);
        } else {

            intentb.putExtra("startclient", "true");
            sendBroadcast(intentb);

            Intent intentc = new Intent();
            intentc.setAction("com.north.socket.client.POSSNBroadcastReceiver");
            intentc.putExtra("sendinfo", "Server Closed Connection. Retrying in 3 seconds.");
            sendBroadcast(intentc);

            Log.d("Network", getString(R.string.trying_again_in_three_sec));
            intentb.putExtra("senddata", "Server Disconnected. Retrying after 3 seconds Received Success : ");
            sendBroadcast(intentb);

            sendToast(getString(R.string.trying_again_in_three_sec));


        }
    }

    public static void stopAlarm() {
        try {
            //FadeOut(3f);
            if (mp != null && mp.isPlaying()) {
                mp.stop();
                mp.release();
                mp = null;
                //mp = new MediaPlayer();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendToast(String data) {
        Intent toasty = new Intent();
        toasty.setAction("com.north.socket.client.BroadcastReceiver");
        toasty.putExtra("sendtoast", data);
        toasty.putExtra("displayContent", data);
        sendBroadcast(toasty);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopAlarm();
    }

    public void runServer() {
        mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
            @Override
            //here the messageReceived method is implemented
            public void messageReceived(String message) {
                try {
                    if (message != null) {
                        sendToast("Connected");

                        seconds = System.currentTimeMillis() / 1000;
                        System.out.println("Needle Return Message from Socket::::: >>>>> " + message + ">>" + seconds + "  >>");

                        if (mTcpClient == null) {
                            sendToast("mTCPCLient Null");
                        }
                        if (mTcpClient != null) {
                            String socketstring = message;
                            socketstring = socketstring.replace("\n", "").replace("\r", "").replace("ping", "");
                            String[] multiplerecordset = socketstring.split("\\^");
                            String[] record = multiplerecordset[0].split("\\|");
                            String[] field = record[0].split("\\$");
                            String[] outputmessage = field[0].split("!");


                            if ((outputmessage[0].equals("USERID")) || (outputmessage[0].equals("NEXTMOVE")) || (outputmessage[0].equals("PUSHREFRESH")) || (outputmessage[0].equals("CALLCUST")) || (outputmessage[0].equals("MOVENEXT"))) {

                                retryServer.removeCallbacksAndMessages(null);
                                if (outputmessage[0].equals("PUSHREFRESH")) {
                                    sendMessageToTCP("DELETEREFRESH|1|");
                                    Intent intentalarm = new Intent();
                                    intentalarm.setAction("com.north.socket.client.POSSNBroadcastReceiver");
                                    intentalarm.putExtra("senddata", "NOTIFY:Status Updated");
                                    System.out.println("NOTIFY:Status Updated");
                                    sendBroadcast(intentalarm);

                                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

                                    AssetFileDescriptor afd;
                                    afd = getAssets().openFd("alarm.mp3");

                                    try {
                                        if (mp != null) {
                                            mp.stop();
                                            mp.release();
                                            mp = null;
                                            //mp = new MediaPlayer();
                                        }
                                        mp = new MediaPlayer();
                                        mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                                        mp.prepare();
                                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                                            @Override
                                            public void onCompletion(MediaPlayer mediaplayer) {

                                            }
                                        });
                                        mp.start();

                                    } catch (Exception e) {
                                        e.printStackTrace();

                                    }


                                }
                                if (outputmessage[0].equals("USERID") || outputmessage[0].equals("NEXTMOVE") || outputmessage[0].equals("MOVENEXT") || outputmessage[0].equals("PUSHREFRESH")) {
                                    ambAddress = field[2];
                                    if (multiplerecordset.length > 3) {
                                        TicketClass.list.clear();
                                        TicketClass.addTicket(multiplerecordset[2]);
                                    } else {
                                        TicketClass.list.clear();
                                    }
                                    if (outputmessage[0].equals("NEXTMOVE") || outputmessage[0].equals("MOVENEXT") || outputmessage[0].equals("PUSHREFRESH")) {
                                        if (multiplerecordset.length == 3) {
                                            TicketClass.list.clear();
                                            TicketClass.addTicket(multiplerecordset[2]);
                                        }
                                    } else if (outputmessage[0].equals("USERID")) {
                                        loginDone = 1;
                                        retryServer.removeCallbacksAndMessages(null);
                                        if (outputmessage.length > 1) {
                                            if (!field[1].equals("0")) {
                                                MenuClass.ambId = outputmessage[1];
                                                MenuClass.loginId = field[3];
                                                if ((!field[field.length - 1].equals("ezetap")) || (!field[field.length - 1].equals("Ezetap"))) {
                                                    MenuClass.ezetap = 1;
                                                    MenuClass.MSwipeUserName = field[4];
                                                    MenuClass.MSWipePassword = field[5];
                                                }
                                                final URL[] url = {null};
                                                Needle.onBackgroundThread().withThreadPoolSize(20).execute(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            url[0] = new URL("http://m.21north.in/ambassadors/ambassadors-" + MenuClass.ambId + ".png");

                                                            MenuClass.ambImage = BitmapFactory.decodeStream(url[0].openConnection().getInputStream());

                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                        // something that we want to do serially but not on the main thread
                                                    }
                                                });
                                            }
                                        }
                                        if (multiplerecordset.length == 3) {
                                            MenuClass.menuList.clear();
                                            MenuClass.addMenu(multiplerecordset[2]);
                                        } else if (multiplerecordset.length == 4) {
                                            MenuClass.menuList.clear();
                                            MenuClass.addMenu(multiplerecordset[3]);
                                        } else if (multiplerecordset.length == 5) {
                                            MenuClass.menuList.clear();
                                            MenuClass.addMenu(multiplerecordset[4]);
                                        }
                                    }
                                }
                                if (multiplerecordset.length == 1) {

                                    String[] wronglogin = multiplerecordset[0].split("\\$");
                                    if (wronglogin[1].equals("0")) {
                                        System.out.println("Multiple Recordset setting disconnected to 1.");
                                        disconnected = 1;
                                        Intent intentb = new Intent();
                                        intentb.setAction("com.north.socket.client.POSSNBroadcastReceiver");
                                        intentb.putExtra("senddata", "red");
                                        sendBroadcast(intentb);

                                        intentb.setAction("com.north.socket.client.BroadcastReceiver");
                                        intentb.putExtra("senddata", "revealbuttons");
                                        sendBroadcast(intentb);
                                        sendBroadcast(intentb);
                                        intentb.setAction("com.north.socket.client.BroadcastReceiver");
                                        intentb.putExtra("senderror", "Wrong Password");
                                        sendBroadcast(intentb);
                                        String root = Environment.getExternalStorageDirectory().toString();
                                        File file = new File(root, "config.txt");
                                        file.delete();

                                        return;
                                    }
                                }
                                if (multiplerecordset.length > 1) {
                                    String[] toGetstate = multiplerecordset[1].split("\\$");

                                    if (Integer.parseInt(toGetstate[1]) > 0) {
                                        queueid = toGetstate[0];
                                        state = Integer.parseInt(toGetstate[1]);
                                        qState = Integer.parseInt(toGetstate[1]);
                                        if (toGetstate.length > 46) {
                                            svcLat = toGetstate[36];
                                            svcLong = toGetstate[37];
                                            range = toGetstate[47];
                                            vehicleType = toGetstate[49];

                                        }
                                    }
                                }
                                isLogged = 1;
                                sendLedNotification("green");

                                Log.d("Network", "Received Userid. Starting Ping Timer.");

                                retryServer.removeCallbacksAndMessages(null);
                                pingServer.postDelayed(serverPing, 20000);
                                //}
                                disconnected = 0;


                                if (outputmessage[0].equals("CALLCUST")) {
                                    Intent intentalarm = new Intent();
                                    intentalarm.setAction("com.north.socket.client.POSSNBroadcastReceiver");
                                    intentalarm.putExtra("callsuccess", "Call Placed Successfully.");
                                    sendBroadcast(intentalarm);
                                }

                                Intent intentc = new Intent();
                                intentc.setAction("com.north.socket.client.POSSNBroadcastReceiver");
                                intentc.putExtra("sendinfo", "Received Ping success. Seting Disconnected to 0 and starting ping timer again.");
                                sendBroadcast(intentc);
                                if (multiplerecordset.length > 1) {
                                    String[] queuestate = multiplerecordset[1].split("\\$");
                                    if (queuestate.length > 1) {
                                        System.out.println(queuestate[0] + "\n");
                                        sendMessageToTCP("STSUC");

                                        if (field.length > 1) {
                                            if (field[1].equals("support")) {
                                                Intent intentstate = new Intent();
                                                intentstate.setAction("com.north.socket.client.BroadcastReceiver");
                                                intentstate.putExtra("sendimplant", multiplerecordset[1].toString());
                                                sendBroadcast(intentstate);
                                                System.out.println("Sending Broadcast\n");
                                            }
                                            if (field[1].equals("disabled") || field[1].equals("defaulter")) {

                                                Intent intentstate = new Intent();

                                                disabled = 1;

                                                intentstate.setAction("com.north.socket.client.BroadcastReceiver");
                                                intentstate.putExtra("noqueue", multiplerecordset[2].toString());
                                                sendBroadcast(intentstate);
                                                System.out.println("Sending Broadcast\n");

                                            }
                                            if (field[1].equals("ambassadors")) {
                                                disabled = 0;
                                                switch (queuestate[3]) {
                                                    case "0": {
                                                        Intent intentstate = new Intent();
                                                        intentstate.setAction("com.north.socket.client.BroadcastReceiver");
                                                        intentstate.putExtra("noqueue", multiplerecordset[1].toString());
                                                        sendBroadcast(intentstate);
                                                        System.out.println("Sending Broadcast\n");
                                                        break;
                                                    }
                                                    case "1":
                                                        isLogged = 1;
                                                        if (state == 100 || state == 602 || state == 603 || state == 2100 || state == 3100 || state == 4100 || state == 5100 || state == 6100 || state == 7100) {

                                                            String acceptTaskHeading;
                                                            if (state == 100) {
                                                                acceptTaskHeading = getString(R.string.pickup);
                                                            } else if (state == 602 || state == 603) {
                                                                acceptTaskHeading = getString(R.string.dropoff);
                                                            }else if(state == 3100){
                                                                acceptTaskHeading = getString(R.string.road_side_assistance);
                                                            } else if (state == 5100) {
                                                                acceptTaskHeading = getString(R.string.home_delivery);
                                                            } else if (state == 4100) {
                                                                acceptTaskHeading = getString(R.string.internal_movement);
                                                            } else if (state == 7100) {
                                                                acceptTaskHeading = getString(R.string.test_drive);
                                                            } else if (state == 6100) {
                                                                acceptTaskHeading = getString(R.string.stockyard);
                                                            } else {
                                                                acceptTaskHeading = getString(R.string.chauffeur);
                                                            }

                                                            String[] acceptTaskArray = new String[10];
                                                            acceptTaskArray[0] = queuestate[28];
                                                            acceptTaskArray[1] = queuestate[30];
                                                            acceptTaskArray[2] = queuestate[31];
                                                            acceptTaskArray[3] = queuestate[7];
                                                            acceptTaskArray[4] = queuestate[34];
                                                            acceptTaskArray[5] = queuestate[35];
                                                            acceptTaskArray[6] = acceptTaskHeading;
                                                            acceptTaskArray[7] = queuestate[42];
                                                            acceptTaskArray[8] = queuestate[1];
                                                            acceptTaskArray[9] = queuestate[50];

                                                            Intent displayAcceptTask = new Intent();
                                                            displayAcceptTask.setAction("com.north.socket.client.BroadcastReceiver");
                                                            displayAcceptTask.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            displayAcceptTask.putExtra("displayAcceptTask", acceptTaskArray);
                                                            sendBroadcast(displayAcceptTask);

                                                        } else {


                                                            String statedata = multiplerecordset[1];

                                                            Intent intentphoto = new Intent();
                                                            intentphoto.setAction("com.north.socket.client.BroadcastReceiver");
                                                            intentphoto.putExtra("activequeue", statedata);
                                                            sendBroadcast(intentphoto);
                                                        }
                                                        break;
                                                    case "2": {
                                                        isLogged = 1;
                                                        if (outputmessage[0].equals("NEXTMOVE") || outputmessage[0].equals("MOVENEXT")) {
                                                            takephoto = 0;
                                                            signaturePage = 0;
                                                        }
                                                        String datab = "";
                                                        datab = "photodon";
                                                        Intent intentphoto = new Intent();
                                                        intentphoto.setAction("com.north.socket.client.BroadcastReceiver");
                                                        intentphoto.putExtra("senddata", datab);
                                                        sendBroadcast(intentphoto);

                                                        String statedata = multiplerecordset[1];
                                                        String[] queueDataBrk = statedata.split("\\|");
                                                        String[] queueDataSecBrk;
                                                        if (queueDataBrk.length >= 1) {
                                                            queueDataSecBrk = queueDataBrk[0].split("\\$");
                                                            if (!prevTicketRaised.equals(queueDataSecBrk[38])) {
                                                                prevTicketRaised = queueDataSecBrk[38];
                                                                takephoto = 0;
                                                            }
                                                            if (queueDataSecBrk.length > 49) {
                                                                vehicleType = queueDataSecBrk[49];
                                                            }
                                                        }
                                                        if (takephoto == 0) {
                                                            String stateview = "com.north.socket.client.PhotoGallery";
                                                            Intent photo = new Intent();
                                                            photo.setAction("com.north.socket.client.BroadcastReceiver");
                                                            photo.putExtra("displayphoto", statedata);
                                                            sendBroadcast(photo);
                                                        }
                                                        break;
                                                    }
                                                    case "3": {
                                                        isLogged = 1;
                                                        if (outputmessage[0].equals("NEXTMOVE") || outputmessage[0].equals("MOVENEXT")) {
                                                            takephoto = 0;
                                                            signaturePage = 0;
                                                        }
                                                        if (signaturePage == 0) {

                                                            String statedata = multiplerecordset[1];

                                                            Intent photo = new Intent();
                                                            photo.setAction("com.north.socket.client.BroadcastReceiver");
                                                            photo.putExtra("displaysignature", statedata);
                                                            sendBroadcast(photo);
                                                        }
                                                        break;
                                                    }
                                                    case "4": {
                                                        isLogged = 1;
                                                        String statedata = multiplerecordset[1];
                                                        Intent photo = new Intent();
                                                        photo.setAction("com.north.socket.client.BroadcastReceiver");
                                                        photo.putExtra("displayPayment", statedata);
                                                        sendBroadcast(photo);
                                                        break;
                                                    }
                                                    case "5": {
                                                        isLogged = 1;
                                                        Intent intentstate = new Intent();
                                                        intentstate.setAction("com.north.socket.client.BroadcastReceiver");
                                                        intentstate.putExtra("displayComplaints", multiplerecordset[2].toString() + "^" + multiplerecordset[3].toString());
                                                        sendBroadcast(intentstate);
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (outputmessage[0].equals("AMBREPORT")) {
                                Intent intentb = new Intent();
                                intentb.setAction("com.north.socket.client.BroadcastReceiver");

                                if (multiplerecordset.length > 2) {
                                    intentb.putExtra("ambreport", multiplerecordset[0].toString() + "^" + multiplerecordset[1].toString() + multiplerecordset[2].toString());
                                    sendBroadcast(intentb);
                                } else {

                                    intentb.putExtra("ambreport", multiplerecordset[0] + "^" + multiplerecordset[1]);
                                    sendBroadcast(intentb);
                                }
                            } else if (outputmessage[0].equals("AMBDETAILREPORT")) {

                                if (Integer.parseInt(outputmessage[1]) < 6) {

                                    Intent intentd = new Intent();
                                    intentd.setAction("com.north.socket.client.POSSNBroadcastReceiverimplantdetail");
                                    intentd.putExtra("ambdetailreport", socketstring);
                                    sendBroadcast(intentd);
                                } else {
                                    Intent intentd = new Intent();
                                    intentd.setAction("com.north.socket.client.POSSNBroadcastListHistory");
                                    intentd.putExtra("ambdetailreport", socketstring);
                                    sendBroadcast(intentd);
                                }
                            }
                            typeofCommand = message.split("\\|");
                            if (typeofCommand[0].equals("HELLO")) {
                                disconnected = 0;
                                sendToast("Starting Ping Timer.");
                                sendLedNotification("green");
                                disconnected = 0;
                                pingServer.removeCallbacksAndMessages(null);
                                retryServer.removeCallbacksAndMessages(null);
                                pingOpen.postDelayed(openPing, 20000);
                                Intent intentb = new Intent();
                                intentb.setAction("com.north.socket.client.POSSNBroadcastReceiver");
                                intentb.putExtra("sendinfo", "Connected To Server Received Hello.");
                                sendBroadcast(intentb);
                                sendToast("Connected. Received Hello.");
                                newversion = Float.parseFloat(typeofCommand[1].trim());

                                if (newversion > version) {
                                    Intent intentd = new Intent();
                                    intentd.setAction("com.north.socket.client.BroadcastReceiver");
                                    intentd.putExtra("wrongversion", "wrongversion");
                                    sendBroadcast(intentd);
                                }

                                String root = Environment.getExternalStorageDirectory().toString();
                                File file = new File(root, "config.txt");
                                String filepath = root + "/config.txt";
                                if (file.exists()) {
                                    BufferedReader br = null;
                                    try {
                                        String sCurrentLine;
                                        br = new BufferedReader(new FileReader(filepath));
                                        logininfo = "";
                                        while ((sCurrentLine = br.readLine()) != null) {

                                            logininfo = logininfo + sCurrentLine;
                                            System.out.println("SocketServiceLogin");
                                        }

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    Intent intent = new Intent();
                                    intent.setAction("com.north.socket.client.BroadcastReceiver");
                                    intent.putExtra("senddata", "hidebuttons");
                                    sendBroadcast(intent);
                                    // logininfo = "AMBLOGIN"+socketseparator+"ramesh@21north.in"+socketseparator+"12345"+socketseparator;
                                    logininfo = logininfo.replace(",", "|");
                                    String sepstringtest = logininfo.substring(logininfo.length() - 1);
                                    if (!sepstringtest.equals("|")) {
                                        logininfo = logininfo + "|";
                                    }
                                    loginstate = logininfo.split("\\|");
                                    deviceInfoCheck = 1;

                                    if (loginstate[2].length() > 1) {
                                        System.out.println("AMB from Hello");
                                        sendMessageToTCP(logininfo);

                                        //retryServer.postDelayed(serverRetry, 5000);
                                        disconnected = 0;
                                        isLogged = 0;

                                        intentb.setAction("com.north.socket.client.POSSNBroadcastReceiver");
                                        intentb.putExtra("sendinfo", "Set Disconnected to 1 and sending login Information.");
                                        sendBroadcast(intentb);
                                        Log.d("Network", "Sending Login Information and starting Ping Timer");
                                    } else {
                                        String rootfile = Environment.getExternalStorageDirectory().toString();
                                        File fileroot = new File(rootfile, "config.txt");
                                        fileroot.delete();
                                        intentb.setAction("com.north.socket.client.POSSNBroadcastReceiver");
                                        intentb.putExtra("sendinfo", "Login Information not found.");
                                        sendBroadcast(intentb);
                                        Intent intentfile = new Intent();
                                        intentfile.setAction("com.north.socket.client.BroadcastReceiver");
                                        intentfile.putExtra("senddata", "revealbuttons");
                                        sendBroadcast(intentfile);
                                    }
                                } else {

                                    intentb.setAction("com.north.socket.client.POSSNBroadcastReceiver");
                                    intentb.putExtra("sendinfo", "Login Information not found.");
                                    sendBroadcast(intentb);
                                    Intent intent = new Intent();
                                    intent.setAction("com.north.socket.client.BroadcastReceiver");
                                    intent.putExtra("senddata", "revealbuttons");
                                    sendBroadcast(intent);
                                }

                            } else if (typeofCommand[0].equals("NEWTASK")) {
                                mp = new MediaPlayer();
                                try {
                                    String ab = getAssets().toString();
                                    AssetFileDescriptor afd = getAssets().openFd("alarm.mp3");
                                    mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                                    mp.prepare();
                                    mp.start();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (typeofCommand[0].equals("CALLSUCCESS")) {
                                //finishActivity(5);
                                Intent intent = new Intent();
                                intent.setAction("com.north.socket.client.POSSNBroadcastReceiver");
                                intent.putExtra("senddata", "callsuccess");
                                sendBroadcast(intent);
                            } else if (typeofCommand[0].equals("closed")) {
                                startEssentialsCheck = 0;
                                System.out.println("Closed setting disconnected to 1.");
                                disconnected = 1;
                                isLogged = 0;
                                sendLedNotification("red");
                                pingServer.removeCallbacksAndMessages(null);
                                retryServer.removeCallbacksAndMessages(null);
                                retryServer.postDelayed(serverRetry, 5000);

                            } else if (typeofCommand[0].equals("ping")) {

                                disconnected = 0;
                                pingServer.removeCallbacksAndMessages(null);
                                retryServer.removeCallbacksAndMessages(null);
                                retryServer.postDelayed(serverRetry, 30000);

                                String devicetokenlcal = "12";
                                if (templocation != null) {
                                    final String data = "POSSITION|" + templocation.getLatitude() + "|" + templocation.getLongitude() + "|" + templocation.getAltitude() + "|" + templocation.getSpeed() + "|" + templocation.getBearing() + "|" + devicetokenlcal + "|" + androidversion + "|";
                                    Intent intentcheck = new Intent();
                                    intentcheck.setAction("com.north.socket.client.POSSNBroadcastReceiver");

                                    intentcheck.putExtra("latlong", templocation.getLatitude() + "|" + templocation.getLongitude() + "|");
                                    sendBroadcast(intentcheck);

                                    Intent intentatten = new Intent();
                                    intentatten.setAction("com.north.socket.client.attendanceBroadcastReceiver");
                                    intentatten.putExtra("latlong", templocation.getLatitude() + "|" + templocation.getLongitude() + "|");
                                    sendBroadcast(intentatten);

                                    Intent intentphoto = new Intent();
                                    intentphoto.setAction("com.north.socket.client.PhotoBroadcastReceiver");

                                    intentphoto.putExtra("latlong", templocation.getLatitude() + "|" + templocation.getLongitude() + "|");
                                    sendBroadcast(intentphoto);

                                    Intent intentsign = new Intent();
                                    intentsign.setAction("com.north.socket.client.SignBroadcastReceiver");

                                    intentsign.putExtra("latlong", templocation.getLatitude() + "|" + templocation.getLongitude() + "|");
                                    sendBroadcast(intentsign);
                                    sendMessageToTCP(data);
                                } else {
                                    Intent intentd = new Intent();
                                    intentd.setAction("com.north.socket.client.POSSNBroadcastReceiver");
                                    intentd.putExtra("senddata", "nolocation");
                                    sendBroadcast(intentd);

                                    intentd.setAction("com.north.socket.client.BroadcastReceiver");
                                    intentd.putExtra("nolocation", "nolocation");
                                    sendBroadcast(intentd);
                                }
                                sendLedNotification("green");
                            } else if (typeofCommand[0].equals("FAILED")) {
                                startEssentialsCheck = 0;
                                System.out.println("Failed setting disconnected to 1.");
                                disconnected = 1;
                                Intent intentb = new Intent();
                                intentb.setAction("com.north.socket.client.POSSNBroadcastReceiver");
                                intentb.putExtra("senddata", "red");
                                sendBroadcast(intentb);

                                intentb.setAction("com.north.socket.client.BroadcastReceiver");
                                intentb.putExtra("senddata", "revealbuttons");
                                sendBroadcast(intentb);
                                intentb.setAction("com.north.socket.client.BroadcastReceiver");
                                intentb.putExtra("senderror", "Wrong Password");
                                sendBroadcast(intentb);
                                String root = Environment.getExternalStorageDirectory().toString();
                                File file = new File(root, "config.txt");
                                file.delete();

                            } else if (typeofCommand[0].equals("POSSNSUCCESS!")) {
                                if (deviceInfoCheck == 1) {
                                    deviceInfoCheck = 0;
                                    TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                                    String deviceID = tManager.getDeviceId();
                                    int androidVerSion = android.os.Build.VERSION.SDK_INT;
                                    String deviceName = android.os.Build.MODEL;
                                    String deviceMan = android.os.Build.MANUFACTURER;
                                    if (deviceID == null) {
                                        deviceID = "000000000000";
                                    }
                                    if (templocation != null) {
                                        final String sendMsg = "AMBDEVICE|" + deviceID + "|" + androidVerSion + "|" + deviceMan + "|" + deviceName + "|" + getLatitude() + "|" + getLongitude() + "|";
                                        System.out.println(sendMsg);
                                        sendMessageToTCP(sendMsg);
                                    }
                                }

                                t2 = new Time();
                                t2.setToNow();

                                sendLedNotification("green");
                                retryServer.removeCallbacksAndMessages(null);
                                pingServer.postDelayed(serverPing, 20000);
                                disconnected = 0;
                                Intent intentc = new Intent();
                                intentc.setAction("com.north.socket.client.POSSNBroadcastReceiver");
                                intentc.putExtra("sendinfo", "Received Ping success. Seting Disconnected to 0 and starting ping timer again.");
                                sendBroadcast(intentc);

                            } else if (typeofCommand[0].equals("NOTIFY")) {
                                Intent intent = new Intent();
                                intent.setAction("com.north.socket.client.POSSNBroadcastReceiver");
                                intent.putExtra("senddata", message);
                                sendBroadcast(intent);
                                Log.d("LOGGIN", message);


                            } else if (typeofCommand[0].equals("state")) {
                                isLogged = 1;
                                Intent intentb = new Intent();
                                intentb.setAction("com.north.socket.client.BroadcastReceiver");
                                intentb.putExtra("statedata", message);
                                sendBroadcast(intentb);
                                sendMessageToTCP("STSUC");

                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        mTcpClient.run();
    }


    public void onStop() {
        if (myReceiver != null) {

            try {
                unregisterReceiver(myReceiver);
                myReceiver = null;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();

            }
        }
    }


    private Runnable serverRetry = new Runnable() {
        @Override
        public void run() {
            try {
                sendToast("Trying Again ");
                retryServer.removeCallbacksAndMessages(null);
                System.out.println("Server Retry. is Disconnected" + disconnected);
                //sendUnsendedReports();
                if (disconnected == 1) {

                    startEssentialsCheck = 0;

                    mTcpClient.stopClientTask();
                    isLogged = 0;
                    mWakeLock.acquire();
                    Needle.onBackgroundThread().withThreadPoolSize(20).execute(new Runnable() {
                        @Override
                        public void run() {
                            runServer();
                            // something that we want to do serially but not on the main thread
                        }
                    });
                }
            } catch (Exception e) {
                // added try catch block to be sure of uninterupted execution
            }

      /* and here comes the "trick" */
            //handler.postDelayed(this, 1000);
        }
    };

    private Runnable openPing = new Runnable() {
        @Override
        public void run() {
            try {
                pingOpen.removeCallbacksAndMessages(null);
                if (disconnected == 1) {
//                    retryServer.postDelayed(serverRetry, 5000);
                    startEssentialsCheck = 0;
                }
            } catch (Exception e) {
                // added try catch block to be sure of uninterupted execution
            }
      /* and here comes the "trick" */
            if (simtest == 0) {
                if (!getMobileDataState()) {
                    Intent intentb = new Intent();
                    intentb.setAction("com.north.socket.client.BroadcastReceiver");
                    intentb.putExtra("enablemobiledata", "enablemobiledata");
                    sendBroadcast(intentb);
                }

                if (newversion > version) {
                    Intent intentb = new Intent();
                    intentb.setAction("com.north.socket.client.BroadcastReceiver");
                    intentb.putExtra("wrongversion", "wrongversion");
                    sendBroadcast(intentb);
                }

                WifiManager wifi = (WifiManager) SocketService.this.getSystemService(Context.WIFI_SERVICE);
                if (wifi.isWifiEnabled()) {
                    //wifi is enabled
                    if (startEssentialsCheck == 1) {
                        startEssentialsCheck = 0;
                        Intent intent = new Intent();
                        intent.setAction("com.north.socket.client.EssentialsNet");
                        intent.putExtra("wifiEnabled", "1");
                        sendBroadcast(intent);
                    }

                }

                locationManager = (LocationManager)
                        getSystemService(Context.LOCATION_SERVICE);
            }
            pingOpen.postDelayed(openPing, 20000);
        }
    };

    public boolean getMobileDataState() {
        try {
            TelephonyManager telephonyService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            Method getMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("getDataEnabled");

            if (null != getMobileDataEnabledMethod) {

                return (boolean) (Boolean) getMobileDataEnabledMethod.invoke(telephonyService);
            }
        } catch (Exception ignored) {

        }

        return false;
    }

    private Runnable serverPing = new Runnable() {
        @Override
        public void run() {
            try {
                System.out.println("Starting Server Ping\n");

                if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    Intent intentb = new Intent();
                    intentb.setAction("com.north.socket.client.BroadcastReceiver");
                    intentb.putExtra("senddata", "GPSDISABLED");
                    sendBroadcast(intentb);
                }
                //
                pingServer.removeCallbacksAndMessages(null);
                if (disconnected == 1) {
                    retryServer.postDelayed(serverRetry, 3000);
                    startEssentialsCheck = 0;

                }
                if (disconnected == 0) {
                    mWakeLock.acquire();

                    if (mTcpClient.socket != null) {
                        if (isLogged == 1) {

                            t1 = new Time();
                            t1.setToNow();
                            Log.d("Network", "Will Disconnect if I do not receive a Ping success in 3 seconds");
                            System.out.println("Ping in 3 seconds setting disconnected to 1.");
                            disconnected = 1;

                            long millis = System.currentTimeMillis();
                            long seconds = millis / 1000;
                            System.out.println("Before Retry PostDelayed " + seconds);
                            retryServer.postDelayed(serverRetry, 23000);

                            String root = Environment.getExternalStorageDirectory().toString();

                            File file = new File(root, "connectionlog.txt");
                            try {
                                FileWriter writer = new FileWriter(file, true);
                                writer.append("Will Disconnect if I do not receive a Ping success in 30 seconds\n");
                                writer.flush();
                                writer.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Intent intent = new Intent();
                        intent.setAction("com.north.socket.client.BroadcastReceiver");
                        intent.putExtra("senderror", "Trying to Connect Again");

                        application.sendBroadcast(intent);
                        reachability = new Reachability();

                        if (Reachability.isNetworkingAvailable(application)) {

                            intent.setAction("com.north.socket.client.BroadcastReceiver");
                            intent.putExtra("senderror", "Connecting to Server");
                            application.sendBroadcast(intent);
                        } else {

                            intent.setAction("com.north.socket.client.BroadcastReceiver");
                            intent.putExtra("senderror", "Network Unavailable");
                            application.sendBroadcast(intent);

                        }
                    }
                }

            } catch (Exception e) {
                // added try catch block to be sure of uninterupted execution
            }
        }
    };

    public static boolean distanceBtwPoints() {
        double distance;
        Location locationA = new Location("Point A");
        if (templocation != null) {
            locationA.setLatitude(templocation.getLatitude());
            locationA.setLongitude(templocation.getLongitude());

            Location locationB = new Location("Point B");
            locationB.setLatitude(Double.parseDouble(svcLat));
            locationB.setLongitude(Double.parseDouble(svcLong));


            distance = locationA.distanceTo(locationB) / 1000;

            System.out.println("Location A: " + locationA + " Location B: " + locationB + " Distance: " + distance + " Range: " + range);

            if (distance < (Double.parseDouble(range))) {
                Intent intent = new Intent();
                intent.setAction("com.north.socket.client.POSSNBroadcastReceiver");
                intent.putExtra("AtSvc", "1");
                context.sendBroadcast(intent);
                return true;
            }
        }
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(this.getClass().getName(), "onBind(..)");
        return null;
    }

    @Override
    public void onDestroy() {
        System.out.println("Stopping Service on Request");
        takephoto = 0;
        if (mTcpClient != null) {
            mTcpClient.stopClientTask();
            System.out.println("Stopping Client on Request");
        }
        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
        }
        Log.v(this.getClass().getName(), "Service onDestroy(). Stop AlarmManager at " + new java.sql.Timestamp(System.currentTimeMillis()).toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        if ((location != null) && (location.getLatitude() != 0) && (location.getLongitude() != 0)) {
            templocation = location;
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            long millis = System.currentTimeMillis();


            if (disconnected == 0) {

                sendLedNotification("green");

                mWakeLock.acquire();
                //sendUnsentReports();

                if (templocation == null) {

                    System.out.println("Temp Location Null");

                    Intent intentd = new Intent();
                    intentd.setAction("com.north.socket.client.POSSNBroadcastReceiver");
                    intentd.putExtra("senddata", "nolocation");
                    sendBroadcast(intentd);

                    intentd.setAction("com.north.socket.client.BroadcastReceiver");
                    intentd.putExtra("nolocation", "nolocation");
                    sendBroadcast(intentd);
                } else {

                    System.out.println("Temp Location Sending");

                    String devicetokenlcal = "12";
                    final String data = "POSSITION|" + templocation.getLatitude() + "|" + templocation.getLongitude() + "|" + templocation.getAltitude() + "|" + templocation.getSpeed() + "|" + templocation.getBearing() + "|" + devicetokenlcal + "|" + androidversion + "|";
                    if (mTcpClient != null) {
                        if (mTcpClient.socket != null) {
                            if (isLogged == 1) {

                                seconds = System.currentTimeMillis() / 1000;

                                Intent intentcheck = new Intent();
                                intentcheck.setAction("com.north.socket.client.POSSNBroadcastReceiver");

                                intentcheck.putExtra("checknetwork", "true");
                                sendBroadcast(intentcheck);

                                intentcheck.putExtra("checknetwork", "true");
                                sendBroadcast(intentcheck);

                                intentcheck.putExtra("checknetwork", "true");
                                sendBroadcast(intentcheck);

                                intentcheck.putExtra("latlong", templocation.getLatitude() + "|" + templocation.getLongitude() + "|");
                                sendBroadcast(intentcheck);

                                Intent intentphoto = new Intent();
                                intentphoto.setAction("com.north.socket.client.PhotoBroadcastReceiver");
                                intentphoto.putExtra("latlong", templocation.getLatitude() + "|" + templocation.getLongitude() + "|");
                                sendBroadcast(intentphoto);

                                Intent intentatten = new Intent();
                                intentatten.setAction("com.north.socket.client.attendanceBroadcastReceiver");
                                intentatten.putExtra("latlong", templocation.getLatitude() + "|" + templocation.getLongitude() + "|");
                                sendBroadcast(intentatten);


                                Intent intentsign = new Intent();
                                intentsign.setAction("com.north.socket.client.SignBroadcastReceiver");
                                intentsign.putExtra("latlong", templocation.getLatitude() + "|" + templocation.getLongitude() + "|");
                                sendBroadcast(intentsign);
                            }
                            sendMessageToTCP(data);
                        }
                    }
                }
            }

            seconds = millis / 1000;
            if (dbopen == 0) {
                if (state == 410) {
                    mydatabase = openOrCreateDatabase(queueid + "_0", MODE_PRIVATE, null);
                } else if ((state == 722) || (state == 723)) {
                    mydatabase = openOrCreateDatabase(queueid + "_1", MODE_PRIVATE, null);
                }
                if ((state == 410) || (state == 722) || (state == 723)) {
                    if (location.getSpeed() * 3.6 > 55) {
                        mp = new MediaPlayer();
                        try {
                            AssetFileDescriptor afd = getAssets().openFd("slow.mp3");
                            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                            mp.prepare();
                            mp.start();
                            //FadeIn(3f);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (location.getSpeed() * 3.6 <= 30) {
                        if (mp != null) {
                            mp.stop();
                        }
                    }
                    try {
                        if (mydatabase.isOpen()) {
                            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS location(creationdatetime DATETIME, latitude VARCHAR,longitude VARCHAR, speed VARCHAR, bearing VARCHAR, state INT, queueid INT);");
                        }
                        if (mydatabase.isOpen()) {
                            mydatabase.execSQL("INSERT INTO location VALUES(datetime(datetime(), 'localtime'), '" + location.getLatitude() + "','" + location.getLongitude() + "','" + location.getSpeed() + "','" + location.getBearing() + "','" + state + "','" + Integer.parseInt(queueid) + "' );");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error inserting batch record into database.", e);
                    }
                }
            }
            if (!svcLat.equals("0")) {
                distanceBtwPoints();
            }
            if (mTcpClient != null) {
                if (mTcpClient.socket != null) {
                    if (isLogged == 1) {

                        seconds = System.currentTimeMillis() / 1000;
                        Intent intentcheck = new Intent();
                        intentcheck.setAction("com.north.socket.client.POSSNBroadcastReceiver");

                        intentcheck.putExtra("latlong", templocation.getLatitude() + "|" + templocation.getLongitude() + "|");
                        sendBroadcast(intentcheck);

                        Intent intentatten = new Intent();
                        intentatten.setAction("com.north.socket.client.attendanceBroadcastReceiver");
                        intentatten.putExtra("latlong", templocation.getLatitude() + "|" + templocation.getLongitude() + "|");
                        sendBroadcast(intentatten);

                        Intent intentphoto = new Intent();
                        intentphoto.setAction("com.north.socket.client.PhotoBroadcastReceiver");

                        intentphoto.putExtra("latlong", templocation.getLatitude() + "|" + templocation.getLongitude() + "|");
                        sendBroadcast(intentphoto);

                        Intent intentsign = new Intent();
                        intentsign.setAction("com.north.socket.client.SignBroadcastReceiver");

                        intentsign.putExtra("latlong", templocation.getLatitude() + "|" + templocation.getLongitude() + "|");
                        sendBroadcast(intentsign);

                    }
                }
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    public static boolean timeDiff() {
        long timeFromLastUP = System.currentTimeMillis();
        long timeInSec = timeFromLastUP / 1000;

        long timeDifference = timeInSec - seconds;

        return timeDifference < 60 && latitude > 0;
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public static Boolean isJPEG(File filename) throws Exception {

        DataInputStream ins = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
        try {
            return ins.readInt() == 0xffd8ffe0;
        } finally {
            ins.close();
        }
    }

    public static boolean checkForNetworkLocationAccess() {
        Application application = (Application) getContext();
        AmbassadorApp app = (AmbassadorApp) application;
        boolean result = false;
        try {
            result = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (IllegalArgumentException | SecurityException e) {
            Log.e("LU:checkForGPS", "Network Provider not available", e);
        }
        Log.v("LU:NetworkLocAccess", String.valueOf(result));
        return result;
    }

    public static boolean checkForGPSLocationAccess() {
        boolean result = false;
        try {
            result = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (IllegalArgumentException | SecurityException e) {
            Log.e("LU:checkForGPS", "GPS Provider not available", e);
        }
        Log.v("LU:GPSLocAccess", String.valueOf(result));
        return result;
    }

    /*
    *  Get Current Location
	*/
    public Location get_current_location() {
        if (permissionCheck) {
            isGPSEnabled = checkForGPSLocationAccess();

            isNetworkEnabled = checkForNetworkLocationAccess();

            if (checkForBestLocationAccess()) {
                if (isGPSEnabled) {
                    if (location == null) {
                        if (locationManager != null) {
                            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


                                locationManager.requestLocationUpdates(
                                        LocationManager.GPS_PROVIDER,
                                        MIN_TIME_BW_UPDATES,
                                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    templocation = location;
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                                long millis = System.currentTimeMillis();
                                seconds = millis / 1000;
                            }
                        }
                    }
                }
                if (isNetworkEnabled) {

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {

                        if (location != null) {
                            templocation = location;
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            long millis = System.currentTimeMillis();
                            seconds = millis / 1000;
                        }
                    }
                }
            }
        }
        return location;
    }

    public double getLatitude() {
        if (location != null) {

            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        return longitude;
    }

    public static boolean checkForBestLocationAccess() {
        boolean networkProvider = checkForNetworkLocationAccess();
        boolean gpsProvider = checkForGPSLocationAccess();
        if (networkProvider && gpsProvider) {
            Log.v("LU:BestLocAccess", String.valueOf(true));
            return true;
        }
        Log.v("LU:BestLocAccess", String.valueOf(false));
        return false;
    }

    public void sendMessageToTCP(final String msg) {
        System.out.println("mTCPisRunningUnderRun");
        Thread.currentThread().interrupt();
        Needle.onBackgroundThread().withThreadPoolSize(20).execute(new Runnable() {
            @Override
            public void run() {
                mTcpClient.sendMessage(msg);
                // something that we want to do serially but not on the main thread
            }
        });
    }

    public class ConnectivityReceiver extends BroadcastReceiver {
        public ConnectivityReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            final String sendchat = intent.getStringExtra("sendchat");
            if (sendchat != null) {
                if (mTcpClient != null) {
                    stopAlarm();
                    System.out.println("mTCPisRunning");
                    sendMessageToTCP(sendchat);


                    String pord = "";
                    if (state == 410) {
                        pord = "_0";
                    }
                    if ((state == 802) || (state == 803)) {
                        pord = "_1";
                    }
                    if ((state == 410) || (state == 802) || (state == 803)) {
                        try {

                            Cursor cursor = null;
                            String speed = "";
                            try {
                                cursor = mydatabase.rawQuery("select max(speed) speed from location where ((state=722)|(state=723));", null);
                                if (cursor.getCount() > 0) {
                                    cursor.moveToFirst();
                                    speed = cursor.getString(cursor.getColumnIndex("speed"));
                                    if (speed != null) {
                                        sendchat.replace("########", speed);
                                    }
                                }

                            } finally {
                                if (cursor != null) {
                                    cursor.close();
                                }
                            }

                            File sd = Environment.getExternalStorageDirectory();
                            File data = Environment.getDataDirectory();

                            if (sd.canWrite()) {
                                String currentDBPath = "/data/com.north.socket.client/databases/" + queueid + pord;
                                String backupDBPath = queueid + pord + ".db";
                                File currentDB = new File(data, currentDBPath);
                                File backupDB = new File(sd + "/PhotoDir", backupDBPath);


                                if (currentDB.exists()) {
                                    FileChannel src = new FileInputStream(currentDB).getChannel();
                                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                                    dst.transferFrom(src, 0, src.size());
                                    src.close();
                                    dst.close();
                                }
                                dbopen = 0;
                                mydatabase.close();
                                application.deleteDatabase(queueid + pord);
                            }

                        } catch (Exception e) {
                            Log.w("Settings Backup", e);
                        }
                    }

                    Log.v("sendingchat", sendchat);
                } else {
                    Needle.onBackgroundThread().withThreadPoolSize(20).execute(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Starting server");
                            runServer();
                        }
                    });

                }
            }

            String deldatabase = intent.getStringExtra("deldatabase");
            if (deldatabase != null) {
                dbopen = 0;
                mydatabase.close();
                application.deleteDatabase(deldatabase);
            }

            final String data = intent.getStringExtra("senddata");
            if (data != null) {
                if (data.equals("startalarm")) {
                    sendMessageToTCP(data);
                }
                if (data.equals("stopalarm")) {
                    stopAlarm();
                }
            }
        }
    }
}