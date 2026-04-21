package com.example.java_lms_group_01.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordUtil {

    // Security Configuration =>
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256"; // hashing algorithm
    private static final int ITERATIONS = 65536;    // hash running times
    private static final int KEY_LENGTH = 256;      // output hash length (bits)
    private static final int SALT_LENGTH = 16;      // to prevent Same passwords having same hash

    private PasswordUtil() {
    }

    // Hash password function
    public static String hashPassword(String rawPassword) {
        try {
            // Generate Each password gets unique random bytes
            byte[] salt = new byte[SALT_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);

            // Hash Password
            byte[] hash = pbkdf2(rawPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

            // Store as String return password
            return "pbkdf2$" + ITERATIONS + "$"
                    + Base64.getEncoder().encodeToString(salt) + "$"
                    + Base64.getEncoder().encodeToString(hash);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to hash password", e);
        }
    }

    // verify the password (check user enter password and hash password are match or not return boolean value)
    public static boolean matches(String rawPassword, String storedPassword) {

        // check is passed values are NULL OR NOT
        if (rawPassword == null || storedPassword == null) {
            return false;
        }

        try {

            // if password are start with our encoding algorithm
            if (storedPassword.startsWith("pbkdf2$")) {

                // Split Stored Password [pbkdf2  iterations  salt  hash] parts to
                String[] parts = storedPassword.split("\\$");

                // check have 4 parts
                if (parts.length != 4) {
                    return false;
                }

                // Converts back to original byte format
                int iterations = Integer.parseInt(parts[1]);
                byte[] salt = Base64.getDecoder().decode(parts[2]);
                byte[] expectedHash = Base64.getDecoder().decode(parts[3]);

                // Recalculate Hash
                byte[] actualHash = pbkdf2(
                        rawPassword.toCharArray(),
                        salt,
                        iterations,
                        expectedHash.length * 8
                );

                // check the hash password and store hash password are same [return boolean val]
                return constantTimeEquals(expectedHash, actualHash);
            }

        } catch (Exception e) {
            return false;
        }

        // in here choose passwords are equal [ Based on the with plain password check ]
        return rawPassword.equals(storedPassword);
    }

    // this method is used to generate Hash Password
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) throws Exception {
        // combine [Password + salt + iterations ]
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        // Gets PBKDF2 algorithm
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);

        // Returns final hash bytes
        return factory.generateSecret(spec).getEncoded();
    }

    // Safely compare two hashes
    private static boolean constantTimeEquals(byte[] a, byte[] b) {

        // 1. If either is null → not equal
        if (a == null || b == null) {
            return false;
        }

        // 2. If lengths are different → not equal
        if (a.length != b.length) {
            return false;
        }

        // 3. Compare each byte
        boolean isEqual = true;

        for (int i = 0; i < a.length; i++) {

            // if any byte is different, mark as false
            if (a[i] != b[i]) {
                isEqual = false;
            }
        }

        return isEqual;
    }
}