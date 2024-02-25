package org.example;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final MessageStore messageStore;
    private final ClientManager clientManager;
    private PrintWriter out;

    public ClientHandler(Socket socket, MessageStore messageStore, ClientManager clientManager) {
        this.clientSocket = socket;
        this.messageStore = messageStore;
        this.clientManager = clientManager;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out.println("Welcome to the chat server! Please enter your username:");
            String username = in.readLine().trim();
            clientManager.addClient(username, out);
            messageStore.deliverMessages(username, out);

            String inputLine;
            while ((inputLine = in.readLine()) != null && !clientSocket.isClosed()) {
                if ("bye".equalsIgnoreCase(inputLine.trim())) {
                    break;
                }
                clientManager.broadcastMessage(username + ": " + inputLine);
            }
        } catch (IOException e) {
            System.out.println("Exception in ClientHandler: " + e.getMessage());
        } finally {
            clientManager.removeClient(out);
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
