package controller.form.notepad;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import models.*;
import utils.scene.SceneManager;
import utils.scene.SceneType;

public class NoteWindowController {

    @FXML
    public TextArea notepadTextarea;
    @FXML
    public Button closeButton;
    @FXML
    public Label priorityLabel;
    @FXML
    public Label nameLabel;

    public void initialize() {
        if (priorityLabel.getText().equals("Gut")) {
            notepadTextarea.setStyle("-fx-background-color: green");
            priorityLabel.setText("Spezifikation: Gut");
        } else if (priorityLabel.getText().equals("Mittel")) {
            notepadTextarea.setStyle("-fx-background-color: yellow");
            priorityLabel.setText("Spezifikation: Mittel");
        } else if (priorityLabel.getText().equals("Schlecht")) {
            notepadTextarea.setStyle("-fx-background-color: red");
            priorityLabel.setText("Spezifikation: Schlecht");
        } else if (priorityLabel.getText().equals("Keine Zuordnung")) {
            notepadTextarea.setStyle("-fx-background-color: grey");
            priorityLabel.setText("Spezifikation: Keine Zuordnung");
        }
    }

    public void closeButton(ActionEvent actionEvent) {
        SceneManager.getInstance().closeWindow(SceneType.NOTE_WINDOW);
    }

    public void setNotepad(Notepad notepad) { //Getting infos of opened Notepad
        nameLabel.setText(notepad.getNotepadName());
        notepadTextarea.setText(notepad.getNotepadContent());
        priorityLabel.setText(notepad.getClassification().toString());
        initialize();
    }
}