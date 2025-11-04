package Client;

import javax.crypto.SecretKey;
import java.io.File;
import java.nio.file.Files;

public class EncryptionUtil {
    // Encrypts file contents using AES key and IV, returns encrypted bytes
    public static byte[] encryptFile(File inputFile, SecretKey aesKey, byte[] iv) throws Exception {
        byte[] fileData = Files.readAllBytes(inputFile.toPath());
        return Crypto.AESEncryption.encryptFile(fileData, aesKey, iv);
    }
}
