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

package net.caspervg.reliablechat.client.ui;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import net.caspervg.reliablechat.client.ReliableChatModel;
import net.caspervg.reliablechat.client.connection.ServerConnection;
import net.caspervg.reliablechat.protocol.LogoutMessage;

public class LogoutMenuItem extends MenuItem implements InvalidationListener, EventHandler<ActionEvent> {

    private ReliableChatModel model;

    public LogoutMenuItem() {
        super();
        setOnAction(this);
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        ServerConnection conn = model.getConnection();
        String username = model.getUsername();

        if (conn != null && username != null) {
            conn.sendMessage(new LogoutMessage(username));

            model.setDisabled(true);
            model.setUsername(null);
            model.setCurrentMessage(null);
            model.setOnlineUsers(null);
            model.setCurrentRecipient(null);
            model.setMessages(null);
            model.setDisabled(false);
            model.setConnection(null);
        }
    }

    @Override
    public void invalidated(Observable observable) {
        if (model.getConnection() == null) {
            this.setDisable(true);
        } else {
            this.setDisable(false);
        }
    }

    public ReliableChatModel getModel() {
        return model;
    }

    public void setModel(ReliableChatModel model) {
        this.model = model;
        model.addListener(this);
    }
}
