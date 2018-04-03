package com.north.socket.client;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Xml;
import android.view.WindowManager;
import android.widget.ImageView;

import com.mswipetech.sdk.network.MSGatewayConnectionListener;
import com.mswipetech.wisepad.sdk.MSWisepadController;
import com.mswipetech.wisepad.sdk.data.CardData;
import com.mswipetech.wisepad.sdk.data.CardSaleResponseData;
import com.mswipetech.wisepad.sdk.data.LoginResponseData;
import com.mswipetech.wisepad.sdk.data.MSDataStore;
import com.mswipetech.wisepad.sdk.device.MSWisepadDeviceController;
import com.mswipetech.wisepad.sdk.device.MSWisepadDeviceControllerResponseListener;
import com.mswipetech.wisepad.sdk.device.WisePadConnection;
import com.mswipetech.wisepad.sdk.listeners.MSWisepadControllerResponseListener;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;

import static com.north.socket.client.EzeTap.typeofCommand;
import static com.north.socket.client.MswipeView.loginMSwipe;
import static com.north.socket.client.MswipeView.mMSWisepadDeviceController;
import static com.north.socket.client.MswipeView.totalAmount;

public class MswipeView extends Activity {

    static String Session_Tokeniser;
    static int activityCheck = 0;

    public boolean mCallCheckCardAfterConnection = true;
    static public MSWisepadDeviceController mMSWisepadDeviceController = null;
    public boolean mIsMSWisepasConnectionServiceBound;
    protected static boolean isConnetCalled = false;
    public boolean mAutoConnect = false;
    public boolean mAllowFirmwareUpdatge = false;
    MSWisepadDeviceObserver mMSWisepadDeviceObserver = null;

    static private MSWisepadController mSWisepadController = null;
    private MSGatewayConncetionObserver mMSGatewayConncetionObserver = null;

    Handler handler = new Handler();
    static CardSaleResponseData cardSaleResponseData;

    static String totalAmount = "0";
    static String tipAmount = "0.00";
    static String customerMobil = "";
    static String invoiceNo = "";
    static String customerEmail = "noreply@21north.in";
    static String notes = "";

    static MSWisepadControllerResponseListener historyListner;
    int ihistoryrecords = 0;
    ArrayList<Object> listData = new ArrayList<Object>();
    static int ezetapCheck = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("data")!= null) {
            String s = bundle.getString("data");
            String[] firstBrk = s.split("\\|");
            typeofCommand = firstBrk[0].split("\\$");
            totalAmount = String.valueOf(Integer.parseInt(typeofCommand[18]) + Integer.parseInt(typeofCommand[19]));
            totalAmount = totalAmount + ".00";
            customerMobil = "+91 " + typeofCommand[32];
            invoiceNo = typeofCommand[0];
            notes = "21North" + "," + typeofCommand[31] + "," + typeofCommand[32];

            cardSaleResponseData = new CardSaleResponseData();
        }

        ezetapCheck = awaitPayment.historyClicked;

        mAutoConnect = getIntent().getBooleanExtra("autoconnect", true);
        mCallCheckCardAfterConnection = getIntent().getBooleanExtra("checkcardAfterConnection", true);


        try{
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            try
            {
                doBindMswipeWisepadDeviceService();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

        }catch(Exception e){}

        mMSWisepadDeviceObserver = new MSWisepadDeviceObserver();
        mSWisepadController = MSWisepadController.getSharedMSWisepadController(getApplicationContext(), AppSharedPrefrences.getAppSharedPrefrencesInstace().getGatewayEnvironment(), AppSharedPrefrences.getAppSharedPrefrencesInstace().getNetworkSource(), mMSGatewayConncetionObserver);
//        loginMSwipe();
    }

