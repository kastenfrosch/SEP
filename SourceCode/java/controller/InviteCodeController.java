package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import modal.ErrorModal;
import models.InviteCode;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.sql.SQLException;

public class InviteCodeController {
    private DBManager db;

    {
        try {
            db = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private ListView<InviteCode> codeListView;
    public void initialize() {

        try {

            // initializing an ObservableList which is filled with all the existing User descriptions
            ObservableList<InviteCode> codeList = FXCollections.observableArrayList();
            Dao<InviteCode, String> userDao = db.getInviteCodeDao();
            codeList.addAll(userDao.queryForAll());

            // filling the ListView with the ObservableList
            codeListView.setItems(codeList);

        } catch (java.sql.SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }

    }

    public void onCancelBtnClicked(ActionEvent event) {
        SceneManager.getInstance().closeWindow(SceneType.INVITE_CODE);
    }

    public void onGenerateCodeBtnClicked(ActionEvent event) {
        String inviteCode = InviteCode.getCode();
        System.out.print(inviteCode);


    }
}
