package Crypto;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

public class HybridCrypto {
    private RSAKeyManager rsaManager;

    public HybridCrypto() throws NoSuchAlgorithmException {
        this.rsaManager = new RSAKeyManager();
    }

    public PublicKey getPublicKey() {
        return rsaManager.getPublicKey();
    }

    public PrivateKey getPrivateKey() {
        return rsaManager.getPrivateKey();
    }

    // Encrypt file with hybrid approach
    public EncryptedPackage encryptFile(byte[] fileData, PublicKey recipientPublicKey)
            throws Exception {
        // Generate AES key and IV
        SecretKey aesKey = AESEncryption.generateAESKey();
        byte[] iv = AESEncryption.generateIV();

        // Encrypt file with AES
        byte[] encryptedFile = AESEncryption.encryptFile(fileData, aesKey, iv);

        // Encrypt AES key with recipient's RSA public key
        byte[] encryptedAESKey = rsaManager.encryptAESKey(
                aesKey.getEncoded(), recipientPublicKey);

        return new EncryptedPackage(encryptedFile, encryptedAESKey, iv);
    }

    // Decrypt file with hybrid approach
    public byte[] decryptFile(EncryptedPackage encPackage) throws Exception {
        // Decrypt AES key with private key
        byte[] aesKeyBytes = rsaManager.decryptAESKey(
                encPackage.getEncryptedAESKey(), getPrivateKey());

        SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

        // Decrypt file with AES key
        return AESEncryption.decryptFile(
                encPackage.getEncryptedFile(), aesKey, encPackage.getIv());
    }

    // Data class to hold encrypted package
    public static class EncryptedPackage implements java.io.Serializable {
        private byte[] encryptedFile;
        private byte[] encryptedAESKey;
        private byte[] iv;

        public EncryptedPackage(byte[] encryptedFile, byte[] encryptedAESKey, byte[] iv) {
            this.encryptedFile = encryptedFile;
            this.encryptedAESKey = encryptedAESKey;
            this.iv = iv;
        }

        public byte[] getEncryptedFile() { return encryptedFile; }
        public byte[] getEncryptedAESKey() { return encryptedAESKey; }
        public byte[] getIv() { return iv; }
    }
}
