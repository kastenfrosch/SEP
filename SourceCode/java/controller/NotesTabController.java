package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import modal.ConfirmationModal;
import modal.ErrorModal;
import models.Notepad;
import utils.scene.SceneManager;
import utils.scene.SceneType;
import java.sql.SQLException;
import java.util.ArrayList;

public class NotesTabController {

    private Notepad notepad;
    private DBManager db;

    {
        try {
            db = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public TilePane notesTilePane;
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

    public void initialize() { //initializing tilePane
        notesTilePane.setOrientation(Orientation.VERTICAL);
        notesTilePane.setAlignment(Pos.CENTER_LEFT);

        /* int x = 0;
        try{
        for(Notepad n : db.getNotepadDao().queryForAll()) { //Setting the number of rows depending on right Notes
            if (this.notepad.getNotepadObject() == ) {
                x++;
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        notesTilePane.setPrefRows(x);

        ArrayList<String> notesList = new ArrayList<>();
        try {
            for (Notepad n : db.getNotepadDao().queryForAll()) { //initializing tilePane items depending on notepad = entity
                if (this.notepad.getNotepadObject() == db.getNotepadDao().getTableName()) {
                    notesList.add(this.notepad.getNotepadName());
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        ObservableList list = notesTilePane.getChildren();
        list.addAll(notesList); */
    }

    public void editButton(ActionEvent actionEvent) {
        SceneManager.getInstance().showInNewWindow(SceneType.EDIT_NOTEPAD_WINDOW);
    }

    public void deleteButton(ActionEvent actionEvent) {
        boolean delete = ConfirmationModal.show("Soll die Notiz gelöscht werden?");
        if (delete) {
            Dao<Notepad, Integer> notepadDao = db.getNotepadDao();
            try {
                notepadDao.delete(this.notepad);
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
//        SceneManager.getInstance().closeWindow(SceneType.NOTE_WINDOW);
    }

    public void cancelButton(ActionEvent actionEvent) {
//        SceneManager.getInstance().closeWindow(SceneType.NOTESTAB_WINDOW);
    }
}
