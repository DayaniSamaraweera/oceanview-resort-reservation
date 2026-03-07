package com.oceanview.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class PasswordHashGeneratorTest {

    @Test
    public void testHashGeneratesCorrectLength() {
        String hash = PasswordHashGenerator
                .generateHash("admin123");
        assertNotNull("Hash should not be null", hash);
        assertEquals("SHA-256 must be 64 characters",
                64, hash.length());
    }

    @Test
    public void testSamePasswordProducesSameHash() {
        String hash1 = PasswordHashGenerator
                .generateHash("admin123");
        String hash2 = PasswordHashGenerator
                .generateHash("admin123");
        assertEquals("Same input must produce "
                + "identical hash", hash1, hash2);
    }

    @Test
    public void testNullPasswordReturnsNull() {
        String hash = PasswordHashGenerator
                .generateHash(null);
        assertNull("Null input should return null",
                hash);
    }

    @Test
    public void testEmptyPasswordReturnsNull() {
        String hash = PasswordHashGenerator
                .generateHash("");
        assertNull("Empty input should return null",
                hash);
    }

    @Test
    public void testVerifyPasswordCorrect() {
        String hash = PasswordHashGenerator
                .generateHash("testPassword");
        boolean result = PasswordHashGenerator
                .verifyPassword("testPassword", hash);
        assertTrue("Correct password should verify",
                result);
    }

    @Test
    public void testVerifyPasswordIncorrect() {
        String hash = PasswordHashGenerator
                .generateHash("testPassword");
        boolean result = PasswordHashGenerator
                .verifyPassword("wrongPassword", hash);
        assertFalse("Wrong password should fail",
                result);
    }

    @Test
    public void testDifferentPasswordsDifferentHashes() {
        String hash1 = PasswordHashGenerator
                .generateHash("password1");
        String hash2 = PasswordHashGenerator
                .generateHash("password2");
        assertNotEquals("Different passwords must "
                + "produce different hashes",
                hash1, hash2);
    }
}