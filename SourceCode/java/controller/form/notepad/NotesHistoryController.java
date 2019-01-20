package controller.form.notepad;

import connection.DBManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import modal.InfoModal;
import models.*;
import utils.scene.SceneManager;
import utils.scene.SceneType;
import java.sql.SQLException;

public class NotesHistoryController {

    private DBManager db;

    {
        try {
            db = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public ListView<NotepadHistory> notesHistoryListView;
    @FXML
    public Label notesHistoryLabel;
    @FXML
    public Button showNoteButton;
    @FXML
    public Button closeButton;

    public void initialize() {
        notesHistoryListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        try {
                notesHistoryListView.getItems().clear();
                notesHistoryListView.getItems().addAll(db.getNotepadHistoryDao().queryForAll());

            notesHistoryListView.setCellFactory(new Callback<ListView<NotepadHistory>, ListCell<NotepadHistory>>() {
                public ListCell<NotepadHistory> call(ListView<NotepadHistory> param) {
                    return new ListCell<NotepadHistory>() {
                        @Override
                        protected void updateItem(NotepadHistory item, boolean empty) {
                            super.updateItem(item, empty);

                            String style = "";
                            if (!empty && item != null) {
                                setText(item.getNotepad().getNotepadName());

                                switch (item.getNotepad().getClassification()) {
                                    case GOOD:
                                        style = "-fx-background-color: green";
                                        break;
                                    case MEDIUM:
                                        style = "-fx-background-color: yellow";
                                        break;
                                    case BAD:
                                        style = "-fx-background-color: red";
                                        break;
                                    case NEUTRAL:
                                        style = "-fx-background-color: grey";
                                        break;
                                }
                            } else {
                                setText("");
                            }
                            setStyle(style);
                        }
                    };
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void showNoteButton(ActionEvent actionEvent) {
        if (notesHistoryListView.getSelectionModel().isEmpty()) {
            InfoModal.show("Bitte wählen Sie eine Notiz aus.");
            return;
        }
        SceneType sceneType;
        SceneManager sm = SceneManager.getInstance();
        sceneType = SceneType.NOTES_HISTORY_SHOW_NOTE;
        sm.getLoaderForScene(sceneType).<NotesHistoryShowNoteController>getController()
                .setNotepad(notesHistoryListView.getSelectionModel().getSelectedItem());
        SceneManager.getInstance().showInNewWindow(SceneType.NOTES_HISTORY_SHOW_NOTE);
    }

    public void closeButton(ActionEvent actionEvent) {
        SceneManager.getInstance().closeWindow(SceneType.NOTES_HISTORY_VIEW);
    }
}