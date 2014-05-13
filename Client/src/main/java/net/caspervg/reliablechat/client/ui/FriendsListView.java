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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import net.caspervg.reliablechat.client.ReliableChatModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class FriendsListView extends ListView<String> implements InvalidationListener {

    private ReliableChatModel model;

    public FriendsListView() {
        super();

        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.getSelectionModel().selectedItemProperty().addListener((observableValue, s, s2) -> {
            List<String> selection = new ArrayList<>(getSelectionModel().getSelectedItems());
            model.setCurrentRecipient(selection);
        });
    }

    @Override
    public void invalidated(Observable observable) {
        ObservableList<String> data = FXCollections.observableArrayList();
        Set<String> friends = model.getOnlineUsers();

        if (friends == null) {
            this.getSelectionModel().clearSelection();
            this.setItems(data);
        } else {
            friends.stream().forEach(data::add);
            this.setItems(data);
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
