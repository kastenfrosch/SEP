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


    private DBManager db;
    // TODO: content and subject variables still needed?
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
    private User currentUser = db.getLoggedInUser();

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
        // Assuming you are sending email through relay.jangosmtp.net

        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", currentUser.getMailSmtpHost());
        props.put("mail.smtp.user", currentUser.getPerson().getEmail());
        //props.put("mail.smtp.password", password);
        props.put("mail.smtp.port", currentUser.getMailSmtpPort());
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
            InfoModal.show("Email wurde erfolgreich gesendet!");
            SceneManager.getInstance().closeWindow(SceneType.SEND_MAIL);

        } catch (MessagingException e) {
            ErrorModal.show("Beim Senden der Email ist ein Fehler aufgetreten!");
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onSaveBTNClicked(ActionEvent actionEvent) {
        // TODO: save mail into templates?
    }

    @FXML
    private void onCancelBTNClicked(ActionEvent actionEvent) {
        // close window
        SceneManager.getInstance().closeWindow(SceneType.SEND_MAIL);
    }

    @FXML
    private void onTemplateBTNClicked(ActionEvent event) {
        // open templates window to select/edit mail templates
        SceneManager.getInstance().showInNewWindow(SceneType.MAIL_TEMPLATES);
    }

    @FXML
    private void onContactsBTNClicked(ActionEvent actionEvent) {
        // open contacts window to select recipients
        SceneManager.getInstance().showInNewWindow(SceneType.MAIL_CONTACTS);
    }

    public void onAttatchementBTNClicked(ActionEvent event) {
    }


    public static class GMailAuthenticator extends Authenticator {
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

        // method called from contacts window which takes a list of email addresses
        // and pastes it into the address textfield
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

    public void setContent(String content){
        this.mailTextArea.setText(content);
    }

    public void setSubject(String subject) {
        this.subjectTextField.setText(subject);
    }
}
