package com.north.socket.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore.Images;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class CaptureSignature extends Activity {
    LinearLayout mContent;
    LinearLayout signaturelayout;
    signature mSignature;
    Button mClear, mGetSign;
    public String current = null;
    private Bitmap mBitmap;
    View mView;
    File mypath;
    private EditText name;
    private EditText mobile;
    private ScrollView scrollView;
    private RatingBar question1;
    private RatingBar question2;
    private RatingBar question3;
    private RatingBar question4;
    private RatingBar question5;
    private RatingBar question6;
    private RatingBar question7;
    String[] queuestate;
    String[] typeofCommand;
    private ImageView ledbutton;
    ConnectivityReceiver myReceiversignature;
    private Handler overlayTimer = new Handler();
    private FrameLayout overlay;
    private String latlongtosubmit;
    private String RatingString = "a";
    int signCheck = -1;

    Context captureSignContext;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();

        myReceiversignature = new ConnectivityReceiver();

        IntentFilter filter = new IntentFilter("com.north.socket.client.SignBroadcastReceiver");
        registerReceiver(myReceiversignature, filter);

        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_capture_signature);
        ledbutton = (ImageView) findViewById(R.id.ledbutton);
        question1 = (RatingBar) findViewById(R.id.question1);
        question2 = (RatingBar) findViewById(R.id.question2);
        question3 = (RatingBar) findViewById(R.id.question3);
        question4 = (RatingBar) findViewById(R.id.question4);
        question5 = (RatingBar) findViewById(R.id.question5);
        question6 = (RatingBar) findViewById(R.id.question6);
        question7 = (RatingBar) findViewById(R.id.question7);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        captureSignContext = this;


        SocketService.signaturePage = 1;

        Intent sendPhotos = new Intent();
        sendPhotos.setAction("com.north.socket.client.LBBroadcastReceiverFile");
        sendPhotos.putExtra("shouldisend", "0");
        sendBroadcast(sendPhotos);

        String root = Environment.getExternalStorageDirectory().toString();
        signaturelayout = (LinearLayout) findViewById(R.id.signatureLayout);
        TextView disclaimer = (TextView) findViewById(R.id.disclaimer);
        if (bundle.getString("qdata") != null) {
            String s = bundle.getString("qdata");
            assert s != null;
            typeofCommand = s.split("\\|");
            queuestate = s.split("\\$");
        }
        name = (EditText) findViewById(R.id.name);
        if (queuestate[1].equals("1003")) {
            signaturelayout.setVisibility(View.INVISIBLE);
            scrollView.setVisibility(View.VISIBLE);
            disclaimer.setText(R.string.declaration_rcv_vehicle);

        }
        if (queuestate[1].equals("430") || queuestate[1].equals("3410")) {
            name.setHint(R.string.svc_name_hint);
        }
        if (queuestate[1].equals("300")) {
            signaturelayout.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.INVISIBLE);

            disclaimer.setText(R.string.declaration);
        }
        File directory = new File(root + "/PhotoDir");
        String uniqueId = queuestate[0] + queuestate[1] + "sign";
        current = uniqueId + ".jpg";
        mypath = new File(directory, current);

        if (mypath.exists()) {
            mypath.delete();

        }
        mContent = (LinearLayout) findViewById(R.id.linearLayout);
        mSignature = new signature(this, null);
        mSignature.setBackgroundColor(Color.WHITE);
        mContent.addView(mSignature, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

        scrollView.requestDisallowInterceptTouchEvent(true);
        mClear = (Button) findViewById(R.id.clear);
        mGetSign = (Button) findViewById(R.id.getsign);
        if (SocketService.timeDiff()) {
            latlongtosubmit = SocketService.latitude + "|" + SocketService.longitude + "|";
            mGetSign.setEnabled(true);
            mGetSign.setAlpha(1.0f);
        } else {
            latlongtosubmit = "";
            mGetSign.setEnabled(false);
            mGetSign.setAlpha(0.2f);
        }
        //mCancel = (Button) findViewById(R.id.cancel);
        mView = mContent;

        mobile = (EditText) findViewById(R.id.mobile);

        overlay = (FrameLayout) findViewById(R.id.overlay);
        overlay.setVisibility(View.INVISIBLE);


        if (queuestate[1].equals("1003")) {
            mClear.setText("Refusing to Accept.");
        }
        if (queuestate[38].equals("40")) {
            RatingString = "1111111111";
            mClear.setTextColor(Color.BLACK);
            mClear.setText("Support will Call");
            scrollView.setVisibility(View.INVISIBLE);
            signaturelayout.setVisibility(View.VISIBLE);
        }
        final String[] nameText = new String[1];
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                nameText[0] = String.valueOf(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String numRegex = ".*[0-9].*";
                String alphaRegex = ".*[A-Z].*";
                if (String.valueOf(s).matches("[0-9]")) {
                    changeNameText(nameText[0]);
                }
                if (String.valueOf(s).matches(numRegex) && String.valueOf(s).matches(alphaRegex)) {
                    changeNameText(nameText[0]);
                }
            }
        });
        name.setText(nameText[0]);
    }

    public void changeNameText(String textName) {
        name.setText(textName);
        name.setSelection(name.getText().length());
    }

    public void onResume() {
        super.onResume();
        if (myReceiversignature == null) {
            myReceiversignature = new ConnectivityReceiver();
            IntentFilter filter = new IntentFilter("com.north.socket.client.POSSNBroadcastReceiver");
            registerReceiver(myReceiversignature, filter);
        }
    }

    public void onStop() {
        if (myReceiversignature != null) {

            try {
                SocketService.signaturePage = 0;
                unregisterReceiver(myReceiversignature);
                myReceiversignature = null;
            } catch (IllegalArgumentException e) {
                // Check wether we are in debug mode

                e.printStackTrace();

            }
        }
        super.onStop();
    }

    public void onBackPressed() {
        // do nothing.
    }

    @Override
    protected void onDestroy() {
        try {
            SocketService.signaturePage = 0;
            Intent intent = new Intent();
            intent.setAction("com.north.socket.client.LBBroadcastReceiver");
            //intent.putExtra("senddata", "LGOUT");
            //sendBroadcast(intent);
            System.out.println("onDestroy.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public void refuse(View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.are_you_sure);
        alertDialogBuilder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (queuestate[1].equals("1003")) {
                            if (queuestate[38].equals("0")) {
                                String datab = "signdone";
                                Intent intentphoto = new Intent();
                                intentphoto.setAction("com.north.socket.client.BroadcastReceiver");
                                intentphoto.putExtra("senddata", datab);
                                sendBroadcast(intentphoto);

                                Intent intent = new Intent();
                                intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                                intent.putExtra("sendchat", "GENTICKETNEW|" + queuestate[0] + "|40|0|0|");
                                sendBroadcast(intent);
                            }
                        } else {
                            signCheck = -1;
                            Log.v("log_tag", "Panel Cleared");
                            mSignature.clear();
                            mGetSign.setEnabled(true);
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

    public void displayOverlay() {
        overlay.setVisibility(View.VISIBLE);
        overlayTimer.removeCallbacksAndMessages(null);
        overlayTimer.postDelayed(overlayTimeout, 10000);
    }

    private Runnable overlayTimeout = new Runnable() {
        @Override
        public void run() {
            try {
                overlay.setVisibility(View.INVISIBLE);
                overlayTimer.removeCallbacksAndMessages(null);
            } catch (Exception e) {
                // added try catch block to be sure of uninterupted execution
            }

      /* and here comes the "trick" */
            //handler.postDelayed(this, 1000);
        }
    };

    public void submitReview(View v) {
        SocketService.stopAlarm();
        if ((question1.getRating() > 0.0) && (question2.getRating() > 0.0) && (question3.getRating() > 0.0) && (question4.getRating() > 0.0) && (question5.getRating() > 0.0) && (question6.getRating() > 0.0) && (question7.getRating() > 0.0)) {
            RatingString = "";
            RatingString = Integer.toString((int) (question1.getRating())) + Integer.toString((int) (question2.getRating())) + Integer.toString((int) (question3.getRating())) + Integer.toString((int) (question4.getRating())) + Integer.toString((int) (question5.getRating())) + Integer.toString((int) (question6.getRating())) + Integer.toString((int) (question7.getRating()));
            scrollView.setVisibility(View.INVISIBLE);
            signaturelayout.setVisibility(View.VISIBLE);
        }else{
            Toast toast = Toast.makeText(this, R.string.please_complete_feedback, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 105, 50);
            toast.show();
        }
    }

    public void acceptSignature(View v) {
        System.out.println("Signed");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        int state = Integer.parseInt(queuestate[1]);
        if (name.getText().length() < 4) {

            Toast toast = Toast.makeText(this, R.string.four_digit_number, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 105, 50);
            toast.show();

            return;
        }
        if (mobile.getText().length() < 10) {
            Toast toast = Toast.makeText(this, R.string.ten_digit_number, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 105, 50);
            toast.show();
            return;
        }

        if (signCheck != 0) {
            System.out.println("Signed");
            mSignature.save(mView);
        } else {
            Toast toast = Toast.makeText(this, R.string.please_sign, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 105, 50);
            toast.show();
            System.out.println("UnSigned");
            return;
        }
        if ((state == 300) || (state == 1002) || (state == 1003) || (state == 430) || state == 3310 || state == 3410 || state == 8100 || state == 6700 || state == 4700 || state == 5700) {
            alertDialogBuilder.setMessage(R.string.submit_signature);
            alertDialogBuilder.setPositiveButton(R.string.yes,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            String data;

                            String datab;
                            datab = "signdone";
                            Intent intentphoto = new Intent();
                            intentphoto.setAction("com.north.socket.client.BroadcastReceiver");
                            intentphoto.putExtra("senddata", datab);
                            sendBroadcast(intentphoto);

                            if (latlongtosubmit.length() > 1) {
                                data = "NEXTMOVE|" + latlongtosubmit + "1|" + queuestate[1] + "|" + RatingString + "," + name.getText() + "," + mobile.getText() + "|#####|";
                                Intent intent = new Intent();
                                intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                                intent.putExtra("sendchat", data);
                                sendBroadcast(intent);
                                displayOverlay();
                            } else {
                                Toast toast = Toast.makeText(captureSignContext, "Please wait for location update", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, 105, 50);
                                toast.show();
                            }
                            //finish();
                        }
                    });

            alertDialogBuilder.setNegativeButton(R.string.cancel,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });
        }


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public class signature extends View {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();
        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);

            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (mBitmap == null) {
                mBitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(mBitmap);
            try {
                FileOutputStream mFileOutStream = new FileOutputStream(mypath);
                v.draw(canvas);
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, mFileOutStream);

                mFileOutStream.flush();
                mFileOutStream.close();
            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }
        }

        public void clear() {
            if (queuestate[1].equals("1003")) {
                Intent intent = new Intent();
                intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                intent.putExtra("sendchat", "GENTICKETNEW|" + queuestate[0] + "|32|0|0|");
                sendBroadcast(intent);
            } else {
                path.reset();
                invalidate();
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
            signCheck = signCheck + 1;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
            //requestDisallowInterceptTouchEvent(true)
            // scrollView.setscrollinge
            float eventX = event.getX();
            float eventY = event.getY();
            //mGetSign.setEnabled(true);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;
                default:
                    debug();
                    return false;
            }
            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));
            lastTouchX = eventX;
            lastTouchY = eventY;
            return true;
        }

        private void debug() {
        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }
            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

    public class ConnectivityReceiver extends BroadcastReceiver {

        public ConnectivityReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            try {

                String latlong;
                latlong = intent.getStringExtra("latlong");
                if (latlong != null) {
                    latlongtosubmit = latlong;
                    mGetSign.setAlpha(1.0f);
                    mGetSign.setEnabled(true);
                }
                String data = intent.getStringExtra("senddata");
                if (data != null) {
                    if (data.equals("green")) {
                        ledbutton.setImageResource(R.drawable.green);
                    } else if (data.equals("red")) {
                        ledbutton.setImageResource(R.drawable.red);
                    }
                }
            } catch (java.lang.NullPointerException ignored) {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}