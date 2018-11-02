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
import javafx.scene.text.Text;
import modal.ErrorModal;
import modal.InfoModal;
import models.Group;
import models.Groupage;
import models.Semester;
import utils.SceneManager;

import java.sql.SQLException;

public class CreateGroupController {

    private DBManager db;

    {
        try {
            db = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public AnchorPane anchorPane;
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

    @FXML
    public void initialize() {

        // initializing combobox data
        try {

            // initializing an ObservableList which is filled with all the existing semester descriptions
            ObservableList<String> semesterList = FXCollections.observableArrayList();
            Dao<Semester, String> semester = db.getSemesterDao();

            for (Semester s : semester.queryForAll()) {
                semesterList.add(s.getDescription());
            }

            // filling the combobox with the ObservableList
            semesterComboBox.setItems(semesterList);

            // initializing an ObservableList which is filled with all the existing groupage descriptions
            ObservableList<String> groupageList = FXCollections.observableArrayList();
            Dao<Groupage, Integer> groupage = db.getGroupageDao();

            for (Groupage g : groupage.queryForAll()) {
                groupageList.add(g.getDescription());
            }

            // filling the combobox with the ObservableList
            groupageComboBox.setItems(groupageList);

        } catch (java.sql.SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }

        //TODO: check which semester is selected and show possible grpgs accordingly

    }


    public void groupnameInput(ActionEvent actionEvent) {
    }

    public void chooseGroupageComboBox(ActionEvent actionEvent) {
    }

    public void chooseSemesterComboBox(ActionEvent actionEvent) {
    }

    public void createGroupCreate(ActionEvent actionEvent) {

        // use all selections and text inputs to create a group.

        // making sure that groupname is not empty.
        // if not, set groupname to the input.
        String name;
        if (groupnameInput.getText() == null || groupnameInput.getText().isBlank()) {
            InfoModal.show("ACHTUNG!", null, "Kein Gruppenname eingegeben!");
            return;
        }
        name = groupnameInput.getText();

        // making sure that groupage is not empty.
        // if not, set groupage to the selection.
        String groupageString;
        if (groupageComboBox.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("ACHTUNG!", null, "Keine Groupage ausgewählt!");
            return;
        }
        groupageString = (String) groupageComboBox.getSelectionModel().getSelectedItem();

        // making sure that semester is not empty.
        // if not, set groupage to the selection.
        String semesterString;
        if (semesterComboBox.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("ACHTUNG!", null, "Kein Semester ausgewählt");
            return;
        }
        semesterString = (String) semesterComboBox.getSelectionModel().getSelectedItem();

        try {

            // setting groupage string to the corresponding groupage id
            Dao<Groupage, Integer> groupageDao = db.getGroupageDao();
            Groupage groupage = groupageDao.queryForEq(Groupage.FIELD_GROUPAGE_DESCRIPTION, groupageString).get(0);

            // setting semester string to the corresponding semester id
            Dao<Semester, String> semesterDao = db.getSemesterDao();
            Semester semester = semesterDao.queryForEq(Semester.FIELD_SEMESTER_DESCRIPTION, semesterString).get(0);

            // creating new group instance
            models.Group newGroup = new Group();

            // passing variables to the new group instance
            newGroup.setName(name);
            newGroup.setSemester(semester);
            newGroup.setGroupage(groupage);

            // save new group into database
            Dao<Group, Integer> groupDao = db.getGroupDao();
            groupDao.create(newGroup);

            // check if group has been created
            // an existing group has an id other than 0
            if (newGroup.getId() != 0) {
                InfoModal.show("Die Gruppe \"" + name + "\" wurde erstellt!");

                // close window
                SceneManager.getInstance().closeWindow(SceneManager.SceneType.EDIT_GROUP);

            } else {
                ErrorModal.show("Gruppe konnte nicht erstellt werden!");
                return;
            }

        } catch (java.sql.SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }

    }

    public void createGroupCancel(ActionEvent actionEvent) {

        // close group creation window
        SceneManager.getInstance().closeWindow(SceneManager.SceneType.EDIT_GROUP);

    }

}
