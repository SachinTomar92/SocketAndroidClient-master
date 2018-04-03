package com.north.socket.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eze.api.EzeAPI;
import com.mswipetech.wisepad.sdk.MSWisepadController;
import com.mswipetech.wisepad.sdk.data.CardHistoryDetailsResponseData;
import com.mswipetech.wisepad.sdk.data.LoginResponseData;
import com.mswipetech.wisepad.sdk.data.MSDataStore;
import com.mswipetech.wisepad.sdk.listeners.MSWisepadControllerResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import static com.north.socket.client.EzeTap.closeEzetap;
import static com.north.socket.client.EzeTap.strTxnId;
import static com.north.socket.client.MswipeView.activityCheck;
import static com.north.socket.client.MswipeView.ezetapCheck;

public class awaitPayment extends Activity {

    String[] typeofCommand;
    String[] queuestate;
    String s;

    private Button button;
    static int ezetap = 0;
    private Button btnback;

    private EditText data1txt;
    private EditText data2txt;
    private Button btndata;
    private int chatticketnumber;

    private RelativeLayout relativeLayout;
    ConnectivityReceiver myReceiverpossn;

    static ArrayList<Object> listData = new ArrayList<>();

    ArrayList<String> listofButtons = new ArrayList<>();
    ArrayList<Integer> buttonID = new ArrayList<>();
    ArrayList<String> innertext = new ArrayList<>();
    ArrayList<Integer> numberOfElements = new ArrayList<>();

    static ArrayList<String> cardNumList = new ArrayList<>();
    static ArrayList<String> receiptDateList = new ArrayList<>();
    static ArrayList<String> amountList = new ArrayList<>();
    static ArrayList<String> authCodeList = new ArrayList<>();

    static ArrayList<JSONObject> cardJSON = new ArrayList<>();
    static ArrayList<JSONObject> txnJSON = new ArrayList<>();
    static ArrayList<JSONObject> customerJSON = new ArrayList<>();
    static ArrayList<JSONObject> receiptJSON = new ArrayList<>();
    static ArrayList<JSONObject> merchantJSON = new ArrayList<>();
    static ArrayList<JSONObject> referencesJSON = new ArrayList<>();

    static int historyClicked = 0;

    ImageView ledButton;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myReceiverpossn = new ConnectivityReceiver();
        IntentFilter filter = new IntentFilter("com.north.socket.client.POSSNBroadcastReceiver");
        registerReceiver(myReceiverpossn, filter);
        ezetap = MenuClass.ezetap;

        mContext = this;
        setContentView(R.layout.activity_await_payment);
        button = (Button) findViewById(R.id.button);
        TextView info = (TextView) findViewById(R.id.info);
        ledButton = (ImageView) findViewById(R.id.paymentLed);

        Bundle bundle = getIntent().getExtras();

        Intent sisintent = new Intent();
        sisintent.setAction("com.north.socket.client.LBBroadcastReceiverFile");
        sisintent.putExtra("shouldisend", "0");
        sendBroadcast(sisintent);

        listofButtons.clear();
        buttonID.clear();
        innertext.clear();
        numberOfElements.clear();
        for (int i = 0; i < TicketClass.list.size(); i++) {
            listofButtons.add(TicketClass.list.get(i).ticketText);
            buttonID.add(Integer.parseInt(TicketClass.list.get(i).ticketID));
            innertext.add(TicketClass.list.get(i).innerText);
            numberOfElements.add(Integer.parseInt(TicketClass.list.get(i).numberOfField));
        }

        relativeLayout = (RelativeLayout) findViewById(R.id.relLayout);
        int state = 0;
        if (bundle.getString("qdata") != null) {
            s = bundle.getString("qdata");
            typeofCommand = s.split("\\|");
            queuestate = s.split("\\$");
            state = Integer.parseInt(queuestate[1]);

            int totalamount = Integer.parseInt(queuestate[18].toString()) + Integer.parseInt(queuestate[19].toString());

            info.setText("\u20B9 " + Integer.toString(totalamount));
            if (totalamount > 3000) {
                button.setEnabled(false);
                button.setAlpha(0.2f);
            }
            if (!queuestate[40].equals("0")) {
                button.setEnabled(true);
                button.setAlpha(1f);
            }
        }

