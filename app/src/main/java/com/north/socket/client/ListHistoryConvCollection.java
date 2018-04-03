package com.north.socket.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListHistoryConvCollection extends Activity {
    String[] typeofCommand;

    ListView historyview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_history_conv);
        Bundle bundle = getIntent().getExtras();
        String jammed;
        String tmp;
        String[] typeofCommandHistory;
        tmp = bundle.getString("data");
        typeofCommandHistory = tmp.split("Æ");
        historyview = (ListView) findViewById(R.id.listViewconv);
        //typeofCommand = s.split("\\$");
        if(bundle.getString("data")!= null) {
            String s;
            s = typeofCommandHistory[0];
            s = s.replace("#", ":");
            String[] historylist;
            s = s.replace("^", "    ");
            s=s.replaceAll("\\[.*?\\]", "");
            s=s.replaceAll("410", "PU");
            s=s.replaceAll("802", "DO");
            s=s.replaceAll("803", "DO");
            s=s.replaceAll("999", "9pm");
            s=s.replaceAll("111", "7am");
            s=s.replaceAll("10000", "Miss");
            s=s.replaceAll("777", "8Task");
            historylist = s.split("\\$");

            historyview.setOnItemClickListener(new ListClickHandler());
            historyview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, historylist));


        }
    }

    public class ListClickHandler implements OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
            // TODO Auto-generated method stub
            //TextView listText = (TextView) view.findViewById(R.id.listText);
            //String text = listText.getText().toString();

            finish();

        }

    }
}
