package controller.form.useradmin;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import modal.InfoModal;
import models.Person;
import models.User;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.sql.SQLException;

public class EditUserController {
    User user = new User();
    Person person = new Person();
    private DBManager dbManager;

    {
        try {
            dbManager = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private TextField lastnameTextfield;


    @FXML
    private TextField emailTextfield;

    @FXML
    private TextField firstnameTextfield;

    @FXML
    private TextField usernameTextfield;


    @FXML
    public void onSaveBtnClicked(ActionEvent event) {

        // making sure that firstname is not empty.
        if (firstnameTextfield.getText().isBlank()) {
            InfoModal.show("FEHLER!", null, "Kein Vornamen eingegeben!");
            return;

        }
        // making sure that lastname is not empty.
        if (lastnameTextfield.getText().isBlank()) {
            InfoModal.show("FEHLER!", null, "Kein Nachnamen eingegeben!");
            return;
        }
        // making sure that email is not empty & valid.
        if (!validateMailAddress(emailTextfield.getText())) {
            InfoModal.show("FEHLER!", null, "E-Mail ist nicht korrekt!");
            return;
        }
        if (usernameTextfield.getText().isBlank()) {
            InfoModal.show("FEHLER!", null, "Kein Username eingegeben!");
            return;
        }
        user.setUsername(usernameTextfield.getText());
        user.getPerson().setFirstname(firstnameTextfield.getText());
        user.getPerson().setLastname(lastnameTextfield.getText());
        user.getPerson().setEmail(emailTextfield.getText());

        Dao<User, String> userDao = dbManager.getUserDao();
        Dao<Person, Integer> personDao = dbManager.getPersonDao();
        try {
            //update the daos with the created person and student
            personDao.update(user.getPerson());
            userDao.update(user);
            //notification of the successful change
            if (!this.user.getUsername().equals(null)) {
                InfoModal.show("Der User \"" + firstnameTextfield.getText() + " " + lastnameTextfield.getText() + "\" wurde bearbeitet!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        SceneManager.getInstance().closeWindow(SceneType.EDIT_USER);

    }

    @FXML
    void onCancelBtnClicked(ActionEvent event) {
        SceneManager.getInstance().closeWindow(SceneType.EDIT_USER);

    }

    @FXML
    void onPasswordResetClicked(ActionEvent event) {
        SceneManager.getInstance().getLoaderForScene(SceneType.PASSWORD_RESET)
                .<PasswordResetController>getController()
                .setUser(this.user);
        SceneManager.getInstance().showInNewWindow(SceneType.PASSWORD_RESET);

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

    public void setUser(User user) {
        this.user = user;
        lastnameTextfield.setText(user.getPerson().getLastname());
        firstnameTextfield.setText(user.getPerson().getFirstname());
        emailTextfield.setText(user.getPerson().getEmail());
        usernameTextfield.setText(user.getUsername());

    }

}
