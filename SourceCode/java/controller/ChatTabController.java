package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import modal.ErrorModal;
import models.User;

import java.sql.SQLException;

public class ChatTabController {

    private DBManager dbManager;

    {
        try {
            dbManager = DBManager.getInstance();
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
        try {
            // initializing an ObservableList which is filled with all the existing usernames
            ObservableList<User> userList = FXCollections.observableArrayList();
            Dao<User, String> userDao = dbManager.getUserDao();
            userList.addAll(userDao.queryForAll());

            // filling the userView with the ObservableList
            userView.setItems(userList);
        } catch (java.sql.SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }
    }

    public void onStartChatButtonClicked(ActionEvent actionEvent) {
    }

    public void onExportHistoryButtonClicked(ActionEvent actionEvent) {
    }
}
