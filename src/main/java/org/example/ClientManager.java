package org.example;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientManager {
    private final Map<String, PrintWriter> clients = new ConcurrentHashMap<>();

    public synchronized void addClient(String username, PrintWriter out) {
        clients.put(username, out);
        broadcastMessage("SERVER: " + username + " has joined the chat!");
    }

    public synchronized void removeClient(PrintWriter out) {
        String key = clients.entrySet().stream().filter(entry -> out.equals(entry.getValue())).map(Map.Entry::getKey).findFirst().orElse(null);
        if (key != null) {
            clients.remove(key);
            broadcastMessage("SERVER: " + key + " has left the chat.");
        }
    }

    public synchronized void broadcastMessage(String message) {
        for (PrintWriter writer : clients.values()) {
            writer.println(message);
        }
    }

    public void closeAllConnections() {
        for (PrintWriter writer : clients.values()) {
            writer.close();
        }
    }
}
