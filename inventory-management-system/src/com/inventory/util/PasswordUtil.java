package com.inventory.util;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil {
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_BYTES = 16;

    private PasswordUtil() {
    }

    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }
        byte[] salt = new byte[SALT_BYTES];
        new SecureRandom().nextBytes(salt);
        byte[] hash = pbkdf2(plainPassword.toCharArray(), salt);
        return "pbkdf2$" + Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPassword(String plainPassword, String storedPassword) {
        if (plainPassword == null || storedPassword == null || plainPassword.isEmpty() || storedPassword.isEmpty()) {
            return false;
        }

        if (!storedPassword.startsWith("pbkdf2$")) {
            return constantTimeEquals(plainPassword, storedPassword);
        }

        String[] parts = storedPassword.split("\\$");
        if (parts.length != 3) {
            return false;
        }

        byte[] salt;
        byte[] expectedHash;
        try {
            salt = Base64.getDecoder().decode(parts[1]);
            expectedHash = Base64.getDecoder().decode(parts[2]);
        } catch (IllegalArgumentException ex) {
            return false;
        }

        byte[] computed = pbkdf2(plainPassword.toCharArray(), salt);
        return constantTimeEquals(expectedHash, computed);
    }

    private static byte[] pbkdf2(char[] password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Failed to hash password", e);
        }
    }

    private static boolean constantTimeEquals(String left, String right) {
        return constantTimeEquals(left.getBytes(StandardCharsets.UTF_8), right.getBytes(StandardCharsets.UTF_8));
    }

    private static boolean constantTimeEquals(byte[] left, byte[] right) {
        if (left.length != right.length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < left.length; i++) {
            result |= left[i] ^ right[i];
        }
        return result == 0;
    }
}
