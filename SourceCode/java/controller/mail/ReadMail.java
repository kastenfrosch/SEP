package controller.mail;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;

import java.io.IOException;

public class ReadMail {


    private static Message mailMessage;
    @FXML
    public TextArea mailContent;
    public TextField dateTextField;
    public TextField senderTextField;
    public TextField subjectTextField;

    public Message getMailMessage() {
        return mailMessage;
    }

    public static void setMailMessage(Message message) {
        mailMessage = message;
    }

    public void initialize(){
        mailContent.clear();
        if(mailMessage != null) {
            try {
                dateTextField.appendText(mailMessage.getSentDate().toString());
                senderTextField.appendText(String.valueOf(mailMessage.getFrom()[0]));
                subjectTextField.appendText(mailMessage.getSubject());
                mailContent.setText(mailMessage.getContent().toString());
                Folder folder = mailMessage.getFolder();
// Open folder in read-only mode
                if (folder.isOpen()) {
                    if ((folder.getMode() & Folder.READ_WRITE) != 0) {
                        folder.close(false);
                        folder.open(Folder.READ_ONLY);
                    }
                } else {
                    folder.open(Folder.READ_ONLY);
                }

                Object content = mailMessage.getContent();
                String body = null;
                if (content instanceof String) {
                    body = (String) content;
                } else if (content instanceof Multipart) {
                    Multipart multipart = (Multipart) content;
                    BodyPart part = multipart.getBodyPart(0);
                    body = (String) part.getContent();
                }
                if (folder.isOpen()) {
                    folder.close(false);
                }
                mailContent.appendText(body);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onRefreshBTNClicked(ActionEvent actionEvent) {
        initialize();
    }




    public static String toString(Message message) throws MessagingException, IOException {
        Object content = message.getContent();
        if (content instanceof MimeMultipart) {
            MimeMultipart multipart = (MimeMultipart) content;
            if (multipart.getCount() > 0) {
                BodyPart part = multipart.getBodyPart(0);
                content = part.getContent();
            }
        }
        if (content != null) {
            return content.toString();
        }
        return null;
    }
}
