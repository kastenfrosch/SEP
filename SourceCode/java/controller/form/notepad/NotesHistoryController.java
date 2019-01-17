package controller.form.notepad;

import connection.DBManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import modal.InfoModal;
import models.NotepadHistory;
import utils.scene.SceneManager;
import utils.scene.SceneType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    public ListView notesHistoryListView;
    @FXML
    public Label notesHistoryLabel;
    @FXML
    public Button showNoteButton;
    @FXML
    public Button recoverButton;
    @FXML
    public Button closeButton;

    public void initialize() throws SQLException {
        notesHistoryListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        notesHistoryListView.getItems().clear();
        db.getNotepadHistoryDao().queryForAll().stream()
                .map(NotepadHistory::getNotepad)
                .forEach(notesHistoryListView.getItems()::add);

        notesHistoryListView.setCellFactory(new Callback<ListView<NotepadHistory>, ListCell<NotepadHistory>>() {
            public ListCell<NotepadHistory> call(ListView<NotepadHistory> param) {
                return new ListCell<NotepadHistory>() {
                    @Override
                    protected void updateItem(NotepadHistory item, boolean empty) {
                        super.updateItem(item, empty);

                        String style = "";
                        if (!empty && item != null) {
                            setText(item.getNotepad().getNotepadName());

                            switch (item.getNotepad().getNotepadPriority()) {
                                case "Gut":
                                    style = "-fx-background-color: green";
                                    break;
                                case "Mittel":
                                    style = "-fx-background-color: yellow";
                                    break;
                                case "Schlecht":
                                    style = "-fx-background-color: red";
                                    break;
                                case "Ohne Zuordnung":
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
                .setNotepad((NotepadHistory)notesHistoryListView.getSelectionModel().getSelectedItem());
        SceneManager.getInstance().showInNewWindow(SceneType.NOTES_HISTORY_SHOW_NOTE);
    }

    public void recoverButton(ActionEvent actionEvent) throws SQLException{
        if(notesHistoryListView.getSelectionModel().isEmpty()) {
            InfoModal.show("Bitte wählen Sie eine Notiz aus.");
            return;
        }

        List<Object> choices = new ArrayList<>();
        choices.add(db.getStudentDao().queryForAll());
        choices.add(db.getGroupageDao().queryForAll());
        choices.add(db.getGroupDao().queryForAll());

        ChoiceDialog<Object> dialog = new ChoiceDialog<>(choices);
        dialog.setTitle("Choice Dialog");
        dialog.setHeaderText("Look, a Choice Dialog");
        dialog.setContentText("Choose your letter:");
    }

    public void closeButton(ActionEvent actionEvent) {
        SceneManager.getInstance().closeWindow(SceneType.NOTES_HISTORY_VIEW);
    }
}