package com.north.socket.client;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by sree on 11/02/16.
 */

public class BootUpReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        /***** For start Service  ****/
        Intent myIntent = new Intent(context, SocketService.class);
        context.startService(myIntent);
    }

}