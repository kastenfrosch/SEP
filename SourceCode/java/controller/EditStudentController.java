package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import modal.InfoModal;
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

}
