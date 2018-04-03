package com.north.socket.client;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.admin.DeviceAdminReceiver;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.north.socket.client.AmbassadorApp.getContext;
import static com.north.socket.client.SocketService.locationManager;
import static com.north.socket.client.SocketService.startEssentialsCheck;
import static com.north.socket.client.SocketService.templocation;

public class Login extends Activity implements LocationListener {

    String socketseparator = "|";
    String logininfo = "";
    private EditText username;
    private EditText password;
    private Button forgotpassword;
    private Button loginbutton;
    private TextView connStatus;
    int notify = 0;
    int emulator = 0;
    private ProgressBar downloadProgress;
    int simtest = 0;
    int disconnected = 0;
    static int takephoto = 0;
    int signdone = 0;
    String statedata = "";
    String stateview = "";

    String latitudepresent;
    String longitudepresent;

    String prevTicketRaised = "0";

    long startTime = 0;
    private PowerManager.WakeLock mWakeLock;
    ConnectivityReceiver myReceiver;
    MediaPlayer mp;
    Application application;
    String tag = "LifeCycleEvents";

    static String twoWheelerCheck = "1";
    static String fourWheelerCheck = "2";
    static String vehicleType = "0";
    static boolean loginDone = false;

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

    public void onStart() {
        super.onStart();
        Log.d(tag, "In the onStart() event");
    }

    public void onRestart() {
        super.onRestart();

        if (!stateview.isEmpty()) {
            try {
                Intent activeview = new Intent(Login.this, Class.forName(stateview));
                activeview.putExtra("qdata", statedata);
                startActivityForResult(activeview, 5);
                Log.d(tag, "In the onRestart() event");
            } catch (ClassNotFoundException ex) {
                System.out.println(ex.toString());
            }
        }
    }

    public void onResume() {
        super.onResume();
        mWakeLock.acquire();
        SocketService.loginDone = 0;
        Log.d(tag, "Login In the onResume() event");
    }

    public void onPause() {
        super.onPause();
        mWakeLock.acquire();
        Log.d(tag, "In the onPause() event");
    }

    public void onStop() {
        super.onStop();
        Log.d(tag, "In the onStop() event");
    }

    public void onForgot(View v) {
        String phnNo = "1";
        if (username.getText().length() == 10) {
            phnNo = username.getText().toString();
        }

        Intent intent = new Intent(this, ForgotPassword.class);
        intent.putExtra("phnNo", phnNo);
        startActivityForResult(intent, 1);

    }

