package com.example.napkinapp.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.napkinapp.models.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ImageGenUtils {

    public static Bitmap genProfleBitmap(User user){
        String name = user.getName();
        String letter = String.valueOf(name.charAt(0)).toUpperCase();
        int background = idToRGB(user.getAndroidId());

        Bitmap profile = Bitmap.createBitmap(500,500, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(profile);

        canvas.drawColor(background);

        Paint textPaint = new Paint();
        textPaint.setTextSize(300);
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);

        //set bounds
        Rect textBounds = new Rect();
        textPaint.getTextBounds(letter, 0, letter.length(), textBounds);
        int x = (profile.getWidth() - textBounds.width()) / 2;
        int y = (profile.getHeight() + textBounds.height()) / 2;

        canvas.drawText(letter, x, y, textPaint);

        return profile;
    }

    public static int idToRGB(String id) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(id.getBytes());

            // Convert the first 3 bytes to RGB values
            int r = Byte.toUnsignedInt(hash[0]);
            int g = Byte.toUnsignedInt(hash[1]);
            int b = Byte.toUnsignedInt(hash[2]);

            return Color.rgb(r, g, b);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}

