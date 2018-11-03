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
import modal.ErrorModal;
import modal.InfoModal;
import models.Group;
import models.Person;
import models.Semester;
import models.Student;
import utils.SceneManager;

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
    public ComboBox<Group> groupComboBox;
    @FXML
    public ComboBox<Semester> semesterComboBox;
    @FXML
    public void initialize() {

        // initializing combobox data

        try {
            // creat an observableList with all semesters
            ObservableList<Semester> semesterList = FXCollections.observableArrayList();
            Dao<Semester, String> semesterDao = dbManager.getSemesterDao();
            semesterList.addAll(semesterDao.queryForAll());
            //set semester combobox with the semester from the observableList
            semesterComboBox.setItems(semesterList);

            // creat an observableList with all groups
            ObservableList<Group> groupList = FXCollections.observableArrayList();
            Dao<Group, Integer> groupDao = dbManager.getGroupDao();
            groupList.addAll(groupDao.queryForAll());
            //set semester combobox with the semester from the observableList
            groupComboBox.setItems(groupList);

        } catch (java.sql.SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }


    }

    @FXML
    public void onSaveBtnClicked(ActionEvent event) {

        // making sure that matNo is not empty.
        if (matNoInput.getText().isEmpty() || matNoInput == null) {
            InfoModal.show("FEHLER!", null, "Keine Matrikelnummer eingegeben!");
            return;
        }
        // making sure that firstname is not empty.
        if (firstnameInput.getText().isEmpty() || firstnameInput == null) {
            InfoModal.show("FEHLER!", null, "Kein Vornamen eingegeben!");
            return;

        }
        // making sure that lastname is not empty.
        if (lastnameInput.getText().isEmpty() || lastnameInput == null) {
            InfoModal.show("FEHLER!", null, "Kein Nachnamen eingegeben!");
            return;
        }
        // making sure that email is not empty & valid.
        if (validateMailAddress(emailInput.toString()) || emailInput == null) {
            InfoModal.show("FEHLER!", null, "E-Mail ist nicht korrekt!");
            return;
        }
        // making sure that group is not empty.
        if (groupComboBox.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("FEHLER!", null, "Keine Gruppe ausgewählt!");
            return;
        }
        // making sure that semester is not empty.
        if (semesterComboBox.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("FEHLER!", null, "Kein Semester ausgewählt!");
            return;
        }
        //create person & student with the given attributes
        student.setGroup(groupComboBox.getSelectionModel().getSelectedItem());
        student.setSemester(semesterComboBox.getSelectionModel().getSelectedItem());
        student.setMatrNo(matNoInput.getText());
        student.getPerson().setFirstname(firstnameInput.getText());
        student.getPerson().setLastname(lastnameInput.getText());
        student.getPerson().setEmail(emailInput.getText());
    //creating the student & person daos
        Dao<Student, Integer> studentDao = dbManager.getStudentDao();
        Dao<Person, Integer> personDao = dbManager.getPersonDao();
        try {
            //update the daos with the created person and student
            personDao.update(student.getPerson());
            studentDao.update(student);
            //notification of the successful change
            if (this.student.getId() != 0) {
                InfoModal.show("Der Student \"" + firstnameInput.getText() +" "+ lastnameInput.getText()+ "\" wurde bearbeitet!");}
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //back to homeview
        SceneManager.getInstance().closeWindow(SceneManager.SceneType.EDIT_STUDENT);
    }

    @FXML
    //back to homeview
    public void onCancelBTNClicked(ActionEvent event) {
        SceneManager.getInstance().closeWindow(SceneManager.SceneType.EDIT_STUDENT);
    }

    @FXML
    public void onDeleteBTNClicked(ActionEvent event) {
        //check if sure
        boolean confirm = ConfirmationModal.show("Soll der Student wirklich gelöscht werden?");
        if(!confirm){
            return;
        }

        Dao<Student, Integer> studentDao = dbManager.getStudentDao();
        try {
            // delete the student
            studentDao.delete(student);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //back to homwview
        SceneManager.getInstance().closeWindow(SceneManager.SceneType.EDIT_STUDENT);

    }
    //Validate the Mail Address by using the javaax.mail InternetAddress object.
    private boolean validateMailAddress(String adr) {
        try {
            InternetAddress a = new InternetAddress(adr);
        } catch (AddressException e) {
            return false;
        }
        return true;
    }

//Set the student
    public void setStudent(Student student) {
        this.student = student;
        lastnameInput.setText(student.getPerson().getLastname());
        firstnameInput.setText(student.getPerson().getFirstname());
        emailInput.setText(student.getPerson().getEmail());
        matNoInput.setText(student.getMatrNo());
        groupComboBox.getSelectionModel().select(student.getGroup());
        semesterComboBox.getSelectionModel().select(student.getSemester());
    }

    public void addToGroup(ActionEvent event) {
    }

    public void addToSemester(ActionEvent event) {
    }


}
