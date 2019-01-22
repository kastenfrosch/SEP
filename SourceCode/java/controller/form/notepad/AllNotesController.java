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

public class AllNotesController {

    private DBManager db;
    private Object object;

    {
        try {
            db = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public ListView<Notepad> allNotesListView;
    @FXML
    public Button showNote;
    @FXML
    public Button editNote;
    @FXML
    public Button closeWindow;
    @FXML
    public TextField searchArea;

    public void initialize() throws SQLException {

        allNotesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        allNotesListView.getItems().clear();

        db.getNotepadDao().queryForAll().stream()
                .forEach(allNotesListView.getItems()::add);

        allNotesListView.setCellFactory(new Callback<ListView<Notepad>, ListCell<Notepad>>() {
            public ListCell<Notepad> call(ListView<Notepad> param) {
                return new ListCell<Notepad>() {
                    @Override
                    protected void updateItem(Notepad item, boolean empty) {
                        super.updateItem(item, empty);

                        String style = "";
                        if (!empty && item != null) {
                            setText(item.getNotepadName());

                            switch (item.getClassification()) {
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
    }

    public void showNote(ActionEvent actionEvent) {
        if (allNotesListView.getSelectionModel().isEmpty()) {
            InfoModal.show("Bitte wählen Sie eine Notiz aus.");
            return;
        }
        SceneType sceneType;
        SceneManager sm = SceneManager.getInstance();
        sceneType = SceneType.NOTE_WINDOW;
        sm.getLoaderForScene(sceneType).<NoteWindowController>getController()
                .setNotepad((Notepad)allNotesListView.getSelectionModel().getSelectedItem());
        SceneManager.getInstance().showInNewWindow(SceneType.NOTE_WINDOW);
    }

    public void searchArea(ActionEvent actionEvent) throws SQLException {
        if(searchArea.getText() != null && !searchArea.getText().isBlank()) {
            ArrayList<Notepad> hilfsListe = new ArrayList<>();
            hilfsListe.addAll(allNotesListView.getItems());
            ListView<Notepad> hilfsListView = new ListView<>();

            for (int i = 0; i < hilfsListe.size(); i++) {
                if (hilfsListe.get(i).getNotepadName().contains(searchArea.getText())) {
                    hilfsListView.getItems().add(hilfsListe.get(i));
                }
                if(hilfsListe.get(i).getNotepadContent().contains(searchArea.getText())) {
                    hilfsListView.getItems().add(hilfsListe.get(i));
                }
            }
            allNotesListView.setItems(hilfsListView.getItems());
        }
        else {
            allNotesListView.getItems().clear();
            initialize();
        }
    }

    public void editNote(ActionEvent actionEvent) throws SQLException {
        if (allNotesListView.getSelectionModel().isEmpty()) {
            InfoModal.show("Bitte wählen Sie eine Notiz aus.");
            return;
        }
        SceneType sceneType = SceneType.EDIT_NOTEPAD_WINDOW;
        SceneManager sm = SceneManager.getInstance();

        Dao<StudentNotepad, Integer> studentNotepadDao = db.getStudentNotepadDao();
        Dao<GroupNotepad, Integer> groupNotepadDao = db.getGroupNotepadDao();
        Dao<GroupageNotepad, Integer> groupageNotepadDao = db.getGroupageNotepadDao();

        for(StudentNotepad s : studentNotepadDao) {
            if(allNotesListView.getSelectionModel().getSelectedItem().equals(s.getNotepad())) {
                this.object = s.getStudent();
            }
        }
        for(GroupageNotepad s : groupageNotepadDao) {
            if(allNotesListView.getSelectionModel().getSelectedItem().equals(s.getNotepad())) {
                this.object = s.getGroupage();
            }
        }
        for(GroupNotepad s : groupNotepadDao) {
            if(allNotesListView.getSelectionModel().getSelectedItem().equals(s.getNotepad())) {
                this.object = s.getGroup();
            }
        }

        //Setting given Object & Notepad for EditNotepadWindow
        sm.getLoaderForScene(sceneType).<EditNotepadController>getController()
                .setObject(this.object, allNotesListView.getSelectionModel().getSelectedItem());
        SceneManager.getInstance().showInNewWindow(SceneType.EDIT_NOTEPAD_WINDOW);
    }

    public void closeWindow(ActionEvent actionEvent) throws SQLException {
        SceneManager sm = SceneManager.getInstance();
        sm.getLoaderForScene(SceneType.NOTESTAB_WINDOW).<NotesTabController>getController()
                .initialize();
        SceneManager.getInstance().closeWindow(SceneType.ALL_NOTES_WINDOW);
    }
}