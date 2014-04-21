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

package connection;

import connection.handler.MessageHandler;
import log.ReliableLogger;
import net.caspervg.reliablechat.protocol.CallMessage;
import net.caspervg.reliablechat.protocol.ChatMessage;
import net.caspervg.reliablechat.protocol.InfoMessage;
import net.caspervg.reliablechat.protocol.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;

public class ServerConnection implements Runnable {

    private Socket server;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ServerConnection(Socket server) {
        this.server = server;
        try {
            this.in = new ObjectInputStream(server.getInputStream());
            this.out = new ObjectOutputStream(server.getOutputStream());
        } catch (IOException e) {
            ReliableLogger.log(Level.WARNING, "Could not retrieve the input or output streams for the server", e);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message msg = (Message) in.readObject();
                ReliableLogger.log(Level.INFO, "Received a message from the server");
                switch (msg.getMessageType()) {
                    case CHAT:
                        ChatMessage chatMessage = (ChatMessage) msg;
                        MessageHandler.handleChat(chatMessage);
                        break;
                    case CALL:
                        CallMessage callMessage = (CallMessage) msg;
                        MessageHandler.handleCall(callMessage);
                        break;
                    case INFO:
                        InfoMessage infoMessage = (InfoMessage) msg;
                        MessageHandler.handleInfo(infoMessage);
                    default:
                        // We do not have to do anything. The server shouldn't be sending LOGOUT or LOGIN messages!
                }
            }
        } catch (ClassNotFoundException e) {
            ReliableLogger.log(Level.SEVERE, "Could not find protocol classes", e);
        } catch (IOException e) {
            ReliableLogger.log(Level.WARNING, "Could not read message from server", e);
        }
    }

    public void sendMessage(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
            ReliableLogger.log(Level.INFO, "Sent a chat message to the server");
        } catch (IOException e) {
            ReliableLogger.log(Level.WARNING, "Failed to send message to the server");
        }
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public ObjectOutputStream getOut() {
        return out;
    }
}
