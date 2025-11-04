package Server;

import javax.crypto.SecretKey;
import java.io.File;
import java.nio.file.Files;

public class DecryptionUtil {
    // Decrypts encrypted bytes and writes to output file
    public static void decryptToFile(byte[] encryptedData, SecretKey aesKey, byte[] iv, File outputFile) throws Exception {
        byte[] plainData = Crypto.AESEncryption.decryptFile(encryptedData, aesKey, iv);
        Files.write(outputFile.toPath(), plainData);
    }
}
