package src;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    public Socket socket;
    public BufferedReader in;
    public PrintWriter out;
    private int index;
    public String displayName;

    public ClientHandler(Socket socket, int index) {
        this.socket = socket;
        this.index = index;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            //out.println("Enter your display name:");This one was commented cause then this was displaying everytime you joined which is a bug
            displayName = in.readLine();
            System.out.println(displayName + " has joined the chat."); 
            broadcast(displayName + " has joined the chat.", null);

            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("/pm")) {
                    String[] parts = message.split(" ", 3);
                    if (parts.length < 3) {
                        out.println("Usage: /pm <username> <message>");
                    } else {
                        privateMessage(parts[1], parts[2]);
                    }
                } else {
                    
                    System.out.println(displayName + ": " + message); 

                    broadcast(displayName + ": " + message, this);
                } 
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + displayName);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
               
            }
            Server.clients[index] = null;
            System.out.println(displayName + " left the chat.");
            broadcast(displayName + " left the chat.", null);
        }
    }

    private void broadcast(String message, ClientHandler exclude) {
        for (int i = 0; i < Server.clients.length; i++) {
            if (Server.clients[i] != null) {
                Server.clients[i].out.println(message);
            }
        }
    }

    private void privateMessage(String recipientName, String message) {
        boolean found = false;
        for (int i = 0; i < Server.clients.length; i++) {
            if (Server.clients[i] != null && Server.clients[i].displayName != null && Server.clients[i].displayName.equals(recipientName)) {
                Server.clients[i].out.println("(Private) " + displayName + ": " + message);
                this.out.println("(Private to " + recipientName + ") " + message);

                // log private messages to server console
                System.out.println("(Private) " + displayName + " to " + recipientName + ": " + message);

                found = true;
                break;
            }
        }
        if (!found) {
            out.println("User " + recipientName + " not found.");
        }
    }
}
