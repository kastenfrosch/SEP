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
import modal.InfoModal;
import models.Group;
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
        private ComboBox<Group> groupComboBox;

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

                        ObservableList<Semester> semesterList = FXCollections.observableArrayList();
                        Dao<Semester, String> semesterDao = dbManager.getSemesterDao();

                        semesterList.addAll(semesterDao.queryForAll());

                        semesterComboBox.setItems(semesterList);

                        ObservableList<Group> groupList = FXCollections.observableArrayList();
                        Dao<Group, Integer> groupDao = dbManager.getGroupDao();

                        groupList.addAll(groupDao.queryForAll());

                        groupComboBox.setItems(groupList);

                } catch (java.sql.SQLException e) {
                        e.printStackTrace();
                }


        }

        @FXML
       public void onCancelBTNClicked(ActionEvent event) {
                SceneManager.getInstance().closeWindow(SceneManager.SceneType.EDIT_GROUPAGE);


        }

        @FXML
        public void onSaveBtnClicked(ActionEvent event) {
                if (nameTextfield.getText().isEmpty() || nameTextfield == null) {
                        InfoModal.show("FEHLER!", null, "Keine Bezeichnung eingegeben!");
                        return;
                }

                Group gCB;
                if (groupComboBox.getSelectionModel().getSelectedItem() == null) {
                        InfoModal.show("FEHLER!", null, "Keine Gruppe ausgewählt!");
                        return;
                }
                gCB = (Group) groupComboBox.getSelectionModel().getSelectedItem();

                Semester sCB;
                if (semesterComboBox.getSelectionModel().getSelectedItem() == null) {
                        InfoModal.show("FEHLER!", null, "Kein Semester ausgewählt!");
                        return;
                }
                sCB = (Semester) semesterComboBox.getSelectionModel().getSelectedItem();
                Dao<Groupage, Integer> groupageDao = dbManager.getGroupageDao();

                try {
                        groupageDao.update(groupage);
                } catch (SQLException e) {
                        e.printStackTrace();
                }

                SceneManager.getInstance().closeWindow(SceneManager.SceneType.EDIT_GROUPAGE);
        }



        public void onDeleteBTNClicked(ActionEvent event) {
                boolean confirm = ConfirmationModal.show("Soll der Student wirklich gelöscht werden?");
                if(!confirm){
                        return;
                }


                Dao<Groupage, Integer> studentDao = dbManager.getGroupageDao();
                try {
                        studentDao.delete(groupage);
                } catch (SQLException e) {
                        e.printStackTrace();
                }
                SceneManager.getInstance().closeWindow(SceneManager.SceneType.EDIT_GROUPAGE);


        }
        @FXML
        public void addToGroup(ActionEvent event) {

        }

        @FXML
        public void addToSemester(ActionEvent event) {

        }
}


