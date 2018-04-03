package com.north.socket.client;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static com.north.socket.client.MainActivity.activity;

public class HistoryListAdapter extends BaseAdapter {

    Context context;
    String[] headingsArray;
    String[][] detailsArray;
    LayoutInflater inflter;

    public HistoryListAdapter(){}
    public HistoryListAdapter(Context applicationContext, String[] headingsArray, String[][] detailsArray) {
        this.context = applicationContext;
        this.headingsArray = headingsArray;
        this.detailsArray = detailsArray;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return detailsArray.length;
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



        LinearLayout listLayout = new LinearLayout(context);
        listLayout.setOrientation(LinearLayout.HORIZONTAL);
        listLayout.setLayoutParams(new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT));

        LinearLayout leftLayout = new LinearLayout(context);
        leftLayout.setOrientation(LinearLayout.VERTICAL);
        leftLayout.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));
        listLayout.addView(leftLayout);

        LinearLayout rightLayout = new LinearLayout(context);
        rightLayout.setOrientation(LinearLayout.VERTICAL);
        rightLayout.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));
        listLayout.addView(rightLayout);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(40,10,10,10);

        for(int j = 0; j < headingsArray.length; j++) {
            TextView listText = new TextView(context);
            leftLayout.addView(listText);
            listText.setLayoutParams(params);
            listText.setText(headingsArray[j]);

            TextView details = new TextView((context));
            rightLayout.addView(details);
            details.setLayoutParams(params);
            if(detailsArray[i].length > j) {
                details.setText(detailsArray[i][j]);
            }
        }
        return listLayout;

    }
}
