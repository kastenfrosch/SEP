package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modal.ErrorModal;
import modal.InfoModal;
import models.User;
import utils.scene.SceneManager;
import utils.scene.SceneType;
import models.Notepad;
import java.sql.SQLException;

    //todo: (600x400) Link zu Student Gruppe Goupage sodass die Notiz quasi dort eingeordnet ist; Tab mit Notizliste zur zugehoerigen Entitaet
    public class CreateNotepadController {

        private DBManager db;

        {
            try {
                db = DBManager.getInstance();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @FXML
        public Label createNotepadLabel;
        @FXML
        public TextField bezeichnungTextField;
        @FXML
        public Button saveButton;
        @FXML
        public ComboBox<String> priorityComboBox;
        @FXML
        public TextArea notepadTextarea;
        @FXML
        public Button cancelButton;

        @FXML
        public void initialize() { //initializing ComboBox
            ObservableList<String> prioritaet = FXCollections.observableArrayList("Hohe Priorität", "Mittlere Priorität",
                                                                                        "Geringe Priorität", "Keine Priorität");
            priorityComboBox.setItems(prioritaet);
            priorityComboBox.getSelectionModel().select(0);
            notepadTextarea.setStyle("-fx-background-color: red");
        }

        public void setPriority(ActionEvent actionEvent) { //Setting Colors in relation to the chosen priority

            if (priorityComboBox.getSelectionModel().getSelectedItem() == "Hohe Priorität") {
                notepadTextarea.setStyle("-fx-background-color: red");
            } else if (priorityComboBox.getSelectionModel().getSelectedItem() == "Mittlere Priorität") {
                notepadTextarea.setStyle("-fx-background-color: yellow");
            } else if (priorityComboBox.getSelectionModel().getSelectedItem() == "niedrige Priorität") {
                notepadTextarea.setStyle("-fx-background-color: green");
            } else if (priorityComboBox.getSelectionModel().getSelectedItem() == "Keine Priorität") {
                notepadTextarea.setStyle("-fx-background-color: grey");
            }
        }

        public void saveButton(ActionEvent actionEvent) { //Saving notepad

            String noteName;
            if(bezeichnungTextField == null || bezeichnungTextField.getText().isBlank()) {
                InfoModal.show("FEHLER!", null, "Bitte Bezeichnung eingeben!");
                return;
            }
            noteName = bezeichnungTextField.getText();

            String priority;
            if(priorityComboBox.getSelectionModel().getSelectedItem() == null) {
                InfoModal.show("FEHLER!", null, "Bitte Priorität bestimmen!");
                return;
            }
            priority = priorityComboBox.getSelectionModel().getSelectedItem();

            String textContent;
            if(notepadTextarea == null || notepadTextarea.getText().isBlank()) {
                InfoModal.show("FEHLER!", null, "Bitte Notiz eingeben");
                return;
            }
            textContent = notepadTextarea.getText();

            try {
                Notepad notepad = new Notepad();
                Dao<User, String> user = db.getUserDao();
                try {
                    User tester = user.queryForId("besttutor");
                    notepad.setUser(tester);
                } catch (
                        SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                notepad.setNotepadContent(textContent);
                notepad.setNotepadPriority(priority);
                notepad.setNotepadName(noteName);
                notepad.setNotepadObject(db.getNotepadDao().getTableName());
                //notepad.setUser(db.getLoggedInUser());

                Dao<Notepad, Integer> notepadDao = db.getNotepadDao();

                notepadDao.create(notepad);
                InfoModal.show("Notiz \"" + noteName + "\" erstellt!");
            } catch (java.sql.SQLException e) {
                ErrorModal.show("Die Notiz konnte nicht erstellt werden: " + e.getMessage());
                e.printStackTrace();
            } finally {
                SceneManager.getInstance().closeWindow(SceneType.CREATE_NOTEPAD_WINDOW);
            }
        }

        public void cancelButton(ActionEvent actionEvent) { //When Cancel Button is pressed
            SceneManager.getInstance().closeWindow(SceneType.CREATE_NOTEPAD_WINDOW);
        }
    }
