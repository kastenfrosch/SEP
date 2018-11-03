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
import javafx.scene.layout.AnchorPane;
import modal.ConfirmationModal;
import modal.ErrorModal;
import modal.InfoModal;

import models.Groupage;
import models.Semester;
import utils.SceneManager;

import java.sql.SQLException;


public class EditGroupageController {
    private Groupage groupage;


    private DBManager dbManager;

    {
        try {
            dbManager = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private Label semesterLabel;

    @FXML
    private ComboBox<Semester> semesterComboBox;

    @FXML
    private Button cancelButton;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label AddGroupageLabel;

    @FXML
    private Label GroupsLabel;

    @FXML
    private Button saveButton;

    @FXML
    private TextField nameTextfield;

    @FXML
    private Label nameLabel;

    @FXML
    public void initialize() {

        // initializing combobox data

        try {
            // creat an observableList with all semesters

            ObservableList<Semester> semesterList = FXCollections.observableArrayList();
            Dao<Semester, String> semesterDao = dbManager.getSemesterDao();
            semesterList.addAll(semesterDao.queryForAll());
            //set semester combobox with the semester from the observableList
            semesterComboBox.setItems(semesterList);


        } catch (java.sql.SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }


    }

    @FXML
    public void onCancelBTNClicked(ActionEvent event) {
        //back to homeview
        SceneManager.getInstance().closeWindow(SceneManager.SceneType.EDIT_GROUPAGE);


    }

    @FXML
    public void onSaveBtnClicked(ActionEvent event) {
        //making sure nameTextfield is not empty
        if (nameTextfield.getText().isEmpty() || nameTextfield == null) {
            InfoModal.show("FEHLER!", null, "Keine Bezeichnung eingegeben!");
            return;
        }

        //making sure semesterCombobox is not empty
        if (semesterComboBox.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("FEHLER!", null, "Kein Semester ausgewählt!");
            return;
        }
        //creating the groupage dao with the edit attributes
        Dao<Groupage, Integer> groupageDao = dbManager.getGroupageDao();
        groupage.setDescription(nameTextfield.getText());
        groupage.setSemester(semesterComboBox.getSelectionModel().getSelectedItem());

        try {
            //update the groupage in the database
            groupageDao.update(groupage);
            if (this.groupage.getId() != 0) {
                InfoModal.show("Klasse \"" + nameTextfield.getText() + "\" wurde geändert!");}

        } catch (SQLException e) {
         ErrorModal.show(e.getMessage());
         e.printStackTrace();
        }
        // back to homeview
        SceneManager.getInstance().closeWindow(SceneManager.SceneType.EDIT_GROUPAGE);
    }


    public void onDeleteBTNClicked(ActionEvent event) {
        //check if user wants to delete the groupage
        boolean confirm = ConfirmationModal.show("Soll der Student wirklich gelöscht werden?");
        if (!confirm) {
            return;
        }


        Dao<Groupage, Integer> groupageDao = dbManager.getGroupageDao();
        try {
            //delete the groupage dao from the database
            groupageDao.delete(groupage);
        } catch (SQLException e) {
            ErrorModal.show("Die Klasse konnte nicht gelöscht werden. Bitte stellen Sie sicher, dass keine Gruppen mehr dieser Klasse zugeordnet sind.");
        }
        //back to the homeview
        SceneManager.getInstance().closeWindow(SceneManager.SceneType.EDIT_GROUPAGE);


    }

    @FXML
    public void addToGroup(ActionEvent event) {

    }

    @FXML
    public void addToSemester(ActionEvent event) {

    }

    public void setGroupage(Groupage groupage) {
        // set groupage
        this.groupage = groupage;
        nameTextfield.setText(groupage.getDescription());
        semesterComboBox.getSelectionModel().select(groupage.getSemester());
    }
}


