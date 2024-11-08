package com.example.napkinapp.utils;

import android.graphics.Bitmap;
import android.util.Base64;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for generating QR codes and hashing strings.
 * This class provides methods to hash a string using SHA-256,
 * and generate QR code bitmaps from the hashed or plain text.
 */
public class QRCodeUtils {

    /**
     * Hashes the input string using the SHA-256 algorithm and encodes the result in Base64.
     *
     * @param input The string to be hashed.
     * @return A Base64-encoded string representing the hashed input, or null if an error occurs.
     */
    public static String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            return Base64.encodeToString(hashBytes, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generates a QR code bitmap from the given text.
     *
     * @param text   The text to encode into the QR code.
     * @param width  The width of the QR code bitmap.
     * @param height The height of the QR code bitmap.
     * @return A Bitmap object representing the generated QR code, or null if an error occurs.
     */
    public static Bitmap generateQRCode(String text, int width, int height) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
