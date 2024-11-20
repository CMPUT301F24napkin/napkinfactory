package com.example.napkinapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

/**
 * Ulilty class for encoding and decoding images in base64
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

    /**
     * Returns an base64-encoded image as a string
     * @param image image to be encoded
     * @param imageQuality desired quality of image, measured from 0-100
     * @return a String encoded in base64
     */
    public static String encodeImage(ImageView image, int imageQuality){
        try{
            BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,imageQuality,bos);
            byte[] bb = bos.toByteArray();
            String encodedImage = Base64.encodeToString(bb, Base64.DEFAULT);

            return encodedImage;
        }
        catch (Exception e){
            e.getMessage();
            return null;
        }
    }
}
