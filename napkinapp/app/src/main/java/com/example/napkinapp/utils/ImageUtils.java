package com.example.napkinapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.widget.ImageView;

/**
 * Ulilty class for handling Base64-encoded images
 */
public class ImageUtils {
    /**
     * Returns a bitmap from a given base64 encoded string
     * @param encodedString Base64-encoded image
     * @return a bitmap of the encoded image
     */
    public static Bitmap decodeImage(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    /*
    TODO: create encoder function
    im not touching that until we figure out how to upload images
    need to know what im taking as an input
     */
}
