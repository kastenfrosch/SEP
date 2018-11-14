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
import modal.ErrorModal;
import modal.InfoModal;
import models.Groupage;
import models.Semester;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.sql.SQLException;


public class CreateGroupageController {

    private DBManager db;

    {
        try {
            db = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public Label AddGroupageLabel;
    @FXML
    public Button cancelButton;
    @FXML
    public Button addSaveButton;
    @FXML
    public TextField nameTextfield;
    @FXML
    public Label nameLabel;
    @FXML
    public Label semesterLabel;
    @FXML
    public ComboBox<Semester> addChooseSemesterComboBox;

    @FXML
    public void initialize() {
        ObservableList<Semester> semesterList = FXCollections.observableArrayList();
        Dao<Semester, String> semesterDao = db.getSemesterDao();

        try {
            semesterList.addAll(semesterDao.queryForAll());
            addChooseSemesterComboBox.setItems(semesterList);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        nameTextfield.clear();
        addChooseSemesterComboBox.getSelectionModel().select(0);

    }

    public void addGroupageCancelButton(ActionEvent actionEvent) {
        SceneManager.getInstance().closeWindow(SceneType.CREATE_GROUPAGE);
    }

    public void addGroupageAddSafeButton(ActionEvent actionEvent) {

        String name;
        if (nameTextfield.getText() == null || nameTextfield.getText().isBlank()) {
            InfoModal.show("FEHLER!", null, "Kein Klassenname eingegeben!");
            return;
        }
        name = nameTextfield.getText();

        Semester semester;
        if (addChooseSemesterComboBox.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("FEHLER!", null, "Bitte Semester angeben!");
            return;
        }
        semester = addChooseSemesterComboBox.getSelectionModel().getSelectedItem();


        try {
            Groupage groupage = new Groupage();

            groupage.setDescription(name);
            groupage.setSemester(semester);

            Dao<Groupage, Integer> groupageDao = db.getGroupageDao();

            groupageDao.create(groupage);
            InfoModal.show("Klasse \"" + name + "\" erstellt!");

        } catch (java.sql.SQLException e) {
            ErrorModal.show("Die Klasse konnte nicht erstellt werden: " + e.getMessage());
            e.printStackTrace();
        } finally {
            SceneManager.getInstance().closeWindow(SceneType.CREATE_GROUPAGE);
        }

    }
}