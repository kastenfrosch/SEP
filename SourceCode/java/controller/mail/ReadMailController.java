package controller.mail;

import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import javax.mail.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadMailController {


    private static Message mailMessage;
    public TextArea mailContent;
    public TextArea dateTextField;
    public TextArea senderTextField;
    public TextArea subjectTextField;


    public Message getMailMessage() {
        return mailMessage;
    }

    // set message to set pass the message from receivemail
    public static void setMailMessage(Message message) {
        mailMessage = message;
    }

    public void init() {
        // clear the text area
        mailContent.clear();
        dateTextField.clear();
        senderTextField.clear();
        subjectTextField.clear();

        if (mailMessage != null) {
            try {
                // set all the elements in the form
                dateTextField.appendText(mailMessage.getSentDate().toString());
                senderTextField.appendText(String.valueOf(mailMessage.getFrom()[0]));
                subjectTextField.appendText(mailMessage.getSubject());
                Folder folder = mailMessage.getFolder();
                //read only
                if (folder.isOpen()) {
                    if ((folder.getMode() & Folder.READ_WRITE) != 0) {
                        folder.close(false);
                        folder.open(Folder.READ_ONLY);
                    }
                } else {
                    folder.open(Folder.READ_ONLY);
                }
                // get the content
                Object content = mailMessage.getContent();
                String body = null;
                // if string return the string
                if (content instanceof String) {
                    body = (String) content;
                    // if multipart convert to string
                } else if (content instanceof Multipart) {
                    Multipart multipart = (Multipart) content;
                    BodyPart part = multipart.getBodyPart(0);
                    body = part.getContent().toString();
                }
                // close folder
                if (folder.isOpen()) {
                    folder.close(false);
                }
                // set content into the textarea
                mailContent.setText(body);
                // mailContent.appendText(body);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onBackBTNClicked(ActionEvent actionEvent) {
        // close the window
        SceneManager.getInstance().closeWindow(SceneType.READ_MAIL);
    }

    public void onReplyBTNClicked(ActionEvent actionEvent) {
        // TODO: has to be tested
        // open new SendMailController
        SendMailController sendMail = SceneManager.getInstance()
                .getLoaderForScene(SceneType.SEND_MAIL).getController();
        SceneManager.getInstance().showInNewWindow(SceneType.SEND_MAIL);

        // getting the password from the inbox controller and setting it to new SendMailController
        ReceiveMailController receiveMailController = SceneManager.getInstance()
                .getLoaderForScene(SceneType.RECEIVE_MAIL).getController();
        sendMail.setPass(receiveMailController.getMailPassword());

        // with this mailContent set as reply
        String replyString = "\n" + "\n" + "\n" +
                "-------------------------------------------------------------------------------------------" + "\n";
        sendMail.setContent(replyString + this.mailContent.getText());

        // and subjectTextfield
        sendMail.setSubject("Awd: " + this.subjectTextField.getText());

        // set previous sender as recipient
        List<String> recipients = new ArrayList<>();
        recipients.add(this.senderTextField.getText());
        sendMail.setRecipients(recipients);

        // close the window
        SceneManager.getInstance().closeWindow(SceneType.READ_MAIL);
    }
}
