package controller.mail;

import connection.DBManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import modal.ErrorModal;
import modal.InfoModal;
import models.User;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.sql.SQLException;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class SendMail {
    private DBManager db;

    {
        try {
            db = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private TextField targetAdressTextField;
    @FXML
    private TextField subjectTextField;
    @FXML
    private TextArea mailTextArea;

    @FXML
    private void onSendBTNClicked(ActionEvent actionEvent) {
        // Recipient's email ID needs to be mentioned.
        String to = targetAdressTextField.getText();//change accordingly

        // Sender's email ID needs to be mentioned
        String from = "MailForSEP@gmail.com";//change accordingly
        final String username = "MailForSEP@gmail.com";//change accordingly
        final String password = "changeme123!";//change accordingly

        // Assuming you are sending email through relay.jangosmtp.net
        String host = "smtp.gmail.com";
        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", password);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        // Get the Session object.
        Session session = Session.getInstance(props, new GMailAuthenticator(username, password));

        try {
            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            // Set Subject: header field
            message.setSubject(subjectTextField.getText());

            // Now set the actual message
            message.setText(mailTextArea.getText());

            // Send message
            Transport.send(message);
            InfoModal.show("Senden war erfolgreich!");
            SceneManager.getInstance().closeWindow(SceneType.SEND_MAIL);

        } catch (MessagingException e) {
            ErrorModal.show("DümDüm senden war nichts");
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onSaveBTNClicked(ActionEvent actionEvent) {
    }

    @FXML
    private void onCancelBTNClicked(ActionEvent actionEvent) {
        SceneManager.getInstance().closeWindow(SceneType.SEND_MAIL);
    }

    class GMailAuthenticator extends Authenticator {
        String user;
        String password;
        public GMailAuthenticator (String username, String password)
        {
            super();
            this.user = username;
            this.password = password;
        }
        public PasswordAuthentication getPasswordAuthentication()
        {
            return new PasswordAuthentication(user, password);
        }
    }
}
