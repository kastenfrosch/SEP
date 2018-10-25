
package controller;


import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import modal.InfoModal;
import models.Person;
import models.Student;
import models.StudentModel;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.sql.SQLException;

public class AddStudentController {
    private DBManager  dbManager;

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
    private Text statusText;

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
    public void AddStudentSave(ActionEvent event) {

        Person person = new Person();

        Student student = new Student();

        if (matNoInput.getText().isEmpty()|| matNoInput==null) {
            InfoModal.show("FEHLER!", null, "Keine Matrikelnummer eingegeben!");
            return;
        }
        if (firstnameInput.getText().isEmpty()|| firstnameInput==null) {
            InfoModal.show("FEHLER!", null, "Kein Vornamen eingegeben!");
            return;

        }if (lastnameInput.getText().isEmpty()|| lastnameInput==null) {
            InfoModal.show("FEHLER!", null, "Kein Nachnamen eingegeben!");
            return;
        }
        if (validateMailAddress(emailInput.toString())==false|| emailInput==null) {
        InfoModal.show("FEHLER!", null, "E-Mail ist nicht korrekt!");
            return;
        }
        person.setFirstname(firstnameInput.getText());
        person.setLastname(lastnameInput.getText());
        person.setEmail(emailInput.getText());
        student.setMatrNo(matNoInput.getText());
        student.setPerson(person);

try{
            Dao<Person, Integer>pDao=dbManager.getPersonDao();
            Dao<Student, Integer> sDao = dbManager.getStudentDao();
            pDao.create(person);
            sDao.create(student);



    }   catch (java.sql.SQLException e) {
    e.printStackTrace();
}
    }





    private boolean validateMailAddress(String adr) {
        try {
            InternetAddress a = new InternetAddress(adr);
        } catch(AddressException e) {
            return false;
        }
        return true;
    }

    @FXML
    void AddStudentCancel(ActionEvent event) {


    }
        //go back to HomeScreen
}
