package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import modal.InfoModal;
import models.GroupModel;
import models.GroupageModel;
import models.SemesterModel;

public class AdditGroupController {

    @FXML
    public Text titleText;
    @FXML
    public Button createGroupBtn;
    @FXML
    public Button cancelBtn;
    @FXML
    public Label groupnameLbl;
    @FXML
    public TextField groupnameInput;
    @FXML
    public Label semesterLbl;
    @FXML
    public Label groupageLbl;
    @FXML
    public ComboBox semesterComboBox;
    @FXML
    public ComboBox groupageComboBox;




    public void groupnameInput(ActionEvent actionEvent) {
    }

    public void chooseGroupageComboBox(ActionEvent actionEvent) {
    }

    public void chooseSemesterComboBox(ActionEvent actionEvent) {
    }

    public void additGroupCreate(ActionEvent actionEvent) {

    }

    public void additGroupCancel(ActionEvent actionEvent) {

        // close window and abort
        InfoModal.show("Testmsg!");

    }

}
