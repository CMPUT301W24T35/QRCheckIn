package com.example.qrcheckin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Helper class to generate document IDs for Firebase
 * collections.
 */
public class docIDHelper {

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
}
