package controller;

import connection.DBManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import models.User;
import utils.SceneManager;

import java.sql.SQLException;

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


    public void initialize() {

        writeMessageBox.requestFocus();
        // paste chat history into chatBox
    }

    public void onSendButtonClicked(ActionEvent actionEvent) {

        /*// send a message
        String message;
        if (writeMessageBox.getText().isBlank()) return;
        message = writeMessageBox.getText();

        // making an sql statement out of the message and creating a message into the database
        try {
            // creating new group instance
            models.ChatMessage newMessage = new ChatMessage();

            // passing variables to the new group instance
            newMessage.setContent(message);
            newMessage.setSender(currentUser);
            newMessage.setReceiver(chatPartner);

            // save new group into database
            Dao<ChatMessage, Integer> msgDao = dbManager.getChatMessageDao();
            msgDao.create(newMessage);
        } catch (SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }*/

    }

    public void onCancelButtonClicked(ActionEvent actionEvent) {
        // close window
        SceneManager.getInstance().closeWindow(SceneManager.SceneType.CHAT_WINDOW);
    }

    public void listen() {
        // listen to incoming messages
        // do i need that?
    }

    public void setChatPartner(User user) {
        this.chatPartner = user;
    }

}