    public void onLogin(View v) {
        hideKeyboard(v);
        if (username.getText().length() < 10) {
            setStatusText(getString(R.string.mobile_no_request));
            return;
        }
        if (password.getText().length() < 5) {
            setStatusText(getString(R.string.password_request));
            return;
        }
        logininfo = "AMBLOGIN" + socketseparator + username.getText() + socketseparator + password.getText() + socketseparator;

        Intent intent = new Intent();
        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
        intent.putExtra("sendchat", logininfo);
        sendBroadcast(intent);
        String root = Environment.getExternalStorageDirectory().toString();

        File file = new File(root, "config.txt");
        try {
            hidebuttons();
            FileWriter writer = new FileWriter(file);
            writer.append(logininfo);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed() {
        // do nothing.
    }

    public void notifyUser() {

        Notification notification = new Notification(R.drawable.launcher, "Staus Updated", System.currentTimeMillis());
        notification.flags = Notification.DEFAULT_LIGHTS;
    }

    public void revealbuttons() {
        username.setVisibility(View.VISIBLE);
        password.setVisibility(View.VISIBLE);
        loginbutton.setVisibility(View.VISIBLE);
        forgotpassword.setVisibility(View.VISIBLE);
    }

    public void hidebuttons() {
        username.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        forgotpassword.setVisibility(View.GONE);
        loginbutton.setVisibility(View.GONE);
        downloadProgress.setVisibility(View.GONE);
    }

    public void startAlarm() {
        notifyUser();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        try {
            if (mp != null) {
                mp.stop();
                mp.release();
                mp = null;
                //mp = new MediaPlayer();
            }
            mp = new MediaPlayer();
            AssetFileDescriptor afd;
            if (notify == 1) {
                afd = getAssets().openFd("frenzy.mp3");
                notify = 0;
            } else {
                afd = getAssets().openFd("alarm.mp3");
            }
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mp.prepare();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mediaplayer) {
//                    mp.stop();
//                    mp.release();
                }
            });
            mp.start();
            // FadeIn(3f);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopAlarm() {
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
            Log.i("", "Dispath event power");
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    public boolean getMobileDataState() {
        try {
            TelephonyManager telephonyService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            Method getMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("getDataEnabled");

            if (null != getMobileDataEnabledMethod) {

                return (boolean) (Boolean) getMobileDataEnabledMethod.invoke(telephonyService);
            }
        } catch (Exception ex) {
            Log.e("MOBILEDATA", "Error getting mobile data state", ex);
        }

        return false;
    }

    static Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myReceiver = new ConnectivityReceiver();

        IntentFilter filter = new IntentFilter("com.north.socket.client.BroadcastReceiver");
        IntentFilter wifiFIlter = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
        IntentFilter gpsFilter = new IntentFilter("android.location.PROVIDERS_CHANGED");
        registerReceiver(myReceiver, filter);
        registerReceiver(myReceiver, gpsFilter);
        registerReceiver(myReceiver, wifiFIlter);

        System.out.println("In Login");
        Toast.makeText(this, "In Login", Toast.LENGTH_SHORT).show();

        context = Login.this;

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        assert powerManager != null;
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        mWakeLock.acquire();


        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();

        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        forgotpassword = (Button) findViewById(R.id.forgotpassword);
        loginbutton = (Button) findViewById(R.id.loginbutton);
        connStatus = (TextView) findViewById(R.id.connStatus);
        connStatus.setSelected(true);
        setStatusText("Checking Connection");
        downloadProgress = (ProgressBar) findViewById(R.id.downloadProgress);
        Log.d("Service", "Starting Service");

        //password.setRawInputType(Configuration.KEYBOARD_QWERTY);
        application = (Application) getContext();
        if (checkAndRequestPermissions()) {
            SocketService.permissionCheck = true;
        }

        File photoDir = new File(Environment.getExternalStorageDirectory().getPath() + "/PhotoDir");
        if (!photoDir.exists()) {
            photoDir.mkdir();
        }

        checkEssentials();

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean areThereMockPermissionApps(Context context) {
        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (String requestedPermission : requestedPermissions) {
                        if (requestedPermission
                                .equals("android.permission.ACCESS_MOCK_LOCATION")
                                && !applicationInfo.packageName.equals(context.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                ///Log.e("Got exception " + e.getMessage());
            }
        }

        return count > 0;
    }

    Handler statusChange;
    boolean changeStatus = true;

    public void setStatusText(final String data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (changeStatus || data.equals("Connected")) {
                    connStatus.setText(data);
                    connStatus.setSelected(true);

                    statusChange = new Handler();
                    Runnable changeStatusOnDelay = new Runnable() {
                        @Override
                        public void run() {
                            changeStatus = true;
                        }
                    };
                    statusChange.removeCallbacksAndMessages(null);
                    statusChange.postDelayed(changeStatusOnDelay, 3000);
//                    changeStatus = false;

                }
            }
        });
    }

    public boolean checkEssentials() {
        setStatusText(getString(R.string.checking_mobile_data));
        hidebuttons();
        Context context;
        context = Login.this;
        if (simtest == 0) {
            if (!getMobileDataState()) {
                setStatusText(getString(R.string.mobile_data_disabled));
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(
                        "com.android.settings",
                        "com.android.settings.Settings$DataUsageSummaryActivity"));
                startActivity(intent);
                return false;
            }
            if (!areThereMockPermissionApps(context)) {
                setStatusText(getString(R.string.location_spoofing_app));
            }

            if (getMobileDataState()) {
                setStatusText(getString(R.string.checking_wifi));
                WifiManager wifi = (WifiManager) Login.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifi.isWifiEnabled()) {
                    setStatusText(getString(R.string.wifi_enabled));
                }
                boolean ab = Reachability.isNetworkingAvailable(application);
                if (!ab) {
                    setStatusText(getString(R.string.network_offline));
                    return false;
                }

                // connect to the server
                setStatusText(getString(R.string.checking_gps));

                LocationManager locationManagerLogin = (LocationManager)
                        getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                criteria.setAltitudeRequired(true);
                criteria.setBearingRequired(true);
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setCostAllowed(true);



                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (locationManagerLogin != null) {
                        locationManagerLogin.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 60, 30, this);
                    }
                }

                assert locationManagerLogin != null;
                if (!locationManagerLogin.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    Intent callGPSSettingIntent = new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(callGPSSettingIntent);
                    return false;
                } else {
                    setStatusText(getString(R.string.network_available));
                    if (!isMyServiceRunning(SocketService.class)) {
                        Context contextsocketstart;
                        contextsocketstart = Login.this;
                        Intent isocketstart = new Intent(Login.this, SocketService.class);
                        contextsocketstart.startService(isocketstart);
                        setStatusText(getString(R.string.starting_service));
                        System.out.println("Starting Socket Service. Service Not Running");
                    } else {
                        Context contextsocketstart;
                        contextsocketstart = Login.this;
                        Intent isocketstart = new Intent(Login.this, SocketService.class);
                        setStatusText(getString(R.string.stoping_service));
                        System.out.println("Stopping Socket Service.");
                        boolean socketSuccess = contextsocketstart.stopService(isocketstart);
                        Intent isocketstartb = new Intent(Login.this, SocketService.class);
                        setStatusText(getString(R.string.starting_service));
                        System.out.println("Starting Socket Service.Service is already running" + socketSuccess);
                        contextsocketstart.startService(isocketstartb);
                    }

                    if (!isMyServiceRunning(SocketServiceFile.class)) {
                        Context contextsocketstart;
                        contextsocketstart = Login.this;
                        Intent isocketstart = new Intent(Login.this, SocketServiceFile.class);
                        contextsocketstart.startService(isocketstart);
                        setStatusText(getString(R.string.starting_service));
                        System.out.println("Starting Socket Service File.");
                    }
                }

            }
        } else {
            if (!isMyServiceRunning(SocketService.class)) {
                Context contextsocketstart;
                contextsocketstart = Login.this;
                Intent isocketstart = new Intent(Login.this, SocketService.class);
                contextsocketstart.startService(isocketstart);
                setStatusText(getString(R.string.starting_service));
            } else {
                Context contextsocketstart;
                contextsocketstart = Login.this;
                Intent isocketstart = new Intent(Login.this, SocketService.class);
                setStatusText(getString(R.string.stoping_service));
                contextsocketstart.stopService(isocketstart);
                Intent isocketstartb = new Intent(Login.this, SocketService.class);
                setStatusText(getString(R.string.starting_service));
                contextsocketstart.startService(isocketstartb);
            }

            if (!isMyServiceRunning(SocketServiceFile.class)) {
                Context contextsocketstart;
                contextsocketstart = Login.this;
                Intent isocketstart = new Intent(Login.this, SocketServiceFile.class);
                contextsocketstart.startService(isocketstart);
                setStatusText(getString(R.string.starting_service));
            }
        }
        return true;
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                String nameText = data.getStringExtra("checkNo");
                if (nameText != null) {
                    username.setText(nameText);
                    password.setText("");
                    password.setHint("Enter Password");
                }

                String recvdata = data.getStringExtra("senddata");
                if (recvdata != null) {
                    if (recvdata.equals("GPSDISABLED")) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                                //.setCancelable(false)
                                .setPositiveButton("Goto Settings Page To Enable GPS",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent callGPSSettingIntent = new Intent(
                                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                                startActivity(callGPSSettingIntent);
                                            }
                                        });
                        alertDialogBuilder.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //dialog.cancel();
                                        Intent callGPSSettingIntent = new Intent(
                                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivity(callGPSSettingIntent);
                                    }
                                });
                        AlertDialog alert = alertDialogBuilder.create();
                        alert.show();

                    }
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        stopAlarm();
        if (myReceiver != null) {

            try {
                unregisterReceiver(myReceiver);
                myReceiver = null;
            } catch (IllegalArgumentException e) {
                // Check wether we are in debug mode

                e.printStackTrace();

            }
        }
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        templocation = location;
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

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    public class ConnectivityReceiver extends BroadcastReceiver {

        public ConnectivityReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int status = NetworkUtil.getConnectivityStatusString(context);

            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                // react on GPS provider change action
                System.out.print("Provider Changed");
                if (ActivityCompat.checkSelfPermission(Login.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Login.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Intent intent1 = new Intent();
                        intent1.setAction("com.north.socket.client.EssentialsNet");
                        intent1.putExtra("wifiEnabled", "1");
                        sendBroadcast(intent1);
                        Log.i("About GPS", "GPS is Enabled in your devide");
                    } else {
                        if (startEssentialsCheck == 0) {
                            try {
                                statedata = getString(R.string.location_off); //"Location Services is not working. Please restart the phone.";
                                stateview = "com.north.socket.client.StartEssentials";
                                Intent activeview = new Intent(Login.this, Class.forName(stateview));
                                activeview.putExtra("qdata", statedata);
                                activeview.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivityForResult(activeview, 5);
                            } catch (ClassNotFoundException ex) {
                                System.out.println(ex.toString());
                            }

                            startEssentialsCheck = 1;

                        }
                    }
                }
            }
            if (!"android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                if (status == NetworkUtil.TYPE_MOBILE) {
                    System.out.println(NetworkUtil.getConnectivityStatus(context));
                    WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    if (wifi.isWifiEnabled()) {
                        //wifi is enabled
                        Intent intent1 = new Intent();
                        intent1.setAction("com.north.socket.client.EssentialsNet");
                        intent1.putExtra("wifiEnabled", "1");
                        sendBroadcast(intent1);
                        System.out.println("true");
                    } else {
                        if (startEssentialsCheck == 0) {
                            startEssentialsCheck = 1;
                            Intent intentb = new Intent();
                            intentb.setAction("com.north.socket.client.BroadcastReceiver");
                            intentb.putExtra("enablewifi", "enablewifi");
                            sendBroadcast(intentb);
                        }

                    }
//                    new ResumeForceExitPause(context).execute();
                } else if (status == NetworkUtil.NETWORK_STAUS_WIFI) {
                    System.out.println(NetworkUtil.TYPE_WIFI);
                    boolean mobileDataEnabled; // Assume disabled
                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    try {
                        Class cmClass = Class.forName(cm.getClass().getName());
                        Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
                        method.setAccessible(true); // Make the method callable
                        // get the setting for "mobile data"
                        mobileDataEnabled = (Boolean) method.invoke(cm);
                        if (mobileDataEnabled) {
                            Intent intent1 = new Intent();
                            intent1.setAction("com.north.socket.client.EssentialsNet");
                            intent1.putExtra("wifiEnabled", "1");
                            sendBroadcast(intent1);
                        } else {
                            if (startEssentialsCheck == 0) {
                                startEssentialsCheck = 1;
                                Intent intentb = new Intent();
                                intentb.setAction("com.north.socket.client.BroadcastReceiver");
                                intentb.putExtra("enablemobiledata", "enablemobiledata");
                                sendBroadcast(intentb);
                            }
                        }
                    } catch (Exception e) {
                        // Some problem accessible private API
                    }
                }

            }

            String wrongversion = intent.getStringExtra("wrongversion");
            String enablemobiledata = intent.getStringExtra("enablemobiledata");
            String displayqueue = intent.getStringExtra("activequeue");
            String sendimplant = intent.getStringExtra("sendimplant");
            String displaysignature = intent.getStringExtra("displaysignature");
            String displayphoto = intent.getStringExtra("displayphoto");
            String noqueue = intent.getStringExtra("noqueue");
            String ambreport = intent.getStringExtra("ambreport");
            String displayComplaints = intent.getStringExtra("displayComplaints");
            String displayPayment = intent.getStringExtra("displayPayment");
            String displayContent = intent.getStringExtra("displayContent");
            if (displayContent != null) {
                setStatusText(displayContent);
            }
            if(displayPayment != null){
                try {
                    String stateview = "com.north.socket.client.awaitPayment";
                    Intent activeview = new Intent(Login.this, Class.forName(stateview));
                    activeview.putExtra("qdata", displayPayment);
                    activeview.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivityForResult(activeview, 5);
                }catch (ClassNotFoundException ignored){

                }
            }
            if (displayComplaints != null) {
                stateview = "com.north.socket.client.TakingComplaints";
                try {
                    Intent activeView = new Intent(Login.this, Class.forName(stateview));
                    activeView.putExtra("bookingData", displayComplaints);
                    activeView.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivityForResult(activeView, 5);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            if (ambreport != null) {
                if (!ambreport.equals("none")) {

                    stateview = "com.north.socket.client.ListHistory";
                    Intent activeview = null;
                    try {
                        activeview = new Intent(Login.this, Class.forName(stateview));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (activeview != null) {
                        activeview.putExtra("data", ambreport);
                    }
                    startActivityForResult(activeview, 5);
                }
            }


            if (wrongversion != null) {
                try {
                    statedata = "This version needs an Update. Please Download the latest version from the Google Play Store.";
                    stateview = "com.north.socket.client.StartEssentials";
                    Intent activeview = new Intent(Login.this, Class.forName(stateview));
                    activeview.putExtra("qdata", statedata);
                    activeview.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivityForResult(activeview, 5);
                } catch (ClassNotFoundException ex) {
                    System.out.println(ex.toString());
                }
            }


            if (enablemobiledata != null) {
                try {
                    statedata = "Please Enable Mobile Data and Restart App";
                    stateview = "com.north.socket.client.StartEssentials";
                    Intent activeview = new Intent(Login.this, Class.forName(stateview));
                    activeview.putExtra("qdata", statedata);
                    activeview.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivityForResult(activeview, 5);
                } catch (ClassNotFoundException ex) {
                    System.out.println(ex.toString());
                }
            }

            if (simtest == 0) {
                String enablegps = intent.getStringExtra("enablegps");
                if (enablegps != null) {
                    try {
                        statedata = "Please Enable GPS and Restart App";
                        stateview = "com.north.socket.client.StartEssentials";
                        Intent activeview = new Intent(Login.this, Class.forName(stateview));
                        activeview.putExtra("qdata", statedata);
                        activeview.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivityForResult(activeview, 5);
                    } catch (ClassNotFoundException ex) {
                        System.out.println(ex.toString());
                    }
                }

                String enablewifi = intent.getStringExtra("enablewifi");
                if (enablewifi != null) {
                    try {
                        statedata = "Please Enable Wifi and Restart App";
                        stateview = "com.north.socket.client.StartEssentials";
                        Intent activeview = new Intent(Login.this, Class.forName(stateview));
                        activeview.putExtra("qdata", statedata);
                        activeview.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivityForResult(activeview, 5);
                    } catch (ClassNotFoundException ex) {
                        System.out.println(ex.toString());
                    }
                }
            }

            if (displayqueue != null) {
                try {
                    statedata = displayqueue;
                    stateview = "com.north.socket.client.ActiveTask";
                    Intent activeview = new Intent(Login.this, Class.forName(stateview));
                    activeview.putExtra("qdata", statedata);
                    if (startTime - System.currentTimeMillis() < 300000) {
                        if ((latitudepresent != null) && (longitudepresent != null))
                            activeview.putExtra("latlong", latitudepresent + "|" + longitudepresent + "|");

                        String[] queueDataBrk = statedata.split("\\|");
                        if (queueDataBrk.length > 1) {
                            String[] queueDataSecBrk = queueDataBrk[0].split("\\$");
                            if (queueDataSecBrk.length > 49) {
                                vehicleType = queueDataSecBrk[49];
                            }
                        }
                    }
                    activeview.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivityForResult(activeview, 5);

                } catch (ClassNotFoundException ex) {
                    System.out.println(ex.toString());
                }
            }

            if (displayphoto != null) {

                try {
                    statedata = displayphoto;
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
                        stateview = "com.north.socket.client.PhotoGallery";
                        Intent activeview = new Intent(Login.this, Class.forName(stateview));
                        activeview.putExtra("qdata", statedata);
                        activeview.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivityForResult(activeview, 5);
                    }
                } catch (ClassNotFoundException ex) {
                    System.out.println(ex.toString());
                }

            }

            if (sendimplant != null) {
                stateview = "com.north.socket.client.implantHome";
                try {
                    Intent activeview = new Intent(Login.this, Class.forName(stateview));
                    activeview.putExtra("qdata", sendimplant);
                    startActivityForResult(activeview, 5);
                } catch (ClassNotFoundException e) {
                    //print("Couldn't find Gum");
                }

            }


            if (noqueue != null) {
                try {
                    statedata = noqueue;
                    String[] queuestate;
                    queuestate = statedata.split("\\$");
                    String drivingtest = queuestate[11];
                    String addressverify = queuestate[9];
                    int drivingtestint = 0;
                    int addressverifyint = 0;
                    if (isInteger(drivingtest)) {
                        drivingtestint = Integer.parseInt(drivingtest);
                    }
                    if (isInteger(addressverify)) {
                        addressverifyint = Integer.parseInt(drivingtest);
                    }
                    if (queuestate[6].equals("0")) {
                        stateview = "com.north.socket.client.Attendance";
                        Intent activeview = new Intent(Login.this, Class.forName(stateview));
                        activeview.putExtra("qdata", statedata);
                        startActivityForResult(activeview, 5);
                    } else if (addressverifyint > 0) {
                        stateview = "com.north.socket.client.AddressVerify";
                        Intent activeview = new Intent(Login.this, Class.forName(stateview));
                        activeview.putExtra("qdata", statedata);
                        startActivityForResult(activeview, 5);
                    } else if (drivingtestint > 0) {
                        stateview = "com.north.socket.client.DrivingTest";
                        Intent activeview = new Intent(Login.this, Class.forName(stateview));
                        activeview.putExtra("qdata", statedata);
                        startActivityForResult(activeview, 5);
                    } else {

                        stateview = "com.north.socket.client.AwaitingTask";
                        Intent activeview = new Intent(Login.this, Class.forName(stateview));
                        activeview.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        activeview.putExtra("qdata", statedata);
                        startActivityForResult(activeview, 5);
                    }
                } catch (ClassNotFoundException ex) {
                    System.out.println(ex.toString());
                }
            }

            String[] displayAcceptTask = intent.getStringArrayExtra("displayAcceptTask");
            if(displayAcceptTask != null){


                Intent acceptTask = new Intent(context, AcceptTask.class);


                acceptTask.putExtra("detailsInfo", displayAcceptTask);
                acceptTask.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(acceptTask);
            }

            if (displaysignature != null) {
                if (signdone == 0) {
                    try {
                        statedata = displaysignature;
                        stateview = "com.north.socket.client.CaptureSignature";
                        signdone = 1;
                        Intent activeview = new Intent(Login.this, Class.forName(stateview));
                        activeview.putExtra("qdata", statedata);
                        startActivityForResult(activeview, 5);
                    } catch (ClassNotFoundException ex) {
                        System.out.println(ex.toString());
                    }
                }
            }

            String errordata = intent.getStringExtra("senderror");
            if (errordata != null) {

                if (errordata.equals("red")) {
                    disconnected = 1;
                    Log.d("Network", "Retrying after 3 seconds");
                    Intent intentb = new Intent();
                    intentb.setAction("com.north.socket.client.POSSNBroadcastReceiver");
                    intentb.putExtra("senddata", "red");
                    sendBroadcast(intentb);
                    intentb.putExtra("senddata", "Reconnecting after 3 seconds");
                    sendBroadcast(intentb);


                    String root = Environment.getExternalStorageDirectory().toString();

                    File file = new File(root, "connectionlog.txt");
                    try {
                        FileWriter writer = new FileWriter(file, true);
                        writer.append("Retry request received. Retrying after 3 seconds\n");
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } else {
                    connStatus.setText(errordata);
                }
            }
            String disabled = intent.getStringExtra("disabled");
            if (disabled != null) {
                if (disabled.equals("0")) {
                    stateview = "com.north.socket.client.DisabledAmbassador";
                    Intent activeview = null;
                    try {
                        activeview = new Intent(Login.this, Class.forName(stateview));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    startActivityForResult(activeview, 5);
                }
            }
            String data = intent.getStringExtra("senddata");
            if (data != null) {
                if (data.equals("red")) {
                    Intent intentb = new Intent();
                    intentb.setAction("com.north.socket.client.POSSNBroadcastReceiver");
                    intentb.putExtra("senddata", "red");
                    sendBroadcast(intentb);
                }
                if (data.equals("photodone")) {
                    takephoto = 0;
                }
                if (data.equals("signdone")) {
                    signdone = 0;
                }
                if (data.equals("green")) {
                    Intent intentb = new Intent();
                    intentb.setAction("com.north.socket.client.POSSNBroadcastReceiver");
                    intentb.putExtra("senddata", "green");
                    sendBroadcast(intentb);
                }

                switch (data) {
                    case "startalarm":
                        //notify = 1;
                        startAlarm();
                        break;
                    case "stopalarm":
                        stopAlarm();
                        break;
                    case "sendnotify":

                        startAlarm();
                        break;
                    case "hidebuttons":
                        hidebuttons();
                        break;
                    case "revealbuttons":
                        revealbuttons();
                        break;
                    default:

                        break;
                }
            }
        }
    }

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        int locationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);

        int storagePermission = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            storagePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        int internalStoragePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int phonePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        if (internalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (phonePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                }
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "sms & location services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showDialogOK("SMS and Location Services Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    finishAffinity();
                                                    break;
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    public static class MyAdmin extends DeviceAdminReceiver {
// implement onEnabled(), onDisabled(), …
    }
}


