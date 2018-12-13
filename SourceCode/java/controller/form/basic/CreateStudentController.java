
package controller.form.basic;


import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import controller.HomeScreenController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import modal.ErrorModal;
import modal.InfoModal;
import models.*;
import utils.scene.SceneManager;
import utils.scene.SceneType;


import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
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
    public ComboBox<Group> groupComboBox;
    @FXML
    private TextField firstnameInput;
    @FXML
    public AnchorPane anchorPane;

    @FXML
    private TextField lastnameInput;

    @FXML
    private TextField matNoInput;


    @FXML
    private TextField emailInput;

    @FXML
    public void initialize() {

        // initializing combobox data
        try {

            // creat an observableList with all groups
            ObservableList<Group> groupList = FXCollections.observableArrayList();
            Dao<Group, Integer> group = dbManager.getGroupDao();
            groupList.addAll(group.queryForAll());
            //set semester combobox with the semester from the observableList
            groupComboBox.setItems(groupList);

        } catch (java.sql.SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }
        //clear all fields in the view
        groupComboBox.getSelectionModel().select(0);
        firstnameInput.clear();
        lastnameInput.clear();
        matNoInput.clear();
        emailInput.clear();


    }

    @FXML
    public void onSaveBtnClicked(ActionEvent event) {

        Person person = new Person();
        Student student = new Student();


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

        if (validateMailAddress(emailInput.getText())==false) {
            InfoModal.show("FEHLER!", null, "E-Mail ist nicht korrekt!");
            return;
        }
        if (groupComboBox.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("FEHLER!", null, "Keine Gruppe ausgewählt!");
            return;
        }

        //create person & student with the given attributes
        person.setFirstname(firstnameInput.getText());
        person.setLastname(lastnameInput.getText());
        person.setEmail(emailInput.getText());
        student.setMatrNo(matNoInput.getText());
        student.setGroup(groupComboBox.getSelectionModel().getSelectedItem());
        student.setPerson(person);


        try {
            //Creating the daos
            Dao<Person, Integer> pDao = dbManager.getPersonDao();
            Dao<Student, Integer> sDao = dbManager.getStudentDao();
            pDao.create(person);
            sDao.create(student);
            //notification that the student is now created
            if (student.getId() != 0) {
                InfoModal.show("Der Student \"" + firstnameInput.getText() +" "+ lastnameInput.getText()+ "\" wurde erstellt!");}
            SceneManager.getInstance().getLoaderForScene(SceneType.HOME).
                    <HomeScreenController>getController().setSelectedNode(student);
        } catch (java.sql.SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }
    }

    //Validate the Mail Address by using the javaax.mail InternetAddress object.
    private boolean validateMailAddress(String adr) {
        try {
            InternetAddress address = new InternetAddress(adr);
            address.validate();
            return true;
        } catch (AddressException e) {
            return false;
        }}

    @FXML
    //back to homeview
    void onCancelBtnClicked(ActionEvent event) {
        SceneManager.getInstance().getLoaderForScene(SceneType.HOME).
                <HomeScreenController>getController().showTabContent();
    }


}




