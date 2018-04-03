package com.north.socket.client;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * Created by sachintomar on 26/09/17.
 */

class CustomAdapter extends BaseAdapter {

    String [][] result;
    Context context;
    int [] imageId;
    private static LayoutInflater inflater=null;

    public CustomAdapter(AwaitingTask awaitingTask, String[][] prgmNameList, int[] prgmImages) {

        result=prgmNameList;
        context=awaitingTask;

        imageId = new int[prgmNameList.length];

        for(int i = 0; i < prgmNameList.length; i++) {
            imageId[i] = R.drawable.edit_icons;
        }
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public CustomAdapter(ActiveTask activeTask, String[][] prgmNameList, int[] prgmImages){

        result=prgmNameList;
        context=activeTask;

        imageId = new int[prgmNameList.length];

        for(int i = 0; i < prgmNameList.length; i++) {
            imageId[i] = R.drawable.edit_icons;
        }
//        imageId=prgmImages;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return result.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class Holder
    {
        TextView tv;
        ImageView img;
    }
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.program_list, null);
        holder.tv=(TextView) rowView.findViewById(R.id.textView1);
        holder.img=(ImageView) rowView.findViewById(R.id.imageView1);
        holder.tv.setText(result[position][1]);
        holder.img.setImageResource(imageId[position]);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (result[position][0]) {
                    case "-1": {

                        MenuClass.reportHeading = result[position][1];
                        Intent intent = new Intent();
                        if (SocketService.state > 0) {
                            intent.setAction("com.north.socket.client.POSSNBroadcastReceiver");
                        } else {
                            intent.setAction("com.north.socket.client.POSSNBroadcastReceiverawaiting");
                        }
                        intent.putExtra("ShowVerify", "1");
                        context.sendBroadcast(intent);
                        break;
                    }
                    case "-2": {
                        MenuClass.reportHeading = result[position][1];
                        Intent intent = new Intent(context, webPageScreen.class);
                        intent.putExtra("Link", "4");
                        context.startActivity(intent);
                        break;
                    }
                    case "-3": {
                        MenuClass.reportHeading = result[position][1];
                        Intent intent = new Intent(context, webPageScreen.class);
                        intent.putExtra("Link", "1");
                        context.startActivity(intent);
                        break;
                    }
                    case "-4": {
                        MenuClass.reportHeading = result[position][1];
                        Intent intent = new Intent(context, webPageScreen.class);
                        intent.putExtra("Link", "2");
                        context.startActivity(intent);
                        break;
                    }
                    case "-5": {
                        MenuClass.reportHeading = result[position][1];
                        Intent intent = new Intent(context, webPageScreen.class);
                        intent.putExtra("Link", "3");
                        context.startActivity(intent);
                        break;
                    }
                    default: {
                        MenuClass.reportHeading = result[position][1];
                        Intent intent = new Intent();
                        if (SocketService.state > 0) {
                            intent.setAction("com.north.socket.client.POSSNBroadcastReceiver");
                        } else {
                            intent.setAction("com.north.socket.client.POSSNBroadcastReceiverawaiting");
                        }
                        System.out.println("Menu Item Clicked");
                        intent.putExtra("DisplayOverlay", "1");
                        context.sendBroadcast(intent);
                        String data = "NEWAMBREPORT|" + result[position][0] + "|";
                        Intent intent1 = new Intent();
                        intent1.setAction("com.north.socket.client.LBBroadcastReceiver");
                        intent1.putExtra("sendchat", data);
                        context.sendBroadcast(intent1);
                        break;
                    }
                }
            }
        });
        return rowView;
    }
}
