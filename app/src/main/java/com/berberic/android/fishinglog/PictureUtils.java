package com.berberic.android.fishinglog;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

/**
 * * Copyright (C) 2017 Carl Berberich
 * Created by berberic on 9/15/2017.
 */

public class PictureUtils {
    private static final String TAG = "PictureUtils";

    public static Bitmap getScaledBitmap(String path, Activity activity){
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return getScaledBitmap(path, size.x,size.y);
    }

    public static Bitmap getScaledBitmap(String path , int destWidth, int destHeight){
        //Read in the dimensions of the image on the disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        //Figure out how much to scale down by
        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth){
            float heightScale = srcHeight / destHeight;
            float widthScale = srcWidth / destWidth;

            inSampleSize = Math.round(heightScale > widthScale ? heightScale : widthScale);
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        //Read in and create final bitmap
        return BitmapFactory.decodeFile(path, options);
    }

    @SuppressWarnings("deprecation")
    public static BitmapDrawable getScaledDrawable(Activity a, String path) {
        Log.d(TAG, "Inside getScaledDrawable()");

        Display display = a.getWindowManager().getDefaultDisplay();
        float destWidth = display.getWidth();
        float destHeight = display.getHeight();

        // Read in the dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        if (srcWidth > 0.0) {
            Log.d(TAG, "srcWidth: " + srcWidth);
            Log.d(TAG, "srcHeight: " + srcHeight);

            int inSampleSize = 1;
            if (srcHeight > destHeight || srcWidth > destWidth) {
                if (srcWidth > srcHeight) {
                    inSampleSize = Math.round(srcHeight / destHeight);
                } else {
                    inSampleSize = Math.round(srcWidth / destWidth);
                }
            }

            options = new BitmapFactory.Options();
            options.inSampleSize = inSampleSize;

            Log.d(TAG, "inSampleSize: " + inSampleSize);

            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            return new BitmapDrawable(a.getResources(), bitmap);
        } else
            return null;
    }

    public static void cleanImageView(ImageView imageView) {
        if( !(imageView.getDrawable() instanceof BitmapDrawable) ) {
            return;
        }

        // Clean up the view's image for the sake of memory
        BitmapDrawable b = (BitmapDrawable) imageView.getDrawable();
        b.getBitmap().recycle();
        imageView.setImageDrawable(null);
    }

}
