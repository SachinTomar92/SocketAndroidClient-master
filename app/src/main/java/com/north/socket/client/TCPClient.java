package com.north.socket.client;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/*
 * @author Prashant Adesara
 * Handle the TCPClient with Socket Server.
 */

public class TCPClient {

    /**
     * Specify the Server Ip Address here. Wherex`as our Socket Server is started.
     */
    private static final String SERVERIP = "202.56.203.38"; // your computer IP address
    public static final int SERVERPORT = 7411;
    private OnMessageReceived mMessageListener = null;
    public boolean mRun = false;
    OutputStreamWriter osw;
    private PrintWriter out = null;
    //private BufferedReader in = null;
    private InputStreamReader in = null;
    private Reader input;
    Socket socket;
    int disconnected = 0;
    private int alreadyconnecting = 0;

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(final OnMessageReceived listener) {
        mMessageListener = listener;
    }


    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    public void sendMessage(String message) {

        try {
            osw.write(message, 0, message.length());
            osw.flush();
            Log.e("Client Wrote", "Message : " + message + message.length());
        } catch (java.lang.NullPointerException e) {
            mRun = false;
            mMessageListener.messageReceived("closed");
        } catch (java.net.SocketException e) {
            mRun = false;
            mMessageListener.messageReceived("closed");
        } catch (Exception e) {
            mRun = false;
            mMessageListener.messageReceived("closed");

            Log.e("TCP SI Error", "SI: Error", e);
            e.printStackTrace();
        } finally {

        }

    }

    public void stopClient() {
        mRun = false;
        //disconnected = 1;
        try {
            if (socket != null) {
                alreadyconnecting = 1;
                socket.close();
            }

        } catch (Exception e) {
            Log.e("TCP SI Error", "SI: Error", e);
            e.printStackTrace();
        } finally {

        }

    }

    public void stopClientTask() {
        mRun = false;
        disconnected = 1;
        try {
            if (socket != null) {
                alreadyconnecting = 1;
                socket.close();
            }
            //try{Thread.sleep(5000);}catch(InterruptedException e){System.out.println(e);}


        } catch (Exception e) {
            Log.e("TCP SI Error", "SI: Error", e);
            e.printStackTrace();
        } finally {
            //the socket must be closed. It is not possible to reconnect to this socket
            // after it is closed, which means a new socket instance has to be created.
            //socket.close();
        }

    }

    public void sendToast(String data) {
        Intent toasty = new Intent();
        toasty.setAction("com.north.socket.client.BroadcastReceiver");
        toasty.putExtra("sendtoast", data);
        Application applicationt = (Application) AmbassadorApp.getContext();
        applicationt.sendBroadcast(toasty);
    }

    public boolean run() {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            Log.e("TCP SI Client", "SI: Connecting...");

            //create a socket to make the connection with the server

            socket = new Socket(serverAddr, SERVERPORT);
            //socket.setSoTimeout(5000);
            Log.e("TCP SI Client", "Passed");
            sendToast("Connecting to Server " + serverAddr.toString());
            //socket = new Socket();
            //socket.setSoTimeout(200);
            //socket.connect(new InetSocketAddress(serverAddr, SERVERPORT), 200);
            try {

                //send the message to the server
                //out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                osw = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");

                //OutputStreamWriter osw;
                Log.e("TCP SI Client", "SI: Sent.");

                // Log.e("TCP SI Client", "SI: Done.");

                //receive the message which the server sends back
                //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                in = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
                //input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                //StringBuffer sb = new StringBuffer();
                int ch = 'a';
                StringBuilder sb = new StringBuilder();
                //in this while the client listens for the messages sent by the server
                Log.v("Looping", "Within Client");
                while (mRun) {

                    try {
                        // System.out.println(ch);
                        while ((ch = in.read()) != -1) {
                            //System.out.println(ch);
                            if (ch == 10) break;
                            if (ch == 0) break;
                            if (ch == (char) 'ø') break;


                            sb.append((char) ch);
                            //System.out.println(sb.toString());

                        }
                        //System.out.println("Building String Completed. Going to send now.");
                        if (ch == -1) {
                            System.out.println("Client Disconnected!");

                            mRun = false;
                            mMessageListener.messageReceived("closed");

                            System.out.println("Timed out trying to read from socket");

                        }
                    } catch (java.net.SocketException e) {
                        System.out.println("Socket Exception");
                        mRun = false;
                        mMessageListener.messageReceived("closed");

                    } catch (java.net.SocketTimeoutException e) {
                        System.out.println("Timed out trying to read from socket");
                        mRun = false;
                        mMessageListener.messageReceived("closed");
                    }
                    if (sb.length() > 0) {
                        disconnected = 0;
                        // System.out.println(System.currentTimeMillis());

                        System.out.println("Sending the received message.");
                        mMessageListener.messageReceived(sb.toString());
                    }
                    sb.setLength(0);
                }

            } catch (Exception e) {
                Log.e("TCP SI Error", "SI: Error", e);
                e.printStackTrace();
                Intent intent = new Intent();
                intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                intent.putExtra("senddata", "hidebuttons");


                intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                intent.putExtra("senderror", "retry");
                Application application = (Application) AmbassadorApp.getContext();
                application.sendBroadcast(intent);

                mRun = false;
                mMessageListener.messageReceived("closed");
            } finally {

                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                //socket.close();
            }
            // return mRun;

        } catch (IOException io) {

            System.out.println("IOException" + io.getMessage());
            mRun = false;
            mMessageListener.messageReceived("closed");

        } catch (Exception e) {
            mRun = false;
            mMessageListener.messageReceived("closed");
            Log.e("TCP SI Error", "SI: Error", e);
            e.printStackTrace();
        } finally {

        }

        return mRun;

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}