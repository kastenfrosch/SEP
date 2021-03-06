package controller.form.useradmin;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import javafx.scene.input.ClipboardContent;
import modal.ErrorModal;
import modal.InfoModal;
import models.InviteCode;
import models.User;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.awt.*;
import java.awt.datatransfer.*;
import java.sql.SQLException;

public class InviteCodeController {


    public ListView<InviteCode> codeListView;
    public ListView<User> usedbyListView;
    @FXML
    private DBManager db;

    {
        try {
            db = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        //create list for user and invite code
        try {
            ObservableList<InviteCode> codeList = FXCollections.observableArrayList();
            Dao<InviteCode, String> codeDao = db.getInviteCodeDao();
            codeList.addAll(codeDao.queryForAll());
            codeListView.setItems(codeList);

            usedbyListView.setCellFactory(userListView -> new ListCell<>() {
                @Override
                protected void updateItem(User user, boolean b) {
                    if(user == null) {
                        setText("");
                    }
                    else
                    {
                        setText(user.getUsername());
                    }
                }
            });

            ObservableList<User> usedByList = FXCollections.observableArrayList();
            // fill list with the users using the invite code
            for (int i = 0; i < codeList.size(); i++) {
                usedByList.add(codeList.get(i).getUsedBy());
            }
            usedbyListView.setItems(usedByList);

            // filling the ListView with the ObservableList
        } catch (java.sql.SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }


    }

    public void onCancelBtnClicked(ActionEvent event) {
        SceneManager.getInstance().closeWindow(SceneType.INVITE_CODE);
    }

    public void onGenerateCodeBtnClicked(ActionEvent event) {
// get a new invite code
        // code generation is part of the class
        Dao<InviteCode, String> codeDao = db.getInviteCodeDao();
        InviteCode newCode = new InviteCode();
        try {
            codeDao.create(newCode);
            codeListView.getItems().add(newCode);


        } catch (SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }
    }

    public void onClipboardBtnClicked(ActionEvent actionEvent) {
        // create a clipboard
        Clipboard codeClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        //get the selected section
    try {
    StringSelection selection = new StringSelection(codeListView.getSelectionModel().getSelectedItem().toString());
    codeClip.setContents(selection, selection);
    }catch(NullPointerException e){
        InfoModal.show("Bitte wähle einen Code aus");
        return;
    }

    }
}