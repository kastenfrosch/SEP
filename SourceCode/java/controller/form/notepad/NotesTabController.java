package controller.form.notepad;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
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
    
    public void initialize() {
        notesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        try {
            if (this.objectType instanceof Student) {
                // 2 ways on how to fill the ListView
               /* ObservableList<Notepad> list = FXCollections.observableArrayList();
                 for (StudentNotepad s : db.getStudentNotepadDao()) {
                    if (((((Student) this.objectType).getId() == s.getStudent().getId()))) {
                        list.add(s.getNotepad());
                    }
                }
                notesListView.setItems(list); */
                StudentNotepad suitingStudent = new StudentNotepad();
                suitingStudent.setStudent((Student) this.objectType);
                notesListView.getItems().clear();
                db.getStudentNotepadDao().queryForMatching(suitingStudent).stream()
                        .map(StudentNotepad::getNotepad)
                        .forEach(notesListView.getItems()::add);
            } else if (this.objectType instanceof Groupage) {
               /* ObservableList<Notepad> list = FXCollections.observableArrayList();
                for (GroupageNotepad s : db.getGroupageNotepadDao()) {
                    if ((((Groupage) this.objectType).getId() == s.getGroupage().getId())) {
                        list.add(s.getNotepad());
                    }
                }
                notesListView.setItems(list); */
                GroupageNotepad suitingGroupage = new GroupageNotepad();
                suitingGroupage.setGroupage((Groupage) this.objectType);
                notesListView.getItems().clear();
                db.getGroupageNotepadDao().queryForMatching(suitingGroupage).stream()
                        .map(GroupageNotepad::getNotepad)
                        .forEach(notesListView.getItems()::add);
            } else if (this.objectType instanceof Group) {
              /*  ObservableList<Notepad> list = FXCollections.observableArrayList();
                for (GroupNotepad s : db.getGroupNotepadDao()) {
                    if (((Group) this.objectType).getId() == s.getGroup().getId()) {
                        list.add(s.getNotepad());
                    }
                }
                notesListView.setItems(list); */
                GroupNotepad suitingGroup = new GroupNotepad();
                suitingGroup.setGroup((Group) this.objectType);
                notesListView.getItems().clear();
                db.getGroupNotepadDao().queryForMatching(suitingGroup).stream()
                        .map(GroupNotepad::getNotepad)
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

                                switch (item.getNotepadPriority()) {
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

    public void editButton(ActionEvent actionEvent) {
        if (notesListView.getSelectionModel().isEmpty()) {
            InfoModal.show("Bitte wählen Sie eine Notiz aus.");
            return;
        }
        SceneType sceneType;
        SceneManager sm = SceneManager.getInstance();
        //Setting given Object & Notepad for EditNotepadWindow
            sceneType = SceneType.EDIT_NOTEPAD_WINDOW;
            sm.getLoaderForScene(sceneType).<EditNotepadController>getController()
                    .setObject(this.objectType, notesListView.getSelectionModel().getSelectedItem());
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
                notepadDao.delete(notesListView.getSelectionModel().getSelectedItem());
                this.notesListView.getItems().remove(notesListView.getSelectionModel().getSelectedItem());
            } catch (SQLException e) {
                ErrorModal.show("Fehler: Die Notiz konnte nicht geloescht werden.");
            }
        } else {
            return;
        }
    }

    public void createButton(ActionEvent actionEvent) {
        SceneType sceneType;
        SceneManager sm = SceneManager.getInstance();
            sceneType = SceneType.CREATE_NOTEPAD_WINDOW;
            sm.getLoaderForScene(sceneType).<CreateNotepadController>getController()
                    .setObject(this.objectType);
        SceneManager.getInstance().showInNewWindow(SceneType.CREATE_NOTEPAD_WINDOW);
    }

    public void showNoteButton(ActionEvent actionEvent) {
        if (notesListView.getSelectionModel().isEmpty()) {
            InfoModal.show("Bitte wählen Sie eine Notiz aus.");
            return;
        }
        SceneType sceneType;
        SceneManager sm = SceneManager.getInstance();
            sceneType = SceneType.NOTE_WINDOW;
            sm.getLoaderForScene(sceneType).<NoteWindowController>getController()
                    .setNotepad(notesListView.getSelectionModel().getSelectedItem());
        SceneManager.getInstance().showInNewWindow(SceneType.NOTE_WINDOW);
    }

    public void setObject(Object object) {
        //Getting Object Type (Group, Groupage or Student)
        this.objectType = object;
        initialize();
    }
}