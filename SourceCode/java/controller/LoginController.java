package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import modal.ErrorModal;
import models.User;
import utils.HashUtils;
import utils.scene.SceneManager;
import utils.scene.SceneType;

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
    private Button loginBtn;

    @FXML
    public void onLoginBtnClicked(ActionEvent event) {
        DBManager dbManager;
        try {
            dbManager = DBManager.getInstance();
        } catch (SQLException ex) {
            ErrorModal.show("ERROR", "Database Error", ex.getMessage());
            return;
        }
        String username = usernameInput.getText();
        String pass = passwordInput.getText();

        Dao<User, String> userDao = dbManager.getUserDao();

        User user;

        try {
            user = userDao.queryForId(username);
            if (user == null) {
                throw new SQLException();
            }
        } catch (SQLException ex) {
            ErrorModal.show("Invalid User / Password");
            return;
        }

        String hexSalt = user.getSalt();
        String hexPass = user.getPasswordHash();

        byte[] salt = HashUtils.fromHex(hexSalt);
        byte[] passEntered = HashUtils.hash(pass, salt);

        if (HashUtils.toHex(passEntered).equals(hexPass)) {
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
            } catch (SQLException ex) {
                //we can theoretially ignore this exception because it only means that
                //updating the salt and hash failed. While it's not good, it will not
                //prevent the application from working properly and the old hash + password will not be altered.
            }

            dbManager.setLoggedInUser(user);
            SceneManager.getInstance().switchTo(SceneType.HOME);
        } else {
            ErrorModal.show("Invalid User / Password!");
        }
    }

    public void onRegisterBtnClicked(ActionEvent event) {
        SceneManager.getInstance().showInNewWindow(SceneType.REGISTER);
    }

    public void onKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) {
            this.loginBtn.fire();
            keyEvent.consume();
        }
    }
}
