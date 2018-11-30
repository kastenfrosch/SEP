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

    private Notepad notepad;
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

    public void initialize() { //Initializing ComboBox
        ObservableList<String> prioritaet = FXCollections.observableArrayList("Hohe Priorität", "Mittlere Priorität",
                                                                                   "Geringe Priorität", "Keine Priorität");
        editNotepadPriorityComboBox.setItems(prioritaet);
    }

    public void setPriority(ActionEvent actionEvent) { //Setting Colors in relation to the chosen priority
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

    public void editNotepadSave(ActionEvent actionEvent) { //Updating notepad changes
        String noteName;
        if (editNotepadName == null || editNotepadName.getText().isBlank()) {
            InfoModal.show("FEHLER!", null, "Bitte Bezeichnung eingeben!");
            return;
        }
        noteName = editNotepadName.getText();

        String priority;
        if (editNotepadPriorityComboBox.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("FEHLER!", null, "Bitte Priorität bestimmen!");
            return;
        }
        priority = editNotepadPriorityComboBox.getSelectionModel().getSelectedItem();

        String textContent;
        if (editNotepadTextarea == null || editNotepadTextarea.getText().isBlank()) {
            InfoModal.show("FEHLER!", null, "Bitte Notiz eingeben");
            return;
        }
        textContent = editNotepadTextarea.getText();

        Dao<Notepad, Integer> notepadDao = dbManager.getNotepadDao();

        this.notepad.setNotepadName(noteName);
        this.notepad.setNotepadPriority(priority);
        this.notepad.setNotepadContent(textContent);
//        this.notepad.setNotepadObject(dbManager.getNotepadDao().getTableName());

        try {
            notepadDao.update(this.notepad);
            InfoModal.show("Notiz" + editNotepadName.getText() + " wurde geändert!");
        } catch (SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }
        SceneManager.getInstance().closeWindow(SceneType.EDIT_NOTEPAD_WINDOW);
    }

    public void editNotepadCancel(ActionEvent actionEvent) { //When Cancel Button is pressed
        SceneManager.getInstance().closeWindow(SceneType.EDIT_NOTEPAD_WINDOW);
    }

    public void setNotepad(Notepad notepad) { //Function to edit Notepad form HomescreenController
        this.notepad = notepad;
        this.notepad.setNotepadName(notepad.getNotepadName());
        this.notepad.setNotepadPriority(notepad.getNotepadPriority());
        this.notepad.setNotepadContent(notepad.getNotepadContent());
    }
}
