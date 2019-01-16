package controller.mail;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import modal.ErrorModal;
import modal.InfoModal;
import models.User;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.sql.SQLException;

public class AddEmailCredentials {


    private DBManager db;
    private User currentUser;

    {
        try {
            db = DBManager.getInstance();
            currentUser = db.getLoggedInUser();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Button cancelBTN;
    public Button saveBTN;
    public TextField usernameField;
    public TextField mailPasswordField;
    public TextField userPasswordField;
    public TextField IMAPServerField;
    public TextField IMAPPortField;
    public TextField SMTPServerField;
    public TextField SMTPPortField;


    public void onSaveBTNClicked(ActionEvent actionEvent) {

        // setting username
        String username;
        if (usernameField.getText().isBlank()) {
            InfoModal.show("ACHTUNG!", null, "Kein Username eingegeben!");
            return;
        }
        username = usernameField.getText();

        // setting mail password
        String mailPassword;
        if (mailPasswordField.getText().isBlank()) {
            InfoModal.show("ACHTUNG!", null, "Kein Mail Passwort eingegeben!");
            return;
        }
        mailPassword = mailPasswordField.getText();

        // TODO: do something else with it?
        // setting/getting?? user password
        String userPassword;
        if (userPasswordField.getText().isBlank()) {
            InfoModal.show("ACHTUNG!", null, "Kein User Passwort eingegeben!");
            return;
        }
        userPassword = userPasswordField.getText();

        // setting imap server
        String IMAPServer;
        if (IMAPServerField.getText().isBlank()) {
            InfoModal.show("ACHTUNG!", null, "Kein IMAP Server eingegeben!");
            return;
        }
        IMAPServer = IMAPServerField.getText();

        // setting imap port
        int IMAPPort = 0;
        if (IMAPPortField.getText().isBlank()) {
            InfoModal.show("ACHTUNG!", null, "Kein IMAP Port eingegeben!");
            return;
        }
        try {
            IMAPPort = Integer.parseInt(IMAPPortField.getText());
        } catch (NumberFormatException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }

        // setting smtp server
        String SMTPServer;
        if (SMTPServerField.getText().isBlank()) {
            InfoModal.show("ACHTUNG!", null, "Kein SMTP Server eingegeben!");
            return;
        }
        SMTPServer = SMTPServerField.getText();

        // setting smtp port
        int SMTPPort = 0;
        if (SMTPPortField.getText().isBlank()) {
            InfoModal.show("ACHTUNG!", null, "Kein SMTP Port eingegeben!");
            return;
        }
        try {
            SMTPPort = Integer.parseInt(SMTPPortField.getText());
        } catch (NumberFormatException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }

        // passing variables to current user instance
        try {
            this.currentUser.setMailUser(username);
            this.currentUser.setMailPassword(mailPassword);
            // TODO: what to do with variable "userPassword"?
            this.currentUser.setMailImapHost(IMAPServer);
            this.currentUser.setMailImapPort(IMAPPort);
            this.currentUser.setMailSmtpHost(SMTPServer);
            this.currentUser.setMailSmtpPort(SMTPPort);

            Dao<User, String> userDao = db.getUserDao();
            userDao.update(this.currentUser);

            InfoModal.show("Mail-Einstellungen für  \"" + this.currentUser + "\" wurden übernommen!");
        } catch (SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }

    }

    public void onCancelBTNClicked(ActionEvent actionEvent) {
        // close window
        SceneManager.getInstance().closeWindow(SceneType.MAIL_CREDENTIALS);
    }

}
