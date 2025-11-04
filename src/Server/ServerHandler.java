package Server;

import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
import Crypto.*;

public class ServerHandler implements Runnable {
    private Socket clientSocket;
    private HybridCrypto crypto;

    public ServerHandler(Socket socket, HybridCrypto crypto) {
        this.clientSocket = socket;
        this.crypto = crypto;
    }

    @Override
    public void run() {
        try (
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())
        ) {
            // Step 1: Key exchange
            out.writeObject(crypto.getPublicKey());
            out.flush();
            PublicKey clientKey = (PublicKey) in.readObject();

            // Step 2: Receive encrypted package
            HybridCrypto.EncryptedPackage ePackage = (HybridCrypto.EncryptedPackage) in.readObject();
            String filename = (String) in.readObject();

            // Step 3: Decrypt file
            byte[] fileData = crypto.decryptFile(ePackage);
            File outputDir = new File("server_received");
            outputDir.mkdirs();
            File outputFile = new File(outputDir, filename);
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                fos.write(fileData);
            }

            out.writeObject("SUCCESS");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { clientSocket.close(); } catch (Exception ex) {}
        }
    }
}

