package Server;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import Crypto.AESEncryption;
import Crypto.RSAKeyManager;

public class ServerGUI extends JFrame {
    private JTextField portField;
    private JButton startButton;
    private JLabel statusLabel;
    private JTextArea logArea;
    private KeyPair rsaKeyPair;

    public ServerGUI() {
        setTitle("Secure File Transfer - Server (Hybrid)");
        setSize(480, 320);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel portLabel = new JLabel("Port");
        portLabel.setBounds(30, 30, 50, 25);
        add(portLabel);

        portField = new JTextField("5000");
        portField.setBounds(90, 30, 70, 25);
        add(portField);

        startButton = new JButton("Start Server");
        startButton.setBounds(180, 30, 120, 25);
        add(startButton);

        statusLabel = new JLabel("Status: Server is stopped");
        statusLabel.setBounds(30, 60, 370, 25);
        add(statusLabel);

        logArea = new JTextArea();
        logArea.setBounds(30, 90, 400, 110);
        logArea.setEditable(false);
        add(logArea);

        startButton.addActionListener(e -> startServerRoutine());

        setLocationRelativeTo(null);
        setVisible(true);

        // Generate RSA key pair on launch using your new RSAKeyManager code
        try {
            rsaKeyPair = RSAKeyManager.generateKeyPair();
            logArea.setText("RSA key pair generated.\n");
            System.out.println("RSA Public Key: " + Base64.getEncoder().encodeToString(rsaKeyPair.getPublic().getEncoded()));
            System.out.println("RSA Private Key: " + Base64.getEncoder().encodeToString(rsaKeyPair.getPrivate().getEncoded()));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "RSA keypair generation failed: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private void startServerRoutine() {
        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException ex) {
            statusLabel.setText("Invalid port number!");
            return;
        }
        startButton.setEnabled(false);
        statusLabel.setText("Status: Server is listening");
        logArea.append("Listening on port " + port + "\n");

        new Thread(() -> runServer(port)).start();
    }

    private void runServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket client = serverSocket.accept();
                SwingUtilities.invokeLater(() -> logArea.append("\nClient connected: " + client.getInetAddress()));
                new Thread(() -> receiveHybridFile(client)).start();
            }
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                logArea.append("\nServer error: " + e.getMessage());
                startButton.setEnabled(true);
                statusLabel.setText("Status: Server is stopped");
            });
        }
    }

    private void receiveHybridFile(Socket client) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());

            // 1. Send server public key to client
            out.writeObject(rsaKeyPair.getPublic());
            out.flush();

            // 2. Receive encrypted AES key, IV, file length and file bytes
            // Receive the filename from the client
            String fileName = (String) in.readObject();
            byte[] encryptedAesKey = (byte[]) in.readObject();
            byte[] iv = (byte[]) in.readObject();
            int encFileLen = in.readInt();
            byte[] encryptedFile = new byte[encFileLen];
            in.readFully(encryptedFile);

            // 3. Decrypt AES key using server private key
            byte[] aesKeyBytes = RSAKeyManager.decryptAESKey(encryptedAesKey, rsaKeyPair.getPrivate());
            javax.crypto.SecretKey aesKey = new javax.crypto.spec.SecretKeySpec(aesKeyBytes, "AES");

            // 4. Decrypt the file using AES key and IV
            byte[] decrypted = AESEncryption.decryptFile(encryptedFile, aesKey, iv);

            // 5. Save decrypted file
            File dir = new File("server_received");
            if (!dir.exists()) dir.mkdirs();
            File outFile = new File(dir, "decrypted_" + fileName);
            java.nio.file.Files.write(outFile.toPath(), decrypted);


            SwingUtilities.invokeLater(() -> logArea.append("\nSaved decrypted file: " + fileName
                    + " (" + decrypted.length + " bytes)"));

            in.close();
            out.close();
            client.close();
        } catch (Exception e) {
            SwingUtilities.invokeLater(() ->
                    logArea.append("\nDecryption failed: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServerGUI::new);
    }
}



