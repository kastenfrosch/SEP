package controller.mail;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import modal.ErrorModal;
import modal.InfoModal;
import models.MailTemplate;
import models.User;
import utils.HashUtils;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.io.File;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class SendMailController {

    private DBManager db;
    private List<String> attachmentList = new ArrayList<>();


    {
        try {
            db = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private User currentUser;

    public AnchorPane rootPane;
    public TextField targetAddressTextField;
    public TextField subjectTextField;
    public TextArea mailTextArea;
    public Button attatchmentBTN;
    public Button sendBTN;
    public Button cancelBTN;
    public Button templateBTN;
    public Button contactsBTN;

    String password;

    public void init(){
        this.currentUser = db.getLoggedInUser();
        this.subjectTextField.clear();
        this.mailTextArea.clear();
        this.targetAddressTextField.clear();
        this.attachmentList.clear();
    }


    @FXML
    private void onSendBTNClicked(ActionEvent actionEvent) {

        // Recipient's email ID needs to be mentioned.
        String to = targetAddressTextField.getText();//change accordingly

        // Sender's email ID needs to be mentioned

        // Assuming you are sending email through relay.jangosmtp.net


        Properties properties = new Properties();
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", currentUser.getMailSmtpHost());
        properties.put("mail.smtp.user", currentUser.getMailUser());
        properties.put("mail.smtp.password", password);
        properties.put("mail.smtp.port", String.valueOf(currentUser.getMailSmtpPort()));
        properties.put("mail.smtp.auth", "true");

        sendMail(properties, currentUser.getMailUser(), password, to);

    }

        // Get the Session object.


    public void sendMail(Properties properties, String mailUsername, String password, String to){
            Session session = Session.getInstance(properties, new MailAuthenticator(currentUser.getMailUser(), password));

            try {
                // Create a default MimeMessage object.
                Message message = new MimeMessage(session);

                // Set From: header field of the header.
                message.setFrom(new InternetAddress(currentUser.getMailUser()));

                // Set To: header field of the header.
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

                // Set Subject: header field
                message.setSubject(subjectTextField.getText());

                if (this.attachmentList.size() > 0) {

                    // using multiple body parts, one for text, rest for attachments
                    MimeBodyPart mbpText = new MimeBodyPart();
                    // setting text
                    mbpText.setText(mailTextArea.getText());

                    // creating multipart, adding text on top
                    Multipart mp = new MimeMultipart();
                    mp.addBodyPart(mbpText);

                    // every path in the attachmentList is added to multipart
                    for (String s : this.attachmentList) {
                        MimeBodyPart mbp = new MimeBodyPart();
                        FileDataSource fds = new FileDataSource(s);
                        mbp.setDataHandler(new DataHandler(fds));
                        mbp.setFileName(fds.getName());
                        mp.addBodyPart(mbp);
                    }

                    // set combined multipart as content
                    message.setContent(mp);

                } else {
                    message.setText(mailTextArea.getText());
                }

                // Send message
                Transport.send(message);
                InfoModal.show("Email wurde erfolgreich gesendet!");

                // delete attachments from MailAttachmentsController
                MailAttachmentsController mailAttachmentsController = SceneManager.getInstance()
                        .getLoaderForScene(SceneType.MAIL_ATTACHMENTS).getController();
                mailAttachmentsController.getAttachmentList().clear();
                mailAttachmentsController.getAttachmentListView().getItems().clear();

                // close the window
                SceneManager.getInstance().closeWindow(SceneType.SEND_MAIL);

            } catch (MessagingException e) {
                ErrorModal.show("Beim Senden der Email ist ein Fehler aufgetreten!");
                throw new RuntimeException(e);
            }
            SceneManager.getInstance().closeWindow(SceneType.SEND_MAIL);
        }

    @FXML
    private void onCancelBTNClicked(ActionEvent actionEvent) {
        // delete attachments from MailAttachmentsController
        MailAttachmentsController mailAttachmentsController = SceneManager.getInstance()
                .getLoaderForScene(SceneType.MAIL_ATTACHMENTS).getController();
        mailAttachmentsController.getAttachmentList().clear();
        mailAttachmentsController.getAttachmentListView().getItems().clear();
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

    @FXML
    private void onAttachmentBTNClicked(ActionEvent actionEvent) {
        // open attachments window to add or remove mail attachments
        SceneManager.getInstance().showInNewWindow(SceneType.MAIL_ATTACHMENTS);
    }

    public void onDraftBTNClicked(ActionEvent event) {
        CreateMailTemplateController createMailTemplateController = SceneManager.getInstance()
                .getLoaderForScene(SceneType.CREATE_MAILTEMPLATES).getController();

        createMailTemplateController.createDraft(subjectTextField.getText(), mailTextArea.getText());
        SceneManager.getInstance().closeWindow(SceneType.SEND_MAIL);
        }


    public static class MailAuthenticator extends Authenticator {
        String user;
        String password;

        public MailAuthenticator(String username, String password) {
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

    public void setAttachmentList(List<String> attachmentList) {
        // set attachments selected in and passed by MailAttachmentsController
        this.attachmentList = attachmentList;
    }

    public void setPass(String password) {
        this.password = password;
    }

    public void setContent(String content){
        this.mailTextArea.setText(content);
    }

    public void setSubject(String subject) {
        this.subjectTextField.setText(subject);
    }

    public void setTo(String to){this.targetAddressTextField.setText(to);}
}
