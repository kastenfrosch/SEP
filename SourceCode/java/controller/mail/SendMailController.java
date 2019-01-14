package controller.mail;

import connection.DBManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import modal.ErrorModal;
import modal.InfoModal;
import models.User;
import utils.HashUtils;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class SendMailController {

    private User currentUser;

    private DBManager db;
    private String content;
    private String subject;

    {
        try {
            db = DBManager.getInstance();
            currentUser = db.getLoggedInUser();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public TextField targetAddressTextField;
    public TextField subjectTextField;
    public TextArea mailTextArea;
    public Button sendBTN;
    public Button saveBTN;
    public Button cancelBTN;
    public Button templateBTN;
    public Button contactsBTN;

    String pass;


    @FXML
    private void onSendBTNClicked(ActionEvent actionEvent) {

        // Recipient's email ID needs to be mentioned.
        String to = targetAddressTextField.getText();//change accordingly

        // Sender's email ID needs to be mentioned
        String from = currentUser.getPerson().getEmail().toString();//change accordingly


       final String password = "";
        String port = "";
        // Assuming you are sending email through relay.jangosmtp.net
        String host = "";
        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        //props.put("mail.smtp.password", password);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");

        // Get the Session object.
       Session session = Session.getInstance(props, new GMailAuthenticator(currentUser.getMailUser(), password));

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
        // TODO: save mail into templates?
    }

    @FXML
    private void onCancelBTNClicked(ActionEvent actionEvent) {
        SceneManager.getInstance().closeWindow(SceneType.SEND_MAIL);
    }

    public void onTemplateBTNClicked(ActionEvent event) {
        SceneManager.getInstance().showInNewWindow(SceneType.MAIL_TEMPLATES);
    }

    public void onContactsBTNClicked(ActionEvent actionEvent) {
        // TODO: open contacts and paste mails into textfield
        SceneManager.getInstance().showInNewWindow(SceneType.MAIL_CONTACTS);
    }


    class GMailAuthenticator extends Authenticator {
        String user;
        String password;

        public GMailAuthenticator(String username, String password) {
            super();
            this.user = username;
            this.password = password;
        }

        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(user, password);
        }
    }

    public void setRecipients(List<String> emailList) {

        String recipients = "";

        for (int i = 0; i < emailList.size(); i++) {
            if (i == 0) {
            recipients += emailList.get(i);
            } else {
                recipients += ", ";
                recipients += emailList.get(i);
            }
        }

        this.targetAddressTextField.setText(recipients);
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setContent(String content){this.mailTextArea.setText(content);}

    public void setSubject(String subject) {
        this.subjectTextField.setText(subject);
    }
}