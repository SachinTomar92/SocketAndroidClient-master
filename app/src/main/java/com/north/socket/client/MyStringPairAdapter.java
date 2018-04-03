package com.north.socket.client;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MyStringPairAdapter extends BaseAdapter {
    private Activity activity;
    private List<MyStringPair> stringPairList;
    

    public MyStringPairAdapter(Activity activity, List<MyStringPair> stringPairList) {
        super();
        this.activity = activity;
        this.stringPairList = stringPairList;
    }

    @Override
    public int getCount() {
        return stringPairList.size();
    }

    @Override
    public Object getItem(int position) {
        return stringPairList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        if (convertView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.listrow, null);
        }
        TextView col1 = (TextView) convertView.findViewById(R.id.column1);
        TextView col2 = (TextView) convertView.findViewById(R.id.column2);
        TextView col3 = (TextView) convertView.findViewById(R.id.column3);
        
        col1.setText(stringPairList.get(position).getColumnOne());
        col2.setText(stringPairList.get(position).getColumnTwo());
        col3.setText(stringPairList.get(position).getColumnThree());
        
        return convertView;
    }
}
