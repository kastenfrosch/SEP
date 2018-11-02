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
    public ComboBox<Groupage> groupageComboBox;

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

        //TODO: check which semester is selected and show possible grpgs accordingly

        groupnameInput.clear();
        groupageComboBox.getSelectionModel().select(0);

    }

    public void onCreateButtonClicked(ActionEvent actionEvent) {

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
        Groupage groupage;
        if (groupageComboBox.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("ACHTUNG!", null, "Keine Groupage ausgewählt!");
            return;
        }
        groupage = groupageComboBox.getSelectionModel().getSelectedItem();

        try {

            // creating new group instance
            models.Group newGroup = new Group();

            // passing variables to the new group instance
            newGroup.setName(name);
            newGroup.setGroupage(groupage);

            // save new group into database
            Dao<Group, Integer> groupDao = db.getGroupDao();
            groupDao.create(newGroup);

            // check if group has been created
            // an existing group has an id other than 0
            if (newGroup.getId() != 0) {
                InfoModal.show("Die Gruppe \"" + name + "\" wurde erstellt!");

                // close window
                SceneManager.getInstance().closeWindow(SceneManager.SceneType.CREATE_GROUP);

            } else {
                ErrorModal.show("Gruppe konnte nicht erstellt werden!");
            }

        } catch (java.sql.SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }

    }

    public void onCancelButtonClicked(ActionEvent actionEvent) {

        // close group creation window
        SceneManager.getInstance().closeWindow(SceneManager.SceneType.CREATE_GROUP);

    }

}
