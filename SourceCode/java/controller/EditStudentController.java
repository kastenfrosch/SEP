package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
import models.Student;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
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
    private TextField lastnameInput;
    @FXML
    private TextField matNoInput;
    @FXML
    private TextField emailInput;
    @FXML
    public ComboBox<Group> groupComboBox;
    @FXML
    public void initialize() {

        // initializing combobox data

        try {

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

        //create person & student with the given attributes
        student.setGroup(groupComboBox.getSelectionModel().getSelectedItem());
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
                InfoModal.show("Der Student \"" + firstnameInput.getText() +" "+ lastnameInput.getText()+ "\" wurde bearbeitet!");
                SceneManager.getInstance().getLoaderForScene(SceneType.HOME).
                        <HomeScreenController>getController().setSelectedNode(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onCancelBTNClicked(ActionEvent event) {
        SceneManager.getInstance().getLoaderForScene(SceneType.HOME).
                <HomeScreenController>getController().showTabContent();
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
    }

    public void addToGroup(ActionEvent event) {
    }
}
