package org.example;

import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MessageStore {
    private final ConcurrentHashMap<String, List<String>> messageMap = new ConcurrentHashMap<>();

    public synchronized void deliverMessages(String user, PrintWriter out) {
        List<String> messages = messageMap.remove(user);
        if (messages != null) {
            for (String message : messages) {
                out.println(message);
            }
        }
    }
}
