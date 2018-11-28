package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import modal.ErrorModal;
import modal.InfoModal;
import models.InviteCode;
import models.Person;
import models.User;
import utils.HashUtils;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.sql.SQLException;
import java.util.ArrayList;

public class RegisterController {
    private DBManager db;

    {
        try {
            db = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    Person person = new Person();
    User user = new User();
    @FXML
    private TextField firstnameInput;

    @FXML
    private TextField usernameInput;

    @FXML
    private TextField lastnameInput;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField passwordFieldTwo;

    @FXML
    private TextField emailInput;

    @FXML
    private TextField codeInputField;

    @FXML
    public void initialize() {
        ArrayList<InviteCode> allCodes = new ArrayList<>();


        firstnameInput.clear();
        lastnameInput.clear();
        passwordField.clear();
        emailInput.clear();
        passwordFieldTwo.clear();
        usernameInput.clear();
        codeInputField.clear();

    }


    public void onSaveBtnClicked(ActionEvent event) {

        if (firstnameInput.getText().isBlank()) {
            InfoModal.show("FEHLER!", null, "Kein Vornamen eingegeben!");
            return;

        }
        if (lastnameInput.getText().isBlank()) {
            InfoModal.show("FEHLER!", null, "Kein Nachnamen eingegeben!");
            return;
        }
        if (validateMailAddress(emailInput.toString()) || emailInput == null) {
            InfoModal.show("FEHLER!", null, "E-Mail ist nicht korrekt!");
            return;
        }
        if (passwordField.getText().isBlank()) {
            InfoModal.show("FEHLER!", null, "Kein Passwort eingegeben!");
            return;
        }
        if (passwordFieldTwo.getText().isBlank() || !passwordFieldTwo.getText().equals(passwordField.getText())) {
            InfoModal.show("FEHLER!", null, "Passwörter stimmen nicht überein");
            return;
        }

        if (usernameInput.getText().isBlank()) {
            InfoModal.show("FEHLER!", null, "Kein User Name eingegeben!");
            return;
        }

        if (codeInputField.getText().isBlank()) {
            InfoModal.show("FEHLER!", null, "Keinen Einwahlcode eingegben !");
            return;
        }
        Dao<InviteCode, String> codeDao = db.getInviteCodeDao();

            InviteCode checkCode;
        try {
             checkCode = codeDao.queryForId(codeInputField.getText());

            if (checkCode == null || codeDao.queryForId(codeInputField.getText()).getUsedBy() != null) {
                InfoModal.show("FEHLER!", null, "Der Einwahlcode ist nicht korrekt!");
                return;
            }
        } catch (SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
            return;
        }


        Dao<User, String> userDao = db.getUserDao();

        person.setFirstname(firstnameInput.getText());
        person.setLastname(lastnameInput.getText());
        person.setEmail(emailInput.getText());
        user.setUsername(usernameInput.getText());
        user.setPerson(person);
        checkCode.setUsedBy(user);

        byte[] salt = HashUtils.getRandomSalt();
        byte[] hashed = HashUtils.hash(passwordField.getText(), salt);

        String hexSalt = HashUtils.toHex(salt);
        String hexPass = HashUtils.toHex(hashed);
        user.setSalt(hexSalt);
        user.setPasswordHash(hexPass);




        try {
            userDao.create(user);
            codeDao.update(checkCode);

        } catch (SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }

    }

    public void onCancelBtnClicked(ActionEvent event) {
        SceneManager.getInstance().closeWindow(SceneType.REGISTER);
    }


    private boolean validateMailAddress(String adr) {
        try {
            new InternetAddress(adr);
        } catch (AddressException e) {
            return false;
        }
        return true;
    }


}
