package Client;
import Crypto.*;
import java.io.*;
import java.net.*;
import java.security.*;
import javax.swing.*;

public class FileTransferClient {
    private String serverAddress;
    private int serverPort;
    private HybridCrypto crypto;

    public FileTransferClient(String serverAddress, int serverPort) throws Exception {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.crypto = new HybridCrypto();
    }

    public void sendFile(File file, JProgressBar progressBar, JTextArea logArea)
            throws Exception {
        Socket socket = new Socket(serverAddress, serverPort);
        log(logArea, "Connected to server: " + serverAddress);

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        // Step 1: Receive server's public key
        PublicKey serverPublicKey = (PublicKey) in.readObject();
        log(logArea, "Server's public key received");

        // Step 2: Send client's public key
        out.writeObject(crypto.getPublicKey());
        out.flush();
        log(logArea, "Public key sent to server");

        // Step 3: Read file
        byte[] fileData = readFile(file, progressBar);
        log(logArea, "File read: " + file.getName() + " (" + fileData.length + " bytes)");

        // Step 4: Encrypt file
        progressBar.setString("Encrypting...");
        HybridCrypto.EncryptedPackage encPackage =
                crypto.encryptFile(fileData, serverPublicKey);
        log(logArea, "File encrypted with AES-256");

        // Step 5: Send encrypted package
        progressBar.setString("Sending...");
        out.writeObject(encPackage);
        out.flush();
        log(logArea, "Encrypted package sent");

        // Step 6: Send filename
        out.writeObject(file.getName());
        out.flush();

        // Step 7: Wait for acknowledgment
        String response = (String) in.readObject();
        log(logArea, "Server response: " + response);

        progressBar.setValue(100);
        progressBar.setString("Transfer Complete!");

        in.close();
        out.close();
        socket.close();
    }

    private byte[] readFile(File file, JProgressBar progressBar) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            byte[] buffer = new byte[8192]; // 8KB buffer
            int bytesRead;
            long totalRead = 0;
            long fileSize = file.length();

            while ((bytesRead = bis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
                totalRead += bytesRead;

                int progress = (int) ((totalRead * 50) / fileSize); // 50% for reading
                progressBar.setValue(progress);
                progressBar.setString("Reading: " + progress + "%");
            }
        }
        return baos.toByteArray();
    }

    private void log(JTextArea logArea, String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + java.time.LocalTime.now() + "] " + message + "\n");
        });
    }
}
