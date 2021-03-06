package controller.chat;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import connection.DBManager;
import connection.PGNotificationHandler;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
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

public class ChatTabController {

    private User currentUser;
    private User chatPartner;
    private DBManager dbManager;
    private String history;
    private int lastId;
    private Tab currentTab;

    {
        try {
            dbManager = DBManager.getInstance();
            lastId = -1;
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
        // send a message, see below
        sendMessage();
    }

    public void onTabClickedListener() {

        // return tab text to normal after clicking on tab with new messages
        currentTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                if (currentTab.isSelected()) {
                    Platform.runLater(() -> currentTab.setText("Chat mit " + chatPartner));
                }
            }
        });

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

        // initializing listener
        registerChatListener();
        onTabClickedListener();
    }

    public void registerChatListener() {
        // listening for new messages
        PGNotificationHandler
                .getInstance()
                .registerListener(PGNotificationHandler.NotificationChannel.CHAT, () -> {

                    List<ChatMessage> msgList = new ArrayList<>();
                    Dao<ChatMessage, Integer> msgDao = dbManager.getChatMessageDao();

                    // query for all messages between the users that are newer than last seen message
                    PreparedQuery<ChatMessage> query =
                            msgDao
                                    .queryBuilder()
                                    .orderBy(ChatMessage.FIELD_TIME, false)
                                    .limit(1L)
                                    .where()
                                    .gt(ChatMessage.FIELD_MESSAGE_ID, lastId)
                                    .and()
                                    .in(ChatMessage.FIELD_FROM_USER_ID,
                                            currentUser.getUsername(),
                                            chatPartner.getUsername())
                                    .and()
                                    .in(ChatMessage.FIELD_TO_USER_ID,
                                            currentUser.getUsername(),
                                            chatPartner.getUsername())
                                    .prepare();

                    // ... and adding it to the list
                    msgList.addAll(msgDao.query(query));


                    ChatMessage msg = msgList.get(0);

                    // formatting history and append message to it
                    history += msg.getSender() + " (" + TimeUtils.toSimpleString(msg.getLocalDateTime()) + "):\r\n";
                    history += msg.getContent() + "\r\n";

                    // if chat tab is not focused, get notification in tab header
                    TabPane tabPane = (TabPane) anchorpaneParent.getParent().getParent();
                    SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();

                    if (selectionModel.getSelectedItem() != currentTab) {
                        Platform.runLater(() -> currentTab.setText("(Neue Nachricht) " + "Chat mit " + chatPartner));
                    }

                    // pasting the string into the upper box
                    chatBox.setText(history);
                    chatBox.positionCaret(history.length());

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
                            .in(ChatMessage.FIELD_FROM_USER_ID,
                                    this.currentUser.getUsername(),
                                    this.chatPartner.getUsername())
                            .and()
                            .in(ChatMessage.FIELD_TO_USER_ID,
                                    this.currentUser.getUsername(),
                                    this.chatPartner.getUsername())
                            .prepare();

            // ... and adding it to the list
            messageHistoryList.addAll(msgDao.query(query));

            // pasting the list into a string with formatting
            this.history = "";

            // just to initialize the variable
            int msgID = 0;

            // ... more formatting
            for (ChatMessage msg : messageHistoryList) {
                this.history += msg.getSender() + " (" + TimeUtils.toSimpleString(msg.getLocalDateTime()) + "):\r\n";
                this.history += msg.getContent() + "\r\n";
                msgID = msg.getMessageId();
            }

            // pasting the string into the upper box
            chatBox.setText(this.history);
            chatBox.positionCaret(this.history.length());

            // setting lastId for the listener
            this.lastId = msgID;

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
        TabPane tabPane = (TabPane) this.anchorpaneParent.getParent().getParent();
        for (Tab t : tabPane.getTabs()) {
          if (t == this.currentTab) {
              tabPane.getTabs().remove(t);
          }
        }
    }

}
