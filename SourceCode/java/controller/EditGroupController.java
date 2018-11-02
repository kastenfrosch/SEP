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
    public ComboBox<Groupage> groupageComboBox;
    @FXML
    public Button deleteBtn;

    @FXML
    public void initialize() {

        // initializing combobox data
        try {

            // initializing an ObservableList which is filled with all the existing groupage descriptions
            ObservableList<Groupage> groupageList = FXCollections.observableArrayList();
            Dao<Groupage, Integer> groupageDao = db.getGroupageDao();
            groupageList.addAll(groupageDao.queryForAll());

            // filling the combobox with the ObservableList
            groupageComboBox.setItems(groupageList);

        } catch (java.sql.SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }

    }

    public void onSaveButtonClicked(ActionEvent actionEvent) {

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
        Groupage groupage = groupageComboBox.getSelectionModel().getSelectedItem();

        try {

            // passing variables to the group instance
            this.groupToEdit.setName(name);
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
            }

        } catch (java.sql.SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }

    }

    public void onDeleteButtonClicked(ActionEvent actionEvent) {

        // check if sure to delete
        boolean confirmDelete = ConfirmationModal.show("Soll die Gruppe wirklich gelöscht werden?");

        if (confirmDelete) {

            // delete group from database
            try {
                Dao<Group, Integer> groupDao = db.getGroupDao();
                groupDao.delete(this.groupToEdit);
            } catch (java.sql.SQLException e) {
                ErrorModal.show(e.getMessage());
                e.printStackTrace();
            }

            // close window
            SceneManager.getInstance().closeWindow(SceneManager.SceneType.EDIT_GROUP);

        }

    }

    public void onCancelButtonClicked(ActionEvent actionEvent) {

        // close group editing window
        SceneManager.getInstance().closeWindow(SceneManager.SceneType.EDIT_GROUP);

    }

    public void setGroup(Group group) {

        // setting up the passed group
        this.groupToEdit = group;

        // initializing the text input in the textfield according to the passed group object
        groupnameInput.setText(group.getName());

        // initializing the pre-marked selections in the comboboxes according to the passed group object
        groupageComboBox.getSelectionModel().select(group.getGroupage());

    }

}
