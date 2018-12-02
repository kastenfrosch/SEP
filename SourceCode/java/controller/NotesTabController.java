package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import modal.ConfirmationModal;
import modal.ErrorModal;
import modal.InfoModal;
import models.*;
import utils.scene.SceneManager;
import utils.scene.SceneType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotesTabController {

    private Object objectType;
    private DBManager db;
    private Notepad notepad;

    {
        try {
            db = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public ListView<String> notesListView;
    @FXML
    public Button editButton;
    @FXML
    public Button deleteButton;
    @FXML
    public Button createButton;
    @FXML
    public Button showNoteButton;
    @FXML
    public Label notesPaneLabel;

    //todo: Aktualisierung wenn Object gewechselt wird;
    // CreateNotepad Inhalte resetten wenn erneut Notepad erstellt werden möchte (Hat jedoch keinen Einfluss auf die Funktionalität!)
    // Aktualisierung wenn etwas edited wurde : ListItem wird ersetzt aber nicht umbenannt, sofern Bezeichnung geändert wurde (Beeinflusst Löschfunktion da löschen abh. von der ausgewählten Bez.)

    public void initialize() throws SQLException {
        //initializing listView
        notesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        //initializing listView items depending on instance + depending on User
        if(this.objectType instanceof Student) {
            List<String> studentNotes = new ArrayList<>();
            for (StudentNotepad n : db.getStudentNotepadDao().queryForAll()) {
                if (n.getNotepad().getUser().equals(db.getLoggedInUser())) {
                    studentNotes.add(n.getNotepad().getNotepadName());
                }
                ObservableList<String> list = FXCollections.observableArrayList();
                list.addAll(studentNotes);
                notesListView.setItems(list);
            }
        }
        else if(this.objectType instanceof Groupage) {
            List<String> groupageNotes = new ArrayList<>();
            for (GroupageNotepad n : db.getGroupageNotepadDao().queryForAll()) {
                if (n.getNotepad().getUser().equals(db.getLoggedInUser())) {
                    groupageNotes.add(n.getNotepad().getNotepadName());
                }
                ObservableList<String> list = FXCollections.observableArrayList();
                list.addAll(groupageNotes);
                notesListView.setItems(list);
            }
        }
        else if(this.objectType instanceof Group) {
            List<String> groupNotes = new ArrayList<>();
            for (GroupNotepad n : db.getGroupNotepadDao().queryForAll()) {
                if (n.getNotepad().getUser().equals(db.getLoggedInUser())) {
                    groupNotes.add(n.getNotepad().getNotepadName());
                }
                ObservableList<String> list = FXCollections.observableArrayList();
                list.addAll(groupNotes);
                notesListView.setItems(list);
            }
        }
    }

    public void editButton(ActionEvent actionEvent) {
        if (notesListView.getSelectionModel().isEmpty()) {
            InfoModal.show("Bitte wählen Sie eine Notiz aus.");
            return;
        }
        //Setting this.notepad depending on chosen ListView Item + comparing chosen item with our DB list in terms of Notepad names
        //since ListView saves Notepad names
        SceneType sceneType = null;
        SceneManager sm = SceneManager.getInstance();
        if(this.objectType instanceof Student) {
            Dao<StudentNotepad, Integer> studentNotepadDao = db.getStudentNotepadDao();
            for (StudentNotepad s : studentNotepadDao) {
                if (s.getNotepad().getNotepadName().equals(notesListView.getSelectionModel().getSelectedItem())) {
                    this.notepad = s.getNotepad();
                }
            }
            //Setting given Object & Notepad for EditNotepadWindow
            if (sceneType == null) {
                sceneType = SceneType.EDIT_NOTEPAD_WINDOW;
                sm.getLoaderForScene(sceneType).<EditNotepadController>getController()
                        .setObject(this.objectType, this.notepad);
            }
        }
        else if(this.objectType instanceof Group) {
            Dao<GroupNotepad, Integer> groupNotepadDao = db.getGroupNotepadDao();
            for(GroupNotepad g : groupNotepadDao) {
                if(g.getNotepad().getNotepadName().equals(notesListView.getSelectionModel().getSelectedItem())) {
                    this.notepad = g.getNotepad();
                }
            }
            if(sceneType == null) {
                sceneType = SceneType.EDIT_NOTEPAD_WINDOW;
                sm.getLoaderForScene(sceneType).<EditNotepadController>getController()
                        .setObject(this.objectType, this.notepad);
            }
        }
        else if(this.objectType instanceof Groupage) {
            Dao<GroupageNotepad, Integer> groupageNotepadDao = db.getGroupageNotepadDao();
            for(GroupageNotepad g : groupageNotepadDao) {
                if(g.getNotepad().getNotepadName().equals(notesListView.getSelectionModel().getSelectedItem())) {
                    this.notepad = g.getNotepad();
                }
            }
            if(sceneType == null) {
                sceneType = SceneType.EDIT_NOTEPAD_WINDOW;
                sm.getLoaderForScene(sceneType).<EditNotepadController>getController()
                        .setObject(this.objectType, this.notepad);
            }
        }
        SceneManager.getInstance().showInNewWindow(SceneType.EDIT_NOTEPAD_WINDOW);
    }

    public void deleteButton(ActionEvent actionEvent) throws SQLException{

        if(notesListView.getSelectionModel().isEmpty()) {
            InfoModal.show("Bitte wählen Sie die zu löschende Notiz aus.");
            return;
        }

        boolean delete = ConfirmationModal.show("Soll die Notiz gelöscht werden?");
        if (delete) {
            Dao<Notepad, Integer> notepadDao = db.getNotepadDao();

            try { //Again comparing chosen ListView item (String) to the DB Notepad Names
                  //Correct item is deleted in notepad DB & ObjectNotepad DB
                  //Refreshing NotesTab & deleting the listView item
                if(this.objectType instanceof Student) {
                    Dao<StudentNotepad, Integer> studentNotepadDao = db.getStudentNotepadDao();
                    for(StudentNotepad n : studentNotepadDao) {
                        if(notesListView.getSelectionModel().getSelectedItem().equals(n.getNotepad().getNotepadName())) {
                            studentNotepadDao.delete(n);
                            notepadDao.delete(n.getNotepad());
                            SceneManager.getInstance().getLoaderForScene(SceneType.NOTESTAB_WINDOW).
                                    <NotesTabController>getController().notesListView.getItems().remove(n.getNotepad().getNotepadName());
                        }
                    }
                }
                else if(this.objectType instanceof Group) {
                    Dao<GroupNotepad, Integer> groupNotepadDao = db.getGroupNotepadDao();
                    for(GroupNotepad n : groupNotepadDao) {
                        if(notesListView.getSelectionModel().getSelectedItem().equals(n.getNotepad().getNotepadName())) {
                            groupNotepadDao.delete(n);
                            notepadDao.delete(n.getNotepad());
                            SceneManager.getInstance().getLoaderForScene(SceneType.NOTESTAB_WINDOW).
                                    <NotesTabController>getController().notesListView.getItems().remove(n.getNotepad().getNotepadName());
                        }
                    }
                }
                else if(this.objectType instanceof Groupage) {
                    Dao<GroupageNotepad, Integer> groupageNotepadDao = db.getGroupageNotepadDao();
                    for(GroupageNotepad n : groupageNotepadDao) {
                        if(notesListView.getSelectionModel().getSelectedItem().equals(n.getNotepad().getNotepadName())) {
                            groupageNotepadDao.delete(n);
                            notepadDao.delete(n.getNotepad());
                            SceneManager.getInstance().getLoaderForScene(SceneType.NOTESTAB_WINDOW).
                                    <NotesTabController>getController().notesListView.getItems().remove(n.getNotepad().getNotepadName());
                        }
                    }
                }
            } catch (SQLException e) {
                ErrorModal.show("Fehler: Die Notiz konnte nicht geloescht werden.");
            }
        }
        else {
            return;
        }
    }

    public void createButton(ActionEvent actionEvent) {
        SceneType sceneType = null;
        SceneManager sm = SceneManager.getInstance();
        if(sceneType == null) {
            sceneType = SceneType.CREATE_NOTEPAD_WINDOW;
            sm.getLoaderForScene(sceneType).<CreateNotepadController>getController()
                    .setObject(this.objectType);
        }
        SceneManager.getInstance().showInNewWindow(SceneType.CREATE_NOTEPAD_WINDOW);
    }

    public void showNoteButton(ActionEvent actionEvent) {
        if (notesListView.getSelectionModel().isEmpty()) {
            InfoModal.show("Bitte wählen Sie eine Notiz aus.");
            return;
        }
        SceneType sceneType = null;
        SceneManager sm = SceneManager.getInstance();
        if(this.objectType instanceof Student) {
            Dao<StudentNotepad, Integer> studentNotepadDao = db.getStudentNotepadDao();
            for (StudentNotepad s : studentNotepadDao) {
                if (s.getNotepad().getNotepadName().equals(notesListView.getSelectionModel().getSelectedItem())) {
                    this.notepad = s.getNotepad();
                }
            }
            if (sceneType == null) {
                sceneType = SceneType.NOTE_WINDOW;
                sm.getLoaderForScene(sceneType).<NoteWindowController>getController()
                        .setNotepad(this.notepad);
            }
            }
        else if(this.objectType instanceof Group) {
            Dao<GroupNotepad, Integer> groupNotepadDao = db.getGroupNotepadDao();
            for(GroupNotepad g : groupNotepadDao) {
                if(g.getNotepad().getNotepadName().equals(notesListView.getSelectionModel().getSelectedItem())) {
                    this.notepad = g.getNotepad();
                }
            }
            if(sceneType == null) {
                sceneType = SceneType.NOTE_WINDOW;
                sm.getLoaderForScene(sceneType).<NoteWindowController>getController()
                        .setNotepad(this.notepad);
            }
        }
        else if(this.objectType instanceof Groupage) {
            Dao<GroupageNotepad, Integer> groupageNotepadDao = db.getGroupageNotepadDao();
            for(GroupageNotepad g : groupageNotepadDao) {
                if(g.getNotepad().getNotepadName().equals(notesListView.getSelectionModel().getSelectedItem())) {
                    this.notepad = g.getNotepad();
                }
            }
            if(sceneType == null) {
                sceneType = SceneType.NOTE_WINDOW;
                sm.getLoaderForScene(sceneType).<NoteWindowController>getController()
                        .setNotepad(this.notepad);
            }
        }
         SceneManager.getInstance().showInNewWindow(SceneType.NOTE_WINDOW);
    }

    public void setObject(Object object) {this.objectType = object;} //Getting Object Type (Group, Groupage or Student)
}
