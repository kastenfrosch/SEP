package controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import connection.DBManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import modal.ErrorModal;
import modal.InfoModal;
import models.ChatMessage;
import models.User;
import utils.SceneManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChatTabContentTest {

    private User chatPartner;
    private User currentUser;
    private DBManager dbManager;

    {
        try {
            dbManager = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    final KeyCombination keyCombinationCtrlEnter = new KeyCodeCombination(
            KeyCode.ENTER, KeyCodeCombination.CONTROL_DOWN);

    @FXML
    public SplitPane splitPane;
    public AnchorPane anchorPane1;
    public TextArea writeMessageBox;
    public AnchorPane anchorPane2;
    public TextArea chatBox;
    public Button btnClose;
    public Button btnSend;

    @FXML
    public void initialize() {

        // setting focus to the box so one can immediately start typing
        Platform.runLater(() -> writeMessageBox.requestFocus());

        // paste chat history into chatBox
        //TODO: put in timestamp after each username, possibly coloring or other formatting
        try {

            List<ChatMessage> messageHistoryList = new ArrayList<>();
            // query for the chat history between the two users
            Dao<ChatMessage, Integer> msgDao = dbManager.getChatMessageDao();
            PreparedQuery<ChatMessage> query =
                    msgDao
                            .queryBuilder()
                            .where()
                            .eq(ChatMessage.FIELD_FROM_USER_ID, this.currentUser)
                            .and()
                            .eq(ChatMessage.FIELD_TO_USER_ID, this.chatPartner)
                            .prepare();

            // ... and adding it to the list
            messageHistoryList.addAll(msgDao.query(query));

            // pasting the list into a string with formatting
            String text = "";
            for (ChatMessage msg : messageHistoryList) {
                text = text + msg.getSender() + " (" + msg.getMessageId() + "):\r\n";
                text = text + msg.getContent() + "\r\n";
            }

            // pasting the string into the upper box
            chatBox.setText(text);

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }

    public void onSendButtonClicked(ActionEvent actionEvent) {
        // send a message
        sendMessage();
    }

    public void onKeyPressed(KeyEvent keyEvent) {

        writeMessageBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                // detect when ctrl+enter is pressed to add a line break, or
                // detect when enter is pressed to send the message
                if (keyCombinationCtrlEnter.match(keyEvent)) {
                    writeMessageBox.appendText("\n");
                } else if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                    sendMessage();
                }
            }
        });

    }

    public void onCloseButtonClicked(ActionEvent actionEvent) {
        // close window
        //TODO: properly close with merlins upcoming implementation
        SceneManager.getInstance().closeWindow(SceneManager.SceneType.CHAT_TAB_CONTENT_TEST);
    }

    public void setChatPartners(User currentUser, User chatPartner) {
        this.currentUser = currentUser;
        this.chatPartner = chatPartner;
    }

    private void sendMessage() {

        String message;
        if (writeMessageBox.getText().isBlank()) return;
        message = writeMessageBox.getText();

        try {
            // creating new message instance
            models.ChatMessage newMessage = new ChatMessage();

            // passing variables to the new message instance
            newMessage.setContent(message);
            newMessage.setSender(this.currentUser);
            newMessage.setReceiver(this.chatPartner);

            // save new message into database
            Dao<ChatMessage, Integer> msgDao = dbManager.getChatMessageDao();
            msgDao.create(newMessage);

            // clearing message box for next message
            writeMessageBox.clear();
        } catch (SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }

    }

}
