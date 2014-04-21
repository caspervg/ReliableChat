package net.caspervg.reliablechat.client;/*
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

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import net.caspervg.reliablechat.client.connection.ServerConnection;
import net.caspervg.reliablechat.client.handler.MessageHandler;
import net.caspervg.reliablechat.client.log.ReliableLogger;
import net.caspervg.reliablechat.protocol.ChatMessage;
import net.caspervg.reliablechat.protocol.LoginMessage;
import net.caspervg.reliablechat.protocol.LogoutMessage;
import org.controlsfx.dialog.Dialogs;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Level;

public class ReliableChatCompanion {

    public MenuItem loginButton;
    public MenuItem logoutButton;
    public TextArea chatArea;
    public TextField chatField;
    public TextArea friendArea;
    public Button sendButton;

    public static Properties properties = new Properties();
    private ServerConnection conn = null;
    private String username = null;
    private int port = -1;

    public void doLogin(ActionEvent actionEvent) {

        String portString = Dialogs.create()
                .title("Connection port")
                .message("Please enter the port you wish to connect to")
                .showTextInput("1000");
        this.port = Integer.parseInt(portString);

        this.username = Dialogs.create()
                .title("Username")
                .message("Please enter the username you want to use")
                .showTextInput("");

        MessageHandler.setCompanion(this);

        connectToServer();
    }

    public void doLogout(ActionEvent actionEvent) {
        if (this.username != null) {
            this.conn.sendMessage(new LogoutMessage(this.username));
            this.conn = null;
            loginButton.setDisable(false);
            logoutButton.setDisable(true);
        } else {
            ReliableLogger.log(Level.WARNING, "Tried to net.caspervg.reliablechat.client.log out without properly logging in");
        }
    }

    public void sendChatMessage(ActionEvent actionEvent) {
        if (this.conn != null) {
            String input = chatField.getText();
            String[] inputSplit = input.split(":");
            this.conn.sendMessage(new ChatMessage(this.username, inputSplit[0], inputSplit[1]));
        }
    }

    private void connectToServer() {
        try {
            properties.load(ClassLoader.getSystemResourceAsStream("config.properties"));
            ReliableLogger.setLevel(Level.parse(properties.getProperty("reliablechat.client.log_level")));
        } catch (IOException e) {
            ReliableLogger.log(Level.SEVERE, "Could not load the configuration file", e);
            System.exit(1);
        }

        if (this.username != null && this.port > 0) {
            try {
                Socket socket = new Socket(InetAddress.getLocalHost(), this.port);
                this.conn = new ServerConnection(socket);

                this.conn.sendMessage(new LoginMessage(this.username));

                Thread serverConnection = new Thread(this.conn);
                serverConnection.start();

                logoutButton.setDisable(false);
                loginButton.setDisable(true);
            } catch (Exception e) {
                ReliableLogger.log(Level.SEVERE, "Could not connect to the server", e);
            }
        } else {
            ReliableLogger.log(Level.WARNING, "Attempted to connect to the server without valid username or port");
        }
    }

}
