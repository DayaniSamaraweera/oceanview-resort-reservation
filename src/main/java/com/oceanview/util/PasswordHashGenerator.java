package com.oceanview.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

//Utility class for generating SHA-256 password hashes.

public final class PasswordHashGenerator {

    private static final Logger HASH_LOGGER =
            Logger.getLogger(PasswordHashGenerator.class.getName());

 
    private PasswordHashGenerator() {
        throw new UnsupportedOperationException(
                "PasswordHashGenerator is a utility class and cannot be instantiated");
    }

    public static String generateHash(String plainTextPassword) {

        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            HASH_LOGGER.warning("Attempted to hash null or empty password");
            return null;
        }

        try {
            // Create SHA-256 message digest instance
            MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");

            // Hash the password bytes using UTF-8 encoding
            byte[] hashedBytes = shaDigest.digest(
                    plainTextPassword.getBytes(StandardCharsets.UTF_8));

            // Convert byte array to hexadecimal string
            StringBuilder hexBuilder = new StringBuilder();
            for (byte singleByte : hashedBytes) {
                String hexValue = Integer.toHexString(0xff & singleByte);
                if (hexValue.length() == 1) {
                    hexBuilder.append('0');
                }
                hexBuilder.append(hexValue);
            }

            return hexBuilder.toString();

        } catch (NoSuchAlgorithmException algorithmException) {
            HASH_LOGGER.log(Level.SEVERE,
                    "SHA-256 algorithm not available", algorithmException);
            return null;
        }
    }


    public static boolean verifyPassword(String plainTextPassword, String storedHash) {

        if (plainTextPassword == null || storedHash == null) {
            return false;
        }

        String computedHash = generateHash(plainTextPassword);
        return storedHash.equals(computedHash);
    }
}