package Crypto;
import java.security.*;
import java.security.spec.*;
import javax.crypto.Cipher;

public class RSAKeyManager {
    private static final int KEY_SIZE = 2048;
    private KeyPair keyPair;

    public RSAKeyManager() throws NoSuchAlgorithmException {
        generateKeyPair();
    }

    private void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(KEY_SIZE, SecureRandom.getInstanceStrong());
        this.keyPair = keyGen.generateKeyPair();
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

    // Encrypt AES key with RSA public key
    public byte[] encryptAESKey(byte[] aesKey, PublicKey publicKey)
            throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(aesKey);
    }

    // Decrypt AES key with RSA private key
    public byte[] decryptAESKey(byte[] encryptedKey, PrivateKey privateKey)
            throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedKey);
    }
}
