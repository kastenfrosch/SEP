package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.beans.Observable;
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
    public Button cancelButton;
    @FXML
    public Button showNoteButton;
    @FXML
    public Label notesPaneLabel;

    public void initialize() throws SQLException { //initializing listView

        notesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        Dao<User, String> user = db.getUserDao(); //Testing
        User tester = user.queryForId("besttutor");
        db.setLoggedInUser(tester);

        Dao<Student, Integer> testStudent = db.getStudentDao(); //Testing
        Student student = testStudent.queryForId(1);
        this.objectType = student;

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
        } /*
        else if(this.objectType instanceof Group) {
                List<Notepad> groupNotes = new ArrayList<>();
                for(GroupNotepad n : db.getGroupNotepadDao().queryForAll()) {
                    if(n.getNotepad().getUser().equals(db.getLoggedInUser())) {
                        groupNotes.add(n.getNotepad());
                    }
                }
            ObservableList<Notepad> list = FXCollections.observableArrayList();
            list.addAll(groupNotes);
            notesListView.setItems(list);
        }
        else if(this.objectType instanceof Groupage) {
                List<Notepad> groupageNotes = new ArrayList<>();
                for(GroupageNotepad n : db.getGroupageNotepadDao().queryForAll()) {
                    if(n.getNotepad().getUser().equals(db.getLoggedInUser())) {
                        groupageNotes.add(n.getNotepad());
                    }
                }
            ObservableList<Notepad> list = FXCollections.observableArrayList();
            list.addAll(groupageNotes);
            notesListView.setItems(list);
        } */
    }

    public void editButton(ActionEvent actionEvent) {
        if(notesListView.getSelectionModel().isEmpty()) {
            InfoModal.show("Bitte wählen Sie die zu editierende Notiz aus.");
            return;
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

            try {
                if(this.objectType instanceof Student) {

                    Dao<StudentNotepad, Integer> studentNotepadDao = db.getStudentNotepadDao();
                    for(StudentNotepad n : studentNotepadDao) {
                        if(notesListView.getSelectionModel().getSelectedItem().equals(n.getNotepad().getNotepadName())) {
                            studentNotepadDao.delete(n);
                            notepadDao.delete(n.getNotepad());
                        }
                    }
                }
             /*   else if(this.objectType instanceof Groupage) {
                    this.notepad = notesListView.getSelectionModel().getSelectedItem();

                    Dao<GroupageNotepad, Integer> groupageNotepadDao = db.getGroupageNotepadDao();
                    for(GroupageNotepad n : groupageNotepadDao) {
                        if(n.getNotepad() == this.notepad) {
                            groupageNotepadDao.delete(n);
                        }
                    }
                    notepadDao.delete(this.notepad);
                }
                else if(this.objectType instanceof Group) {
                    this.notepad = notesListView.getSelectionModel().getSelectedItem();

                    Dao<GroupNotepad, Integer> groupNotepadDao = db.getGroupNotepadDao();
                    for(GroupNotepad n : groupNotepadDao) {
                        if(n.getNotepad() == this.notepad) {
                            groupNotepadDao.delete(n);
                        }
                    }
                    notepadDao.delete(this.notepad);
                } */
            } catch (SQLException e) {
                ErrorModal.show("Fehler: Die Notiz konnte nicht geloescht werden.");
            }
        }
        else {
            return;
        }
    }

    public void createButton(ActionEvent actionEvent) {
        SceneManager.getInstance().showInNewWindow(SceneType.CREATE_NOTEPAD_WINDOW);
    }

    public void showNoteButton(ActionEvent actionEvent) {
        if(notesListView.getSelectionModel().isEmpty()) {
            InfoModal.show("Bitte wählen Sie eine Notiz aus.");
            return;
        }
     //   SceneManager.getInstance().showInNewWindow(SceneType.NOTE_WINDOW);
    }

    public void cancelButton(ActionEvent actionEvent) {
       // SceneManager.getInstance().closeWindow(SceneType.NOTESTAB_WINDOW);
    }

    public void setObject(Object object) {this.objectType = object;} //Getting Object Type (Group, Groupage or Student)
}
