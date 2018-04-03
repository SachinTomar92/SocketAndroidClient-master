package com.north.socket.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import needle.Needle;


public class PhotoGallery extends Activity {

    public static final int CAMERA_REQUEST_CODE = 1999;
    PhotoConnectivityReceiver myReceiverphoto;


    private GridViewAdapter gridAdapter;
    String[] toppings;
    int pagercount = 0;
    int PAGES = 9;
    int state = 0;

    Button submitPhotoButton;
    ImageView led;
    int numberOfPhotos = 0;
    TextView photosCount;
    Button backButton;

    Bundle bundle;
    String[] queuestate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        Login.takephoto = 1;

        bundle = getIntent().getExtras();

        backButton = (Button) findViewById(R.id.backButton);
        submitPhotoButton = (Button) findViewById(R.id.submitPhoto);
        led = (ImageView) findViewById(R.id.led);
        photosCount = (TextView) findViewById(R.id.numberOfPhoto);
        submitPhotoButton.setVisibility(View.INVISIBLE);
        overlay = (FrameLayout) findViewById(R.id.overlay);
        overlay.setVisibility(View.INVISIBLE);
        Button noRCButton = (Button) findViewById(R.id.noRC);
        Button noInsButton = (Button) findViewById(R.id.noInsu);
        noInsButton.setVisibility(View.INVISIBLE);
        noRCButton.setVisibility(View.INVISIBLE);

        String socketData = bundle.getString("qdata");

        assert socketData != null;
        queuestate = socketData.split("\\$");

        latlongtosubmit = SocketService.latitude + "|" + SocketService.longitude + "|";

        myReceiverphoto = new PhotoConnectivityReceiver();
        IntentFilter filter = new IntentFilter("com.north.socket.client.PhotoGalleryReceiver");
        registerReceiver(myReceiverphoto, filter);

        Intent sisintent = new Intent();
        sisintent.setAction("com.north.socket.client.LBBroadcastReceiverFile");
        sisintent.putExtra("shouldisend", "0");
        sendBroadcast(sisintent);

        state = SocketService.qState;

        switch (queuestate[38]) {
            case "53":
                noRCButton.setTextColor(Color.WHITE);
                break;
            case "52":
                noInsButton.setTextColor(Color.WHITE);
                break;
            default:
                noRCButton.setTextColor(Color.BLACK);
                noInsButton.setTextColor(Color.BLACK);
                break;
        }

