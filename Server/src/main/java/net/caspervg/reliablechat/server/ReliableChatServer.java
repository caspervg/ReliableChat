package net.caspervg.reliablechat.server;

/*
 * Copyright (c) 2014 Casper Van Gheluwe
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */

import net.caspervg.reliablechat.server.connection.ClientConnection;
import net.caspervg.reliablechat.server.log.ReliableLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReliableChatServer {

    private static final Properties properties = new Properties();
    private static Map<String, ClientConnection> activeConnections = new HashMap<>();

    public static void main(String[] args) {

        try {
            properties.load(ClassLoader.getSystemResourceAsStream("config.properties"));

            ReliableLogger.setLevel(Level.parse(properties.getProperty("reliablechat.server.log_level")));
        } catch (IOException e) {
            ReliableLogger.log(Level.SEVERE, "Could not load the configuration file", e);
            System.exit(1);
        }

        int serverPort = -1;
        ServerSocket serverSocket = null;

        try {
            serverPort = Integer.parseInt(properties.getProperty("reliablechat.server.port"));

            if (serverPort < 0) {
                ReliableLogger.log(Level.SEVERE, "Could not find port to listen on in the configuration, or it is invalid");
                System.exit(1);
            }

            serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            ReliableLogger.log(Level.SEVERE, "Could not initialize server on port " + serverPort, e);
            System.exit(1);
        }

        Socket clientSocket = null;
        while (true) {
            try {
                ReliableLogger.log(Level.INFO, "Waiting for a client");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                ReliableLogger.log(Level.WARNING, "Failed to accept a client", e);
            }

            Thread clientThread = new Thread(new ClientConnection(clientSocket));
            clientThread.start();
        }
    }

    public synchronized static Map<String, ClientConnection> getActiveConnections() {
        return activeConnections;
    }
}
