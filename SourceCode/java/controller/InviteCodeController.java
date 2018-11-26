package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import javafx.scene.input.ClipboardContent;
import javafx.scene.text.Text;
import modal.ErrorModal;
import models.InviteCode;
import models.User;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.awt.*;
import java.awt.datatransfer.*;
import java.sql.SQLException;

public class InviteCodeController {


    public ListView codeListView;
    public ListView usedbyListView;
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

        try {
            ObservableList<InviteCode> codeList = FXCollections.observableArrayList();
            Dao<InviteCode, String> codeDao = db.getInviteCodeDao();
            codeList.addAll(codeDao.queryForAll());
            codeListView.setItems(codeList);

            ObservableList<User> usedByList = FXCollections.observableArrayList();

          for(int i =0; i<codeList.size();i++){
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

        Dao<InviteCode, String> codeDao = db.getInviteCodeDao();
       InviteCode newCode = new InviteCode();
       try{
           codeDao.create(newCode);
           codeListView.getItems().add(newCode);

       } catch (SQLException e) {
           ErrorModal.show(e.getMessage());
           e.printStackTrace();
       }
    }

    public void onClipboardBtnClicked(ActionEvent actionEvent) {
        ClipboardContent codeContent = new ClipboardContent();
        Clipboard codeClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(codeListView.getSelectionModel().getSelectedItem().toString());
        codeClip.setContents(selection,selection);



    }
}
