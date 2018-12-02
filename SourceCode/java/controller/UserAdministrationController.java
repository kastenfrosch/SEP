package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import modal.ConfirmationModal;
import modal.ErrorModal;
import modal.InfoModal;
import models.InviteCode;
import models.User;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.sql.SQLException;

public class UserAdministrationController {
// get user and db Manager
    static User user = new User();
    private DBManager db;

    {
        try {
            db = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private ListView<User> listView;

    @FXML
    public void initialize() {

        try {

            // initializing an ObservableList which is filled with all the existing User descriptions
            ObservableList<User> userList = FXCollections.observableArrayList();
            Dao<User, String> userDao = db.getUserDao();
            userList.addAll(userDao.queryForAll());

            // filling the ListView with the ObservableList
            listView.setItems(userList);

        } catch (java.sql.SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }

    }

    @FXML
    public void onEditBtnClicked(ActionEvent event) {
        //creating the user dao with the edit attributes
        User selectedItem = listView.getSelectionModel().getSelectedItem();
        SceneManager.getInstance()
                .getLoaderForScene(SceneType.EDIT_USER)
                .<EditUserController>getController()
                .setUser(selectedItem);
        SceneManager.getInstance().showInNewWindow(SceneType.EDIT_USER);
    }

    @FXML
    public void onDeleteBtnClicked(ActionEvent event) {
        //check if user wants to delete the groupage
        boolean confirm = ConfirmationModal.show("Soll der User wirklich gelöscht werden?");
        if (!confirm) {
            return;
        }

        Dao<User, String> userDao = db.getUserDao();
        user = listView.getSelectionModel().getSelectedItem();

        try {
            //delete the userDao from the database
            userDao.delete(user);
            InfoModal.show("User " + user.getUsername() + " wurde gelöscht.");
            listView.getItems().remove(user);
        } catch (SQLException e) {
            ErrorModal.show("Der User konnte nicht gelöscht werden.");
        }
        //back to the homeview

    }

    @FXML
    public void onCancelBtnClicked(ActionEvent event) {
        SceneManager.getInstance().closeWindow(SceneType.USER_ADMIN);
    }


    public void onCodeBtnClicked(ActionEvent event) {
        SceneManager.getInstance().showInNewWindow(SceneType.INVITE_CODE);


    }
}


