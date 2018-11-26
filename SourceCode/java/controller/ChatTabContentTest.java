package controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import connection.DBManager;
import connection.PGNotificationHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import modal.ErrorModal;
import models.ChatMessage;
import models.User;
import utils.TimeUtils;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatTabContentTest                                                                                         {

    private User currentUser;
    private User chatPartner;
    private DBManager dbManager;
    private String history;
    private Timestamp latestTime;
    private int greatestID;
    private Tab currentTab;

    {
        try {
            dbManager = DBManager.getInstance();
            // don't know why timestamp is not set at this point
            latestTime = Timestamp.valueOf(LocalDateTime.now());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // defining key combination to add a manual linebreak in chat window
    final KeyCombination keyCombinationCtrlEnter = new KeyCodeCombination(
            KeyCode.ENTER, KeyCodeCombination.CONTROL_DOWN);

    @FXML
    public AnchorPane anchorpaneParent;
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

    public void setChatPartners(User currentUser, User chatPartner, Tab currentTab) {
        this.currentUser = currentUser;
        this.chatPartner = chatPartner;
        this.currentTab = currentTab;

        registerListener();
    }

    public void registerListener() {
        // listening for new messages
        PGNotificationHandler
                .getInstance()
                .registerListener(PGNotificationHandler.NotificationChannel.CHAT, () -> {

                    List<ChatMessage> msgList = new ArrayList<>();
                    Dao<ChatMessage, Integer> msgDao = dbManager.getChatMessageDao();

                    // query for all messages between the users, order by descending timestamp
                    // limit query results to 1
                    // this way the list only contains only the latest message between the chat partners
                    PreparedQuery<ChatMessage> query =
                            msgDao
                                    .queryBuilder()
                                    .orderBy(ChatMessage.FIELD_TIME, false)
                                    .limit(1L)
                                    .where()
                                    .gt(ChatMessage.FIELD_MESSAGE_ID, greatestID)
                                    .and()
                                    .eq(ChatMessage.FIELD_FROM_USER_ID, currentUser)
                                    .and()
                                    .eq(ChatMessage.FIELD_TO_USER_ID, chatPartner)
                                    .prepare();

                    // ... and adding it to the list
                    msgList.addAll(msgDao.query(query));
                    ChatMessage msg = msgList.get(msgList.size()-1);

                    // temporary fix until latestTame can be set at initialization
                    if (this.latestTime == null) {
                        this.latestTime = Timestamp.valueOf(msg.getLocalDateTime().minusSeconds(1));
                    }
                    // compare timestamp of message to latest message bestween those users
                    // if timestamp is later than latest timestamp, add the message to history
                    if (msg.getTime().after(this.latestTime)) {
                        history += msg.getSender() + " (" + TimeUtils.toSimpleString(msg.getLocalDateTime()) + "):\r\n";
                        history += msg.getContent() + "\r\n";

                        // updating latesttime
                        this.latestTime = msg.getTime();

                        // pasting the string into the upper box
                        chatBox.setText(history);
                        chatBox.positionCaret(history.length());
                    }
                    
                    return null;
                });
    }

    public void loadHistory() {
        // paste chat history into chatBox
        try {

            List<ChatMessage> messageHistoryList = new ArrayList<>();
            // query for the complete chat history between the chat partners
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
            int msgID = 0;

            // ... more formatting
            for (ChatMessage msg : messageHistoryList) {
                this.history += msg.getSender() + " (" + TimeUtils.toSimpleString(msg.getLocalDateTime()) + "):\r\n";
                this.history += msg.getContent() + "\r\n";
                latestTimestamp = msg.getTime();
                msgID = msg.getMessageId();
            }

            // pasting the string into the upper box
            chatBox.setText(this.history);
            chatBox.positionCaret(this.history.length());

            // setting latestTime for the listener
            this.latestTime = latestTimestamp;
            this.greatestID = msgID;

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

    public void onCloseButtonClicked(ActionEvent actionEvent) {
        // close window
        TabPane test = (TabPane) this.anchorpaneParent.getParent().getParent();
        for (Tab t : test.getTabs()) {
          if (t == this.currentTab) {
              test.getTabs().remove(t);
          }
        }
    }

}
