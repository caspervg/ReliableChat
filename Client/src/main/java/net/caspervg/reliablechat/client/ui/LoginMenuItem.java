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
import net.caspervg.reliablechat.client.handler.MessageHandler;
import net.caspervg.reliablechat.client.log.ReliableLogger;
import net.caspervg.reliablechat.protocol.LoginMessage;
import org.controlsfx.dialog.Dialogs;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;


public class LoginMenuItem extends MenuItem implements InvalidationListener, EventHandler<ActionEvent> {

    private ReliableChatModel model;

    public LoginMenuItem() {
        super();
        setOnAction(this);
    }

    @Override
    public void invalidated(Observable observable) {
        if (model.getConnection() != null) {
            this.setDisable(true);
        } else {
            this.setDisable(false);
        }
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        String portString = Dialogs.create()
                .title("Connection port")
                .message("Please enter the port you wish to connect to")
                .nativeTitleBar()
                .showTextInput("1000");
        int port = Integer.parseInt(portString);

        String username = Dialogs.create()
                .title("Username")
                .message("Please enter the username you want to use")
                .nativeTitleBar()
                .showTextInput("");

        connectToServer(port, username);
    }

    private void connectToServer(int port, String username) {
        if (username != null && port > 0) {
            try {
                Socket socket = new Socket(InetAddress.getLocalHost(), port);
                ServerConnection conn = new ServerConnection(socket);

                conn.sendMessage(new LoginMessage(username));

                Thread serverConnection = new Thread(conn);
                serverConnection.start();

                model.setDisabled(true);
                model.setUsername(username);
                model.setCurrentMessage(null);
                model.setCurrentRecipient(null);
                model.setDisabled(false);
                model.setConnection(conn);

            } catch (Exception e) {
                ReliableLogger.log(Level.SEVERE, "Could not connect to the server", e);
            }
        } else {
            Dialogs.create()
                    .title("Invalid data")
                    .nativeTitleBar()
                    .message("Please enter a valid username and port number")
                    .showError();

            ReliableLogger.log(Level.WARNING, "Attempted to connect to the server without valid username or port");
        }
    }

    public ReliableChatModel getModel() {
        return model;
    }

    public void setModel(ReliableChatModel model) {
        this.model = model;
        this.model.addListener(this);
    }
}
