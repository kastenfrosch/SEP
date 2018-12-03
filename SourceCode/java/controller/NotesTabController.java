package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import modal.ConfirmationModal;
import modal.ErrorModal;
import modal.InfoModal;
import models.*;
import utils.scene.SceneManager;
import utils.scene.SceneType;
import java.sql.SQLException;

public class NotesTabController {

    private Object objectType;
    private DBManager db;

    {
        try {
            db = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public ListView<Notepad> notesListView;
    @FXML
    public Button editButton;
    @FXML
    public Button deleteButton;
    @FXML
    public Button createButton;
    @FXML
    public Button showNoteButton;
    @FXML
    public Label notesPaneLabel;

    //todo: Aktualisierung wenn innerhalb eines Objects gewechselt wird (Bspw. Student 1 zu 2), Fehler liegt im Stream Filter (?)
    //todo: Löschfunktion korrigieren

    public void initialize() {
        notesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        try {
            if (this.objectType instanceof Student) {
                ObservableList<Notepad> list = FXCollections.observableArrayList();
                for (StudentNotepad s : db.getStudentNotepadDao()) {
                    if ((db.getLoggedInUser() == s.getNotepad().getUser()) && ((((Student) this.objectType).getId() == s.getStudent().getId()))) {
                        list.add(s.getNotepad());
                    }
                }
                notesListView.setItems(list);
                notesListView.getItems().clear(); //not needed if list is definitly empty
                db.getStudentNotepadDao().queryForAll().stream()
                        .map(StudentNotepad::getNotepad)
                        .filter(n -> n.getUser().equals(db.getLoggedInUser()))
                        .forEach(notesListView.getItems()::add);
            }
            else if (this.objectType instanceof Groupage) {
                ObservableList<Notepad> list = FXCollections.observableArrayList();
                for (GroupageNotepad s : db.getGroupageNotepadDao()) {
                    if (db.getLoggedInUser() == s.getNotepad().getUser() && (((Groupage) this.objectType).getId() == s.getGroupage().getId())) {
                        list.add(s.getNotepad());
                    }
                }
                notesListView.setItems(list);
                notesListView.getItems().clear(); // this is not necessary, if the list is guaranteed to be empty
                db.getGroupageNotepadDao().queryForAll().stream()
                        .map(GroupageNotepad::getNotepad)
                        .filter(n -> n.getUser().equals(db.getLoggedInUser()))
                        .forEach(notesListView.getItems()::add);
            }
            else if (this.objectType instanceof Group) {
                ObservableList<Notepad> list = FXCollections.observableArrayList();
                for (GroupNotepad s : db.getGroupNotepadDao()) {
                    if (db.getLoggedInUser() == s.getNotepad().getUser() && ((Group) this.objectType).getId() == s.getGroup().getId()) {
                        list.add(s.getNotepad());
                    }
                }
                notesListView.setItems(list);
                notesListView.getItems().clear(); // this is not necessary, if the list is guaranteed to be empty
                db.getGroupNotepadDao().queryForAll().stream()
                        .map(GroupNotepad::getNotepad)
                        .filter(n -> n.getUser().equals(db.getLoggedInUser()))
                        .forEach(notesListView.getItems()::add);
            }
            notesListView.setCellFactory(new Callback<ListView<Notepad>, ListCell<Notepad>>() {
                public ListCell<Notepad> call(ListView<Notepad> param) {
                    return new ListCell<Notepad>() {
                        @Override
                        protected void updateItem(Notepad item, boolean empty) {
                            super.updateItem(item, empty);

                            String style = "";
                            if (!empty && item != null) {
                                setText(item.getNotepadName());

                                // this switch could be rewritten using a Map<String, String>
                                switch (item.getNotepadPriority()) {
                                    case "Gut":
                                        style = "-fx-background-color: red";
                                        break;
                                    case "Mittel":
                                        style = "-fx-background-color: yellow";
                                        break;
                                    case "Schlecht":
                                        style = "-fx-background-color: green";
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

    public void editButton(ActionEvent actionEvent) {
        if (notesListView.getSelectionModel().isEmpty()) {
            InfoModal.show("Bitte wählen Sie eine Notiz aus.");
            return;
        }
        SceneType sceneType = null;
        SceneManager sm = SceneManager.getInstance();
        //Setting given Object & Notepad for EditNotepadWindow
        if (sceneType == null) {
            sceneType = SceneType.EDIT_NOTEPAD_WINDOW;
            sm.getLoaderForScene(sceneType).<EditNotepadController>getController()
                    .setObject(this.objectType, notesListView.getSelectionModel().getSelectedItem());
        }
        SceneManager.getInstance().showInNewWindow(SceneType.EDIT_NOTEPAD_WINDOW);
    }

    public void deleteButton(ActionEvent actionEvent) {

        if (notesListView.getSelectionModel().isEmpty()) {
            InfoModal.show("Bitte wählen Sie die zu löschende Notiz aus.");
            return;
        }

        boolean delete = ConfirmationModal.show("Soll die Notiz gelöscht werden?");
        if (delete) {
            Dao<Notepad, Integer> notepadDao = db.getNotepadDao();

            try {
                if (this.objectType instanceof Student) {
                    Dao<StudentNotepad, Integer> studentNotepadDao = db.getStudentNotepadDao();
                    for (StudentNotepad n : studentNotepadDao) {
                        if (notesListView.getSelectionModel().getSelectedItem().equals(n.getNotepad())) {
                            studentNotepadDao.delete(n);
                            notepadDao.delete(n.getNotepad());
                            SceneManager.getInstance().getLoaderForScene(SceneType.NOTESTAB_WINDOW).
                                    <NotesTabController>getController().initialize();
                        }
                    }
                }
                else if (this.objectType instanceof Group) {
                    Dao<GroupNotepad, Integer> groupNotepadDao = db.getGroupNotepadDao();
                    for (GroupNotepad n : groupNotepadDao) {
                        if (notesListView.getSelectionModel().getSelectedItem().equals(n.getNotepad())) {
                            groupNotepadDao.delete(n);
                            notepadDao.delete(n.getNotepad());
                            SceneManager.getInstance().getLoaderForScene(SceneType.NOTESTAB_WINDOW).
                                    <NotesTabController>getController().initialize();
                        }
                    }
                }
                else if (this.objectType instanceof Groupage) {
                    Dao<GroupageNotepad, Integer> groupageNotepadDao = db.getGroupageNotepadDao();
                    for (GroupageNotepad n : groupageNotepadDao) {
                        if (notesListView.getSelectionModel().getSelectedItem().equals(n.getNotepad())) {
                            groupageNotepadDao.delete(n);
                            notepadDao.delete(n.getNotepad());
                            SceneManager.getInstance().getLoaderForScene(SceneType.NOTESTAB_WINDOW).
                                    <NotesTabController>getController().initialize();
                        }
                    }
                }
            } catch (SQLException e) {
                ErrorModal.show("Fehler: Die Notiz konnte nicht geloescht werden.");
            }
        } else {
            return;
        }
    }

    public void createButton(ActionEvent actionEvent) {
        SceneType sceneType = null;
        SceneManager sm = SceneManager.getInstance();
        if (sceneType == null) {
            sceneType = SceneType.CREATE_NOTEPAD_WINDOW;
            sm.getLoaderForScene(sceneType).<CreateNotepadController>getController()
                    .setObject(this.objectType);
        }
        SceneManager.getInstance().showInNewWindow(SceneType.CREATE_NOTEPAD_WINDOW);
    }

    public void showNoteButton(ActionEvent actionEvent) {
        if (notesListView.getSelectionModel().isEmpty()) {
            InfoModal.show("Bitte wählen Sie eine Notiz aus.");
            return;
        }
        SceneType sceneType = null;
        SceneManager sm = SceneManager.getInstance();
        if (sceneType == null) {
            sceneType = SceneType.NOTE_WINDOW;
            sm.getLoaderForScene(sceneType).<NoteWindowController>getController()
                    .setNotepad(notesListView.getSelectionModel().getSelectedItem());
        }
        SceneManager.getInstance().showInNewWindow(SceneType.NOTE_WINDOW);
    }

    public void setObject(Object object) {
        //Getting Object Type (Group, Groupage or Student)
        this.objectType = object;
        initialize();
    }
}