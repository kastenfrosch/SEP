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
import modal.ConfirmationModal;
import modal.ErrorModal;
import modal.InfoModal;
import models.Group;
import models.Groupage;
import models.Semester;
import utils.SceneManager;

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
    public AnchorPane anchorPane;
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

    public void groupNameInput(ActionEvent actionEvent) {
    }

    public void chooseSemesterComboBox(ActionEvent actionEvent) {
    }

    public void chooseGroupageComboBox(ActionEvent actionEvent) {
    }

    public void editGroupEdit(ActionEvent actionEvent) {

        // use all selections and text inputs to edit the group.

        // making sure that groupname is not empty.
        // if not, set groupname to the input.
        String name;
        if (groupnameInput.getText() == null || groupnameInput.getText().isBlank()) {
            InfoModal.show("ACHTUNG!", null, "Kein Gruppenname eingegeben!");
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

            // passing variables to the group instance
            this.groupToEdit.setName(name);
            this.groupToEdit.setSemester(semester);
            this.groupToEdit.setGroupage(groupage);

            // save edited group into database
            Dao<Group, Integer> groupDao = db.getGroupDao();
            groupDao.update(this.groupToEdit);

            // check if group has been edited
            // an existing group has an id other than 0
            if (this.groupToEdit.getId() != 0) {
                InfoModal.show("Gruppe \"" + name + "\" wurde geändert!");

                // close window
                SceneManager.getInstance().closeWindow(SceneManager.SceneType.EDIT_GROUP);

            } else {
                ErrorModal.show("Gruppe konnte nicht geändert werden!");
                return;
            }

        } catch (java.sql.SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }

    }

    public void editGroupDelete(ActionEvent actionEvent) {

        // check if sure to delete
        boolean confirmDelete = ConfirmationModal.show("Soll die Gruppe wirklich gelöscht werden?");

        if (confirmDelete) {

            // delete group from database
            try {
                Dao<Group, Integer> groupDao = db.getGroupDao();
                groupDao.delete(groupToEdit);
            } catch (java.sql.SQLException e) {
                ErrorModal.show(e.getMessage());
                e.printStackTrace();
            }

            // close window
            SceneManager.getInstance().closeWindow(SceneManager.SceneType.EDIT_GROUP);

        }

    }

    public void editGroupCancel(ActionEvent actionEvent) {

        // close group editing window
        SceneManager.getInstance().closeWindow(SceneManager.SceneType.EDIT_GROUP);

    }

    public void setSelectedGroup(Group group) {

        // setting up the passed group
        this.groupToEdit = group;

        // initializing the text input in the textfield according to the passed group object
        groupnameInput.setText(group.getName());

        // initializing the pre-marked selections in the comboboxes according to the passed group object
        semesterComboBox.getSelectionModel().select(group.getSemester().getDescription());
        groupageComboBox.getSelectionModel().select(group.getGroupage().getDescription());

    }

}
