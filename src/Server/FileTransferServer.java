package Server;

import Crypto.*;
import java.io.*;
import java.net.*;
import java.security.*;
import javax.swing.*;

public class FileTransferServer {
    private static final int PORT = 5000;
    private ServerSocket serverSocket;
    private HybridCrypto crypto;
    private JTextArea logArea;

    public FileTransferServer(JTextArea logArea) throws Exception {
        this.serverSocket = new ServerSocket(PORT);
        this.crypto = new HybridCrypto();
        this.logArea = logArea;
        log("Server started on port " + PORT);
        log("RSA Key Pair generated");
    }

    public void start() {
        new Thread(() -> {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    log("Client connected: " + clientSocket.getInetAddress());

                    // Handle client in separate thread
                    new Thread(new ClientHandler(clientSocket)).start();

                } catch (IOException e) {
                    log("Error accepting connection: " + e.getMessage());
                }
            }
        }).start();
    }

    private class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                // Step 1: Send server's public key
                out.writeObject(crypto.getPublicKey());
                out.flush();
                log("Public key sent to client");

                // Step 2: Receive client's public key
                PublicKey clientPublicKey = (PublicKey) in.readObject();
                log("Client's public key received");

                // Step 3: Receive encrypted package
                HybridCrypto.EncryptedPackage encPackage =
                        (HybridCrypto.EncryptedPackage) in.readObject();
                log("Encrypted file package received");

                // Step 4: Receive filename
                String filename = (String) in.readObject();
                log("Filename received: " + filename);

                // Step 5: Decrypt file
                byte[] decryptedFile = crypto.decryptFile(encPackage);
                log("File decrypted successfully");

                // Step 6: Save file
                File outputFile = new File("server_received/" + filename);
                outputFile.getParentFile().mkdirs();

                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    fos.write(decryptedFile);
                }

                log("File saved: " + outputFile.getAbsolutePath());
                log("File size: " + decryptedFile.length + " bytes");

                // Send acknowledgment
                out.writeObject("SUCCESS");
                out.flush();

                in.close();
                out.close();
                socket.close();

            } catch (Exception e) {
                log("Error handling client: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + java.time.LocalTime.now() + "] " + message + "\n");
        });
    }
}
