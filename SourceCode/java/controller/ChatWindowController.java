package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import modal.ErrorModal;
import models.ChatMessage;
import models.User;
import utils.SceneManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChatWindowController {

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

    @FXML
    public TabPane tabPane;
    @FXML
    public AnchorPane anchorPane;
    @FXML
    public AnchorPane anchorPane1;
    @FXML
    public AnchorPane anchorPane2;
    @FXML
    public SplitPane splitPane;
    @FXML
    public TextArea chatBox;
    @FXML
    public TextArea writeMessageBox;
    @FXML
    public Button btnSend;
    @FXML
    public Button btnCancel;

    @FXML
    public void initialize() {

        Platform.runLater(()-> writeMessageBox.requestFocus());

        // paste chat history into chatBox
        List<ChatMessage> messageHistoryList = new ArrayList<>();
        try {
            Dao<ChatMessage, Integer> msgDao = dbManager.getChatMessageDao();
            var query =
                    msgDao
                            .queryBuilder()
                            .where()
                            .eq(ChatMessage.FIELD_FROM_USER_ID, this.currentUser)
                            .and()
                            .eq(ChatMessage.FIELD_TO_USER_ID, this.chatPartner)
                            .prepare();
            messageHistoryList.addAll(msgDao.query(query));

            String text = "";
            for (ChatMessage msg : messageHistoryList) {
                text = text + msg.getSender() + " (" + msg.getMessageId() + "):\n";
                text = text + msg.getContent() + "\n";
            }
            chatBox.setText(text);
        } catch (java.sql.SQLException e){
            e.printStackTrace();
        }

    }

    public void onSendButtonClicked(ActionEvent actionEvent) {

        // send a message
        String message;
        if (writeMessageBox.getText().isBlank()) return;
        message = writeMessageBox.getText();

        // making an sql statement out of the message and creating a message into the database
        try {
            // creating new group instance
            models.ChatMessage newMessage = new ChatMessage();

            // passing variables to the new group instance
            newMessage.setContent(message);
            newMessage.setSender(this.currentUser);
            newMessage.setReceiver(this.chatPartner);

            // save new group into database
            Dao<ChatMessage, Integer> msgDao = dbManager.getChatMessageDao();
            msgDao.create(newMessage);
            writeMessageBox.clear();
        } catch (SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }

    }

    public void onCancelButtonClicked(ActionEvent actionEvent) {
        // close window
        SceneManager.getInstance().closeWindow(SceneManager.SceneType.CHAT_WINDOW);
    }

    public void listen() {
        // listen to incoming messages
        // do i need that?
    }

    public void setChatPartners(User currentUser, User chatPartner) {
        this.currentUser = currentUser;
        this.chatPartner = chatPartner;
    }

}