//    public static void checkHistory(){
//        loginMSwipe();
//    }

    public static void reinit(){
        initiateWisepadConnection();
    }

    public static void initiateWisepadConnection()
    {
        if(!isConnetCalled)
        {
            if (mMSWisepadDeviceController != null)
            {
                mMSWisepadDeviceController.connect();
            }
            else{

            }
            isConnetCalled = true;
        }
    }

    @Override
    protected void onStart() {

//        if (IS_DEBUGGING_ON)
//            Logs.v(ApplicationData.packName,  " *********** onStart ", true, true);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                /**
                 * binding the service will start the service, and notifies back to the bound objects and this will
                 * unable the interactions with the service
                 *
                 */

                try
                {
                    doBindMswipeWisepadDeviceService();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }, 1000);

        super.onStart();
    }

    @Override
    protected void onStop() {
        //unbinding the service , when the app no longer requires the connection to the wisepad,
        //this will disconnect the connection to the wise pad
        try
        {
            doUnbindMswipeWisepadDeviceService();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        super.onStop();
    }

    static String Reference_Id;
    static MSWisepadControllerResponseListener msWisepadControllerResponseListener = new MSWisepadControllerResponseListener() {
        @Override
        public void onReponseData(MSDataStore msDataStore) {

            LoginResponseData loginResponseData = (LoginResponseData) msDataStore;
            String firstName = loginResponseData.getFirstName();
            Session_Tokeniser =  loginResponseData.getSessionTokeniser();
            Reference_Id =  loginResponseData.getReferenceId();
//            if(ezetapCheck == 0 && activityCheck == 1) {
                MainActivity.mswipeInfo.setText("Login Done");
//            }
//            else if(ezetapCheck == 1){
//                historyRequest();
//            }
        }
    };

//    private static void historyRequest() {
//        mSWisepadController.getCardHistory("1000090161", Session_Tokeniser, historyListner);
//    }

    static MSWisepadControllerResponseListener transactionResponseListener = new MSWisepadControllerResponseListener() {
        @Override
        public void onReponseData(MSDataStore msDataStore) {
            MSDataStore check = msDataStore;

            CardSaleResponseData uploadSignatureResponseData = (CardSaleResponseData) check;

            Boolean responseStatus = check.getResponseStatus();
            if(responseStatus.equals(false)){
                String errorCode = check.getErrorCode();
                int errorNo = check.getErrorNo();
                String responseErrorMsg = check.getResponseFailureReason();
//                if(ezetapCheck == 0 && activityCheck == 1) {
                    MainActivity.mswipeInfo.setText(responseErrorMsg + "\n Please Retry");

//                }
                System.out.println("responseErrorMsg: " + responseErrorMsg);
            }else{


                String carHolderName =  uploadSignatureResponseData.getCardHolderName();
                String stnTxn = uploadSignatureResponseData.getAuthCode();
                System.out.println("cardHolderName: " + carHolderName);

                String data = "";
                data = "NEXTMOVE|28.625374683906088|77.12294930512509|3|902|" + stnTxn + "|####|";
                Intent intenteze = new Intent();
                intenteze.setAction("com.north.socket.client.LBBroadcastReceiver");
                intenteze.putExtra("sendchat", data);
//                if(ezetapCheck == 0 && activityCheck == 1) {
                    MainActivity.mainContext.sendBroadcast(intenteze);
                    MainActivity.closeActivity();
//                }
            }



        }
    };

    static public void loginMSwipe(){

        mSWisepadController.authenticateMerchant(MenuClass.MSwipeUserName, MenuClass.MSWipePassword, msWisepadControllerResponseListener);

//        mSWisepadController.authenticateMerchant("9538734077", "001234", msWisepadControllerResponseListener);

    }
    static public void onlineTransaction(){
        mSWisepadController.processCardSaleOnline(Reference_Id, Session_Tokeniser,totalAmount, tipAmount, customerMobil, invoiceNo, customerEmail, notes, false, false, "", 0.00, 0, "","","","","","","","","","",transactionResponseListener);
    }
    void doBindMswipeWisepadDeviceService()
    {
        bindService(new Intent(this, MSWisepadDeviceController.class), mMSWisepadDeviceControllerService, Context.BIND_AUTO_CREATE);
    }

    public void doUnbindMswipeWisepadDeviceService()
    {

        if (mIsMSWisepasConnectionServiceBound)
        {
            unbindService(mMSWisepadDeviceControllerService);

            mIsMSWisepasConnectionServiceBound = false;
        }
    }


    private ServiceConnection mMSWisepadDeviceControllerService = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                MSWisepadDeviceController.LocalBinder localBinder = (MSWisepadDeviceController.LocalBinder) service;
                mMSWisepadDeviceController = localBinder.getService();
                mIsMSWisepasConnectionServiceBound = true;
//start the connection to the wise pad asynchronously, and call backs the listeners object //with the status of the connection

                if (isConnetCalled) {

                    mAutoConnect = getIntent().getBooleanExtra("autoconnect", true);

                    mCallCheckCardAfterConnection = getIntent().getBooleanExtra("checkcardAfterConnection", true);
                }
                mMSWisepadDeviceController.initMswipeWisepadDeviceController(mMSWisepadDeviceObserver, mAutoConnect, false, mCallCheckCardAfterConnection,
                        mAllowFirmwareUpdatge, MSWisepadDeviceController.WisepadCheckCardMode.SWIPE_OR_INSERT);
            } catch (Exception e) {

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // this is called when the connection with the service has been // unexpectedly disconnected - process crashed. mIsMSWisepasConnectionServiceBound = false;
            mMSWisepadDeviceController = null;

        }
    };

}

