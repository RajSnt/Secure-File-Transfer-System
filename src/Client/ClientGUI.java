package Client;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ClientGUI extends JFrame {
    private JTextField serverAddressField;
    private JTextField serverPortField;
    private JTextField selectedFileField;
    private JButton browseButton;
    private JButton sendButton;
    private JProgressBar progressBar;
    private JTextArea logArea;
    private File selectedFile;

    public ClientGUI() {
        setTitle("Secure File Transfer - Client");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Top panel - Server connection
        JPanel topPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Server Connection"));

        topPanel.add(new JLabel("Server Address:"));
        serverAddressField = new JTextField("localhost");
        topPanel.add(serverAddressField);

        topPanel.add(new JLabel("Server Port:"));
        serverPortField = new JTextField("5000");
        topPanel.add(serverPortField);

        add(topPanel, BorderLayout.NORTH);

        // Middle panel - File selection
        JPanel middlePanel = new JPanel(new BorderLayout(5, 5));
        middlePanel.setBorder(BorderFactory.createTitledBorder("File Selection"));

        selectedFileField = new JTextField();
        selectedFileField.setEditable(false);
        middlePanel.add(selectedFileField, BorderLayout.CENTER);

        browseButton = new JButton("Browse...");
        browseButton.addActionListener(e -> browseFile());
        middlePanel.add(browseButton, BorderLayout.EAST);

        add(middlePanel, BorderLayout.CENTER);

        // Bottom panel - Progress and controls
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));

        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("Ready");
        bottomPanel.add(progressBar, BorderLayout.NORTH);

        // Log area
        logArea = new JTextArea(10, 50);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Transfer Log"));
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        // Send button
        sendButton = new JButton("Send File");
        sendButton.setEnabled(false);
        sendButton.addActionListener(e -> sendFile());
        bottomPanel.add(sendButton, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void browseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            selectedFileField.setText(selectedFile.getAbsolutePath());
            sendButton.setEnabled(true);
            logArea.append("File selected: " + selectedFile.getName() + "\n");
        }
    }

    private void sendFile() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Please select a file first!");
            return;
        }

        sendButton.setEnabled(false);
        browseButton.setEnabled(false);
        progressBar.setValue(0);
        progressBar.setString("Initializing...");

        new Thread(() -> {
            try {
                String serverAddr = serverAddressField.getText();
                int port = Integer.parseInt(serverPortField.getText());

                FileTransferClient client = new FileTransferClient(serverAddr, port);
                client.sendFile(selectedFile, progressBar, logArea);

                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "File sent successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Error: " + e.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                    progressBar.setString("Error");
                });
                logArea.append("ERROR: " + e.getMessage() + "\n");
                e.printStackTrace();
            } finally {
                SwingUtilities.invokeLater(() -> {
                    sendButton.setEnabled(true);
                    browseButton.setEnabled(true);
                });
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientGUI());
    }
}

