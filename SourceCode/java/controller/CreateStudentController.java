
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
import modal.InfoModal;
import models.Person;
import models.Student;


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
    public void AddStudentSave(ActionEvent event) {

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
        //creat person & student
        person.setFirstname(firstnameInput.getText());
        person.setLastname(lastnameInput.getText());
        person.setEmail(emailInput.getText());
        student.setMatrNo(matNoInput.getText());
        student.setPerson(person);

        try {
            //Creating the dao's
            Dao<Person, Integer> pDao = dbManager.getPersonDao();
            Dao<Student, Integer> sDao = dbManager.getStudentDao();
            pDao.create(person);
            sDao.create(student);


        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
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
    void AddStudentCancel(ActionEvent event) {
        try {
            Parent p = FXMLLoader.load(getClass().getResource("/fxml/HomeScreenView.fxml"));
            anchorPane.getScene().setRoot(p);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
//go back to HomeScreen



