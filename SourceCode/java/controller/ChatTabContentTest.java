package controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import connection.DBManager;
import connection.PGNotificationHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import modal.ErrorModal;
import models.ChatMessage;
import models.User;
import utils.TimeUtils;
import utils.scene.SceneManager;
import utils.scene.SceneType;
import utils.scene.TabInfo;

import javax.mail.Message;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChatTabContentTest                                                                                         {

    private User currentUser;
    private User chatPartner;
    private DBManager dbManager;
    private String history;
    private Timestamp latestTime;

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

        //TODO: listener

        PGNotificationHandler
            .getInstance()
                .registerListener(PGNotificationHandler.NotificationChannel.CHAT, () -> {

                    List<ChatMessage> msgList = new ArrayList<>();

                    Dao<ChatMessage, Integer> msgDao = dbManager.getChatMessageDao();
                    PreparedQuery<ChatMessage> query =
                            msgDao
                                    .queryBuilder()
                                    .orderBy(ChatMessage.FIELD_TIME, true)
                                    .limit(1L)
                                    .where()
                                    .gt(ChatMessage.FIELD_TIME, latestTime)
                                    .and()
                                    .eq(ChatMessage.FIELD_FROM_USER_ID, this.currentUser)
                                    .and()
                                    .eq(ChatMessage.FIELD_TO_USER_ID, this.chatPartner)
                                    .prepare();

                    // ... and adding it to the list
                    msgList.addAll(msgDao.query(query));
                    ChatMessage msg = msgList.get(msgList.size()-1);

                    if (msg.getTime().after(this.latestTime)) {
                        history += msg.getSender() + " (" + TimeUtils.toSimpleString(msg.getLocalDateTime()) + "):\r\n";
                        history += msg.getContent() + "\r\n";
                    }

                    // pasting the string into the upper box
                    chatBox.setText(history);
                    chatBox.positionCaret(history.length());

                    return null;
                });

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
        SceneManager.getInstance().closeWindow(SceneType.CHAT_TAB_CONTENT_TEST);

    }

    public void setChatPartners(User currentUser, User chatPartner) {
        this.currentUser = currentUser;
        this.chatPartner = chatPartner;
    }

    public void loadHistory() {
        // paste chat history into chatBox
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
            this.history = "";

            Timestamp latestTimestamp = null;

            for (ChatMessage msg : messageHistoryList) {
                this.history += msg.getSender() + " (" + TimeUtils.toSimpleString(msg.getLocalDateTime()) + "):\r\n";
                this.history += msg.getContent() + "\r\n";
                latestTimestamp = msg.getTime();
            }

            // pasting the string into the upper box
            chatBox.setText(this.history);
            chatBox.positionCaret(this.history.length());

            // setting latestTime for the listener
            this.latestTime = latestTimestamp;

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

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
            newMessage.setTime(LocalDateTime.now());

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
