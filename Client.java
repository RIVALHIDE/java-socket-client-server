import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private JFrame frame;
    private JTextArea messageArea;
    private JTextField messageField;
    private JButton sendButton;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private String clientName;

    public Client() {
        // Prompt for Server IP and Client Name
        String serverIp = JOptionPane.showInputDialog(frame, "Enter Server IP Address:", "127.0.0.1");
        if (serverIp == null || serverIp.trim().isEmpty()) {
             System.exit(0);
        }

        clientName = JOptionPane.showInputDialog(frame, "Enter your name:", "Guest");
        if (clientName == null || clientName.trim().isEmpty()) {
            clientName = "Guest" + (int)(Math.random() * 1000);
        }

        // --- GUI Setup ---
        frame = new JFrame("Chat Client - " + clientName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);


        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(messageArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Send");
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (out != null) {
                    // This is a simple way to indicate disconnection.
                    // A more robust solution would be a specific "LOGOUT" message.
                    out.println(clientName + " is disconnecting.");
                }
                try {
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException ioException) {
                    // Handle error
                }
            }
        });


        frame.setVisible(true);

        // --- Networking ---
        try {
            socket = new Socket(serverIp, 9999);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send client name to server as the first message
            out.println(clientName);

            // Start a new thread to listen for messages from the server
            new Thread(new ServerListener()).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Could not connect to the server: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.trim().isEmpty()) {
            out.println(message);
            messageArea.append("You: " + message + "\n"); // Display own message
            messageField.setText("");
        }
    }

    // --- Inner class to listen for server messages ---
    class ServerListener implements Runnable {
        @Override
        public void run() {
            try {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    final String msg = serverMessage;
                    SwingUtilities.invokeLater(() -> messageArea.append(msg + "\n"));
                }
            } catch (IOException e) {
                 SwingUtilities.invokeLater(() -> messageArea.append("Connection to server lost.\n"));
            }
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
