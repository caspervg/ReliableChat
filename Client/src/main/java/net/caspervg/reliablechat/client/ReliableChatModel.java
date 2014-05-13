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

package net.caspervg.reliablechat.client;

import net.caspervg.reliablechat.client.connection.ServerConnection;
import net.caspervg.reliablechat.client.core.Model;
import net.caspervg.reliablechat.client.handler.MessageHandler;
import net.caspervg.reliablechat.protocol.ChatMessage;

import java.util.List;
import java.util.Set;

public class ReliableChatModel extends Model {

    private ServerConnection connection;
    private String username;
    private Set<String> onlineUsers;
    private String currentMessage;
    private List<String> currentRecipient;
    private List<ChatMessage> messages;

    public ReliableChatModel() {
        super();
        MessageHandler.setModel(this);
    }

    public ServerConnection getConnection() {
        return connection;
    }

    public void setConnection(ServerConnection connection) {
        if (this.connection == null || connection == null || connection != this.connection) {
            this.connection = connection;
            fireInvalidationEvent();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (this.username == null || username == null || !username.equals(this.username)) {
            this.username = username;
            fireInvalidationEvent();
        }
    }

    public Set<String> getOnlineUsers() {
        return onlineUsers;
    }

    public void setOnlineUsers(Set<String> onlineUsers) {
        if (this.onlineUsers == null || onlineUsers == null || !onlineUsers.equals(this.onlineUsers)) {
            this.onlineUsers = onlineUsers;
            fireInvalidationEvent();
        }
    }

    public String getCurrentMessage() {
        return currentMessage;
    }

    public void setCurrentMessage(String currentMessage) {
        if (this.currentMessage == null || currentMessage == null || !currentMessage.equals(this.currentMessage)) {
            this.currentMessage = currentMessage;
            fireInvalidationEvent();
        }
    }

    public List<String> getCurrentRecipient() {
        return currentRecipient;
    }

    public void setCurrentRecipient(List<String> currentRecipient) {
        if (this.currentRecipient == null || currentRecipient == null || !currentRecipient.equals(this.currentRecipient)) {
            this.currentRecipient = currentRecipient;
            fireInvalidationEvent();
        }
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
        fireInvalidationEvent();
    }
}
