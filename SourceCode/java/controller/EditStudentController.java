package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import modal.ConfirmationModal;
import modal.InfoModal;
import models.Group;
import models.Semester;
import models.Student;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.sql.SQLException;

public class EditStudentController {
    private Student student;

    private DBManager dbManager;

    {
        try {
            dbManager = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private TextField firstnameInput;
    @FXML
    private AnchorPane anchorPane;


    @FXML
    private Button DeletStudent;

    @FXML
    private TextField lastnameInput;

    @FXML
    private Button StudentCancel;

    @FXML
    private Text statusText;

    @FXML
    private Button StudentCreate;

    @FXML
    private TextField matNoInput;

    @FXML
    private TextField emailInput;
    @FXML
    public ComboBox groupComboBox;
    @FXML
    public ComboBox semesterComboBox;
    @FXML
    public void initialize() {

        // initializing combobox data

        try {

            ObservableList<Semester> semesterList = FXCollections.observableArrayList();
            Dao<Semester, String> semester = dbManager.getSemesterDao();

            for (Semester s : semester.queryForAll()) {
                semesterList.add(s);
            }

            semesterComboBox.setItems(semesterList);


            ObservableList<Group> groupList = FXCollections.observableArrayList();
            Dao<Group, Integer> group = dbManager.getGroupDao();

            for (Group g : group.queryForAll()) {
                groupList.add(g);
            }

            groupComboBox.setItems(groupList);

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }


    }

    @FXML
    public void editStudentSave(ActionEvent event) {

        matNoInput.setText(student.getMatrNo());
        firstnameInput.setText(student.getPerson().getFirstname());
        lastnameInput.setText(student.getPerson().getLastname());
        emailInput.setText(student.getPerson().getEmail());


        if (matNoInput.getText().isEmpty() || matNoInput == null) {
            InfoModal.show("FEHLER!", null, "Keine Matrikelnummer eingegeben!");
            return;
        }
        if (firstnameInput.getText().isEmpty() || firstnameInput == null) {
            InfoModal.show("FEHLER!", null, "Kein Vornamen eingegeben!");
            return;

        }
        if (lastnameInput.getText().isEmpty() || lastnameInput == null) {
            InfoModal.show("FEHLER!", null, "Kein Nachnamen eingegeben!");
            return;
        }
        if (validateMailAddress(emailInput.toString()) || emailInput == null) {
            InfoModal.show("FEHLER!", null, "E-Mail ist nicht korrekt!");
            return;
        }
        Group gCB;
        if (groupComboBox.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("FEHLER!", null, "Keine Gruppe ausgewählt!");
            return;
        }
        gCB = (Group) groupComboBox.getSelectionModel().getSelectedItem();

        Semester sCB;
        if (semesterComboBox.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("FEHLER!", null, "Kein Semester ausgewählt!");
            return;
        }
        sCB = (Semester) semesterComboBox.getSelectionModel().getSelectedItem();

        Dao<Student, Integer> studentDao = dbManager.getStudentDao();

        try {
            studentDao.update(student);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void cancleEditStudent(ActionEvent event) {
        try {
            Parent p = FXMLLoader.load(getClass().getResource("/fxml/HomeScreenView.fxml"));
            anchorPane.getScene().setRoot(p);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void DeleteCurrentStudent(ActionEvent event) {

        boolean confirm = ConfirmationModal.show("Soll der Student wirklich gelöscht werden?");
        if(!confirm){
            return;
        }


        Dao<Student, Integer> studentDao = dbManager.getStudentDao();
        try {
            studentDao.delete(student);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private boolean validateMailAddress(String adr) {
        try {
            InternetAddress a = new InternetAddress(adr);
        } catch (AddressException e) {
            return false;
        }
        return true;
    }

    private void setStudent(Student student) {
        this.student = student;
    }

    public void addToGroup(ActionEvent event) {
    }

    public void addToSemester(ActionEvent event) {
    }
}
