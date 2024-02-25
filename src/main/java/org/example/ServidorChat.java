package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ServidorChat {
    private static final int PORT = 12345;
    private final MessageStore messageStore = new MessageStore();
    private final ClientManager clientManager = new ClientManager();

    public static void main(String[] args) {
        new ServidorChat().startServer();
    }

    public void startServer() {
        System.out.println("Chat Server starting...");
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat Server started on port " + PORT);

            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdownServer));

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, messageStore, clientManager);
                executor.execute(clientHandler);
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
        } finally {
            executor.shutdownNow();
        }
    }

    private void shutdownServer() {
        System.out.println("Shutting down the chat server...");
        clientManager.broadcastMessage("Server is shutting down!");
        clientManager.closeAllConnections();
    }
}
