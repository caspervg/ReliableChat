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

package net.caspervg.reliablechat.server.handler;

import net.caspervg.reliablechat.protocol.*;
import net.caspervg.reliablechat.server.ReliableChatServer;
import net.caspervg.reliablechat.server.connection.ClientConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MessageHandler {

    public static void handleChat(ChatMessage msg) {
        String sendTo = msg.getTo();
        String sendFrom = msg.getFrom();
        ClientConnection clientConnection = ReliableChatServer.getActiveConnections().get(sendTo);

        if (clientConnection != null) {
            clientConnection.sendMessage(msg);

            clientConnection = ReliableChatServer.getActiveConnections().get(sendFrom);
            clientConnection.sendMessage(new CallMessage(CallType.MESSAGE_SENT));
            clientConnection = ReliableChatServer.getActiveConnections().get(sendFrom);
            clientConnection.sendMessage(msg);
        } else {
            clientConnection = ReliableChatServer.getActiveConnections().get(sendFrom);
            clientConnection.sendMessage(new CallMessage(CallType.RECIPIENT_NOT_EXIST));
        }
    }

    public static void handleLogin(LoginMessage msg, ClientConnection conn) {
        String username = msg.getUsername();
        Map<String, ClientConnection> connections = ReliableChatServer.getActiveConnections();

        if (! connections.containsKey(username)) {
            connections.put(username, conn);
            conn.sendMessage(new CallMessage(CallType.YOU_LOGGED_IN));

            List<String> onlineUsers = connections.keySet().stream().collect(Collectors.toList());
            for (ClientConnection cconn : connections.values()) {
                cconn.sendMessage(new InfoMessage(CallType.USER_LOGGED_IN, onlineUsers));
            }

        } else {
            conn.sendMessage(new CallMessage(CallType.ALREADY_ONLINE));
        }
    }

    public static void handleLogout(LogoutMessage msg, ClientConnection conn) {
        String username = msg.getUsername();
        Map<String, ClientConnection> connections = ReliableChatServer.getActiveConnections();

        if (connections.containsKey(username)) {
            connections.remove(username);
            conn.sendMessage(new CallMessage(CallType.YOU_LOGGED_OUT));
            conn.setStopped(true);

            List<String> onlineUsers = connections.keySet().stream().collect(Collectors.toList());
            for (ClientConnection cconn : connections.values()) {
                cconn.sendMessage(new InfoMessage(CallType.USER_LOGGED_OUT, onlineUsers));
            }
        } else {
            conn.sendMessage(new CallMessage(CallType.NOT_ONLINE));
        }
    }

}
