package src;
import java.io.*;
import java.net.*;

public class Server {
    private static final int PORT = 12345;
    private static final int MAX_CLIENTS = 8;
    public static ClientHandler[] clients = new ClientHandler[MAX_CLIENTS];  

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Waiting for clients...");

            // Start the server input handler in background
            new Thread(new Runnable() {
                public void run() {
                    handleServerInput();
                }
            }).start();

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected.");

                // Find free slot
                int index = -1;
                for (int i = 0; i < MAX_CLIENTS; i++) {
                    if (clients[i] == null) {
                        index = i;
                        break;
                    }
                }

                if (index != -1) {
                    ClientHandler clientHandler = new ClientHandler(socket, index);
                    clients[index] = clientHandler;
                    new Thread(clientHandler).start();
                } else {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("Server is full.");
                    socket.close();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void handleServerInput() {
        try {
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(System.in));
            String command;

            while (true) {
                command = serverInput.readLine();

                if (command.equals("/list")) {
                    System.out.println("Connected users:");
                    for (int i = 0; i < MAX_CLIENTS; i++) {
                        if (clients[i] != null && clients[i].displayName != null) {
                            System.out.println("- " + clients[i].displayName);
                        }
                    }
                } else if (command.startsWith("/@ban ")) {
                    String[] parts = command.split(" ", 2);
                    if (parts.length == 2) {
                        String nameToBan = parts[1];
                        boolean found = false;
                        for (int i = 0; i < MAX_CLIENTS; i++) {
                            if (clients[i] != null && clients[i].displayName != null && clients[i].displayName.equals(nameToBan)) {
                                clients[i].out.println("You have been banned by the server.");
                                clients[i].socket.close();
                                clients[i] = null;
                                found = true;
                                System.out.println(nameToBan + " has been banned.");
                                break;
                            }
                        }
                        if (!found) {
                            System.out.println("User " + nameToBan + " not found.");
                        }
                    }
                } else {
                    // Broadcast server message to all clients
                    for (int i = 0; i < MAX_CLIENTS; i++) {
                        if (clients[i] != null) {
                            clients[i].out.println("Server: " + command);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