        if ((state == 902) || (state == 3400) || (state == 2500)) {
            LinearLayout layoutleft = (LinearLayout) findViewById(R.id.leftLinearlayout);
            LinearLayout layoutright = (LinearLayout) findViewById(R.id.rightLinearlayout);

            for (int i = 0; i < listofButtons.size(); i++) {
                final Button btn;
                btn = new Button(this);
                btn.setId(buttonID.get(i));
                btn.setOnClickListener(btnclick);
                //relativeLayout.addView(btn);
                btn.setText(listofButtons.get(i));
                btn.setTextSize(14);
                //setMargins(btn, 0, 450+x, 0, 0);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) btn.getLayoutParams();


                LinearLayout.LayoutParams paramsb = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                paramsb.bottomMargin = 10;
                btn.setLayoutParams(paramsb);

                if ((i % 2) == 0) {
                    layoutleft.addView(btn);
                } else {
                    layoutright.addView(btn);
                }
            }

            int enableid = Integer.parseInt(queuestate[38]);
            if (enableid != 0) {
                Button enablebutton = (Button) findViewById(enableid);

                if (enablebutton != null) {
                    for (int z = 0; z < listofButtons.size(); z++) {
                        Button disablebutton = (Button) findViewById(buttonID.get(z));
                        if (disablebutton != null) {
                            disablebutton.setEnabled(false);
                            disablebutton.setTextColor(ContextCompat.getColor(this,R.color.black));
                        }
                    }
                    enablebutton.setTextColor(ContextCompat.getColor(this,R.color.white));
                }

            }
        }
        button.setEnabled(true);
    }

    void createTicketNoOpt(int ticketnumber) {
        Intent intent = new Intent();
        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
        intent.putExtra("sendchat", "GENTICKETNEW|" + queuestate[0] + "|" + ticketnumber + "|0|0|");
        sendBroadcast(intent);
    }

    int backButton = 0;
    void createTicketOpt(int numdata, int typeofticket, String data1text, String data2text, int ticketnum, String buttontext) {
        btnback = new Button(awaitPayment.this);
        btnback.setId(100);
        btnback.setOnClickListener(btnclick);
        relativeLayout.addView(btnback);
        btnback.setWidth(relativeLayout.getWidth());
        btnback.setHeight(relativeLayout.getHeight());
        btnback.setBackgroundColor(Color.BLACK);
        data1txt = new EditText(awaitPayment.this);
        data1txt.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        data1txt.setText(data1text);
        data1txt.setSingleLine(false);
        data1txt.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        data1txt.setBackgroundResource(R.color.white);
        relativeLayout.addView(data1txt);
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) data1txt.getLayoutParams();
        lp.topMargin = 300;
        chatticketnumber = ticketnum;
        data1txt.setWidth(relativeLayout.getWidth());
        data1txt.setLayoutParams(lp);
        if (numdata == 2) {
            data2txt = new EditText(awaitPayment.this);
            //nametxt.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            data2txt.setText(data2text);
            data2txt.setSingleLine(true);
            //nametxt.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
            data2txt.setBackgroundResource(R.color.white);
            relativeLayout.addView(data2txt);
            ViewGroup.MarginLayoutParams lpconvreason = (ViewGroup.MarginLayoutParams) data2txt.getLayoutParams();
            lpconvreason.topMargin = 380;
            data2txt.setWidth(relativeLayout.getWidth());
            data2txt.setLayoutParams(lpconvreason);
        }

        btndata = new Button(awaitPayment.this);
        btndata.setId(typeofticket);
        btndata.setOnClickListener(btnclick);
        relativeLayout.addView(btndata);
        ViewGroup.MarginLayoutParams lpbut = (ViewGroup.MarginLayoutParams) btndata.getLayoutParams();
        lpbut.topMargin = 400;
        if (numdata == 2) {
            lpbut.topMargin = 460;
        }
        btndata.setBackgroundColor(Color.parseColor("#F28500"));
        btndata.setLayoutParams(lpbut);
        btndata.setWidth(relativeLayout.getWidth());
        btndata.setText(buttontext);


        btndata.requestFocus();
        InputMethodManager immaddress = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        immaddress.showSoftInput(data1txt, InputMethodManager.SHOW_IMPLICIT);

        backButton = 1;

    }



    View.OnClickListener btnclick = new View.OnClickListener() {

        @Override
        public void onClick(final View view) {


            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(awaitPayment.this);
            alertDialogBuilder.setMessage(R.string.are_you_sure);
            alertDialogBuilder.setPositiveButton(R.string.OK,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            String ab = queuestate[0];
                            int indexNo = buttonID.indexOf(view.getId());
                            if (view.getId() < 99 && indexNo >= 0) {

                                int numberOfData = numberOfElements.get(indexNo);
                                switch (numberOfData) {
                                    case 0:
                                        createTicketNoOpt(buttonID.get(indexNo));
                                        break;
                                    case 1:
                                        createTicketOpt(numberOfData, 101, "", "", buttonID.get(indexNo), innertext.get(indexNo));
                                        break;
                                    case 2:
                                        createTicketOpt(numberOfData, 101, "", "", buttonID.get(indexNo), innertext.get(indexNo));
                                        break;
                                }
                            } else {
                                switch (view.getId()) {
                                    case 101:
                                        Log.i("TAG", "The index is" + view.getId());
                                        View viewthirteen = awaitPayment.this.getCurrentFocus();
                                        if (viewthirteen != null) {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(viewthirteen.getWindowToken(), 0);
                                        }
                                        if (btnback != null) {
                                            btnback.setVisibility(View.GONE);
                                        }
                                        if (data1txt != null) {
                                            data1txt.setVisibility(View.GONE);
                                        }
                                        if (data2txt != null) {
                                            data1txt.setVisibility(View.GONE);
                                        }
                                        if (btndata != null) {
                                            btndata.setVisibility(View.GONE);
                                        }
                                        String data = "";
                                        if (data1txt != null) {
                                            //data = "TICKET‰" + typeofCommand[1] + "‰" + chatticketnumber + "‰" + data1txt.getText().toString() + "‰‰";
                                            data = "GENTICKETNEW|" + queuestate[0] + "|" + chatticketnumber + "|" + data1txt.getText().toString() + "|0|";
                                            if (data2txt != null) {
                                                //  data = "TICKET‰" + typeofCommand[1] + "‰" + chatticketnumber + "‰" + data1txt.getText().toString() + "‰" + data2txt.getText().toString() + "‰";
                                                data = "GENTICKETNEW|" + queuestate[0] + "|" + chatticketnumber + "|" + data1txt.getText().toString() + "|" + data2txt.getText().toString() + "|";
                                            }
                                        }
//                                        displayOverlay();
                                        Intent intent = new Intent();
                                        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                                        intent.putExtra("sendchat", data);
                                        sendBroadcast(intent);
                                        break;
                                    case 100:
                                        Log.i("TAG", "The index is" + view.getId());
                                        if (btnback != null) {
                                            btnback.setVisibility(View.GONE);
                                        }
                                        if (data1txt != null) {
                                            data1txt.setVisibility(View.GONE);
                                        }
                                        if (data2txt != null) {
                                            data2txt.setVisibility(View.GONE);
                                        }
                                        if (btndata != null) {
                                            btndata.setVisibility(View.GONE);
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    });

            alertDialogBuilder.setNegativeButton(R.string.cancel,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            return;
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();


        }
    };


    public void onBackPressed() {
        // do nothing.
        if (btnback != null) {
            btnback.setVisibility(View.GONE);
        }
        if (data1txt != null) {
            data1txt.setVisibility(View.GONE);
        }
        if (data2txt != null) {
            data2txt.setVisibility(View.GONE);
        }
        if (btndata != null) {
            btndata.setVisibility(View.GONE);
        }
    }

    protected void onDestroy() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }



    public void callSupportpay(View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to call Support?");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String data = "";
                        data = "CALLS,";
                        Intent intent = new Intent();
                        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                        intent.putExtra("sendchat", data);
                        sendBroadcast(intent);
                        //finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    static Context mContext;

    public void callMswipe(View v) {
        if (ezetap == 0) {
            buttonCheck = 0;
            EzeTap ezeTap = new EzeTap();
            ezeTap.dataValues(s);
            ezeTap.context = this;
            ezeTap.initEzetap();
        }
        if (ezetap == 1) {
            mContext = this;
            Intent activeview = new Intent(this, MainActivity.class);
            //activeview.setAction(Intent.ACTION_CALL_BUTTON);
            activeview.putExtra("data", s);
            startActivityForResult(activeview, 1);
        }
    }

    String transcationCheck = "0";

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (intent != null && intent.hasExtra("response")) {
            Bundle bundle = intent.getExtras();
            String[] typeofCommand;
            String s;
            s = bundle.getString("response");
            try {
                JSONObject response = new JSONObject(intent.getStringExtra("response"));
                String status;
                if (transcationCheck.equals("1")) {
                    transcationCheck = "2";
                }

                status = response.getString("status");
                if (status.equals("fail")) {
                    Intent intentb = new Intent();
                    String data = "";
                    data = strTxnId;
                    intentb.putExtra("senddata", data);
                    setResult(RESULT_OK, intentb);
                    closeEzetap();
                    finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("SampleAppLogs", intent.getStringExtra("response"));
            try {
                JSONObject jsonArray = new JSONObject(s);
                System.out.println("transcationCheck" + transcationCheck);
                String status = jsonArray.getString("status");

                if (status.equals("success")) {
                    if (buttonCheck == 0) {
                        EzeTap.doSaleTxn();
                        buttonCheck = 2;
                    } else if (buttonCheck == 1) {
                        doSearchTxn();
                        buttonCheck = 2;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        switch (requestCode) {
            case EzeTap.REQUESTCODE_INIT:
            case EzeTap.REQUESTCODE_PREP:
            case EzeTap.REQUESTCODE_CASH:
            case EzeTap.REQUESTCODE_CASHBACK:
            case EzeTap.REQUESTCODE_CASHATPOS:
            case EzeTap.REQUESTCODE_WALLET:
            case EzeTap.REQUESTCODE_SALE:
                JSONObject response = null;
                JSONObject queueid = null;
                try {
                    response = new JSONObject(intent.getStringExtra("response"));
                    queueid = new JSONObject(intent.getStringExtra("response"));
                    response = response.getJSONObject("result");
                    if (!response.isNull("txn")) {
                        response = response.getJSONObject("txn");
                        strTxnId = response.getString("txnId");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (strTxnId != null) {
                    try {
                        queueid = queueid.getJSONObject("result");
                        queueid = queueid.getJSONObject("references");
                        String qid = queueid.getString("reference1");
                        if (queuestate[0].equals(qid)) {
                            String data = "";
                            data = "NEXTMOVE|28.625374683906088|77.12294930512509|3|902|" + strTxnId + "|#####|";
                            Intent intenteze = new Intent();
                            intenteze.setAction("com.north.socket.client.LBBroadcastReceiver");
                            intenteze.putExtra("sendchat", data);
                            sendBroadcast(intenteze);
                            EzeTap.closeEzetap();
                            finish();
                        }
                    } catch (Exception ignored) {

                    }
                }
                break;
            case EzeTap.REQUESTCODE_UPDATE:
                if (resultCode == RESULT_OK) {
                    try {
                        response = new JSONObject(intent.getStringExtra("response"));
                        response = response.getJSONObject("result");
                        if (!response.isNull("txn")) {
                            response = response.getJSONObject("txn");
                            strTxnId = response.getString("txnId");
                            EzeTap.emiID = response.getString("emiId");
                        }
                        if (strTxnId != null) {
                            Intent intentb = new Intent();
                            String data = "";
                            data = strTxnId;
                            intentb.putExtra("senddata", data);
                            setResult(RESULT_OK, intentb);
                            finish();
                            closeEzetap();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Intent intentb = new Intent();
                    setResult(RESULT_OK, intentb);
                    finish();
                    closeEzetap();
                }
                break;

            case EzeTap.REQUESTCODE_SEARCH:
//                JSONObject response = null;
                try {
                    response = new JSONObject(intent.getStringExtra("response"));
                    response = response.getJSONObject("result");
                    String dataString = response.getString("data");
                    JSONArray dataArray = response.getJSONArray("data");

                    System.out.println(dataString);
                    if (dataString.equals("[]") || dataString.equals(null)) {
                        Toast.makeText(this, "No History", Toast.LENGTH_SHORT).show();
                    } else {
                        System.out.println("Data:-");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject transData = dataArray.getJSONObject(i);
                            JSONObject receipt = transData.getJSONObject("receipt");
                            JSONObject card = transData.getJSONObject("card");
                            JSONObject txn = transData.getJSONObject("txn");
                            JSONObject customer = transData.getJSONObject("customer");
                            JSONObject merchant = transData.getJSONObject("merchant");
                            JSONObject references = transData.getJSONObject("references");

                            cardNumList.add(card.getString("maskedCardNo"));
                            receiptDateList.add(receipt.getString("receiptDate"));
                            amountList.add(txn.getString("amount"));
                            authCodeList.add(txn.getString("authCode"));

                            cardJSON.add(card);
                            txnJSON.add(txn);
                            customerJSON.add(customer);
                            receiptJSON.add(receipt);
                            merchantJSON.add(merchant);
                            referencesJSON.add(references);
                        }

                        Intent activeview = new Intent(this, TransactionHistory.class);
                        startActivity(activeview);
//                        System.out.println("cardNumList:- " + cardNumList + " receiptDateList:- " + receiptDateList + " amountList:- " + amountList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            default:
                break;
        }
    }

    private void doSearchTxn() {
        /*********************************
         {
         "agentName": "Demo User"
         }
         *********************************/
        JSONObject jsonRequest = new JSONObject();
        try {

//            {
//                "dateFormat": "yyyy-MM-dd",
//                    "filterByUser": true,
//                    "username": "ezetap-test",
//                    "appKey":"2a59391d-dc5f-4a7e-8925-b8f1b5a26b9e"
//            }
            jsonRequest.put("dateFormat", "2017/09/07");
            jsonRequest.put("filterByUser", "true");
//            jsonRequest.put("agentName", "ezetap-test");
            jsonRequest.put("appKey", "2a59391d-dc5f-4a7e-8925-b8f1b5a26b9e");
            jsonRequest.put("agentName", "Sachin");
//            jsonRequest.put("agentName", "Demouser");
//            jsonRequest.put("agentName", queuestate[15]);
//            jsonRequest.put("prodAppKey", "2a59391d-dc5f-4a7e-8925-b8f1b5a26b9e");
//            jsonRequest.put("startDate", "02/09/2017");
//            jsonRequest.put("endDate", "07/09/2017");
//            jsonRequest.put("txnType", "card");
            EzeAPI.searchTransaction(this, EzeTap.REQUESTCODE_SEARCH, jsonRequest);
//            EzeAPI.getTransaction(this, EzeTap.REQUESTCODE_VOID, "170902065007531E020064793");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    int buttonCheck = 0;

    public void callCash(View v) {
        if (ezetap == 0) {
            buttonCheck = 1;
            EzeTap ezeTap = new EzeTap();
            ezeTap.dataValues(s);
            ezeTap.context = this;
            EzeTap.initEzetap();
        } else if (ezetap == 1) {
            historyClicked = 1;
            ezetapCheck = 1;
            historyRelated();
//            MswipeView.checkHistory();
        }
    }


    private MSWisepadController mSWisepadController = null;
    private MSGatewayConncetionObserver mMSGatewayConncetionObserver = null;
    MSWisepadControllerResponseListener historyListner;

    public void historyRelated() {
        mSWisepadController = MSWisepadController.getSharedMSWisepadController(mContext, AppSharedPrefrences.getAppSharedPrefrencesInstace().getGatewayEnvironment(), AppSharedPrefrences.getAppSharedPrefrencesInstace().getNetworkSource(), mMSGatewayConncetionObserver);
        mSWisepadController.authenticateMerchant(MenuClass.MSwipeUserName, MenuClass.MSWipePassword, msWisepadControllerResponseListener);

        historyListner = new MSWisepadControllerResponseListener() {
            @Override
            public void onReponseData(MSDataStore msDataStore) {
                CardHistoryDetailsResponseData cardHistoryDetailsResponseData = (CardHistoryDetailsResponseData) msDataStore;
                String historyData = cardHistoryDetailsResponseData.getCardHistoryResultData();
                try {
                    String listData1 = getHistoryListFromXml(historyData);
                    HistoryDetails cardSaleDetails = (HistoryDetails) listData.get(0);
                    listData = listData;
                    System.out.println("listData:" + cardSaleDetails);
                    Intent activeview = new Intent(mContext, TransactionHistory.class);
                    startActivity(activeview);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("History: ");

            }
        };
    }

    int ihistoryrecords = 0;

    public String getHistoryListFromXml(String xmlHistoryData) throws Exception {
        XmlPullParser parser = Xml.newPullParser();
        String errMsg = "";
        boolean isErrElementExists = false;
        HashMap<String, String> hashMember = new HashMap<String, String>();

        String strDataTags[] = new String[]{"TrxDate", "CardLastFourDigits",
                "TrxAmount", "TrxType", "TrxNotes", "MerchantInvoice",
                "StanNo", "VoucherNo", "AuthNo",
                "RRNo", "TrxStatus", "TerminalMessage"};


        HistoryDetails historyData = null;
        try {
            parser.setInput(new StringReader(xmlHistoryData));
            int eventType = XmlPullParser.START_TAG;
            boolean leave = false;
            boolean isDocElementExists = false;

            boolean isRowTagFound = false;

            int iDataTagIndex = 0;

            while (!leave && eventType != XmlPullParser.END_DOCUMENT) {
                eventType = parser.next();
                switch (eventType) {
                    case XmlPullParser.START_TAG: {
                        if ("ResultElement".equalsIgnoreCase(parser.getName().trim())) {
                            //String xmlText = parser.Text(); // there is no text for this
                            hashMember.put("ResultElement", "");// store the key
                            isDocElementExists = true;

                        } else if ("data".equalsIgnoreCase(parser.getName().trim())) {
                            isRowTagFound = true;
                            historyData = new HistoryDetails();

                        } else if (isDocElementExists && isRowTagFound && !isErrElementExists) {
                            String xmlTag = parser.getName().toString();
                            if (iDataTagIndex < strDataTags.length && strDataTags[iDataTagIndex].equals(xmlTag)) {

                                eventType = parser.next();
                                String xmlText = "";
                                if (eventType == XmlPullParser.TEXT) {
                                    xmlText = ((parser.getText() == null) ? "" : parser.getText());
                                } else if (eventType == XmlPullParser.END_TAG) {

                                }
                                if (iDataTagIndex == 0) historyData.TrxDate = xmlText;
                                else if (iDataTagIndex == 1)
                                    historyData.CardLastFourDigits = xmlText;
                                else if (iDataTagIndex == 2) historyData.TrxAmount = xmlText;
                                else if (iDataTagIndex == 3) historyData.TrxType = xmlText;
                                else if (iDataTagIndex == 4) historyData.TrxNotes = xmlText;
                                else if (iDataTagIndex == 5) historyData.MerchantInvoice = xmlText;
                                else if (iDataTagIndex == 6) historyData.StanNo = xmlText;
                                else if (iDataTagIndex == 7) historyData.VoucherNo = xmlText;
                                else if (iDataTagIndex == 8) historyData.AuthNo = xmlText;
                                else if (iDataTagIndex == 9) historyData.RRNo = xmlText;
                                else if (iDataTagIndex == 10) historyData.TrxStatus = xmlText;
                                else if (iDataTagIndex == 11) historyData.TerminalMessage = xmlText;
                                iDataTagIndex++;
                            }
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG: {
                        if (!isErrElementExists && isRowTagFound && "data".equalsIgnoreCase(parser.getName().trim())) {
                            isRowTagFound = false;
                            ihistoryrecords++;

                            try {
                                //saveValueToHistoryDB(Constants.compressRespData(stHistoryRowData,context), ihistoryrecords);
                                listData.add(historyData);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                errMsg = "Unable to get the History data, please contact support.";
                                leave = true;
                            }
                            //stHistoryRowData = "";
                            iDataTagIndex = 0;
                        }
                        break;

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (parser != null) {
                parser = null;
            }
        }
        return errMsg;

    }

    String Reference_Id;
    String Session_Tokeniser;
    MSWisepadControllerResponseListener msWisepadControllerResponseListener = new MSWisepadControllerResponseListener() {
        @Override
        public void onReponseData(MSDataStore msDataStore) {

            LoginResponseData loginResponseData = (LoginResponseData) msDataStore;
            String firstName = loginResponseData.getFirstName();
            Session_Tokeniser = loginResponseData.getSessionTokeniser();
            Reference_Id = loginResponseData.getReferenceId();
            if (ezetapCheck == 0 && activityCheck == 1) {
                MainActivity.mswipeInfo.setText("Login Done");
            } else if (ezetapCheck == 1) {
                historyRequest();
            }
        }
    };

    private void historyRequest() {
        mSWisepadController.getCardHistory(MenuClass.MSwipeUserName, Session_Tokeniser, historyListner);
//        1000090161
    }


    public class ConnectivityReceiver extends BroadcastReceiver {

        public ConnectivityReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {


            try {

                String data = intent.getStringExtra("senddata");
                if (data != null) {
                    switch (data) {
                        case "green":
                            ledButton.setImageResource(R.drawable.green);
                            break;
                        case "red":
                            ledButton.setImageResource(R.drawable.red);
                            break;
                    }
                }

                String latlong;
                latlong = intent.getStringExtra("latlong");
                if (latlong != null) {
                    button.setAlpha(1.0f);
                    button.setEnabled(true);
                }

                //History MSwipe
                String historyData = intent.getStringExtra("listData");
                if (historyData != null) {
                    Intent activeview = new Intent(mContext, TransactionHistory.class);
                    startActivity(activeview);
                }
            } catch (java.lang.NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
