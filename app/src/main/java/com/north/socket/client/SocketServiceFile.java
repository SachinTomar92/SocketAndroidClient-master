package com.north.socket.client;

import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

import needle.Needle;

/*
 * Created by sree on 11/02/16.
 */
public class SocketServiceFile extends Service implements LocationListener {
    private TCPClientFile mTcpClient = null;
    Application application;
    ConnectivityReceiver myReceiver;
    public static int disconnected = 0;
    int issending = 0;

    String[] typeofCommand;

    private Handler pingServer = new Handler();
    private Handler pingOpen = new Handler();
    private Handler retryServer = new Handler();

    private Handler sendFiles = new Handler();

    int state = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, START_NOT_STICKY, startId);
//        return Service.START_STICKY;
        return Service.START_NOT_STICKY;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        if (android.os.Debug.isDebuggerConnected())
        {
            android.os.Debug.waitForDebugger();
        }
        mTcpClient = null;

        application = (Application) AmbassadorApp.getContext();
        myReceiver = new ConnectivityReceiver();
        IntentFilter filter = new IntentFilter("com.north.socket.client.LBBroadcastReceiverFile");
        registerReceiver(myReceiver, filter);

        if (Reachability.isNetworkingAvailable(application)) {


            //conctTask.executeOnExecutor(threadPoolExecutor);


            Needle.onBackgroundThread().withThreadPoolSize(20).execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Connecting to 21North Photo Server.");
                    runServer();
                    // something that we want to do serially but not on the main thread
                }
            });
        }

    }


    public void sendToast(String data) {
        Intent toasty = new Intent();
        toasty.setAction("com.north.socket.client.BroadcastReceiver");
        toasty.putExtra("sendtoast", data);
        sendBroadcast(toasty);
    }

    public void runServer() {
        mTcpClient = new TCPClientFile(new TCPClientFile.OnMessageReceived() {
            @Override
            //here the messageReceived method is implemented
            public void messageReceived(String message) {
                try {
                    if (message != null) {
                        System.out.println("Needle Return Message from Socket File>>>>> " + message);
                        if (mTcpClient != null) {
                            String[] typeofCommand = message.split("\\|");
                            if (typeofCommand[0].equals("HELLO")) {
                                retryServer.removeCallbacksAndMessages(null);
                                pingServer.postDelayed(serverPing, 20000);
                                sendFiles.postDelayed(sendAllFiles, 50);
                                //}
                                disconnected = 0;
                                TCPClientFile.alreadysending = 0;
                                //mTcpClient.sendFile();
                            }
                            else if (typeofCommand[0].equals("ULCOMP")) {

                                if(TCPClientFile.timerToSetDefault != null) {
                                    TCPClientFile.timerToSetDefault.removeCallbacksAndMessages(null);
                                }
                                String root = Environment.getExternalStorageDirectory().toString();
                                File directory = new File(root + "/PhotoDir");
                                File f = new File(directory, typeofCommand[1]);
                                if(!typeofCommand[1].contains("donotdelete")) {
                                    f.delete();
                                }
                                Intent intentb = new Intent();
                                intentb.setAction("com.north.socket.client.POSSNBroadcastReceiver");
                                intentb.putExtra("fileCount", "1");
                                sendBroadcast(intentb);

                                Intent intent = new Intent();
                                intent.setAction("com.north.socket.client.POSSNBroadcastReceiverawaiting");
                                intent.putExtra("fileCount", "1");
                                sendBroadcast(intent);

                                issending = 0;
                                TCPClientFile.alreadysending = 0;

                                Needle.onBackgroundThread().withThreadPoolSize(20).execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTcpClient.sendFile();
                                        // something that we want to do serially but not on the main thread
                                    }
                                });

                            }
                            if (message.equals("closed")) {

                                disconnected = 1;
                                issending = 0;
                                TCPClientFile.alreadysending = 0;

                                sendToast(getString(R.string.network_offline));
                                retryServer.removeCallbacksAndMessages(null);
                                retryServer.postDelayed(serverRetry, 5000);
                            }
                        }
                    }
                    //sendToast(message);
                    //this method calls the onProgressUpdate
                    //publishProgress(message);
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
                // Check wether we are in debug mode

                e.printStackTrace();

            }
        }
        //super.onStop();

    }

    private Runnable serverRetry = new Runnable() {
        @Override
        public void run() {
            try {
                retryServer.removeCallbacksAndMessages(null);
                sendToast("Trying now");
                //sendUnsendedReports();

                if (disconnected == 1) {

                    Needle.onBackgroundThread().withThreadPoolSize(20).execute(new Runnable() {
                        @Override
                        public void run() {
                            mTcpClient.stopClientTask();
                            // something that we want to do serially but not on the main thread
                        }
                    });


                    Needle.onBackgroundThread().withThreadPoolSize(20).execute(new Runnable() {
                        @Override
                        public void run() {
                            runServer();
                            // something that we want to do serially but not on the main thread
                        }
                    });

                    //mTcpClient.disconnected = 1;
                    //mTcpClient.run();

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
                Intent intentb = new Intent();
                intentb.setAction("com.north.socket.client.BroadcastReceiver");
                intentb.putExtra("retryserver", "true");
                sendBroadcast(intentb);

                pingOpen.removeCallbacksAndMessages(null);
                if (disconnected == 1) {
                    retryServer.postDelayed(serverRetry, 5000);
                }
            } catch (Exception e) {
                // added try catch block to be sure of uninterupted execution
            }
      /* and here comes the "trick" */
            pingOpen.postDelayed(openPing, 20000);
        }
    };


    private Runnable sendAllFiles = new Runnable() {
        @Override
        public void run() {
            try {


                sendFiles.removeCallbacksAndMessages(null);
                if (issending == 0) {
                    Needle.onBackgroundThread().withThreadPoolSize(20).execute(new Runnable() {
                        @Override
                        public void run() {
                            issending = 1;
                            int issent = mTcpClient.sendFile();
                            if (issent == 0) {
                                issending = 0;
                            }

                            // something that we want to do serially but not on the main thread
                        }
                    });
                }
                sendFiles.postDelayed(sendAllFiles, 5000);
            } catch (Exception e) {
                // added try catch block to be sure of uninterupted execution
            }
      /* and here comes the "trick" */
            //pingServer.postDelayed(this, 1000);
        }
    };

    private Runnable serverPing = new Runnable() {
        @Override
        public void run() {
            try {

                pingServer.removeCallbacksAndMessages(null);
                System.out.println("Starting Server Ping" + disconnected + "\n");
                if (disconnected == 1) {
                    retryServer.postDelayed(serverRetry, 5000);
                }


            } catch (Exception e) {
                // added try catch block to be sure of uninterupted execution
            }
      /* and here comes the "trick" */
            //pingServer.postDelayed(this, 1000);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(this.getClass().getName(), "onBind(..)");
        return null;
    }

    @Override
    public void onDestroy() {
        if (mTcpClient != null) {
            Needle.onBackgroundThread().withThreadPoolSize(20).execute(new Runnable() {
                @Override
                public void run() {
                    mTcpClient.stopClientTask();
                    // something that we want to do serially but not on the main thread
                }
            });


        }
        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
        }
        //Toast.makeText(this, "Service Stopped!", Toast.LENGTH_LONG).show();
        Log.v(this.getClass().getName(), "Service onDestroy(). Stop AlarmManager at " + new java.sql.Timestamp(System.currentTimeMillis()).toString());
    }

    @Override
    public void onLocationChanged(Location location) {

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
            try {
                String shouldisend = intent.getStringExtra("shouldisend");
                if (shouldisend != null) {
                    if (mTcpClient != null) {
                        if (mTcpClient.socket != null) {
                            if (shouldisend.equals("1")) {
                                Needle.onBackgroundThread().withThreadPoolSize(20).execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTcpClient.startsendFile();

                                        // something that we want to do serially but not on the main thread
                                    }
                                });

                            }

                        }
                        if (mTcpClient.socket != null) {
                            if (shouldisend.equals("0")) {
                                Needle.onBackgroundThread().withThreadPoolSize(20).execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTcpClient.stopsendFile();
                                        // something that we want to do serially but not on the main thread
                                    }
                                });

                            }
                        }
                    }
                }
            } catch (Exception ignored) {

            }
        }
    }
}