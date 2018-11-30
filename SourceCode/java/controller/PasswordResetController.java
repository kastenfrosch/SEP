package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import modal.ErrorModal;
import modal.InfoModal;
import models.User;
import utils.HashUtils;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.sql.SQLException;

public class PasswordResetController {
    User user = new User();
    private DBManager db;

    {
        try {
            db = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private PasswordField newPasswordTwo;

    @FXML
    private PasswordField newPassword;

    @FXML
    private PasswordField formerPassword;

    public void onCancelBtnClicked(ActionEvent event) {
        SceneManager.getInstance().closeWindow(SceneType.PASSWORD_RESET);
    }

    public void onSaveBtnClicked(ActionEvent event) {
      //check if password fields a equal and not blank
        if ((!(newPassword.getText().equals(newPasswordTwo.getText())))||newPassword.getText().isBlank()) {
            InfoModal.show("FEHLER!", null, "Passwörter sind nicht gleich!");
            return;
        }
        Dao<User, String> userDao = db.getUserDao();
        String pass = formerPassword.getText();

        String hexSalt = user.getSalt();
        String hexPass = user.getPasswordHash();

        byte[] hashedPass = HashUtils.hash(pass, HashUtils.fromHex(hexSalt));

        if (user.getPasswordHash().equals(HashUtils.toHex(hashedPass))) {
            byte[] newSalt = HashUtils.getRandomSalt();
            String newPass = newPassword.getText();

            byte[] hexNewPass = HashUtils.hash(newPass, newSalt);

            user.setPasswordHash(HashUtils.toHex(hexNewPass));
            user.setSalt(HashUtils.toHex(newSalt));

            try {
                userDao.update(user);
            } catch (SQLException ex) {
                ErrorModal.show("Passwort konnte nicht neu gesetzt werden.");
            }
        }
    }


    public void setUser(User user) {
        this.user = user;

    }
}
