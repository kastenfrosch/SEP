package controller.form.notepad;

import com.j256.ormlite.dao.Dao;
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
    public ListView<NotepadHistory> notesHistoryListView;
    @FXML
    public Label notesHistoryLabel;
    @FXML
    public Button showNoteButton;
    @FXML
    public Button recoverButton;
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
        dialog.setTitle("Auswahldialog");
        dialog.setContentText("Wähle das Objekt:");
        Object item = dialog.getSelectedItem();

        Notepad notepad = new Notepad();
        notepad.setNotepadContent(notesHistoryListView.getSelectionModel()
                .getSelectedItem().getNotepad().getNotepadContent());
        notepad.setNotepadPriority(notesHistoryListView.getSelectionModel()
                .getSelectedItem().getNotepad().getNotepadPriority());
        notepad.setNotepadName(notesHistoryListView.getSelectionModel()
                .getSelectedItem().getNotepad().getNotepadName());

        Dao<Notepad, Integer> notepadDao = db.getNotepadDao();
        notepadDao.create(notepad);

        if(item instanceof Student) {
            StudentNotepad studentNotepad = new StudentNotepad();
            studentNotepad.setStudent((Student)item);
            studentNotepad.setNotepad(notepad);
            Dao<StudentNotepad, Integer> studentNotepadDao = db.getStudentNotepadDao();
            studentNotepadDao.create(studentNotepad);
            //Refreshing NotesTab & adding created Notepad
            SceneManager.getInstance().getLoaderForScene(SceneType.NOTESTAB_WINDOW).
                    <NotesTabController>getController().notesListView.getItems().add(studentNotepad.getNotepad());
        } else if(item instanceof Groupage) {
            GroupageNotepad groupageNotepad = new GroupageNotepad();
            groupageNotepad.setGroupage((Groupage)item);
            groupageNotepad.setNotepad(notepad);
            Dao<GroupageNotepad, Integer> groupageNotepadDao = db.getGroupageNotepadDao();
            groupageNotepadDao.create(groupageNotepad);
            //Refreshing NotesTab & adding created Notepad
            SceneManager.getInstance().getLoaderForScene(SceneType.NOTESTAB_WINDOW).
                    <NotesTabController>getController().notesListView.getItems().add(groupageNotepad.getNotepad());
        } else if(item instanceof Group) {
            GroupNotepad groupNotepad = new GroupNotepad();
            groupNotepad.setGroup((Group)item);
            groupNotepad.setNotepad(notepad);
            Dao<GroupNotepad, Integer> groupNotepadDao = db.getGroupNotepadDao();
            groupNotepadDao.create(groupNotepad);
            //Refreshing NotesTab & adding created Notepad
            SceneManager.getInstance().getLoaderForScene(SceneType.NOTESTAB_WINDOW).
                    <NotesTabController>getController().notesListView.getItems().add(groupNotepad.getNotepad());
        }
    }

    public void closeButton(ActionEvent actionEvent) {
        SceneManager.getInstance().closeWindow(SceneType.NOTES_HISTORY_VIEW);
    }
}