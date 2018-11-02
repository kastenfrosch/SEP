
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
import modal.InfoModal;
import models.*;
import utils.SceneManager;


import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.sql.SQLException;

public class CreateStudentController {

    private DBManager dbManager;

    {
        try {
            dbManager = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public ComboBox groupComboBox;
    @FXML
    public ComboBox semesterComboBox;

    @FXML
    private TextField firstnameInput;
    @FXML
    public AnchorPane anchorPane;

    @FXML
    private TextField lastnameInput;

    @FXML
    private TextField matNoInput;

    @FXML
    private Button StudentCreate;

    @FXML
    private Button StudentCancle;


    @FXML
    private TextField emailInput;

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
    public void onSaveBtnClicked(ActionEvent event) {

        Person person = new Person();
        Student student = new Student();

        // use all selections and text inputs to create a student.

        // making sure that matNo is not empty.

        if (matNoInput.getText().isEmpty() || matNoInput == null) {
            InfoModal.show("FEHLER!", null, "Keine Matrikelnummer eingegeben!");
            return;
        }
        // making sure that firstname is not empty.

        if (firstnameInput.getText().isEmpty() || firstnameInput == null) {
            InfoModal.show("FEHLER!", null, "Kein Vornamen eingegeben!");
            return;
            // making sure that lastname is not empty.

        }
        if (lastnameInput.getText().isEmpty() || lastnameInput == null) {
            InfoModal.show("FEHLER!", null, "Kein Nachnamen eingegeben!");
            return;
        }
        // making sure that mail adress is valid using the  validateMailAddress.

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

            //creat person & student
        person.setFirstname(firstnameInput.getText());
        person.setLastname(lastnameInput.getText());
        person.setEmail(emailInput.getText());
        student.setMatrNo(matNoInput.getText());

        student.setSemester(sCB);
        student.setGroup(gCB);


        try {
            //Creating the dao's
            Dao<Person, Integer> pDao = dbManager.getPersonDao();
            Dao<Student, Integer> sDao = dbManager.getStudentDao();
            pDao.create(person);
            sDao.create(student);


        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
            SceneManager.getInstance().closeWindow(SceneManager.SceneType.CREATE_STUDENT);

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

    @FXML
    void onCancelBTNClicked(ActionEvent event) {
        SceneManager.getInstance().closeWindow(SceneManager.SceneType.CREATE_STUDENT);


    }

    public void addToSemester(ActionEvent event) {
    }

    public void addToGroup(ActionEvent event) {
    }
}




