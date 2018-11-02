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

    /**
     *
     * @return Returns a random byte array with the same length as the hash
     */
    public static byte[] getRandomSalt() {
        byte[] salt = new byte[HASH_LENGTH/8];
        rand.nextBytes(salt);
        return salt;
    }

    /**
     * @see HashUtils#hash(char[], byte[])
     */
    public static byte[] hash(String cleartext, byte[] salt) {
        return hash(cleartext.toCharArray(), salt);
    }

    /**
     *
     * @param cleartext The text to gasg
     * @param salt The salt to use when hashing
     * @return A byte array which is the result of hashing the cleartext and the key
     */
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

    /**
     * @see HashUtils#verify(char[], byte[], byte[])
     * **/
    public static boolean verify(String cleartext, String hash, String salt) {
        return verify(cleartext.toCharArray(), fromHex(hash), fromHex(salt));
    }

    /**
     * Calculates a hash for cleartext and salt and compares it to a given hash
     * @param cleartext The cleartext to use when calculating the hash
     * @param hash The hash to compare to
     * @param salt The salt to use when calculating the hash
     * @return true if the hash of cleartext+salt equals the given hash
     */
    public static boolean verify(char[] cleartext, byte[] hash, byte[] salt) {
        return Arrays.equals(hash(cleartext, salt), hash);
    }

    /**
     * Converts a byte array to a hex string
     * @param data The bytes to convert
     * @return A string with the hex value of the byte array
     */
    public static String toHex(byte[] data) {
        char[] hex = new char[data.length * 2];

        for (int i = 0; i < data.length; i++) {
            int v = data[i] & 0xFF;
            hex[2 * i] = HEX_CHARS[v >> 4];
            hex[2 * i + 1] = HEX_CHARS[v & 0x0F];
        }

        return new String(hex);
    }

    /**
     * Converts a hex string to a byte array
     * @param hex The hex string to convert
     * @return A byte array with the value of the hex string
     */
    public static byte[] fromHex(String hex) {
        byte[] data = new byte[hex.length() / 2];

        for (int i = 0; i < hex.length(); i+=2) {
            data[i/2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }

        return data;
    }
}
