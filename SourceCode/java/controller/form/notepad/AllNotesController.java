package controller.form.notepad;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    public Label chooseObjectLabel;
    @FXML
    public Label choosePriorityLabel;
    @FXML
    public ListView<Notepad> allNotesListView;
    @FXML
    public Button refreshButton;
    @FXML
    public ListView<Object> filterObjectListView;
    @FXML
    public Button filterButton;
    @FXML
    public ListView<Notepad.Classification> filterPrioListView;
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

        filterPrioListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        filterPrioListView.getItems().clear();
        filterPrioListView.setItems(FXCollections.observableArrayList(Notepad.Classification.GOOD, Notepad.Classification.MEDIUM,
                Notepad.Classification.BAD, Notepad.Classification.NEUTRAL));

        filterObjectListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        filterObjectListView.getItems().clear();

        db.getStudentDao().queryForAll().stream()
                .forEach(filterObjectListView.getItems()::add);

        db.getGroupDao().queryForAll().stream()
                .forEach(filterObjectListView.getItems()::add);

        db.getGroupageDao().queryForAll().stream()
                .forEach(filterObjectListView.getItems()::add);
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
            if(allNotesListView.getSelectionModel().getSelectedItem() == s.getNotepad()) {
                this.object = s.getStudent();
            }
        }
        for(GroupageNotepad s : groupageNotepadDao) {
            if(allNotesListView.getSelectionModel().getSelectedItem() == s.getNotepad()) {
                this.object = s.getGroupage();
            }
        }
        for(GroupNotepad s : groupNotepadDao) {
            if(allNotesListView.getSelectionModel().getSelectedItem() == s.getNotepad()) {
                this.object = s.getGroup();
            }
        }

        //Setting given Object & Notepad for EditNotepadWindow
        sm.getLoaderForScene(sceneType).<EditNotepadController>getController()
                .setObject(this.object, allNotesListView.getSelectionModel().getSelectedItem());
        SceneManager.getInstance().showInNewWindow(SceneType.EDIT_NOTEPAD_WINDOW);
    }

    public void refreshButton(ActionEvent actionEvent) throws SQLException{
        initialize();
    }

    public void filterButton(ActionEvent actionEvent) throws SQLException {
        ArrayList<Notepad> hilfsListe = new ArrayList<>();
        hilfsListe.addAll(allNotesListView.getItems());
        ListView<Notepad> hilfsListView = new ListView<>();

        /*
                    Wahrheitstabelle:       a b c
                                            0 0 0
                                            0 0 1
                                            0 1 0
                                            0 1 1
                                            1 0 0
                                            1 0 1
                                            1 1 0
                                            1 1 1

         */

        //Wenn nur Object ausgewählt
        if ((searchArea.getText() == null || searchArea.getText().isBlank()) && filterPrioListView.getSelectionModel().getSelectedItem() == null
                && filterObjectListView.getSelectionModel().getSelectedItem() != null) {

           if(filterObjectListView.getSelectionModel().getSelectedItem() instanceof Student) {
               for(StudentNotepad s : db.getStudentNotepadDao()) {
                   if(s.getStudent().equals(filterObjectListView.getSelectionModel().getSelectedItem())) {
                       hilfsListView.getItems().add(s.getNotepad());
                   }
               }
           } else if(filterObjectListView.getSelectionModel().getSelectedItem() instanceof Group) {
                for(GroupNotepad s : db.getGroupNotepadDao()) {
                    if(s.getGroup().equals(filterObjectListView.getSelectionModel().getSelectedItem())) {
                        hilfsListView.getItems().add(s.getNotepad());
                    }
                }
            } else if(filterObjectListView.getSelectionModel().getSelectedItem() instanceof Groupage) {
                for(GroupageNotepad s : db.getGroupageNotepadDao()) {
                    if(s.getGroupage().equals(filterObjectListView.getSelectionModel().getSelectedItem())) {
                        hilfsListView.getItems().add(s.getNotepad());
                    }
                }
            }
            allNotesListView.setItems(hilfsListView.getItems());

        } //Wenn nur Prio ausgewählt
        else if ((searchArea.getText() == null || searchArea.getText().isBlank()) && filterPrioListView.getSelectionModel().getSelectedItem() != null
                && filterObjectListView.getSelectionModel().getSelectedItem() == null) {

        }
        //Wenn nur Object und Prio ausgewählt
        else if ((searchArea.getText() == null || searchArea.getText().isBlank()) && filterPrioListView.getSelectionModel().getSelectedItem() != null
                && filterObjectListView.getSelectionModel().getSelectedItem() != null) {

        }
        //Wenn nur Suchwort eingegeben
        else if ((searchArea.getText() != null && !searchArea.getText().isBlank()) && filterPrioListView.getSelectionModel().getSelectedItem() == null
                && filterObjectListView.getSelectionModel().getSelectedItem() == null) {
            for (int i = 0; i < hilfsListe.size(); i++) {
                if (hilfsListe.get(i).getNotepadName().contains(searchArea.getText()) || hilfsListe.get(i).getNotepadContent().contains(searchArea.getText())) {
                    hilfsListView.getItems().add(hilfsListe.get(i));
                }
            }
            allNotesListView.setItems(hilfsListView.getItems());
        }
        //Wenn nur Suchwort & Objekt ausgewählt
        else if ((searchArea.getText() != null && !searchArea.getText().isBlank()) && filterPrioListView.getSelectionModel().getSelectedItem() == null
                && filterObjectListView.getSelectionModel().getSelectedItem() != null) {

        }
        //Wenn nur Suchwort & Prio ausgewählt
        else if ((searchArea.getText() != null && !searchArea.getText().isBlank()) && filterPrioListView.getSelectionModel().getSelectedItem() != null
                && filterObjectListView.getSelectionModel().getSelectedItem() == null) {

        }
        //Wenn alle Filtermöglichkeiten ausgewählt
        else if ((searchArea.getText() != null && !searchArea.getText().isBlank()) && filterPrioListView.getSelectionModel().getSelectedItem() != null
                && filterObjectListView.getSelectionModel().getSelectedItem() != null) {

        }
        else { //Wenn nichts ausgewählt
            allNotesListView.getItems().clear();
            initialize();
        }
    }

    public void closeWindow(ActionEvent actionEvent) throws SQLException {
        SceneManager sm = SceneManager.getInstance();
        sm.getLoaderForScene(SceneType.NOTESTAB_WINDOW).<NotesTabController>getController()
                .initialize();
        SceneManager.getInstance().closeWindow(SceneType.ALL_NOTES_WINDOW);
    }
}