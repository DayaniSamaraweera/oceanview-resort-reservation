package com.oceanview.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for generating SHA-256 password hashes.
 * 
 * <p><b>Security:</b> Passwords are never stored in plain text.
 * This class produces the same SHA-256 hex output as MySQL's
 * SHA2() function, ensuring compatibility between Java
 * authentication and database-stored hashes.</p>
 * 
 * <p><b>Usage:</b></p>
 * <pre>
 * String hash = PasswordHashGenerator.generateHash("admin123");
 * // Matches: SELECT SHA2('admin123', 256) in MySQL
 * </pre>
 * 
 * @author Dayani Samaraweera
 * @version 1.0
 */
public final class PasswordHashGenerator {

    /** Logger for this utility class */
    private static final Logger HASH_LOGGER =
            Logger.getLogger(PasswordHashGenerator.class.getName());

    /**
     * Private constructor prevents instantiation.
     * This is a utility class with only static methods.
     */
    private PasswordHashGenerator() {
        throw new UnsupportedOperationException(
                "PasswordHashGenerator is a utility class and cannot be instantiated");
    }

    /**
     * Generates a SHA-256 hash of the given plain text password.
     * The output is a 64-character lowercase hexadecimal string,
     * identical to MySQL's SHA2(password, 256) function output.
     *
     * @param plainTextPassword the password to hash
     * @return the SHA-256 hex string, or null if hashing fails
     */
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

    /**
     * Verifies whether a plain text password matches a stored hash.
     *
     * @param plainTextPassword the password to verify
     * @param storedHash the stored SHA-256 hash to compare against
     * @return true if the password matches the hash
     */
    public static boolean verifyPassword(String plainTextPassword, String storedHash) {

        if (plainTextPassword == null || storedHash == null) {
            return false;
        }

        String computedHash = generateHash(plainTextPassword);
        return storedHash.equals(computedHash);
    }
}