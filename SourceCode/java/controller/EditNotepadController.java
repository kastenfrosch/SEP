package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modal.ConfirmationModal;
import modal.ErrorModal;
import modal.InfoModal;
import models.Notepad;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.sql.SQLException;

public class EditNotepadController {

    private DBManager dbManager;

    {
        try {
            dbManager = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    public TextArea editNotepadTextarea;
    @FXML
    public Label editNotepadLabel;
    @FXML
    public TextField editNotepadName;
    @FXML
    public ComboBox<String> editNotepadPriorityComboBox;
    @FXML
    public Button editNotepadSaveButton;
    @FXML
    public Button editNotepadCancelButton;
    @FXML
    public Button editNotepadDeleteButton;

    public void initializeComboBox() {
        ObservableList<String> prioritaet = FXCollections.observableArrayList();
        prioritaet.add("Hohe Priorität");
        prioritaet.add("Mittlere Priorität");
        prioritaet.add("Geringe Priorität");
        prioritaet.add("Keine Priorität");

        for (String s : prioritaet) {
            editNotepadPriorityComboBox.setItems(prioritaet);
        }
        editNotepadPriorityComboBox.getSelectionModel().select(0);
    }

    public void setPriority(ActionEvent actionEvent) {
        if (editNotepadPriorityComboBox.getSelectionModel().getSelectedItem() == "Hohe Priorität") {
            editNotepadTextarea.setStyle("-fx-background-color: red");
        } else if (editNotepadPriorityComboBox.getSelectionModel().getSelectedItem() == "Mittlere Priorität") {
            editNotepadTextarea.setStyle("-fx-background-color: yellow");
        } else if (editNotepadPriorityComboBox.getSelectionModel().getSelectedItem() == "niedrige Priorität") {
            editNotepadTextarea.setStyle("-fx-background-color: green");
        } else if (editNotepadPriorityComboBox.getSelectionModel().getSelectedItem() == "Keine Priorität") {
            editNotepadTextarea.setStyle("-fx-background-color: grey");
        }
    }

    public void editNotepadSave(ActionEvent actionEvent) {

        Notepad notepad = new Notepad();

        String noteName;
        if(editNotepadName == null || editNotepadName.getText().isBlank()) {
            InfoModal.show("FEHLER!", null, "Bitte Bezeichnung eingeben!");
            return;
        }
        noteName = editNotepadName.getText();

        String priority;
        if(editNotepadPriorityComboBox.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("FEHLER!", null, "Bitte Priorität bestimmen!");
            return;
        }
        priority = editNotepadPriorityComboBox.getSelectionModel().getSelectedItem();

        String textContent;
        if(editNotepadTextarea == null || editNotepadTextarea.getText().isBlank()) {
            InfoModal.show("FEHLER!", null, "Bitte Notiz eingeben");
            return;
        }
        textContent = editNotepadTextarea.getText();

        Dao<Notepad, Integer> notepadDao = dbManager.getNotepadDao();

        notepad.setNotepadName(noteName);
        notepad.setNotepadPriority(priority);
        notepad.setNotepadContent(textContent);

        try {
            notepadDao.update(notepad);
            InfoModal.show("Notiz" + editNotepadName.getText() + " wurde geändert!");
        } catch (SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }
        SceneManager.getInstance().closeWindow(SceneType.EDIT_NOTEPAD_WINDOW);
    }

    public void editNotepadDelete(ActionEvent actionEvent) {
        Notepad notepad = new Notepad();

        boolean delete = ConfirmationModal.show("Soll die Notiz gelöscht werden?");
        if (delete) {
            Dao<Notepad, Integer> notepadDao = dbManager.getNotepadDao();
            try {
                notepadDao.delete(notepad);
            } catch (SQLException e) {
                ErrorModal.show("Fehler: Die Notiz konnte nicht geloescht werden.");
            }
            SceneManager.getInstance().closeWindow(SceneType.EDIT_NOTEPAD_WINDOW);
        }
        else {
            return;
        }
    }

    public void editNotepadCancel(ActionEvent actionEvent) {
        SceneManager.getInstance().closeWindow(SceneType.EDIT_NOTEPAD_WINDOW);
    }
}
