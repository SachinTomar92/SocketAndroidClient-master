package com.north.socket.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sachintomar on 22/11/17.
 */

public class CustomAdapterComp extends BaseAdapter {

    Context context;
    ArrayList <String> expTitleList;
    ArrayList <Integer> flags;
    LayoutInflater inflter;

    public CustomAdapterComp(Context applicationContext, ArrayList expTitleList, ArrayList flags) {
        this.context = context;
        this.expTitleList = expTitleList;
        this.flags = flags;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return expTitleList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflter.inflate(R.layout.activity_list_comp, null);
        TextView country = (TextView) view.findViewById(R.id.textView);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        country.setText(expTitleList.get(i));
        icon.setImageResource(flags.get(i));
        return view;
    }


}
