package controller;

import connection.DBManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import modal.InfoModal;

import java.sql.SQLException;

public class EditGroupController {

    private DBManager db;

    {
        try {
            db = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public Text titleText;
    @FXML
    public Button cancelBtn;
    @FXML
    public Label groupNameLbl;
    @FXML
    public Button editBtn;
    @FXML
    public Label semesterLbl;
    @FXML
    public Label groupageLbl;
    @FXML
    public TextField lblInputName;
    @FXML
    public ComboBox semesterComboBox;
    @FXML
    public ComboBox groupageComboBox;
    @FXML
    public Button deleteBtn;

    @FXML
    public void initialize() {

        //TODO: initializing comboboxes

    }

    public void groupNameInput(ActionEvent actionEvent) {
    }

    public void chooseSemesterComboBox(ActionEvent actionEvent) {
    }

    public void chooseGroupageComboBox(ActionEvent actionEvent) {
    }

    public void editGroupEdit(ActionEvent actionEvent) {

        //TODO: on button click: save changes

    }

    public void editGroupDelete(ActionEvent actionEvent) {

        //TODO: on button click: delete selected group

    }

    public void editGroupCancel(ActionEvent actionEvent) {

        //TODO: close window and abort
        InfoModal.show("Testmsg!");

    }

}
