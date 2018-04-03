package com.north.socket.client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.eze.api.EzeAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class EzeTap extends Activity implements
		OnClickListener {
	protected static final int REQUESTCODE_INIT = 10001;
	protected static final int REQUESTCODE_PREP = 10002;

	protected static final int REQUESTCODE_WALLET = 10003;
	private final int REQUESTCODE_CHEQUE = 10004;
	protected static final int REQUESTCODE_SALE = 10006;
	protected static final int REQUESTCODE_CASHBACK = 10007;
	protected static final int REQUESTCODE_CASHATPOS = 10008;
	protected static final int REQUESTCODE_CASH = 10009;

	protected static final int REQUESTCODE_SEARCH = 10010;
	protected static final int REQUESTCODE_VOID = 10011;
	private final int REQUESTCODE_ATTACHSIGN = 10012;
	protected static final int REQUESTCODE_UPDATE = 10013;
	private static final int REQUESTCODE_CLOSE = 10014;
	protected static final int REQUESTCODE_GETTXNDETAIL = 10015;
	private final int REQUESTCODE_GETINCOMPLETETXN = 10016;

	public static Context context;

	private TextView txtRefNum, txtAmount, txtAmountCashback, txtName,
			txtMobile, txtEmail, txtChqNum, txtBankCode, txtBankName,
			txtBankACNum, txtChqDate;
	private Button btnInit, btnPrep, btnWalletTxn, btnCardSale,
			btnCardCashback, btnCardCashAtPOS, btnChqTxn, btnCash,
			btnSearchTxn, btnVoidTxn, btnAttachSign, btnUpdate, btnClose,btnCheckIncompleteTxn,btnGetTxnDetail;
	private ImageView img;
	public static String strTxnId = null, emiID = null;
	private static String txtvarName, txtvarEmail, txtvarMobile, txtvarRefNum, txtvarAmount, ambVarMobile;
	private static String mandatoryErrMsg = "Please fill up mandatory params.";
	int sale = 0;
	static int totalamount;
	static String[] typeofCommand;
	static String[] queuestate;
	String s;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nativesample);

		btnInit = ((Button) findViewById(R.id.initdevice));
		btnInit.setOnClickListener(this);

		btnPrep = ((Button) findViewById(R.id.prepdevice));
		btnPrep.setOnClickListener(this);

		btnCardSale = ((Button) findViewById(R.id.makepayment));
		btnCardSale.setOnClickListener(this);

