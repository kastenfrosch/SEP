package controller;

import connection.DBManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import models.Notepad;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.sql.SQLException;

public class NoteWindowController {

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
    public TextArea notepadTextarea;
    @FXML
    public Button closeButton;
    @FXML
    public Label priorityLabel;
    @FXML
    public Label nameLabel;

    public void initialize() {
        nameLabel.setText(this.notepad.getNotepadName());
        notepadTextarea.setText(this.notepad.getNotepadContent());
        priorityLabel.setText(this.notepad.getNotepadPriority());
    }

    public void closeButton(ActionEvent actionEvent) {
//        SceneManager.getInstance().closeWindow(SceneType.NOTE_WINDOW);
   }
}
