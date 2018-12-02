package controller;

import connection.DBManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import models.*;
import utils.scene.SceneManager;
import utils.scene.SceneType;
import java.sql.SQLException;

public class NoteWindowController {

    private DBManager db;

    {
        try {
            db = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public TextArea notepadTextarea;
    @FXML
    public Button closeButton;
    @FXML
    public Label priorityLabel;
    @FXML
    public Label nameLabel;

    public void initialize() {
        if (priorityLabel.getText().equals("Hoch")) {
            notepadTextarea.setStyle("-fx-background-color: red");
        } else if (priorityLabel.getText().equals("Mittel")) {
            notepadTextarea.setStyle("-fx-background-color: yellow");
        } else if (priorityLabel.getText().equals("Niedrig")) {
            notepadTextarea.setStyle("-fx-background-color: green");
        } else if (priorityLabel.getText().equals("Neutral")) {
            notepadTextarea.setStyle("-fx-background-color: grey");
        }
        /*
        Dao<Notepad, Integer> notepadDao = db.getNotepadDao(); //Testing
        Notepad notepad = notepadDao.queryForId(8);

        Dao<StudentNotepad, Integer> studentNotepad = db.getStudentNotepadDao(); //Testing
        StudentNotepad studentNote = studentNotepad.queryForId(2);

        notepadTextarea.setText(studentNote.getNotepad().getNotepadContent());
        nameLabel.setText(studentNote.getNotepad().getNotepadName());
        priorityLabel.setText(studentNote.getNotepad().getNotepadPriority());
        */
    }

    public void closeButton(ActionEvent actionEvent) {
      //  SceneManager.getInstance().closeWindow(SceneType.NOTE_WINDOW);
    }

    public void setNotepad(Notepad notepad) { //Getting infos of opened Notepad
        nameLabel.setText(notepad.getNotepadName());
        notepadTextarea.setText(notepad.getNotepadContent());
        priorityLabel.setText(notepad.getNotepadPriority());
    }
}
