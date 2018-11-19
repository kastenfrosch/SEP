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
import utils.scene.SceneManager;
import utils.scene.SceneType;
import models.Notepad;
import java.sql.SQLException;

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
        public TextArea addNotepad;
        @FXML
        public Button saveButton;
        @FXML
        public ComboBox<String> priorityComboBox;
        @FXML
        public TextArea notepadTextarea;
        @FXML
        public Button cancelButton;

        public void initializeComboBox() {
            ObservableList<String> prioritaet = FXCollections.observableArrayList();
            prioritaet.add("Hohe Priorität");
            prioritaet.add("Mittlere Priorität");
            prioritaet.add("Geringe Priorität");
            prioritaet.add("Keine Priorität");

            for (String s : prioritaet) {
                priorityComboBox.setItems(prioritaet);
            }
            priorityComboBox.getSelectionModel().select(0);
        }

        public void setPriority(ActionEvent actionEvent) {

            if (priorityComboBox.getSelectionModel().getSelectedItem() == "Hohe Priorität") {
                addNotepad.setStyle("-fx-background-color: red");
            } else if (priorityComboBox.getSelectionModel().getSelectedItem() == "Mittlere Priorität") {
                addNotepad.setStyle("-fx-background-color: yellow");
            } else if (priorityComboBox.getSelectionModel().getSelectedItem() == "niedrige Priorität") {
                addNotepad.setStyle("-fx-background-color: green");
            } else if (priorityComboBox.getSelectionModel().getSelectedItem() == "Keine Priorität") {
                addNotepad.setStyle("-fx-background-color: grey");
            }
        }

        public void saveButton(ActionEvent actionEvent) {

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

                notepad.setNotepadContent(textContent);
                notepad.setNotepadPriority(priority);
                notepad.setNotepadName(noteName);

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

        public void cancelButton(ActionEvent actionEvent) {
            SceneManager.getInstance().closeWindow(SceneType.CREATE_NOTEPAD_WINDOW);
        }
    }
