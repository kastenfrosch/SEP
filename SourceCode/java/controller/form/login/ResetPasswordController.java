
package controller.form.login;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import controller.mail.SendMailController;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import modal.ErrorModal;
import modal.InfoModal;
import models.Person;
import models.User;
import utils.HashUtils;
import utils.scene.SceneManager;
import utils.scene.SceneType;
import org.apache.commons.lang.RandomStringUtils;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;

public class ResetPasswordController {
    User user = new User();
    private DBManager dbManager;

    {
        try {
            dbManager = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public TextField usernameTextField;

    public void onResetBTNClicked(ActionEvent actionEvent) {
        if (usernameTextField.getText().isBlank()) {
            ErrorModal.show("Bitte geben Sie den Username ein.");
        }
        String username = usernameTextField.getText();
        Dao<User, String> userDao = dbManager.getUserDao();
        try {
            user = userDao.queryForId(usernameTextField.getText());
        } catch (SQLException e) {
            e.printStackTrace();
        }


        byte[] array = new byte[7]; // length is bounded by 7
        new Random().nextBytes(array);
        String newPass =    RandomStringUtils.randomAscii(32);

        byte[] newSalt = HashUtils.getRandomSalt();


        byte[] hexNewPass = HashUtils.hash(newPass, newSalt);

        user.setSalt(HashUtils.toHex(newSalt));
        user.setPasswordHash(HashUtils.toHex(hexNewPass));

        try {
            userDao.update(user);
            InfoModal.show("Das Passwort wurde zurückgesetzt. ");
            sendNewPassword(user, newPass);
            SceneManager.getInstance().closeWindow(SceneType.RESET_PASSWORD);
        } catch (SQLException ex) {
            ErrorModal.show("Passwort konnte nicht zurückgesetzt werden.");
        }

    }

    public void onCancelBTNClicked(ActionEvent actionEvent) {
        SceneManager.getInstance().closeWindow(SceneType.RESET_PASSWORD);
    }

    private void sendNewPassword(User user, String newPass){



        // Recipient's email ID needs to be mentioned.
        String to = user.getPerson().getEmail().toString();

        // Sender's email ID needs to be mentioned
        String from = "MailForSEP@gmail.com";


        final String password = "changeme123!";
        String port = "587";
        // Assuming you are sending email through relay.jangosmtp.net
        String host = "smtp.gmail.com";
        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        //props.put("mail.smtp.password", password);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");

        // Get the Session object.
        Session session = Session.getInstance(props, new SendMailController.MailAuthenticator(from, password));

        try {
            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            // Set Subject: header field
            message.setSubject("Dein Passwort wurde zurückgesetzt");

            // Now set the actual message
            message.setText("Lieber "+user.getUsername()+" dein Password wurde zurückgesetzt.\n"+
                    "Dein neues Password lautet: \n" + newPass+"\n"+
                    "Bitte denke daran dir ein neues Password im System zu setzten."
                    );

            // Send message
            Transport.send(message);
            InfoModal.show("Senden war erfolgreich!");

        } catch (MessagingException e) {
            ErrorModal.show("DümDüm senden war nichts");
            throw new RuntimeException(e);
        }
    }



    }



