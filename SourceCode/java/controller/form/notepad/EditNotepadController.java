package controller.form.notepad;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modal.ErrorModal;
import modal.InfoModal;
import models.*;
import utils.scene.SceneManager;
import utils.scene.SceneType;
import java.sql.SQLException;
import java.sql.Timestamp;

public class EditNotepadController {

    private Object objectType;
    private Notepad notepad;
    private DBManager db;
    private Timestamp time;

    {
        try {
            db = DBManager.getInstance();
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

    public void initialize() {
        //Initializing ComboBox
        ObservableList<String> prioritaet = FXCollections.observableArrayList("Gut", "Mittel",
                "Schlecht", "Ohne Zuordnung");
        editNotepadPriorityComboBox.setItems(prioritaet);
    }

    public void setPriority(ActionEvent actionEvent) {
        //Setting Colors in relation to the chosen priority
        try {
            if (editNotepadPriorityComboBox.getSelectionModel().getSelectedItem().equals("Gut")) {
                editNotepadTextarea.setStyle("-fx-background-color: green");
            } else if (editNotepadPriorityComboBox.getSelectionModel().getSelectedItem().equals("Mittel")) {
                editNotepadTextarea.setStyle("-fx-background-color: yellow");
            } else if (editNotepadPriorityComboBox.getSelectionModel().getSelectedItem().equals("Schlecht")) {
                editNotepadTextarea.setStyle("-fx-background-color: red");
            } else if (editNotepadPriorityComboBox.getSelectionModel().getSelectedItem().equals("Ohne Zuordnung")) {
                editNotepadTextarea.setStyle("-fx-background-color: grey");
            }
        } catch(NullPointerException e) {

        }
    }

    public void editNotepadSave(ActionEvent actionEvent) throws SQLException { //Updating notepad changes
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

        Dao<Notepad, Integer> notepadDao = db.getNotepadDao();
        Dao<NotepadHistory, Integer> notepadHistoryDao = db.getNotepadHistoryDao();
        NotepadHistory notepadHistory = new NotepadHistory();

        //Saving the Notepad which is to be edited & Logging the original Note in the history
        Notepad ersatz = this.notepad;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        notepadHistory.setNotepad(ersatz);
        notepadHistory.setTimestamp(timestamp);
        notepadHistory.setUser(db.getLoggedInUser());
        notepadHistoryDao.create(notepadHistory);

        //Saving the notepad after editing
        this.notepad.setNotepadName(noteName);
        this.notepad.setNotepadPriority(priority);
        this.notepad.setNotepadContent(textContent);

        try {
            notepadDao.update(this.notepad);

            if(this.objectType instanceof Student) {
                StudentNotepad studentNotepad = new StudentNotepad();
                studentNotepad.setStudent((Student) this.objectType);
                studentNotepad.setNotepad(notepad);

                Dao<StudentNotepad, Integer> studentNotepadDao = db.getStudentNotepadDao();
                studentNotepadDao.update(studentNotepad);
            }
            else if(this.objectType instanceof Groupage) {
                GroupageNotepad groupageNotepad = new GroupageNotepad();
                groupageNotepad.setGroupage((Groupage) this.objectType);
                groupageNotepad.setNotepad(notepad);

                Dao<GroupageNotepad, Integer> groupageNotepadDao = db.getGroupageNotepadDao();
                groupageNotepadDao.update(groupageNotepad);
            }
            else if(this.objectType instanceof Group) {
                GroupNotepad groupNotepad = new GroupNotepad();
                groupNotepad.setGroup((Group) this.objectType);
                groupNotepad.setNotepad(notepad);

                Dao<GroupNotepad, Integer> groupNotepadDao = db.getGroupNotepadDao();
                groupNotepadDao.update(groupNotepad);
            }
            //Adding the edited Notepad to the List in NotesTab
            SceneManager.getInstance().getLoaderForScene(SceneType.NOTESTAB_WINDOW).
                    <NotesTabController>getController().notesListView.getItems().add(this.notepad);
            //Removing the Notepad which was to be edited in the List in NotesTab
            SceneManager.getInstance().getLoaderForScene(SceneType.NOTESTAB_WINDOW).
                    <NotesTabController>getController().notesListView.getItems().remove(ersatz);

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

    public void setObject(Object object, Notepad notepad) {
        this.objectType = object;
        this.notepad = notepad;

        //preconfig of items depending on given Notepad
        editNotepadName.setText(notepad.getNotepadName());
        editNotepadPriorityComboBox.getSelectionModel().select(notepad.getNotepadPriority());
        editNotepadTextarea.setText(notepad.getNotepadContent());
        //Setting Colors
        if(editNotepadPriorityComboBox.getSelectionModel().getSelectedItem().equals("Gut")) {
            editNotepadTextarea.setStyle("-fx-background-color: green");
        }
        else if(editNotepadPriorityComboBox.getSelectionModel().getSelectedItem().equals("Mittel")) {
            editNotepadTextarea.setStyle("-fx-background-color: yellow");
        }
        else if(editNotepadPriorityComboBox.getSelectionModel().getSelectedItem().equals("Schlecht")) {
            editNotepadTextarea.setStyle("-fx-background-color: red");
        }
        else if(editNotepadPriorityComboBox.getSelectionModel().getSelectedItem().equals("Ohne Zuordnung")) {
            editNotepadTextarea.setStyle("-fx-background-color: grey");
        }
    }
}