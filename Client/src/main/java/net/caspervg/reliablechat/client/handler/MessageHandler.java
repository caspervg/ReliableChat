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

package net.caspervg.reliablechat.client.handler;

import net.caspervg.reliablechat.client.ReliableChatModel;
import net.caspervg.reliablechat.protocol.CallMessage;
import net.caspervg.reliablechat.protocol.CallType;
import net.caspervg.reliablechat.protocol.ChatMessage;
import net.caspervg.reliablechat.protocol.InfoMessage;
import org.controlsfx.dialog.Dialogs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MessageHandler {

    private static ReliableChatModel model = null;

    public static void setModel(ReliableChatModel model) {
        MessageHandler.model = model;
    }

    public static void handleChat(ChatMessage msg) {
        List<ChatMessage> messages = model.getMessages();
        if (messages == null) {
            messages = new ArrayList<>();
        }

        messages.add(msg);

        model.setMessages(messages);
    }

    public static void handleCall(CallMessage msg) {
        switch (msg.getCallType()) {
            case ALREADY_ONLINE:
            case NOT_ONLINE:
                model.setUsername(null);
                model.setConnection(null);
            case RECIPIENT_NOT_EXIST:
                CallType type = msg.getCallType();
                Dialogs.create()
                        .title("Something went wrong..")
                        .message(String.format("Error %d: %s", type.getCode(), type.getMessage()))
                        .nativeTitleBar()
                        .showError();
        }
    }

    @SuppressWarnings("unchecked")
    public static void handleInfo(InfoMessage msg) {
        switch (msg.getCallType()) {
            case USER_LOGGED_IN:
            case USER_LOGGED_OUT:
                model.setOnlineUsers(new HashSet<>((List<String>) msg.getPayload()));
                break;
            default:
                // Do nothing
        }
    }
}
