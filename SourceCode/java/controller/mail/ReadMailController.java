package controller.mail;

import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;

public class ReadMailController {


    private static Message mailMessage;
    public TextArea mailContent;
    public TextArea dateTextField;
    public TextArea senderTextField;
    public TextArea subjectTextField;


    public Message getMailMessage() {
        return mailMessage;
    }

    //set message to set pass the message from receivemail
    public static void setMailMessage(Message message) {
        mailMessage = message;
    }

    public void init() {
        //clear the text area
        mailContent.clear();
        dateTextField.clear();
        senderTextField.clear();
        subjectTextField.clear();

        mailContent.setEditable(false);
        dateTextField.setEditable(false);
        senderTextField.setEditable(false);
        subjectTextField.setEditable(false);
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
                    //if multipart convert to string
                } else if (content instanceof Multipart) {
                    Multipart multipart = (Multipart) content;
                    BodyPart part = multipart.getBodyPart(0);
                    body = (String) part.getContent();
                }
                //close folder
                if (folder.isOpen()) {
                    folder.close(false);
                }
                //set content into the textarea
                mailContent.setText(body);
                //mailContent.appendText(body);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void onReplyBTNClicked(ActionEvent actionEvent) {


    }


    public void onBackBTNClicked(ActionEvent actionEvent) {
        SceneManager.getInstance().closeWindow(SceneType.READ_MAIL);

    }
}
