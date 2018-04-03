package com.north.socket.client;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.north.socket.client.ActiveTask;
import com.north.socket.client.R;

public class webPageScreen extends Activity {

    Bundle bundle;
    String queueID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_page_screen);

        bundle = getIntent().getExtras();

        String link = bundle.getString("Link");
        WebView webpage = (WebView)findViewById(R.id.webPage);
        if(link.equals("5")){
            queueID = bundle.getString("queueID");
            webpage.loadUrl("http://m.21north.in/hybrid/link.php?link=" + link + "&id=" + MenuClass.ambId + "&queueID=" + queueID);
        }else {
            webpage.loadUrl("http://m.21north.in/hybrid/link.php?link=" + link + "&id=" + MenuClass.ambId);
        }
    }
}
