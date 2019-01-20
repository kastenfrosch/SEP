package controller.form.notepad;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import models.Notepad;
import models.NotepadHistory;
import utils.scene.SceneManager;
import utils.scene.SceneType;

public class NotesHistoryShowNoteController {

    @FXML
    public TextArea notepadText;
    @FXML
    public Label noteNameLabel;
    @FXML
    public Label priorityLabel;
    @FXML
    public Label userLabel;
    @FXML
    public Label dateLabel;
    @FXML
    public Button closeButton;

    public void initialize () {
        if (priorityLabel.getText().equals(Notepad.Classification.GOOD)) {
            notepadText.setStyle("-fx-background-color: green");
            priorityLabel.setText("Spezifikation: Gut");
        } else if (priorityLabel.getText().equals(Notepad.Classification.MEDIUM)) {
            notepadText.setStyle("-fx-background-color: yellow");
            priorityLabel.setText("Spezifikation: Mittel");
        } else if (priorityLabel.getText().equals(Notepad.Classification.BAD)) {
            notepadText.setStyle("-fx-background-color: red");
            priorityLabel.setText("Spezifikation: Schlecht");
        } else if (priorityLabel.getText().equals(Notepad.Classification.NEUTRAL)) {
            notepadText.setStyle("-fx-background-color: grey");
            priorityLabel.setText("Spezifikation: Ohne Zuordnung");
        }
    }

    public void setNotepad(NotepadHistory notepadHistory) { //Getting infos of opened Notepad
        noteNameLabel.setText(notepadHistory.getNotepad().getNotepadName());
        notepadText.setText(notepadHistory.getNotepad().getNotepadContent());
        priorityLabel.setText(notepadHistory.getNotepad().getClassification().toString());
        userLabel.setText(notepadHistory.getUser().toString());
        dateLabel.setText(notepadHistory.getTimestamp().toString());
        initialize();
    }

    public void closeButton(ActionEvent actionEvent) {SceneManager.getInstance().closeWindow(SceneType.NOTES_HISTORY_SHOW_NOTE);}
}