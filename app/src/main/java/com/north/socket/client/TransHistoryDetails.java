package com.north.socket.client;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import static com.north.socket.client.R.string.card;
import static com.north.socket.client.R.string.customer;

public class TransHistoryDetails extends Activity {

    int indexNo = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_history_details);

        TextView dateTextView = (TextView)findViewById(R.id.dateTxtHistory);
        TextView midTextView = (TextView)findViewById(R.id.midTxtHistory);
        TextView deviceTextView = (TextView)findViewById(R.id.deviceTxtHistory);
        TextView timeTextView = (TextView)findViewById(R.id.timeTxtHistory);
        TextView tidTextView = (TextView)findViewById(R.id.tidTxtHistory);
        TextView authTextView = (TextView)findViewById(R.id.authTxtHistory);
        TextView cardTextView = (TextView)findViewById(R.id.cardTxtHistory);
        TextView brandTextView = (TextView)findViewById(R.id.brandTxtHistory);
        TextView amountTextView = (TextView)findViewById(R.id.amtTxtHistory);
        TextView nameTextView = (TextView)findViewById(R.id.nameTxtHistory);
        TextView emailTextView = (TextView)findViewById(R.id.emailTxtHistory);
        TextView mobileTextView = (TextView)findViewById(R.id.mobileTxtHistory);
        TextView qidTextView = (TextView)findViewById(R.id.qidTxtHistory);
        TextView mNameTextView = (TextView)findViewById(R.id.mNameTxtHistory);



        Bundle bundle = getIntent().getExtras();
        if(bundle.getInt("indexNo") != -1){
            indexNo = bundle.getInt("indexNo");

            if(awaitPayment.ezetap == 0) {
                String txt1 = awaitPayment.receiptDateList.get(indexNo);
                String[] text1Array = txt1.split("T");
                dateTextView.setText(": " + text1Array[0]);

                String[] timeArray = text1Array[1].split("\\+");
                timeTextView.setText(": " + timeArray[0]);

                JSONObject txn = awaitPayment.txnJSON.get(indexNo);
                JSONObject card = awaitPayment.cardJSON.get(indexNo);
                JSONObject customer = awaitPayment.customerJSON.get(indexNo);
                JSONObject merchant = awaitPayment.merchantJSON.get(indexNo);
                JSONObject references = awaitPayment.referencesJSON.get(indexNo);
                try {
                    String midText = txn.getString("mid");
                    midTextView.setText(": " + midText);

                    String tidText = txn.getString("tid");
                    tidTextView.setText(": " + tidText);

                    String deviceText = txn.getString("deviceSerial");
                    deviceTextView.setText(": " + deviceText);

                    String authText = txn.getString("authCode");
                    authTextView.setText(": " + authText);

                    String amountText = txn.getString("amount");
                    amountTextView.setText(": \u20B9 " + amountText);

                    String carNoText = card.getString("maskedCardNo");
                    cardTextView.setText(": " + carNoText);

                    String brandText = card.getString("cardBrand");
                    brandTextView.setText(": " + brandText);

                    String nameText = customer.getString("name");
                    nameTextView.setText(": " + nameText);

                    String emailText = customer.getString("email");
                    emailTextView.setText(": " + emailText);

                    String mobileText = customer.getString("mobileNo");
                    mobileTextView.setText(": " + mobileText);

                    String qidText = references.getString("reference1");
                    qidTextView.setText(": " + qidText);

                    String merchantText = merchant.getString("merchantName");
                    mNameTextView.setText(": " + merchantText);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                HistoryDetails historyDetails = (HistoryDetails) awaitPayment.listData.get(indexNo);
                System.out.println(historyDetails);

                String txt1 = historyDetails.TrxDate;
                String[] timeArray = txt1.split("\\s+");
                String check1 = timeArray[0] + "/" + timeArray[1] + "/" + timeArray[2];
                dateTextView.setText(": " + check1);

                String check2 = timeArray[3] + " " + timeArray[4];
                timeTextView.setText(": " + check2);

//                String midText = historyDetails.AuthNo;
                midTextView.setText(": ");

//                String tidText = txn.getString("tid");
                tidTextView.setText(": ");

                String deviceText = MenuClass.MSwipeUserName;
                deviceTextView.setText(": " + deviceText);

                String authText = historyDetails.AuthNo;
                authTextView.setText(": " + authText);

                String amountText = historyDetails.TrxAmount;
                amountTextView.setText(": \u20B9 " + amountText);

                String carNoText = historyDetails.CardLastFourDigits;
                cardTextView.setText(": ****" + carNoText);

                String brandText = historyDetails.TrxType;
                brandTextView.setText(": " + brandText);

                String merchantText = historyDetails.TrxNotes;
                String notes[] = merchantText.split("\\,");

                if(notes.length == 3) {
//                String nameText = customer.getString("name");
                    nameTextView.setText(": " + notes[1]);

//                String emailText = customer.getString("email");
                    emailTextView.setText(": ");

//                String mobileText = customer.getString("mobileNo");
                    mobileTextView.setText(": "+ notes[2]);
                    mNameTextView.setText(": " + notes[0]);
                }else{
                    nameTextView.setText(": ");
                    mNameTextView.setText(": " + merchantText);
                    emailTextView.setText(": ");
                    mobileTextView.setText(": ");
                }

                String qidText = historyDetails.MerchantInvoice;
                qidTextView.setText(": " + qidText);





            }

        }
    }
}
