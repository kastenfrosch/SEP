package controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import connection.DBManager;
import connection.PGNotificationHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import modal.ErrorModal;
import modal.InfoModal;
import models.ChatMessage;
import models.User;
import utils.TimeUtils;
import utils.scene.SceneManager;
import utils.scene.SceneType;
import utils.scene.TabInfo;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChatWindowTabPaneTestController {

    private DBManager dbManager;
    private User currentUser;
    private int lastId;

    {
        try {
            dbManager = DBManager.getInstance();
            lastId = -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            currentUser = dbManager.getUserDao().queryForId("moretutor");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public TabPane tabPane;
    public ListView userListView;
    public Button btnExportHistory;
    public Button btnStartChat;

    @FXML
    public void initialize() {
        // initialize userView
        try {
            // initializing an ObservableList which is filled with all the existing usernames except oneself
            ObservableList<User> userList = FXCollections.observableArrayList();
            Dao<User, String> userDao = dbManager.getUserDao();
            userList.addAll(userDao.queryForAll());
            userList.remove(currentUser);

            // filling the userView with the ObservableList
            userListView.setItems(userList);
        } catch (java.sql.SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }

        registerListener();
    }

    public void registerListener() {
        // listening for new messages to show notifications
        PGNotificationHandler
                .getInstance()
                .registerListener(PGNotificationHandler.NotificationChannel.CHAT, () -> {

                    Dao<ChatMessage, Integer> msgDao = dbManager.getChatMessageDao();
                    lastId = msgDao.queryForAll().get(msgDao.queryForAll().size()-1).getMessageId();

                    User receiver;
                    List<ChatMessage> msgList = new ArrayList<>();

                    // query for all new messages to currentUser
                    PreparedQuery<ChatMessage> query =
                            msgDao
                                    .queryBuilder()
                                    // statement
                                    .where()
                                    .eq(ChatMessage.FIELD_MESSAGE_ID, lastId)
                                    .and()
                                    .eq(ChatMessage.FIELD_TO_USER_ID, currentUser)
                                    .prepare();

                    // ... and adding it to the list
                    msgList.addAll(msgDao.query(query));

                    for (ChatMessage msg : msgList)
                    System.out.println(msg.getMessageId() + msg.getContent());

                    if (msgList.size() > 0) {
                        ChatMessage msg = msgList.get(0);

                        receiver = msg.getReceiver();
                        User sender = msg.getSender();
                        int id = msg.getMessageId();

                        System.out.println();
                        System.out.println("receiver: " + receiver);
                        System.out.println("sender: " + sender);
                        System.out.println("id: " + id);
                        System.out.println(msg.getContent());
                        System.out.println();


                        // pop up when someone sent a message to current user
                        if (!currentUser.getUsername().equals(sender.getUsername())) {
                            Platform.runLater(() -> InfoModal.show("Neue Nachricht von " + sender));
                        }
                    }

                    return null;
                });
    }


    public void onUserListViewClicked(MouseEvent mouseEvent) {
        // this eventhandler detects a doubleclick on a name in the userlist and opens the corresponding chat tab
        userListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                // detect a double click
                if (click.getClickCount() == 2) {
                    // selecting a user on doubleclick
                    User chatPartner = (User) userListView.getSelectionModel().getSelectedItem();
                    String chatWindowTitle = "Chat mit " + chatPartner;

                    // creating new chat tab and set chat header
                    TabInfo tabInfo = SceneManager.getInstance().createNewTab(SceneType.CHAT_TAB_CONTENT_TEST);
                    Tab newChatTab = tabInfo.getTab();
                    newChatTab.setText(chatWindowTitle);

                    // setting chat partners and load chat history between chatpartners
                    var controller = tabInfo.<ChatTabContentTest>getController();
                    controller.setChatPartners(currentUser, chatPartner, newChatTab);
                    controller.loadHistory();

                    // adding new chat tab to tab pane and open chat tab
                    tabPane.getTabs().add(0, newChatTab);
                    tabPane.getSelectionModel().select(newChatTab);

                }
            }
        });

    }

    public void onStartChatButtonClicked(ActionEvent actionEvent) {
        // checking if chatpartner is null and setting chatpartner
        User chatPartner;
        if (userListView.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("Bitte wählen Sie einen Benutzer aus!");
            return;
        }
        chatPartner = (User) userListView.getSelectionModel().getSelectedItem();
        String chatWindowTitle = "Chat mit " + chatPartner;

        // creating new chat tab and set chat header
        TabInfo tabInfo = SceneManager.getInstance().createNewTab(SceneType.CHAT_TAB_CONTENT_TEST);
        Tab newChatTab = tabInfo.getTab();
        newChatTab.setText(chatWindowTitle);

        // setting chat partners and load chat history between chatpartners
        var controller = tabInfo.<ChatTabContentTest>getController();
        controller.setChatPartners(this.currentUser, chatPartner, newChatTab);
        controller.loadHistory();

        // adding new chat tab to tab pane and open chat tab
        tabPane.getTabs().add(0, newChatTab);
        tabPane.getSelectionModel().select(newChatTab);

    }

    public void onExportHistoryButtonClicked(ActionEvent actionEvent) {

        // export history
        try {

            List<ChatMessage> messageHistoryList = new ArrayList<>();
            // getting chat partner
            User chatPartner;
            if (userListView.getSelectionModel().getSelectedItem() == null) {
                InfoModal.show("Bitte wählen Sie einen Benutzer aus!");
                return;
            }
            chatPartner = (User) userListView.getSelectionModel().getSelectedItem();

            // query for the chat history between the two users
            Dao<ChatMessage, Integer> msgDao = dbManager.getChatMessageDao();
            PreparedQuery<ChatMessage> query =
                    msgDao
                            .queryBuilder()
                            .where()
                            .in(ChatMessage.FIELD_FROM_USER_ID,
                                    this.currentUser.getUsername(),
                                    chatPartner.getUsername())
                            .and()
                            .in(ChatMessage.FIELD_TO_USER_ID,
                                    this.currentUser.getUsername(),
                                    chatPartner.getUsername())
                            .prepare();

            // ... and adding it to the list
            messageHistoryList.addAll(msgDao.query(query));

            // pasting the list into a string with formatting
            String history = "";
            for (ChatMessage msg : messageHistoryList) {
                history += msg.getSender() + " (" + TimeUtils.toSimpleString(msg.getLocalDateTime()) + "):\r\n";
                history += msg.getContent() + "\r\n";
            }

            // creating filechooser
            FileChooser chooser = new FileChooser();
            // creating extension filter for text files
            FileChooser.ExtensionFilter extensionFilter =
                    new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
            // setting extension filter
            chooser.getExtensionFilters().add(extensionFilter);
            // open filechooser
            Stage stage = (Stage) tabPane.getScene().getWindow();
            File selectedPath = chooser.showSaveDialog(stage);

            if (selectedPath != null) {
                // getting the path which the file will be saved into
                String savingPath = selectedPath.getAbsolutePath();
                try {
                    // writing the text into file
                    PrintWriter out = new PrintWriter(savingPath);
                    out.print(history);
                    out.close();
                } catch (java.io.FileNotFoundException e) {
                    ErrorModal.show(e.getMessage());
                    e.printStackTrace();
                }
            } else return;

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

    }

}