        if (state == 200 || state == 4400 || state == 5400 || state == 6400 || state == 7400 || state == 3300) {
            noInsButton.setVisibility(View.VISIBLE);
            noRCButton.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);
            if (Login.vehicleType.equals(Login.twoWheelerCheck)) {
                PAGES = 9;
            } else {
                PAGES = 21;
            }
        } else if (state == 2210) {
            if (Login.vehicleType.equals(Login.twoWheelerCheck)) {
                PAGES = 8;
            } else {
                PAGES = 20;
            }
        } else {
            if (Login.vehicleType.equals(Login.twoWheelerCheck)) {
                PAGES = 6;
            } else {
                PAGES = 18;
            }
        }

        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, R.layout.photo_gallery_grid_layout, getData());
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String root = Environment.getExternalStorageDirectory().toString();
                File directory = new File(root + "/PhotoDir");

                pagercount = position;

                pagercount = pagercount + 100;
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(directory, String.valueOf(state) + queuestate[0] + pagercount + ".jpg");
                pagercount = pagercount - 100;
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
//                intent.putExtra("return-data", true);
//                startActivityForResult(intent, CAMERA_REQUEST_CODE);
                Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                photoIntent.putExtra("return-data", true);
                startActivityForResult(photoIntent, CAMERA_REQUEST_CODE);
            }
        });

    }

    public void onResume() {
        super.onResume();
        if (myReceiverphoto == null) {
            myReceiverphoto = new PhotoConnectivityReceiver();
            IntentFilter filter = new IntentFilter("com.north.socket.client.PhotoGalleryReceiver");
            registerReceiver(myReceiverphoto, filter);
        }
    }

    public void onStop() {
        if (myReceiverphoto != null) {

            try {
                unregisterReceiver(myReceiverphoto);
                myReceiverphoto = null;
            } catch (IllegalArgumentException e) {
                // Check wether we are in debug mode

                e.printStackTrace();

            }
        }
        super.onStop();
    }

    private FrameLayout overlay;
    private Handler overlayTimer = new Handler();

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

        }
    };

    public void BackOneStep(View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        int state = Integer.parseInt(queuestate[1]);
        if ((state == 200) || (state == 712) || (state == 713) || state == 2210 || state == 2410 || state == 4400 || state == 5400 || state == 6400 || state == 5600 || state == 7400) {
            alertDialogBuilder.setMessage(R.string.go_back);
            alertDialogBuilder.setPositiveButton(R.string.yes,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            String data;

                            displayOverlay();
                            String datab;
                            datab = "photodone";
                            Intent intentphoto = new Intent();
                            intentphoto.setAction("com.north.socket.client.BroadcastReceiver");
                            intentphoto.putExtra("senddata", datab);
                            sendBroadcast(intentphoto);


                            if (SocketService.timeDiff()) {
                                latlongtosubmit = SocketService.latitude + "|" + SocketService.longitude + "|";
                            }

                            if (latlongtosubmit.length() > 1) {
                                data = "NEXTMOVE|" + latlongtosubmit + "0|" + queuestate[1] + "|a|####|";
                                Intent intent = new Intent();
                                intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                                intent.putExtra("sendchat", data);
                                sendBroadcast(intent);
                            } else {
                                Toast.makeText(PhotoGallery.this, "", Toast.LENGTH_SHORT).show();

                            }

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

    public void noInsur(View view) {
        if (queuestate[38].equals("0")) {
            showAlert(52);
        }
    }

    public void noRC(View view) {
        if (queuestate[38].equals("0")) {
            showAlert(53);
        }
    }

    public void showAlert(final int ticketID) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PhotoGallery.this);
        alertDialogBuilder.setMessage(R.string.are_you_sure);
        alertDialogBuilder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        raiseTicket(ticketID);
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

    public void raiseTicket(int ticketID) {
        Intent intent = new Intent();
        intent.setAction("com.north.socket.client.LBBroadcastReceiver");
        String data;// = "sendchat", "TICKET‰" + typeofCommand[1] + "‰" + ticketnumber + "‰‰‰";
        data = "GENTICKETNEW|" + queuestate[0] + "|" + ticketID + "|0|0|";
        intent.putExtra("sendchat", data);
        sendBroadcast(intent);
    }

    String latlongtosubmit = "";

    public void submitPhotos(View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage(R.string.submit_photos_question);
        alertDialogBuilder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        String data;
                        String datab;
                        datab = "photodone";
                        Intent intentphoto = new Intent();
                        intentphoto.setAction("com.north.socket.client.BroadcastReceiver");
                        intentphoto.putExtra("senddata", datab);
                        sendBroadcast(intentphoto);


                        if (SocketService.timeDiff()) {
                            latlongtosubmit = SocketService.latitude + "|" + SocketService.longitude + "|";
                        }
                        if (latlongtosubmit.length() > 1) {
                            data = "NEXTMOVE|" + latlongtosubmit + "1|" + queuestate[1] + "|0|####|";
                            Intent intent = new Intent();
                            intent.setAction("com.north.socket.client.LBBroadcastReceiver");
                            intent.putExtra("sendchat", data);
                            sendBroadcast(intent);
                            displayOverlay();
                        } else {
                            Toast.makeText(PhotoGallery.this, "Waiting For Location", Toast.LENGTH_SHORT).show();
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

    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        toppings = new String[50];
        if (Login.vehicleType.equals(Login.twoWheelerCheck)) {
            toppings = getResources().getStringArray(R.array.two_wheeler);
        } else {
            toppings = getResources().getStringArray(R.array.four_wheeler);
        }

        for (int imageCount = 0; imageCount < PAGES; imageCount++) {
            String url;
            if (Login.vehicleType.equals(Login.twoWheelerCheck)) {
                url = "drawable/pagetw" + imageCount;
            } else {
                url = "drawable/page" + imageCount;
            }
            int imageKey = getResources().getIdentifier(url, "drawable", getPackageName());
            Bitmap bitmap = decodeSampledBitmapFromResource(getResources(), imageKey, 100, 100);
            imageItems.add(new ImageItem(bitmap, (toppings[imageCount] + "\uD83D\uDCF7")));
        }

        File directoryPath = new File(root + "/PhotoDir");
        File listOfFile[] = directoryPath.listFiles();
        for (int j = 0; j < PAGES; j++) {
            String checkThisFile = String.valueOf(state) + queuestate[0] + String.valueOf(j + 100) + ".jpg";

            for (File aListOfFile : listOfFile) {

                if ((aListOfFile.getName()).equals(checkThisFile)) {
                    try {
                        if (SocketService.isJPEG(new File(directoryPath + "/" + checkThisFile))) {
                            Bitmap bitmap = decodeSampledBitmapFromFile((directoryPath + "/" + checkThisFile), 100, 100);
                            imageItems.set(j, new ImageItem(bitmap, (toppings[j] + " ✓")));
                            numberOfPhotos += 1;
                        } else {
                            File fDelete = new File(directoryPath + "/" + checkThisFile);
                            fDelete.delete();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            photosCount.setText(String.valueOf(numberOfPhotos) + "/" + String.valueOf(PAGES));
            if (numberOfPhotos > 0) {
                backButton.setVisibility(View.INVISIBLE);
            }
            if (numberOfPhotos >= PAGES) {
                submitPhotoButton.setVisibility(View.VISIBLE);
            }
        }

        return imageItems;
    }

    String root = Environment.getExternalStorageDirectory().toString();

    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            Needle.onBackgroundThread().withThreadPoolSize(20).execute(new Runnable() {
                @Override
                public void run() {
                    // something that we want to do serially but not on the main thread

                    File directory = new File(root + "/PhotoDir");
                    pagercount = pagercount + 100;

                    final File f = new File(directory, String.valueOf(state) + queuestate[0] + pagercount + ".jpg");

                    pagercount = pagercount - 100;
                    Bitmap b = BitmapFactory.decodeFile(f.getPath());
                    Bitmap rotatedin;
                    Bitmap outin = null;
                    int orientation = 0;
                    try {
                        ExifInterface ei;
                        ei = new ExifInterface(f.getPath());
                        orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                    } catch (Exception ignored) {
                        System.out.println(ignored);
                    }
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_NORMAL:
                            if (b != null) {
                                outin = Bitmap.createScaledBitmap(b, 1707, 1024, true);
                            }
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            if (b != null) {
                                rotatedin = rotateImage(b, 90);
                                outin = Bitmap.createScaledBitmap(rotatedin, 1024, 1707, true);
                            }
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            if (b != null) {
                                rotatedin = rotateImage(b, 180);
                                outin = Bitmap.createScaledBitmap(rotatedin, 1707, 1024, true);
                            }
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            if (b != null) {
                                rotatedin = rotateImage(b, 270);
                                outin = Bitmap.createScaledBitmap(rotatedin, 1024, 1707, true);
                            }
                            break;
                        case ExifInterface.ORIENTATION_UNDEFINED:
                            if (b != null) {
                                rotatedin = rotateImage(b, 0);
                                if (b.getHeight() < b.getWidth()) {
                                    outin = Bitmap.createScaledBitmap(rotatedin, 1707, 1024, true);
                                } else {
                                    outin = Bitmap.createScaledBitmap(rotatedin, 1024, 1707, true);
                                }
                            }
                            break;
                    }
                    FileOutputStream fOut;
                    try {
                        fOut = new FileOutputStream(f);
                        if (outin == null) {
                            f.delete();
                        }

                        if (outin != null && outin.getWidth() > 0 && outin.getHeight() > 0) {
                            outin.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
                            fOut.flush();
                            fOut.close();
                            b.recycle();
                            outin.recycle();
                            //Log.w("Photo Save", "Name : " + filename[0].toString());
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                numberOfPhotos = 0;
                                gridAdapter.refresh(getData());
                            }
                        });
                    } catch (Exception e) {
                        System.out.println("PhotoGallery Exception" + e);
                    }
                }
            });
        }
    }

    public void onBackPressed() {

    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap decodeSampledBitmapFromFile(String resId,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(resId, options);
        BitmapFactory.decodeFile(resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public class PhotoConnectivityReceiver extends BroadcastReceiver {

        public PhotoConnectivityReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {


            try {
                String data = intent.getStringExtra("senddata");
                if (data != null) {
                    Log.d("LOG", "Received senddata : " + data);
                    if (data.equals("green")) {
                        led.setImageResource(R.drawable.green);
                    } else if (data.equals("red")) {
                        led.setImageResource(R.drawable.red);
                    }
                }
            } catch (Exception ignored) {

            }
        }
    }
}