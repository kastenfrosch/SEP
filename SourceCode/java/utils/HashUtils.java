package utils;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

public class HashUtils {

    private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
    private static final int HASH_ITERATIONS = 10000;
    private static final int HASH_LENGTH = 128;
    private static final SecureRandom rand = new SecureRandom();

    public static byte[] getRandomSalt() {
        byte[] salt = new byte[HASH_LENGTH/8];
        rand.nextBytes(salt);
        return salt;
    }

    public static byte[] hash(String cleartext, byte[] salt) {
        return hash(cleartext.toCharArray(), salt);
    }

    public static byte[] hash(char[] cleartext, byte[] salt) {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec keySpec = new PBEKeySpec(cleartext, salt, HASH_ITERATIONS, HASH_LENGTH);
            SecretKey key = skf.generateSecret(keySpec);
            return key.getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static boolean verify(String cleartext, String hash, String salt) {
        return verify(cleartext.toCharArray(), fromHex(hash), fromHex(salt));
    }

    public static boolean verify(char[] cleartext, byte[] hash, byte[] salt) {
        return Arrays.equals(hash(cleartext, salt), hash);
    }

    public static String toHex(byte[] data) {
        char[] hex = new char[data.length * 2];

        for (int i = 0; i < data.length; i++) {
            int v = data[i] & 0xFF;
            hex[2 * i] = HEX_CHARS[v >> 4];
            hex[2 * i + 1] = HEX_CHARS[v & 0x0F];
        }

        return new String(hex);
    }

    public static byte[] fromHex(String hex) {
        byte[] data = new byte[hex.length() / 2];

        for (int i = 0; i < hex.length(); i+=2) {
            data[i/2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }

        return data;
    }
}
