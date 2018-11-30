package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modal.ErrorModal;
import modal.InfoModal;
import models.*;
import utils.scene.SceneManager;
import utils.scene.SceneType;
import java.sql.SQLException;

    //todo: (600x400) Link zu Student Gruppe Goupage sodass die Notiz quasi dort eingeordnet ist; Tab mit Notizliste zur zugehoerigen Entitaet
    public class CreateNotepadController {

        private DBManager db;
        private Object objectType;

        {
            try {
                db = DBManager.getInstance();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @FXML
        public Label createNotepadLabel;
        @FXML
        public TextField bezeichnungTextField;
        @FXML
        public Button saveButton;
        @FXML
        public ComboBox<String> priorityComboBox;
        @FXML
        public TextArea notepadTextarea;
        @FXML
        public Button cancelButton;

        @FXML
        public void initialize() { //initializing ComboBox
            ObservableList<String> prioritaet = FXCollections.observableArrayList("Hoch", "Mittel",
                                                                                        "Niedrig", "Neutral");
            priorityComboBox.setItems(prioritaet);
            priorityComboBox.getSelectionModel().select(0);
            notepadTextarea.setStyle("-fx-background-color: red"); //Since first item of ComboBox is "Hoch"
        }

        public void setPriority(ActionEvent actionEvent) { //Setting Colors in relation to the chosen priority

            if (priorityComboBox.getSelectionModel().getSelectedItem().equals("Hoch")) {
                notepadTextarea.setStyle("-fx-background-color: red");
            } else if (priorityComboBox.getSelectionModel().getSelectedItem().equals("Mittel")) {
                notepadTextarea.setStyle("-fx-background-color: yellow");
            } else if (priorityComboBox.getSelectionModel().getSelectedItem().equals("Niedrig")) {
                notepadTextarea.setStyle("-fx-background-color: green");
            } else if (priorityComboBox.getSelectionModel().getSelectedItem().equals("Neutral")) {
                notepadTextarea.setStyle("-fx-background-color: grey");
            }
        }

        public void saveButton(ActionEvent actionEvent) { //Saving notepad

            String noteName;
            if(bezeichnungTextField == null || bezeichnungTextField.getText().isBlank()) {
                InfoModal.show("FEHLER!", null, "Bitte Bezeichnung eingeben!");
                return;
            }
            noteName = bezeichnungTextField.getText();

            String priority;
            if(priorityComboBox.getSelectionModel().getSelectedItem() == null) {
                InfoModal.show("FEHLER!", null, "Bitte Priorität bestimmen!");
                return;
            }
            priority = priorityComboBox.getSelectionModel().getSelectedItem();

            String textContent;
            if(notepadTextarea == null || notepadTextarea.getText().isBlank()) {
                InfoModal.show("FEHLER!", null, "Bitte Notiz eingeben");
                return;
            }
            textContent = notepadTextarea.getText();

            try {
                Notepad notepad = new Notepad();

                Dao<User, String> user = db.getUserDao(); //Testing
                User tester = user.queryForId("besttutor");
                notepad.setUser(tester);

                notepad.setNotepadContent(textContent);
                notepad.setNotepadPriority(priority);
                notepad.setNotepadName(noteName);
             //   notepad.setUser(db.getLoggedInUser());

                Dao<Notepad, Integer> notepadDao = db.getNotepadDao();
                notepadDao.create(notepad);

                Dao<Student, Integer> testStudent = db.getStudentDao(); //Testing for StudentNote
                Student student = testStudent.queryForId(1);
                this.objectType = student;

                if(this.objectType instanceof Student) {
                    StudentNotepad studentNotepad = new StudentNotepad();
                    studentNotepad.setStudent((Student) this.objectType);
                    studentNotepad.setNotepad(notepad); //Creating StudentNotepad
                   // studentNotepad.setUser(db.getLoggedInUser());

                    Dao<StudentNotepad, Integer> studentNotepadDao = db.getStudentNotepadDao();
                    studentNotepadDao.create(studentNotepad);
                }
                else if(this.objectType instanceof Groupage) {
                    GroupageNotepad groupageNotepad = new GroupageNotepad();
                    groupageNotepad.setGroupage((Groupage) this.objectType);
                    groupageNotepad.setNotepad(notepad);
                    //groupageNotepad.setUser(db.getLoggedInUser());

                    Dao<GroupageNotepad, Integer> groupageNotepadDao = db.getGroupageNotepadDao();
                    groupageNotepadDao.create(groupageNotepad);
                }
                else if(this.objectType instanceof Group) {
                    GroupNotepad groupNotepad = new GroupNotepad();
                    groupNotepad.setGroup((Group) this.objectType);
                    groupNotepad.setNotepad(notepad);
                    //groupNotepad.setUser(db.getLoggedInUser());

                    Dao<GroupNotepad, Integer> groupNotepadDao = db.getGroupNotepadDao();
                    groupNotepadDao.create(groupNotepad);
                }

                InfoModal.show("Notiz \"" + noteName + "\" erstellt!");
            } catch (java.sql.SQLException e) {
                ErrorModal.show("Die Notiz konnte nicht erstellt werden: " + e.getMessage());
                e.printStackTrace();
            } finally {
                SceneManager.getInstance().closeWindow(SceneType.CREATE_NOTEPAD_WINDOW);
            }
        }

        public void cancelButton(ActionEvent actionEvent) { //When Cancel Button is pressed
            SceneManager.getInstance().closeWindow(SceneType.CREATE_NOTEPAD_WINDOW);
        }

        public void setObject(Object object) {
            this.objectType = object;
        } //Getting Object info (Student, Group or Groupage)
    }
