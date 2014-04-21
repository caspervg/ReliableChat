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


import connection.ServerConnection;
import log.ReliableLogger;
import net.caspervg.reliablechat.protocol.ChatMessage;
import net.caspervg.reliablechat.protocol.LoginMessage;
import net.caspervg.reliablechat.protocol.LogoutMessage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;

public class ReliableChatClient {

    private static final Properties properties = new Properties();
    /**
     * Temporary method to test the server
     * @param args
     */
    public static void main(String[] args) {

        try {
            properties.load(ClassLoader.getSystemResourceAsStream("config.properties"));

            ReliableLogger.setLevel(Level.parse(properties.getProperty("reliablechat.client.log_level")));
        } catch (IOException e) {
            ReliableLogger.log(Level.SEVERE, "Could not load the configuration file", e);
            System.exit(1);
        }

        try {
            int port = Integer.parseInt(properties.getProperty("reliablechat.client.port"));
            Socket socket = new Socket(InetAddress.getLocalHost(), port);

            System.out.println("Enter your desired username:");
            Scanner reader = new Scanner(new InputStreamReader(System.in));

            String username = reader.nextLine();

            ServerConnection conn = new ServerConnection(socket);

            conn.sendMessage(new LoginMessage(username));

            Thread serverConnection = new Thread(conn);
            serverConnection.start();

            System.out.println("You have been connected. Send messages now..");
            while (true) {
                String msg = reader.nextLine();

                if (msg.equalsIgnoreCase("stop")) {
                    break;
                }

                String[] msgParts = msg.split(":");
                ChatMessage chatMessage = new ChatMessage(username, msgParts[0], msgParts[1]);
                conn.sendMessage(chatMessage);
            }

            conn.sendMessage(new LogoutMessage(username));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
