package com.north.socket.client;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by sree on 11/08/16.
 */
public class LoadImageTask extends AsyncTask<String, Void, String> {

    private ImageView mImageView = null;

    public LoadImageTask(String stringname) {

        //mImageView = imageView;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    public void sendToast (String data) {
        Intent toasty = new Intent();
        toasty.setAction("com.north.socket.client.BroadcastReceiver");
        toasty.putExtra("sendtoast", data);
//        sendBroadcast(toasty);
//        LoadImageTask.this.getbaseconte
    }


    protected String doInBackground(String... filename) {

        String root = Environment.getExternalStorageDirectory().toString();
        File directory = new File(root + "");
        File f = new File(directory,filename[0].toString());
        Bitmap b = BitmapFactory.decodeFile(f.getPath().toString());
        int orientation = 0;
        try {
            ExifInterface ei = new ExifInterface(f.getPath().toString());
            orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        } catch (Exception e) {
        }

        Bitmap rotated = null;
        Bitmap out = null;
        Application application;
        Context context = (Application) AmbassadorApp.getContext();

        sendToast(String.valueOf(orientation));
        Log.v("ExifInterface : ", String.valueOf(orientation));
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                if (b != null) {
                    out = Bitmap.createScaledBitmap(b, 1707, 1024, true);
                }
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                if (b != null) {
                    rotated = rotateImage(b, 90);
                    out = Bitmap.createScaledBitmap(rotated, 1024, 1707, true);
                }
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                //rotated = rotateImage(b, 180);
                if (b != null) {
                    rotated = rotateImage(b, 180);
                    out = Bitmap.createScaledBitmap(rotated, 1707, 1024, true);
                }
            case ExifInterface.ORIENTATION_ROTATE_270:
                //rotated = rotateImage(b, 180);
                if (b != null) {
                    rotated = rotateImage(b, 270);
                    out = Bitmap.createScaledBitmap(rotated, 1024, 1707, true);
                }
                break;
            case ExifInterface.ORIENTATION_UNDEFINED:
                if (b != null) {
                    rotated = rotateImage(b, 0);
                    out = Bitmap.createScaledBitmap(rotated, 1707, 1024, true);
                }
                break;




        }

        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(f);
            if (out == null)
            {
                f.delete();
            }
            if (out.getWidth() > 0 && out.getHeight() > 0)
            {
                out.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
                fOut.flush();
                fOut.close();
                b.recycle();
                out.recycle();
                Log.w("Photo Save", "Name : " + filename[0].toString());
            }

        } catch (Exception e) {}


        return null;
    }

//    protected void onPostExecute(Bitmap result) {
//        if (result != null) {
//          //  mImageView.setImageBitmap(result);
//        }
//    }
}