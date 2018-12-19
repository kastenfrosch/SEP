package controller.mail;

import com.sun.mail.imap.protocol.MailboxInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.CalendarExtraInfo;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.MimeMultipart;

public class ReceiveMail {

    private static final String email_id = "MailForSEP@gmail.com";
    private static final String password = "changeme123!";
    @FXML
    private TableView<MailboxInfo> mailTableView;
    //set properties


    @FXML
    private void initialize() {

        Properties properties = new Properties();
        //You can use imap or imaps , *s -Secured
        properties.put("mail.store.protocol", "imaps");
        //Host Address of Your Mail
        properties.put("mail.imaps.host", "imap.gmail.com");
        //Port number of your Mail Host
        properties.put("mail.imaps.port", "993");

        properties.put("mail.imaps.timeout", "10000");


        //initialization of the tableview
        TableColumn<MailboxInfo, String> subject = new TableColumn<>("Betreff:");
        TableColumn<MailboxInfo, String> sender = new TableColumn<>("Absender:");
        TableColumn<MailboxInfo, Date> date = new TableColumn<>("Datum:");


        try {

            //create a session
            Session session = Session.getDefaultInstance(properties, null);
            //SET the store for IMAPS
            Store store = session.getStore("imaps");

            System.out.println("Connection initiated......");
            //Trying to connect IMAP server
            store.connect(email_id, password);
            System.out.println("Connection is ready :)");


            //Get inbox folder
            Folder inbox = store.getFolder("inbox");
            //SET readonly format (*You can set read and write)
            inbox.open(Folder.READ_ONLY);


            //Display email Details

            //Inbox email count
            int messageCount = inbox.getMessageCount();
            System.out.println("Total Messages in INBOX: " + messageCount);


            for (int i = 0; i < messageCount; i++) {
                System.out.println("Mail Subject:- " + inbox.getMessage(messageCount - i).getSubject());
                System.out.println("-------------------------------------------------------------");
                System.out.println("Mail From:- " + inbox.getMessage(messageCount - i).getFrom()[0]);
                System.out.println("-------------------------------------------------------------");
                System.out.println("Mail Content:- " + getTextFromMimeMultipart((MimeMultipart) inbox.getMessage(messageCount - i).getContent()));
                System.out.println("------------------------------------------------------------");
                System.out.println("Mail Content:- " + inbox.getMessage(messageCount - i).getSentDate().toString());
                System.out.println("****************************************************************************");
            }

            mailTableView.getColumns().addAll(date, subject, sender);


            inbox.close(true);
            store.close();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onRefreshBTNClicked(ActionEvent actionEvent) {
    }
    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws Exception {
        String result = "";
        int partCount = mimeMultipart.getCount();
        for (int i = 0; i < partCount; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                // result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
                result = html;
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            }
        }
        return result;
    }
}
