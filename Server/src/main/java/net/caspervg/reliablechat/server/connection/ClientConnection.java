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

package net.caspervg.reliablechat.server.connection;

import net.caspervg.reliablechat.protocol.ChatMessage;
import net.caspervg.reliablechat.protocol.LoginMessage;
import net.caspervg.reliablechat.protocol.LogoutMessage;
import net.caspervg.reliablechat.protocol.Message;
import net.caspervg.reliablechat.server.ReliableChatServer;
import net.caspervg.reliablechat.server.handler.ChatHandler;
import net.caspervg.reliablechat.server.log.ReliableLogger;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;

public class ClientConnection implements Runnable {

    private Socket client;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean stopped = false;

    public ClientConnection(Socket client) {
        this.client = client;

        try {
            this.in = new ObjectInputStream(client.getInputStream());
            this.out = new ObjectOutputStream(client.getOutputStream());
        } catch (IOException e) {
            ReliableLogger.log(Level.WARNING, "Could not retrieve input or output streams for a client", e);
        }
    }

    @Override
    public void run() {
        try {
            while (! stopped) {
                Message msg = (Message) in.readObject();
                ReliableLogger.log(Level.INFO, "Received a message from a client");
                switch (msg.getMessageType()) {
                    case LOGIN:
                        LoginMessage loginMessage = (LoginMessage) msg;
                        ReliableChatServer.getActiveConnections().put(loginMessage.getUsername(), this);
                        break;
                    case LOGOUT:
                        LogoutMessage logoutMessage = (LogoutMessage) msg;
                        ReliableChatServer.getActiveConnections().remove(logoutMessage.getUsername());
                        stopped = true;
                        break;
                    case CHAT:
                        ChatMessage chatMessage = (ChatMessage) msg;
                        ChatHandler.handle(chatMessage);
                        break;

                }
            }
        } catch (ClassNotFoundException e) {
            ReliableLogger.log(Level.SEVERE, "Could not find protocol classes", e);
        } catch (IOException e) {
            ReliableLogger.log(Level.WARNING, "Could not read message from client", e);
        }
    }

    public void sendMessage(Message msg) {
        try {
            out.writeObject(msg);
            ReliableLogger.log(Level.INFO, "Sent a message to a client");
        } catch (IOException e) {
            ReliableLogger.log(Level.WARNING, "Failed to send message to a client");
        }
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public ObjectOutputStream getOut() {
        return out;
    }
}