//		btnGetTxnDetail = (Button) findViewById(R.id.gettran);
//		btnGetTxnDetail.setOnClickListener(this);
//
//		btnClose = ((Button) findViewById(R.id.closeeze));
//		btnClose.setOnClickListener(this);

		this.context = this;

		Bundle bundle = getIntent().getExtras();
		if(bundle.getString("data")!= null) {
			s = bundle.getString("data");
			typeofCommand = s.split("\\|");
			queuestate = s.split("\\$");

			totalamount = Integer.parseInt(queuestate[18].toString())+Integer.parseInt(queuestate[19].toString());
			txtvarName = queuestate[31];
			txtvarMobile = queuestate[32];
			txtvarEmail = "noreply@21north.in";
			txtvarRefNum = queuestate[0];
			txtvarAmount = Integer.toString(totalamount);
			ambVarMobile = queuestate[15];
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.initdevice:
				initEzetap();
				break;
			case R.id.prepdevice:
				prepareEzetap();
				break;
			case R.id.makepayment:
				doSaleTxn();
				break;
//			case R.id.gettran:
//				getTxnDetails();
//				break;
////			case R.id.closeeze:
//				closeEzetap();
//				break;
			default:
				break;
		}
	}

	public static void dataValues(String s){
		typeofCommand = s.split("\\|");
		queuestate = s.split("\\$");

		totalamount = Integer.parseInt(queuestate[18].toString())+Integer.parseInt(queuestate[19].toString());
		txtvarName = queuestate[31];
		txtvarMobile = queuestate[32];
		txtvarEmail = "noreply@21north.in";
		txtvarRefNum = queuestate[0];
		txtvarAmount = Integer.toString(totalamount);
		ambVarMobile = queuestate[15];

	}
	private void getTxnDetails() {
		EzeAPI.getTransaction(this, REQUESTCODE_GETTXNDETAIL, txtvarRefNum.trim());
	}

	private void checkIncompleteTxn() {
		EzeAPI.checkForIncompleteTransaction(this, REQUESTCODE_GETINCOMPLETETXN);
	}

	public static void initEzetap() {
		JSONObject jsonRequest = new JSONObject();
		try {
			jsonRequest.put("demoAppKey", "cd718977-3ded-4263-af97-d2867723213a");
			//jsonRequest.put("appMode", "DEMO");
			jsonRequest.put("prodAppKey", "2a59391d-dc5f-4a7e-8925-b8f1b5a26b9e");
			jsonRequest.put("appMode", "PROD");
			jsonRequest.put("merchantName", "Demo");
//			jsonRequest.put("userName", "Demouser");
			jsonRequest.put("userName", "Sachin");
//			jsonRequest.put("userName", queuestate[15]);
			jsonRequest.put("currencyCode", "INR");
			jsonRequest.put("captureSignature", "false");
			jsonRequest.put("prepareDevice", "false");// Set it true if you want
														// to initialize and
														// prepare device at a
														// time
														// or false if you want
														// to initialize only
														// and prepare device
														// later for card txn.
		} catch (JSONException e) {
			e.printStackTrace();
		}
		EzeAPI.initialize(context, REQUESTCODE_INIT, jsonRequest);
	}

	private void prepareEzetap() {
		EzeAPI.prepareDevice(this, REQUESTCODE_PREP);
	}


	protected static void doSaleTxn() {
		if (isMandatoryParamsValid()) {
			JSONObject jsonRequest = new JSONObject();
			JSONObject jsonOptionalParams = new JSONObject();
			JSONObject jsonReferences = new JSONObject();
			JSONObject jsonCustomer = new JSONObject();
			try {
				// Building Customer Object
				jsonCustomer.put("name", txtvarName.trim());
				jsonCustomer.put("mobileNo", txtvarMobile.trim()
				);
				jsonCustomer.put("email", txtvarEmail.trim());

				// Building References Object
				jsonReferences.put("reference1", txtvarRefNum.trim());
				
				//Passing Additional References
				JSONArray array = new JSONArray();
				array.put("addRef_xx1");
				array.put("addRef_xx2");
				jsonReferences.put("additionalReferences",array);

				// Building Optional params Object
				jsonOptionalParams.put("amountCashback", 0.00);// Cannot have
																// amount
																// cashback in
																// SALE
																// transaction.
				jsonOptionalParams.put("amountTip", 0.00);
				jsonOptionalParams.put("references", jsonReferences);
				jsonOptionalParams.put("customer", jsonCustomer);

				// Building final request object
				jsonRequest
						.put("amount", txtvarAmount.trim());
				jsonRequest.put("mode", "SALE");// This attributes determines
												// the type of transaction
				jsonRequest.put("options", jsonOptionalParams);

				EzeAPI.cardTransaction(context, REQUESTCODE_SALE, jsonRequest);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else
			displayToast(mandatoryErrMsg);
	}




	private void updateDevice() {
		EzeAPI.checkForUpdates(this, REQUESTCODE_UPDATE);
	}

	public static void closeEzetap() {
		EzeAPI.close(context, REQUESTCODE_CLOSE);
	}

	private static boolean isMandatoryParamsValid() {
		if (txtvarAmount.equalsIgnoreCase("")
				|| txtvarRefNum.equalsIgnoreCase(""))
			return false;
		else
			return true;
	}


	private boolean isTransactionIdValid() {
		if (strTxnId == null)
			return false;
		else
			return true;
	}

	private static void displayToast(String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (intent != null && intent.hasExtra("response")) {
			Bundle bundle = intent.getExtras();
			String[] typeofCommand;
			String s;
			s = bundle.getString("response");
			try {
			JSONObject response = new JSONObject(
					intent.getStringExtra("response"));
				JSONObject queueid = new JSONObject(intent.getStringExtra("response"));
			//response = response.getJSONObject("result");
			//response = response.getJSONObject("txn");
			//String code;
			//	code = response.getString("code");
			String status;
			status = response.getString("status");
			if (status.equals("fail"))
			{
				Intent intentb = new Intent();
				String data = "";
				data = strTxnId;
				intentb.putExtra("senddata", data);
				setResult(RESULT_OK, intentb);
				closeEzetap();
				finish();
			}
			if (status.equals("success"))
			{
				response = response.getJSONObject("result");
				response = response.getJSONObject("txn");
				strTxnId = response.getString("txnId");

				if (strTxnId != null)
				{
					queueid = queueid.getJSONObject("result");
					queueid = queueid.getJSONObject("references");
					String qid = queueid.getString("reference1");
					if (queuestate[0].equals(qid))
					{
						String data = "";
						data = "NEXTMOVE|28.625374683906088|77.12294930512509|3|902|"+strTxnId+"|#####|#####|";
						Intent intenteze = new Intent();
						intenteze.setAction("com.north.socket.client.LBBroadcastReceiver");
						intenteze.putExtra("sendchat", data);
						sendBroadcast(intenteze);
						closeEzetap();
						finish();
					}
//					String datas;
//					datas = "UPDPT,10,0,3,0,1,0," + txtvarRefNum + "," + strTxnId + "," + strTxnId + ",";
//					Intent intenta = new Intent();
//					intenta.setAction("com.north.socket.client.LBBroadcastReceiver");
//					intenta.putExtra("sendchat", datas);
//					sendBroadcast(intenta);
//					closeEzetap();
//					finish();
				}
			}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Toast.makeText(this, intent.getStringExtra("response"),
					Toast.LENGTH_SHORT).show();
			Log.i("SampleAppLogs", intent.getStringExtra("response"));
			try {
				JSONObject jsonArray = new JSONObject(s);
				String status = jsonArray.getString("status");
				if(status.equals("success")){
//					doSaleTxn();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		switch (requestCode) {
			case REQUESTCODE_INIT:
			case REQUESTCODE_PREP:
		case REQUESTCODE_CASH:
		case REQUESTCODE_CASHBACK:
		case REQUESTCODE_CASHATPOS:
		case REQUESTCODE_WALLET:
		case REQUESTCODE_SALE:
			break;
		case REQUESTCODE_UPDATE:
			if (resultCode == RESULT_OK) {
				try {
					JSONObject response = new JSONObject(intent.getStringExtra("response"));
					response = response.getJSONObject("result");
					response = response.getJSONObject("txn");
					strTxnId = response.getString("txnId");
					emiID = response.getString("emiId");
					if (strTxnId != null)
					{
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

			}
			else
			{
				Intent intentb = new Intent();
				setResult(RESULT_OK, intentb);
				finish();
				closeEzetap();
			}
			break;

		default:
			break;
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
}
