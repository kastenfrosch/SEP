package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import modal.ErrorModal;
import models.User;
import utils.HashUtils;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Label passwordLbl;

    @FXML
    private TextField usernameInput;

    @FXML
    private Text titleText;

    @FXML
    private Text subtitleText;

    @FXML
    private Label usernameLbl;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private Text statusText;

    @FXML
    public void onLoginBtnClicked(ActionEvent event) {
        DBManager dbManager;
        try {
            dbManager = DBManager.getInstance();
        } catch(SQLException ex) {
            ErrorModal.show("ERROR", "Database Error", ex.getMessage());
            return;
        }
        String username = usernameInput.getText();
        String pass = passwordInput.getText();

        Dao<User, String> userDao = dbManager.getUserDao();

        User user;

        try {
            user = userDao.queryForId(username);
            if(user == null) {
                throw new SQLException();
            }
        } catch(SQLException ex) {
            //TODO: put this in a function it's a dupe
            ErrorModal.show("Invalid User / Password");
            return;
        }

        String hexSalt = user.getSalt();
        String hexPass = user.getPasswordHash();

        byte[] salt = HashUtils.fromHex(hexSalt);
        byte[] passEntered = HashUtils.hash(pass, salt);

        if(HashUtils.toHex(passEntered).equals(hexPass)) {
            //dingding, you're in
            //now we re-calculate a new hash with a new salt. Security, baby!
            salt = HashUtils.getRandomSalt();

            byte[] hashed = HashUtils.hash(pass, salt);

            hexSalt = HashUtils.toHex(salt);
            hexPass = HashUtils.toHex(hashed);
            user.setSalt(hexSalt);
            user.setPasswordHash(hexPass);

            try {
                userDao.update(user);
            } catch(SQLException ex) {
                //we can theoretially ignore this exception because it only means that
                //updating the salt and hash failed. While it's not good, it will not
                //prevent the application from working properly and the old hash + password will not be altered.
            }
            try {
                Parent p = FXMLLoader.load(getClass().getResource("/fxml/HomeScreenView.fxml"));
                rootPane.getScene().setRoot(p);
            } catch(IOException ex) {
                ErrorModal.show(
                        "Error",
                        "A critical error has occured and the program needs to shut down",
                        "The login was successful but an internal exception was encountered: " + ex.getMessage());
            }
        }
        else
        {
            //TODO: put this in a function it's a dupe
            ErrorModal.show("Invalid User / Password!");
        }
    }

}
