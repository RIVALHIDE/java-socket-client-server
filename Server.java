import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private JFrame frame;
    private JTextArea messageArea;
    private List<ClientHandler> clients = new ArrayList<>();

    public Server() {
        // --- GUI Setup ---
        frame = new JFrame("Chat Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(messageArea);
        frame.add(scrollPane, BorderLayout.CENTER);
        
        // Title Label
        JLabel titleLabel = new JLabel("Server Log", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(titleLabel, BorderLayout.NORTH);

        frame.setVisible(true);
        log("Server started.");

        // --- Server Logic ---
        try {
            ServerSocket serverSocket = new ServerSocket(9999);
            log("Waiting for clients on port 9999...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                log("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            log("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void log(String message) {
        // Append message to the server's text area
        SwingUtilities.invokeLater(() -> messageArea.append(message + "\n"));
    }

    private synchronized void broadcastMessage(String message, ClientHandler sender) {
        log("Broadcasting: " + message);
        for (ClientHandler client : clients) {
            // Send to all clients except the sender
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }
    
    private synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
        log("Client disconnected: " + client.socket.getInetAddress().getHostAddress());
    }

    // --- Inner class to handle each client connection ---
    class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String clientName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // First message from client is their name
                clientName = in.readLine();
                if(clientName == null || clientName.trim().isEmpty()){
                    clientName = "Anonymous@" + socket.getInetAddress().getHostAddress();
                }
                broadcastMessage(clientName + " has joined the chat.", this);
                log(clientName + " has set their name.");

                String message;
                while ((message = in.readLine()) != null) {
                    broadcastMessage(clientName + ": " + message, this);
                }

            } catch (IOException e) {
                // Client disconnected or an error occurred
            } finally {
                // Cleanup
                removeClient(this);
                broadcastMessage(clientName + " has left the chat.", null); // Broadcast to all remaining clients
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
