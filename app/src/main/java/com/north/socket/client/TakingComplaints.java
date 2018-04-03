package com.north.socket.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TakingComplaints extends Activity {

    static String[][] category;
    static String[][] subCategry;

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;

    String[][] complainAdded;
    String[] subCompCat;
    Context takingComp;

    int addedComplaints = 0;
    ListView addedCompList;


    static ArrayList<String> expParentTitleList;
    static ArrayList<Integer> flags;

    CustomAdapterComp customAdapterComp;

    int dataCheck = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taking_complaints);

        takingComp = this;

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        final int screenHeight = displayMetrics.heightPixels;

        expParentTitleList = new ArrayList<>();
        flags = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        final String displayComp = bundle.getString("bookingData");
        if (displayComp != null) {
            String[] firstSplit = displayComp.split("\\^");
            String[] compCat = firstSplit[0].split("\\|");
            subCompCat = firstSplit[1].split("\\|");
            category = new String[compCat.length + 1][2];
            subCategry = new String[subCompCat.length][3];
            for (int i = 0; i < compCat.length; i++) {
                String[] eachCat = compCat[i].split("\\$");
                category[i][0] = eachCat[0];
                category[i][1] = eachCat[1];

            }
            category[compCat.length][0] = "-1";
            category[compCat.length][1] = "Other Complaints";
            for (int i = 0; i < subCompCat.length; i++) {
                String[] subComp = subCompCat[i].split("\\$");
                subCategry[i][0] = subComp[0];
                subCategry[i][1] = subComp[1];
                subCategry[i][2] = subComp[2];
            }
            complainAdded = new String[subCompCat.length][3];
        }

        addedCompList = (ListView) findViewById(R.id.addedCompList);
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);

        expandableListDetail = ExpandableListDataPump.getData();
        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {


            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < expandableListTitle.size(); i++) {

                    if ((expandableListTitle.get(groupPosition)).equals("Other Complaints")) {
                        if (dataCheck == 1) {
                            Intent intent = new Intent(takingComp, FreeComplaints.class);
                            intent.putExtra("addedComp", complainAdded);
                            intent.putExtra("numberOfComp", addedComplaints);
                            startActivity(intent);
                            dataCheck = 0;
                        }
                    }
                }
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

                for (int i = 0; i < expandableListTitle.size(); i++) {

                    if ((expandableListTitle.get(groupPosition)).equals("Other Complaints")) {
                        if (dataCheck == 1) {
                            Intent intent = new Intent(takingComp, FreeComplaints.class);
                            intent.putExtra("addedComp", complainAdded);
                            intent.putExtra("numberOfComp", addedComplaints);
                            startActivity(intent);
                            dataCheck = 0;
                        }
                    }
                }
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                for (int i = 0; i < subCategry.length; i++) {
                    if ((expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition)).equals(subCategry[i][2])) {

                        complainAdded[addedComplaints][0] = subCategry[i][0];
                        complainAdded[addedComplaints][1] = subCategry[i][1];
                        complainAdded[addedComplaints][2] = subCategry[i][2];

                        List<String[]> l = new ArrayList<>(Arrays.asList(subCategry));
                        l.remove(i);
                        subCategry = l.toArray(new String[][]{});

                        expParentTitleList.clear();
                        flags.clear();
                        for (int j = 0; j <= addedComplaints; j++) {
                            expParentTitleList.add(complainAdded[j][2]);
                            flags.add(R.drawable.delete_sign);
                        }
                        addedComplaints = addedComplaints + 1;

                        customAdapterComp = new CustomAdapterComp(takingComp, expParentTitleList, flags);
                        addedCompList.setAdapter(customAdapterComp);
                        customAdapterComp.notifyDataSetChanged();
                        expandableListDetail = ExpandableListDataPump.getData();
                        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
                        expandableListAdapter = new CustomExpandableListAdapter(takingComp, expandableListTitle, expandableListDetail);
                        expandableListView.setAdapter(expandableListAdapter);

                        ListAdapter listadp = addedCompList.getAdapter();
                        if (listadp != null) {
                            int totalHeight = 0;
                            for (int k = 0; k < listadp.getCount(); k++) {
                                View listItem = listadp.getView(k, null, addedCompList);
                                listItem.measure(0, 0);
                                totalHeight += listItem.getMeasuredHeight();
                            }
                            ViewGroup.LayoutParams params = addedCompList.getLayoutParams();
                            params.height = totalHeight + (addedCompList.getDividerHeight() * (listadp.getCount() - 2));
                            addedCompList.setLayoutParams(params);
                            addedCompList.requestLayout();
                            expandableListView.setLayoutParams(new LinearLayout.LayoutParams(displayMetrics.widthPixels, screenHeight));
                        }
                        break;
                    }
                }
                return false;
            }
        });
        addedCompList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                for (int j = 0; j < complainAdded.length; j++) {
                    if (expParentTitleList.get(i).equals(complainAdded[j][2])) {
                        expParentTitleList.remove(i);
                        flags.remove(i);

                        addedComplaints = addedComplaints - 1;
                        List<String[]> subCatList = new ArrayList<>(Arrays.asList(subCategry));

                        String[] tempArray = new String[3];
                        tempArray[0] = complainAdded[j][0];
                        tempArray[1] = complainAdded[j][1];
                        tempArray[2] = complainAdded[j][2];
                        subCatList.add(tempArray);
                        subCategry = subCatList.toArray(new String[][]{});

                        List<String[]> compAddedList = new ArrayList<>(Arrays.asList(complainAdded));
                        compAddedList.remove(j);
                        complainAdded = compAddedList.toArray(new String[][]{});

                        ListAdapter listadp = addedCompList.getAdapter();
                        if (listadp != null) {
                            int totalHeight = 0;
                            for (int k = 0; k < listadp.getCount(); k++) {
                                View listItem = listadp.getView(k, null, addedCompList);
                                listItem.measure(0, 0);
                                totalHeight += listItem.getMeasuredHeight();
                            }
                            ViewGroup.LayoutParams params = addedCompList.getLayoutParams();
                            params.height = totalHeight + (addedCompList.getDividerHeight() * (listadp.getCount() - 1));
                            addedCompList.setLayoutParams(params);
                            addedCompList.requestLayout();
//                            expandableListView.setY(totalHeight);
                        }

                        customAdapterComp = new CustomAdapterComp(takingComp, expParentTitleList, flags);
                        addedCompList.setAdapter(customAdapterComp);
                        customAdapterComp.notifyDataSetChanged();
                        expandableListDetail = ExpandableListDataPump.getData();
                        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
                        expandableListAdapter = new CustomExpandableListAdapter(takingComp, expandableListTitle, expandableListDetail);
                        expandableListView.setAdapter(expandableListAdapter);
                        break;

                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onResume() {
        super.onResume();
        dataCheck = 1;
    }

    String addedCompId = "0,";

    public void submitComplains(View view) {
        if (addedComplaints > 0) {
            for (int i = 0; i < addedComplaints; i++) {
                addedCompId = addedCompId.concat(complainAdded[i][1] + ",");
            }

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(R.string.add_other_complaints);
            alertDialogBuilder.setPositiveButton(R.string.yes,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            Intent intent = new Intent(takingComp, FreeComplaints.class);
                            intent.putExtra("addedComp", complainAdded);
                            intent.putExtra("numberOfComp", addedComplaints);
                            startActivity(intent);
                        }
                    });

            alertDialogBuilder.setNegativeButton(R.string.skip,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            String data = "NEXTMOVE|" + SocketService.latitude + "|" + SocketService.longitude + "|1|210|" + addedCompId + "|#####|";
                            Intent intent = new Intent();
                            intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                            intent.putExtra("sendchat", data);
                            sendBroadcast(intent);
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(R.string.add_one_comp);
            alertDialogBuilder.setPositiveButton(R.string.OK,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
//        data = "NEXTMOVE|" + SocketService.latitude + "|" + SocketService.longitude + "|1|210|" + addedCompId + "|#####|";
//        Intent intent = new Intent();
//        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
//        intent.putExtra("sendchat", data);
//        sendBroadcast(intent);
    }
}
