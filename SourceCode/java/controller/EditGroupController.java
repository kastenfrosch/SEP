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
import modal.ConfirmationModal;
import modal.InfoModal;
import models.Group;
import models.Groupage;
import models.Semester;

import java.sql.SQLException;

public class EditGroupController {

    private Group groupToEdit;

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
    public TextField groupnameInput;
    @FXML
    public ComboBox semesterComboBox;
    @FXML
    public ComboBox groupageComboBox;
    @FXML
    public Button deleteBtn;

    @FXML
    public void initialize() {

        // initializing combobox data

        try {

            ObservableList<String> semesterList = FXCollections.observableArrayList();
            Dao<Semester, String> semester = db.getSemesterDao();

            for (Semester s : semester.queryForAll()) {
                semesterList.add(s.getDescription());
            }

            semesterComboBox.setItems(semesterList);


            ObservableList<String> groupageList = FXCollections.observableArrayList();
            Dao<Groupage, Integer> groupage = db.getGroupageDao();

            for (Groupage g : groupage.queryForAll()) {
                groupageList.add(g.getDescription());
            }

            groupageComboBox.setItems(groupageList);

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        //TODO: check which semester is selected and show possible grpgs accordingly

    }

    public void groupNameInput(ActionEvent actionEvent) {
    }

    public void chooseSemesterComboBox(ActionEvent actionEvent) {
    }

    public void chooseGroupageComboBox(ActionEvent actionEvent) {
    }

    public void editGroupEdit(ActionEvent actionEvent) {

        //TODO: on button click: save changes

        // use all selections and text inputs to edit the group.

        // making sure that groupname is not empty.
        // if not, set groupname to the input.
        String name;
        if (groupnameInput.getText() == null || groupnameInput.getText().isBlank()) {
            InfoModal.show("FEHLER!", null, "Kein Gruppenname eingegeben!");
            return;
        }
        name = groupnameInput.getText();

        // set groupage to the selection.
        String groupageString;
        groupageString = (String) groupageComboBox.getSelectionModel().getSelectedItem();

        // set groupage to the selection.
        String semesterString;
        semesterString = (String) semesterComboBox.getSelectionModel().getSelectedItem();

        try {

            // setting groupage string to the corresponding groupage id
            Dao<Groupage, Integer> groupageDao = db.getGroupageDao();
            Groupage groupage = groupageDao.queryForEq(Groupage.FIELD_GROUPAGE_DESCRIPTION, groupageString).get(0);

            // setting semester string to the corresponding semester id
            Dao<Semester, String> semesterDao = db.getSemesterDao();
            Semester semester = semesterDao.queryForEq(Semester.FIELD_SEMESTER_DESCRIPTION, semesterString).get(0);

            //TODO: is this the selected group?
            // passing variables to the group instance
            this.groupToEdit.setName(name);
            this.groupToEdit.setSemester(semester);
            this.groupToEdit.setGroupage(groupage);

            // save edited group into database
            //TODO: will it be updated?
            Dao<Group, Integer> groupDao = db.getGroupDao();
            groupDao.update(this.groupToEdit);

            if (this.groupToEdit.getId() != 0) {
                InfoModal.show("Group \"" + name + "\" created!");
                //TODO: close window
                // how to close a window?
            } else {
                //TODO:
                InfoModal.show("FEHLER!", null, "Gruppe konnte nicht geändert werden!");
            }

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }


    }

    public void editGroupDelete(ActionEvent actionEvent) {

        // check if sure
        boolean confirmDelete = ConfirmationModal.show("Soll die Gruppe wirklich gelöscht werden?");

        if (confirmDelete == true) {

            // delete group from database
            try {
                Dao<Group, Integer> groupDao = db.getGroupDao();
                groupDao.delete(groupToEdit);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }

            //TODO: ... and close the window.

        }

    }

    public void editGroupCancel(ActionEvent actionEvent) {

        //TODO: close window and abort

    }

    public void setSelectedGroup(Group group) {

        this.groupToEdit= group;

        semesterComboBox.getSelectionModel().select(group.getSemester().getDescription());
        groupageComboBox.getSelectionModel().select(group.getGroupage().getDescription());

    }

}
