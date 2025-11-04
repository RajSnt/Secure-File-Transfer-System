package Crypto;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.io.*;

public class AESEncryption {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 256;
    private static final int IV_SIZE = 16;

    // Generate random AES key
    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(KEY_SIZE, SecureRandom.getInstanceStrong());
        return keyGenerator.generateKey();
    }

    // Generate random IV
    public static byte[] generateIV() throws NoSuchAlgorithmException {
        byte[] iv = new byte[IV_SIZE];
        SecureRandom.getInstanceStrong().nextBytes(iv);
        return iv;
    }

    // Encrypt file data
    public static byte[] encryptFile(byte[] fileData, SecretKey secretKey, byte[] iv)
            throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        return cipher.doFinal(fileData);
    }

    // Decrypt file data
    public static byte[] decryptFile(byte[] encryptedData, SecretKey secretKey, byte[] iv)
            throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        return cipher.doFinal(encryptedData);
    }
}
