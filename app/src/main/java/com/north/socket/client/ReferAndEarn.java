package com.north.socket.client;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ReferAndEarn extends Activity {

    EditText nameEditText;
    EditText mobileEditText;
    EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_and_earn);

        nameEditText = (EditText)findViewById(R.id.editTextName);
        mobileEditText = (EditText)findViewById(R.id.EditTextMobile);
        emailEditText = (EditText)findViewById(R.id.EditTextEmail);

        final String[] nameText = new String[1];
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                nameText[0] = String.valueOf(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(String.valueOf(s).contains("[0-9]")){

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                String numRegex   = ".*[0-9].*";
                String alphaRegex = ".*[A-Z].*";
                if(String.valueOf(s).matches("[0-9]")){
                    changeNameText(nameText[0]);
                }
                if (String.valueOf(s).matches(numRegex) && String.valueOf(s).matches(alphaRegex)) {
                    changeNameText(nameText[0]);
                }
            }
        });
        nameEditText.setText(nameText[0]);

    }
    public void changeNameText(String textName){
        nameEditText.setText(textName);
        nameEditText.setSelection(nameEditText.getText().length());
    }
    public void submitRef(View view){
        if(nameEditText.getText().length() < 5){
            Toast.makeText(this, "Please Enter Correct Name", Toast.LENGTH_SHORT).show();
        }else if(mobileEditText.getText().length() < 10){
            Toast.makeText(this, "Please enter correct number", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Refer sent to HR", Toast.LENGTH_SHORT).show();
        }
    }
}