class MSWisepadDeviceObserver implements MSWisepadDeviceControllerResponseListener {

    private CardSaleData mCardSaleData = new CardSaleData();

    public void setTextInWindow(String data){
        data = data.replace("_", " ");
        MainActivity.mswipeInfo.setText(data);
    }


    @Override
    public void onReturnWisepadConnection(WisePadConnection wisePadConnection, BluetoothDevice bluetoothDevice) {
        System.out.println("Bluetooth");
        System.out.println(bluetoothDevice);
//        if(ezetapCheck == 0 && activityCheck == 1) {
//            MainActivity.mswipeInfo.setText("onReturnWisepadConnection: " + wisePadConnection.toString());
//        }

        setTextInWindow(wisePadConnection.toString());
        if(wisePadConnection.toString().equals("WisePadConnection_CONNECTED")){
            loginMSwipe();
        }
    }

    @Override
    public void onRequestWisePadCheckCardProcess(CheckCardProcess checkCardProcess, ArrayList<String> arrayList) {
//        if(ezetapCheck == 0 && activityCheck == 1) {
//            MainActivity.mswipeInfo.setText("onRequestWisePadCheckCardProcess: " + checkCardProcess.toString());
//        }
        setTextInWindow(checkCardProcess.toString());
        if(checkCardProcess.toString().equals("CheckCardProcess_SET_AMOUNT")) {
            mMSWisepadDeviceController.setAmount(totalAmount);
        }
    }

    @Override
    public void onReturnWisePadOfflineCardTransactionResults(CheckCardProcessResults checkCardProcessResults, Hashtable<String, Object> hashtable) {
        System.out.println("onReturnWisePadOfflineCardTransactionResults: " + checkCardProcessResults);
//        if(ezetapCheck == 0 && activityCheck == 1) {
//            MainActivity.mswipeInfo.setText("onReturnWisePadOfflineCardTransactionResults: " + checkCardProcessResults.toString());
//        }
        setTextInWindow(checkCardProcessResults.toString());
        if(checkCardProcessResults.toString().equals("ON_REQUEST_ONLINEPROCESS")) {

            CardData cardData1 = (CardData)hashtable.get("cardData");
            String lastFourDigit = cardData1.getLast4Digits();
            String validThru = cardData1.getExpiryDate().toString();

            String validDate = validThru.substring(0, 2) + "/" + validThru.substring(2, validThru.length());

            MainActivity.cardNoTextView.setText("• • • •    • • • •    • • • •    " + lastFourDigit.replace("", " ").trim());
            MainActivity.validThruTextView.setText(validDate);
            MswipeView.onlineTransaction();
        }
    }


    @Override
    public void onError(Error error, String s) {
//        if(ezetapCheck == 0 && activityCheck == 1) {
//            MainActivity.mswipeInfo.setText("onError: " + error.toString());
//        }
        setTextInWindow(error.toString());
        System.out.println("onError" + s);
    }

    @Override
    public void onRequestDisplayWispadStatusInfo(MSWisepadDeviceControllerResponseListener.DisplayText displayText) {
        System.out.println("onRequestDisplayWispadStatusInfo" + displayText);
//        if(ezetapCheck == 0 && activityCheck == 1) {
//            MainActivity.mswipeInfo.setText(displayText.toString());
//        }
        setTextInWindow(displayText.toString());
    }

    @Override
    public void onReturnDeviceInfo(Hashtable<String, String> hashtable) {
        System.out.println("onReturnDeviceInfo: " + hashtable);
//        if(ezetapCheck == 0 && activityCheck == 1) {
            MainActivity.mswipeInfo.setText(hashtable.toString());
//        }
    }

    @Override
    public void onReturnWispadNetwrokSettingInfo(WispadNetwrokSetting wispadNetwrokSetting, boolean b, Hashtable<String, Object> hashtable) {
        System.out.println(hashtable);
//        if(ezetapCheck == 0 && activityCheck == 1) {
//            MainActivity.mswipeInfo.setText("onReturnWispadNetwrokSettingInfo: " + wispadNetwrokSetting.toString());
//        }
        setTextInWindow(wispadNetwrokSetting.toString());
    }

    @Override
    public void onReturnNfcDetectCardResult(NfcDetectCardResult nfcDetectCardResult, Hashtable<String, Object> hashtable) {

    }

    @Override
    public void onReturnNfcDataExchangeResult(boolean b, Hashtable<String, String> hashtable) {

    }
}

class MSGatewayConncetionObserver implements MSGatewayConnectionListener {

    public ImageView imgHostConnectionStatus;

