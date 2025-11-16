package Client;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import javax.crypto.SecretKey;
import Crypto.AESEncryption;
import Crypto.RSAKeyManager;
import java.security.PublicKey;
import java.util.Base64;

public class ClientGUI extends JFrame {
    private JTextField fileField;
    private JButton browseButton;
    private JComboBox<String> algoBox;
    private JTextField serverIpField, serverPortField;
    private JButton sendButton;
    private JProgressBar progressBar;
    private JTextArea statusArea;
    private File selectedFile;

    public ClientGUI() {
        setTitle("Secure File Transfer - Client (Hybrid)");
        setSize(480, 320);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel label1 = new JLabel("Select File");
        label1.setBounds(30, 20, 100, 25);
        add(label1);

        browseButton = new JButton("Browse");
        browseButton.setBounds(150, 20, 90, 25);
        add(browseButton);
        browseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedFile = chooser.getSelectedFile();
                fileField.setText(selectedFile.getAbsolutePath());
            }
        });

        fileField = new JTextField();
        fileField.setBounds(250, 20, 180, 25);
        fileField.setEditable(false);
        add(fileField);

        JLabel label2 = new JLabel("Encryption Algorithm");
        label2.setBounds(30, 55, 130, 25);
        add(label2);

        algoBox = new JComboBox<>(new String[]{"AES-256/RSA-2048"});
        algoBox.setBounds(180, 55, 150, 25);
        add(algoBox);

        JLabel label3 = new JLabel("Server IP");
        label3.setBounds(30, 90, 80, 25);
        add(label3);

        serverIpField = new JTextField("127.0.0.1");
        serverIpField.setBounds(150, 90, 80, 25);
        add(serverIpField);

        serverPortField = new JTextField("5000");
        serverPortField.setBounds(250, 90, 60, 25);
        add(serverPortField);

        sendButton = new JButton("Send File");
        sendButton.setBounds(150, 130, 120, 30);
        add(sendButton);

        progressBar = new JProgressBar();
        progressBar.setBounds(30, 170, 400, 25);
        add(progressBar);

        JLabel label4 = new JLabel("Status");
        label4.setBounds(30, 205, 80, 25);
        add(label4);

        statusArea = new JTextArea();
        statusArea.setBounds(30, 230, 400, 55);
        statusArea.setEditable(false);
        add(statusArea);

        setVisible(true);

        sendButton.addActionListener(e -> {
            if (selectedFile == null) {
                statusArea.setText("Select a file first!");
                return;
            }
            sendButton.setEnabled(false);
            progressBar.setValue(0);
            statusArea.setText("Encrypting and sending file...");

            new Thread(() -> {
                try {
                    String serverIp = serverIpField.getText().trim();
                    int serverPort = Integer.parseInt(serverPortField.getText().trim());
                    Socket socket = new Socket(serverIp, serverPort);

                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                    // 1. Receive server's RSA public key
                    PublicKey serverPubKey = (PublicKey) in.readObject();

                    // 2. Generate AES key/IV
                    SecretKey aesKey = AESEncryption.generateAESKey();
                    byte[] iv = AESEncryption.generateIV();
                    System.out.println("AES key (Base64): " + Base64.getEncoder().encodeToString(aesKey.getEncoded()));
                    System.out.println("IV (Base64): " + Base64.getEncoder().encodeToString(iv));

                    // 3. Encrypt file with AES key
                    byte[] fileBytes = java.nio.file.Files.readAllBytes(selectedFile.toPath());
                    byte[] encryptedFile = AESEncryption.encryptFile(fileBytes, aesKey, iv);

                    // 4. Encrypt AES key with server's RSA public key
                    byte[] encryptedAesKey = RSAKeyManager.encryptAESKey(aesKey.getEncoded(), serverPubKey);

                    // 5. Send: [encrypted AES key], [IV], [encrypted file size], [encrypted file]
                    out.writeObject(selectedFile.getName());
                    out.writeObject(encryptedAesKey);
                    out.writeObject(iv);
                    out.writeInt(encryptedFile.length);
                    out.flush();
                    out.write(encryptedFile);
                    out.flush();

                    SwingUtilities.invokeLater(() -> {
                        progressBar.setValue(100);
                        statusArea.setText("Hybrid transfer complete!\nFile securely sent.");
                        sendButton.setEnabled(true);
                    });

                    in.close(); out.close(); socket.close();

                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        statusArea.setText("Error: " + ex.getMessage());
                        sendButton.setEnabled(true);
                    });
                    ex.printStackTrace();
                }
            }).start();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientGUI::new);
    }
}

