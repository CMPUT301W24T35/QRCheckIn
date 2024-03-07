package com.example.qrcheckin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Helper class to generate document IDs for Firebase and collections.
 */

public class Helpers {

    public static String createDocID(String field1, String field2, String field3) {
        String combinedString = field1 + field2 + field3;

        try {
            // Create a MessageDigest instance for SHA-256 hashing
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Get the byte array of the combined fields
            byte[] bytes = combinedString.getBytes();

            // Update the digest with the byte array
            digest.update(bytes);

            // Get the hashed byte array
            byte[] hashedBytes = digest.digest();

            // Convert the hashed byte array to a hexadecimal string
            StringBuilder stringBuilder = new StringBuilder();
            for (byte hashedByte : hashedBytes) {
                stringBuilder.append(Integer.toString((hashedByte & 0xff) + 0x100, 16).substring(1));
            }

            // Return the hexadecimal string as the document ID
            return stringBuilder.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Converts bitmap to Base64 string for saving as Firebase field
     * Source: https://www.thepolyglotdeveloper.com/2015/06/from-bitmap-to-base64-and-back-with-native-android/
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * Converts Base64 to bitmap string for saving as Firebase field
     * Source: https://www.thepolyglotdeveloper.com/2015/06/from-bitmap-to-base64-and-back-with-native-android/
     * @param b64
     * @return
     */
    public static Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }
}