    @Override
    public void Connected(String msg) {

        imgHostConnectionStatus.setAnimation(null);
//        imgHostConnectionStatus.setImageResource(R.drawable.topbar_img_host_active);
    }

    @Override
    public void Connecting(String msg) {


			/*if(ApplicationData.IS_PERFORMENCE_TEST_ON){
				Logs.p(ApplicationData.packName, "Wedrocket connection started", true, true);
			}*/


//        imgHostConnectionStatus.startAnimation(alphaAnim);
//        imgHostConnectionStatus.setImageResource(R.drawable.topbar_img_host_inactive);
    }

    @Override
    public void disConnect(String msg) {


			/*if(ApplicationData.IS_PERFORMENCE_TEST_ON){
				Logs.p(ApplicationData.packName, "Wedrocket disconnected", true, true);
			}*/

//        if (ApplicationData.IS_DEBUGGING_ON)
//            Logs.v(ApplicationData.packName, " msg " + msg, true, true);

//        imgHostConnectionStatus.setAnimation(null);
//        imgHostConnectionStatus.setImageResource(R.drawable.topbar_img_host_inactive);
    }
//    private CardSaleData mCardSaleData = new CardSaleData();
//    ArrayList<BluetoothDevice> pairedDevicesFound = new ArrayList<BluetoothDevice>();
//    String[] typeofCommand;
//    String totalAmount = "0";
//    String s;
//    private Button refreshbut;
//    private WisePadSwiperListener listener;
//    private WisePadGatewayConncetionListener messagelistener;
//    String[][] strTags;
//    int emv;
//    private TextView mswipeinfo;
//    private MswipeWisepadController wisePadController;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_mswipe_view);
//        //getActionBar().setDisplayHomeAsUpEnabled(true);
//        //getActionBar().hide();
//        mswipeinfo = (TextView) findViewById(R.id.mswipeinfo);
//        Bundle bundle = getIntent().getExtras();
//        if(bundle.getString("data")!= null) {
//            s = bundle.getString("data");
//            String[] firstBrk = s.split("\\|");
//            typeofCommand = firstBrk[0].split("\\$");
//            totalAmount = String.valueOf(Integer.parseInt(typeofCommand[18]) + Integer.parseInt(typeofCommand[19]));
//        }
//
//       connectMSwipe();
//      //  bindService(new Intent(this, MswipeWiseposDeviceService.class), mMswipeWiseposDeviceServiceConnection, Context.BIND_AUTO_CREATE);
//
//        // Register for broadcasts on BluetoothAdapter state change
//        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//        registerReceiver(mReceiver, filter);
//    }
//    public void connectMSwipe(){
//        listener = new WisePadSwiperListener();
//
//        messagelistener = new WisePadGatewayConncetionListener();
//        MswipeWisepadController.GATEWAYENV gateWayDefault = MswipeWisepadController.GATEWAYENV.LABS;
//        wisePadController = new MswipeWisepadController(this, MswipeWisepadController.GATEWAYENV.values()[1], messagelistener);
//        wisePadController.AuthenticateMerchant(loginHandler, "1000072095", "123456");
//
////        1000072095, 123456
//
//        mswipeinfo.setText("Connecting to MSwipe Server");
//    }
//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
//
//            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
//                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
//                        BluetoothAdapter.ERROR);
//                switch (state) {
//                    case BluetoothAdapter.STATE_OFF:
//                        mswipeinfo.setText("Bluetooth off");
//                        break;
//                    case BluetoothAdapter.STATE_TURNING_OFF:
//                        mswipeinfo.setText("Turning Bluetooth off...");
//                        break;
//                    case BluetoothAdapter.STATE_ON:
//                        mswipeinfo.setText("Bluetooth on");
//                        connectMSwipe();
//                        break;
//                    case BluetoothAdapter.STATE_TURNING_ON:
//                        mswipeinfo.setText("Turning Bluetooth on...");
//                        break;
//                }
//            }
//        }
//    };
//    public void onStop() {
//
//
//        wisePadController.stopMswipeGatewayConnection();
//        super.onStop();
//
//    }
//
//    public void callRefreshmswp(View v)
//    {
//        System.exit(2);
//    }
//
//    @Override
//    public void onStart() {
//
//
//        super.onStart();
//
//    }
//
//    public void onBackPressed() {
//        // do nothing.
//        finish();
//    }
//
//    public boolean checkConnections() {
//        pairedDevicesFound.clear();
//        if (BluetoothAdapter.getDefaultAdapter() == null)
//            return false;
//
//        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
//
//
//
////            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
////            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//            return false;
//
//        } else {
//
//
//            //String stBluetoothAddress = preferences.getString("bluetoothaddress", ""); //34:C8:03:6D:2B:91
//            String stBluetoothAddress = "";
//            Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
//            if (pairedDevices.size() > 0) {
//                for (BluetoothDevice device : pairedDevices) {
//                    if (device.getName().toLowerCase().startsWith("wisepad") ||
//                            device.getName().toLowerCase().startsWith("wp") ||
//                            device.getName().toLowerCase().startsWith("1084")) {
//                        if (stBluetoothAddress.equals(device.getAddress()) || stBluetoothAddress.contains(device.getAddress())) {
//                            pairedDevicesFound.clear();
//                            pairedDevicesFound.add(device);
//                            break;
//                        } else {
//                            pairedDevicesFound.add(device);
//                        }
//                    }
//                }
//            }
//
//
//
//        }
//        if (pairedDevicesFound.size() == 1) {
//
//            //wisePadDeviceConnecting = true;
//            //bluetoothConnectionState = DEVICE_PAIREDDEVICE_MATCHING;
//            wisePadController.startBTv2(pairedDevicesFound.get(0));
//            return true;
//        }
//        return true;
//    }
//
//    @Override
//    protected void onDestroy()
//    {
//        unregisterReceiver(mReceiver);
//        try
//        {
//            Intent intent = new Intent();
//            intent.setAction("com.north.socket.client.BroadcastReceiver");
//            //intent.putExtra("senddata", "LGOUT");
//            //sendBroadcast(intent);
//            System.out.println("onDestroy.");
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        super.onDestroy();
//    }
//
//
    public static void parseXml(String xmlString,String[][] strTags ) throws Exception {

        XmlPullParser parser = Xml.newPullParser();

        try {
            parser.setInput(new StringReader(xmlString));
            int eventType = XmlPullParser.START_TAG;
            boolean leave = false;

            while (!leave && eventType != XmlPullParser.END_DOCUMENT) {
                eventType = parser.next();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String xmlTag = parser.getName().toString();
                        for (int iTagIndex = 0; iTagIndex < strTags.length; iTagIndex++) {
                            if (strTags[iTagIndex][0].equals(xmlTag)) {
                                eventType = parser.next();
                                if (eventType == XmlPullParser.TEXT) {
                                    String xmlText = parser.getText();


                                    strTags[iTagIndex][1] = ( xmlText == null ? "" : xmlText);// store the key
                                } else if (eventType == XmlPullParser.END_TAG) {
                                    strTags[iTagIndex][1]  =  "";
                                }
                                break;
                            }
                        }
                        break;
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
            throw e;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (parser != null) {
                parser = null;
            }
        }
//
    }
//    public Handler loginHandler = new Handler()
//    {
//        public void handleMessage(android.os.Message msg) {
//
//
//            Bundle bundle = msg.getData();
//            String responseMsg = bundle.getString("response");
//
//            strTags = new String[][]{{"status", ""}, {"ErrMsg", ""}, {"IS_Password_Changed", ""}, {"First_Name", ""},
//                    {"Reference_Id", ""}, {"Session_Tokeniser", ""}, {"Currency_Code", ""}, {"IS_TIP_REQUIRED", ""}, {"CONVENIENCE_PERCENTAGE", ""}, {"SERVICE_TAX", ""}};
//
//            try {
//                parseXml(responseMsg, strTags);
//            }
//            catch (Exception e) {
//            e.printStackTrace();
//
//            //throw e;
//        }
//
//
//            if (strTags[0][1].equalsIgnoreCase("true")) {
//                mswipeinfo.setText("Successfully connected to MSwipe Server");
//                wisePadController.initMswipeWisePadDevice(listener);
//                wisePadController.startMswipeGatewayConnection();
//                if (!wisePadController.isDevicePresent()) {
//                    // TODO Auto-generated method stub
//                    mswipeinfo.setText("Connecting to MSwipe Device.");
//                    ConnectDeviceTask connecttask = new ConnectDeviceTask();
//                    connecttask.execute();
//                }
//            }
//
//            if (strTags[0][1].equalsIgnoreCase("False")) {
//                mswipeinfo.setText(strTags[1][1]);
//            }
//
//        }
//    };
//
//    public void processCardSale_MCR() {
//        //if its card sale and not preauth or sale with cash
//        wisePadController.CreditSale_MCR(CreditSaleHandler,
//                strTags[4][1],
//                strTags[5][1],
//                    "rcpt",
//                    mCardSaleData.mAmexSecurityCode,
//                totalAmount,
//                totalAmount,
//                "+91"+typeofCommand[20],
//                    "noreply@21north.in",
//                    "",
//                    "0",false,
//                    "0",
//                "0",
//                "0",
//                "0",
//                "0");
//
//
//
//    }
//
//    public void processCardSale_EMV() {
//        wisePadController.CreditSale_EMV(CreditSaleHandler,
//                    strTags[4][1],
//                    strTags[5][1],
//                    "rcpt",
//                totalAmount,
//                totalAmount,
//                    "+91"+typeofCommand[32],
//                "noreply@21north.in",
//                    "",
//                mCardSaleData.mTVR, mCardSaleData.mTSI,"0",false,
//                    "0",
//                    "0",
//                    "0",
//                    "0",
//                    "0");
//
//
//    }
//
//    public Handler CreditSaleHandler = new Handler() {
//        public void handleMessage(android.os.Message msg)
//        {
//            Bundle bundle = msg.getData();
//            String responseMsg = bundle.getString("response");
//            String errMsg = "";
//            mswipeinfo.setText("Reading Response from MSwipe Server");
//            try
//            {
//
//                String[][] strTags = new String[][]{{"status", ""}, {"ErrMsg", ""},
//                        {"StandId", ""}, {"RRNO", ""},
//                        {"AuthCode", ""}, {"Date", ""},
//                        {"F055tag", ""}, {"EmvCardExpdate", ""},
//                        {"SwitchCardType", ""}, {"IssuerAuthCode", ""},
//                        {"MID", ""}, {"TID", ""},
//                        {"BatchNo", ""},
//                        {"VoucherNo", ""}};
//
//                parseXml(responseMsg, strTags);
//
//                if (strTags[0][1].equals("True"))
//                {
//                    mswipeinfo.setText("Transaction Successful");
//                    Intent intent = new Intent();
//                    String data = "";
//                    data = strTags[0][1]+"-"+strTags[4][1];
//                    data = "NEXTMOVE|28.625374683906088|77.12294930512509|3|902|" + strTags[0][1]+"-"+strTags[4][1] + "|";
//                    Intent intenteze = new Intent();
//                    intenteze.setAction("com.north.socket.client.LBBroadcastReceiver");
//                    intenteze.putExtra("sendchat", data);
//                    sendBroadcast(intenteze);
//                    finish();
//                }
//                if (strTags[0][1].equals("False")) {
//                    mswipeinfo.setText("Transaction Unsuccessful "+strTags[0][2]);
//                }
//                try {
//                    wisePadController.stopBTv2();
//                } catch (Exception ex) {}
//
//
//            }
//            catch (Exception ex) {
//                errMsg = "Invalid response from Mswipe server, please contact support.";
//                mswipeinfo.setText("Invalid response from Mswipe server, please contact support.");
//            }
//            }
//
//
//
//    };
//
//    class WisePosGatewayConncetionListener implements  MswipeWisePadGatewayConnectionListener{
//
//        @Override
//        public void Connected(String msg) {
//            System.out.println("Connected");
////            ((TextView)findViewById(R.id.topbar_LBL_status)).setText(msg);
//
//        }
//
//        @Override
//        public void Connecting(String msg) {
//            System.out.println("Connecting");
//            //((TextView)findViewById(R.id.topbar_LBL_status)).setText(msg);
//
//        }
//
//        @Override
//        public void disConnect(String msg) {
//            System.out.println("disConnect");
//            //((TextView)findViewById(R.id.topbar_LBL_status)).setText(msg);
//
//        }
//
//
//    }
//
//    class WisePadSwiperListener implements MswipeWisePadDeviceListener {
//
//        @Override
//        public void onRequestInsertCard() {
//            int check = 1;
//            System.out.println("onRequestInsertCard");
//
//
//        }
//
//        @Override
//        public void onWaitingForCard() {
//            System.out.println("onRequestInsertCard");
//            mswipeinfo.setText("Please Insert/Swipe Card");
//
//        }
//
//
//        @Override
//        public void onReturnCheckCardResult(CheckCardResult checkCardResult, Hashtable<String, String> decodeData) {
//            //dismissDialog();
//
//            //reset all the data, since this value would be used for both
//            //ic an mag card
//            mCardSaleData = new CardSaleData();
//            if (checkCardResult == CheckCardResult.NONE) {
//                //statusEditText.setText(CreditSaleView.this.getString(R.string.no_card_detected));
//
//            } else if (checkCardResult == CheckCardResult.ICC) {
//                //statusEditText.setText(CreditSaleView.this.getString(R.string.icc_card_inserted));
//                emv = 1;
//                wisePadController.startEmv();
//
//            } else if (checkCardResult == CheckCardResult.NOT_ICC) {
//
//            } else if (checkCardResult == CheckCardResult.BAD_SWIPE) {
//                //statusEditText.setText(CreditSaleView.this.getString(R.string.bad_swipe));
//                mswipeinfo.setText(CheckCardResult.BAD_SWIPE.toString());
//
//            } else if (checkCardResult == CheckCardResult.MCR) {
//                processMagCardData(checkCardResult, decodeData);
//            } else if (checkCardResult == CheckCardResult.SERVICECODE_FAIL_USE_CHIPCARD) {
//                mswipeinfo.setText(CheckCardResult.SERVICECODE_FAIL_USE_CHIPCARD.toString());
//
//            } else if (checkCardResult == CheckCardResult.MCR_AMEXCARD) {
//
//
//
//            } else if (checkCardResult == CheckCardResult.NO_RESPONSE) {
//                //statusEditText.setText(CreditSaleView.this.getString(R.string.card_no_response));
//
//
//            } else if (checkCardResult == CheckCardResult.TRACK2_ONLY) {
//
//                processMagCardData(checkCardResult, decodeData);
//
//            }
//        }
//
//        public void processMagCardData(CheckCardResult checkCardResult, Hashtable<String, String> decodeData) {
//
//            mCardSaleData.mCreditCardNo = decodeData.get("maskedPAN") == null ? "" : decodeData.get("maskedPAN");
//            mCardSaleData.mCardHolderName = decodeData.get("cardholderName") == null ? "" : decodeData.get("cardholderName");
//            mCardSaleData.mExpiryDate = decodeData.get("expiryDate") == null ? "" : decodeData.get("expiryDate");
//            mswipeinfo.setText("Processing Card");
//            processCardSale_MCR();
//            //showCreditDetailsScreen();
//
//        }
//
//
//        @Override
//        public void onReturnStartEmvResult(StartEmvResult startEmvResult, String ksn) {
//            if (startEmvResult == StartEmvResult.SUCCESS) {
//                //statusEditText.setText(CreditSaleView.this.getString(R.string.start_emv_success));
//            } else {
//                //statusEditText.setText(CreditSaleView.this.getString(R.string.start_emv_fail));
//
//                //mBtnSwipe.setVisibility(View.VISIBLE);
//                //mBtnSwipeOk.setVisibility(View.INVISIBLE);
//
//
//            }
//        }
//
//
//        @Override
//        public void onRequestSelectApplication(ArrayList<String> appList) {
//            mswipeinfo.setText("onRequestSelectApplication");
//        }
//
//
//        @Override
//        public void onRequestSetAmount() {
//            wisePadController.setAmount(totalAmount,"586" );
//        }
//
//        @Override
//        public void onReturnAmountConfirmResult(boolean isSuccess) {
//            // TODO Auto-generated method stub
//
//
//        }
//
//        @Override
//        public void onRequestPinEntry(PinEntry pinentry) {
//            mswipeinfo.setText(pinentry.toString());
//        }
//
//        @Override
//        public void onReturnPinEntryResult(PinEntryResult pinEntryResult, String epb, String ksn) {
//
//        }
//
//
//        @Override
//        public void onRequestTerminalTime() {
//
//        }
//
//        @Override
//        public void onRequestCheckServerConnectivity() {
//
//        }
//
//        @Override
//        public void onRequestFinalConfirm() {
//
//
//        }
//
//        @Override
//        public void onRequestOnlineProcess(HashMap<String, String> tlv) {
//
//            mCardSaleData.mCardHolderName = tlv.get("CardHolderName");
//            mCardSaleData.mAppIdentifier = tlv.get("AppIdentifier");;
//            mCardSaleData.mCertif = tlv.get("Certif");;
//            mCardSaleData.mApplicationName = tlv.get("ApplicationName");
//
//            mCardSaleData.mTVR = tlv.get("TVR");
//            mCardSaleData.mTSI = tlv.get("TSI");
//            mCardSaleData.mExpiryDate = tlv.get("ExpiryDate");
//
//            mCardSaleData.mCreditCardNo = tlv.get("CreditCardNo");
//            String tempCreditNo = "";
//            int ilen = mCardSaleData.mCreditCardNo.length();
//            mswipeinfo.setText("Processing Card");
//            processCardSale_EMV();
//        }
//
//        @Override
//        public void onRequestReferProcess(String pan) {
//
//        }
//
//        @Override
//        public void onRequestAdviceProcess(String tlv) {
//
//        }
//
//
//        @Override
//        public void onReturnReversalData(String tlv) {
//
//        }
//
//
//        @Override
//        public void onReturnBatchData(HashMap<String, String> tlv) {
//
//
//
//        }
//        @Override
//        public void onReturnTransactionResult(TransactionResult transactionResult, String offlineDeclineTag) {
//
//        }
//
//
//
//        @Override
//        public void onReturnTransactionLog(String tlv) {
//
//
//        }
//
//
//
//        @Override
//        public void onReturnDeviceInfo(Hashtable<String, String> arg0) {
//
//        }
//
//
//        @Override
//        public void onRequestDisplayText(DisplayText displayText) {
//
//        }
//
//        @Override
//        public void onRequestClearDisplay() {
//
//        }
//
//        @Override
//        public void onBatteryLow(BatteryStatus batteryStatus) {
//
//        }
//
//
//        @Override
//        public void onBTv2Detected() {
//
//        }
//
//
//        @Override
//        public void onBTv2Connected(BluetoothDevice bluetoothDevice) {
//            try {
//                bluetoothDevice.getAddress();
//            } catch (Exception ex) {
//            }
//            mswipeinfo.setText("Sucessfully connected to MSwipe Device.");
//            wisePadController.checkCard();
//
//            // we delay the connected process to wait for about a sec so that internally the sdk prepares it self for the communications,
//            // and then show up the start command and then
//            //wisePadDeviceConnecting = false;
//
//           // DeviceConnectedWaitTask deviceConnectedWaitTask = new DeviceConnectedWaitTask(); //every time create new object, as AsynTask will only be executed one time.
//           // deviceConnectedWaitTask.execute();
//        }
//
//        //when trying to start and if the device is not found this will get called
//        @Override
//        public void onBTv2DeviceNotFound() //same as onNodeviceDected
//        {
//
//        }
//
//
//        //when the devcie after the connection get diconnected
//        @Override
//        public void onBTv2Disconnected() //this is equivalent to onDeviceUnPlugged this is bluetooth diconnected.
//        {
//
//
//        }
//
//        @Override
//        public void onError(Error errorState) {
//
//        }
//
//        @Override
//        public void onAudioDeviceNotFound() {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onBTv4Connected() {
//            // TODO Auto-generated method stub
//            int ab =1;
//        }
//
//        @Override
//        public void onBTv4DeviceListRefresh(List<BluetoothDevice> arg0) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onBTv4Disconnected() {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onBTv4ScanStopped() {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onBTv4ScanTimeout() {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onDevicePlugged() {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onDeviceUnplugged() {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onPrinterOperationEnd() {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onRequestPrinterData(int arg0, boolean arg1) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onRequestVerifyID(String arg0) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onReturnAmount(String arg0, String arg1) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onReturnCancelCheckCardResult(boolean isSuccess) {
//
//
//        }
//
//        @Override
//        public void onReturnDisableInputAmountResult(boolean arg0) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onReturnEmvCardDataResult(boolean arg0, String arg1) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onReturnEnableInputAmountResult(boolean arg0) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onReturnEncryptDataResult(String arg0, String arg1) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onReturnInjectSessionKeyResult(boolean arg0) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onReturnPhoneNumber(PhoneEntryResult arg0, String arg1) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onReturnPrinterResult(PrinterResult arg0) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onReturnReadTerminalSettingResult(
//                TerminalSettingStatus arg0, String arg1) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onReturnUpdateTerminalSettingResult(
//                TerminalSettingStatus arg0) {
//            // TODO Auto-generated method stub
//
//        }
//
//        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//            // Check which request we're responding to
////            if (requestCode == MS_CARDSALE_ACTIVITY_REQUEST_CODE) {
//                // Make sure the request was successful
//                if (resultCode == RESULT_OK) {
//                    System.out.println(requestCode);
//
//                    // The user picked a contact.
//                    // The Intent's data Uri identifies which contact was selected.
//
//                    // Do something with the contact here (bigger example below)
//                }
////            }
//        }
//
//
//    }
//
//    private ServiceConnection mMswipeWiseposDeviceServiceConnection = new ServiceConnection() {
//
//        public void onServiceConnected(ComponentName className, IBinder service) {
//
//        }
//
//        public void onServiceDisconnected(ComponentName className)
//        {
//            // This is called when the connection with the service has been
//            // unexpectedly disconnected - process crashed.
//            //isMswipeWiseposConnectionServiceBound = false;
//
//        }
//    };
//
//    private class ConnectDeviceTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... urls) {
//            return "";
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//
//            if (checkConnections()) {
//
//            } else {
//                mswipeinfo.setText("Please Turn On the Bluetooth");
//
//            }
//
//        }
//    }
//
//    class WisePadGatewayConncetionListener implements  MswipeWisePadGatewayConnectionListener{
//
//        @Override
//        public void Connected(String msg) {
//
//        }
//
//        @Override
//        public void Connecting(String msg) {
//
//        }
//
//        @Override
//        public void disConnect(String msg) {
//
//        }
//    }
}
