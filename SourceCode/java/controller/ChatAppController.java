package controller;

import connection.DBManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import modal.InfoModal;
import models.User;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.sql.SQLException;

@Deprecated
public class ChatAppController {

    private DBManager dbManager;
    private User currentUser;

    {
        try {
            dbManager = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            currentUser = dbManager.getUserDao().queryForId("besttutor");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public AnchorPane anchorpane;
    @FXML
    public ListView userView;
    @FXML
    public Button startChatButton;
    @FXML
    public Button exportHistoryButton;


    public void initialize() {
        // initialize userView
        /*try {
            // initializing an ObservableList which is filled with all the existing usernames
            ObservableList<User> userList = FXCollections.observableArrayList();
            Dao<User, String> userDao = dbManager.getUserDao();
            userList.addAll(userDao.queryForAll());

            // filling the userView with the ObservableList
            userView.setItems(userList);
        } catch (java.sql.SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }*/
    }

    public void onStartChatButtonClicked(ActionEvent actionEvent) {
        // check if selection in userView has been made
        User chatPartner;
        if (userView.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("Bitte wählen Sie einen Benutzer aus!");
            return;
        }
        chatPartner = (User) userView.getSelectionModel().getSelectedItem();
        String chatWindowTitle = "Chat mit " + chatPartner;

        // open chat window with selected user
        SceneManager.getInstance()
                .getLoaderForScene(SceneType.CHAT_WINDOW)
                .<ChatWindowController>getController()
                .setChatPartners(this.currentUser, chatPartner);
        SceneManager.getInstance().showInNewWindow(SceneType.CHAT_WINDOW, chatWindowTitle);
    }

    public void onListViewClicked(MouseEvent mouseEvent) {

        userView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                // detect a double click
                if (click.getClickCount() == 2) {
                    User chatPartner = (User) userView.getSelectionModel().getSelectedItem();
                    String chatWindowTitle = "Chat mit " + chatPartner;

                    // open chat window with selected user
                    SceneManager.getInstance()
                            .getLoaderForScene(SceneType.CHAT_WINDOW)
                            .<ChatWindowController>getController()
                            .setChatPartners(currentUser, chatPartner);
                    SceneManager.getInstance().showInNewWindow(SceneType.CHAT_WINDOW, chatWindowTitle);
                }
            }
        });

    }

    public void onExportHistoryButtonClicked(ActionEvent actionEvent) {
    }

}
