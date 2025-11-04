package Server;
import javax.swing.*;
import java.awt.*;

public class ServerGUI extends JFrame {
    private JTextArea logArea;
    private JButton startButton;
    private JButton stopButton;
    private JLabel statusLabel;

    public ServerGUI() {
        setTitle("Secure File Transfer - Server");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Top panel - Status
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("Server Status"));

        statusLabel = new JLabel("Status: Stopped");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(statusLabel);

        add(topPanel, BorderLayout.NORTH);

        // Center panel - Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Server Log"));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel - Controls
        JPanel bottomPanel = new JPanel(new FlowLayout());

        startButton = new JButton("Start Server");
        startButton.addActionListener(e -> startServer());
        bottomPanel.add(startButton);

        stopButton = new JButton("Stop Server");
        stopButton.setEnabled(false);
        bottomPanel.add(stopButton);

        add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void startServer() {
        try {
            FileTransferServer server = new FileTransferServer(logArea);
            server.start();

            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            statusLabel.setText("Status: Running on port 5000");
            statusLabel.setForeground(Color.GREEN);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error starting server: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ServerGUI());
    }
}
