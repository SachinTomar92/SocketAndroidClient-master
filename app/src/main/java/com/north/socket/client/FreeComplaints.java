package com.north.socket.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class FreeComplaints extends Activity {

    TextView addedCompTextView;
    EditText compEditText;

    Bundle bundle;

    String complaintsText;
    String addedCompID;
    ListView addedCompList;
    ArrayList<String> addedCompArrayList;
    ArrayList<String> addedCompIDArrayList;
    ArrayList<Integer> flags;

    CustomAdapterComp customAdapterComp;
    Context freeComplaintsContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_complaints);

        freeComplaintsContext = this;

        bundle = getIntent().getExtras();

        addedCompList = (ListView) findViewById(R.id.addedCompListFree);

        compEditText = (EditText) findViewById(R.id.ComplaintsEditText);
        addedCompArrayList = new ArrayList<>();
        addedCompIDArrayList = new ArrayList<>();
        flags = new ArrayList<>();

        complaintsText = "";
        addedCompID = "0";

        int numberOfComp = bundle.getInt("numberOfComp");
        if (numberOfComp != 0) {
            String[][] addedComp = (String[][]) bundle.get("addedComp");
            for (int i = 0; i < numberOfComp; i++) {
                complaintsText = complaintsText + addedComp[i][2] + ", ";
                addedCompID = addedCompID + addedComp[i][1] + ",";
                addedCompIDArrayList.add(addedComp[i][1]);
                addedCompArrayList.add(addedComp[i][2]);
                flags.add(R.drawable.tools);
            }
        }
        customAdapterComp = new CustomAdapterComp(this, addedCompArrayList, flags);
        addedCompList.setAdapter(customAdapterComp);

        ListAdapter listadp = addedCompList.getAdapter();
        if (listadp != null) {
            int totalHeight = 0;
            ViewGroup.LayoutParams params = addedCompList.getLayoutParams();
            for (int k = 0; k < listadp.getCount(); k++) {
                View listItem = listadp.getView(k, null, addedCompList);
                listItem.measure(0, 0);
                if (listadp.getCount() <= 3) {
                    totalHeight += listItem.getMeasuredHeight();
                    params.height = totalHeight + (addedCompList.getDividerHeight() * (listadp.getCount() - 2));
                } else {
                    params.height = 400;
                    break;
                }
            }


            addedCompList.setLayoutParams(params);
            addedCompList.requestLayout();
        }

    }

    public void nextButtonCLicked(View view) {
        final String[] data = new String[1];


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.are_you_sure);
        alertDialogBuilder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if ((compEditText.getText().length()) == 0) {
                            data[0] = "NEXTMOVE|" + SocketService.latitude + "|" + SocketService.longitude + "|1|210|" + addedCompID + "|#####|";
                            Intent intent = new Intent();
                            intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                            intent.putExtra("sendchat", data[0]);
                            sendBroadcast(intent);
                        } else {
                            data[0] = "NEXTMOVE|" + SocketService.latitude + "|" + SocketService.longitude + "|1|210|" + addedCompID + "|" + compEditText.getText() + "|";
                            Intent intent = new Intent();
                            intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                            intent.putExtra("sendchat", data[0]);
                            sendBroadcast(intent);
                        }
                    }
                });

        alertDialogBuilder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }
}
