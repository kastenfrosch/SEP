package controller.mail;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import modal.InfoModal;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Properties;

public class ReceiveMail {

    private static final String email_id = "MailForSEP@gmail.com";
    private static final String password = "changeme123!";
    @FXML
    private TableView<Message> mailTableView;
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
        TableColumn<Message, String> subject = new TableColumn<>("Betreff:");
        TableColumn<Message, Address> sender = new TableColumn<>("Absender:");
        TableColumn<Message, Date> date = new TableColumn<>("Datum:");

        subject.setCellValueFactory(new PropertyValueFactory<>("subject"));
        sender.setCellValueFactory(c -> {
            try {
                return new SimpleObjectProperty<>(c.getValue().getFrom()[0]);
            } catch(MessagingException | IndexOutOfBoundsException ex) {
                return null;
            }
        });
        date.setCellValueFactory(c -> {
            try {
                return new SimpleObjectProperty<>(c.getValue().getSentDate());
            } catch(MessagingException ex) {
                return new SimpleObjectProperty<>(Date.from(Instant.EPOCH));
            }
        });

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

            mailTableView.setItems(FXCollections.observableArrayList(inbox.getMessages()));

            inbox.close(true);
            store.close();

            mailTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                    if (t.getClickCount() == 2 && mailTableView.getSelectionModel().getSelectedCells() != null) {
                        ReadMail.setMailMessage(mailTableView.getSelectionModel().getSelectedItem());
                        InfoModal.show("Double Clicked"+ mailTableView.getSelectionModel().getSelectedItem());
                        SceneManager.getInstance().showInNewWindow(SceneType.READ_MAIL);

                    }
                }
            });


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
        initialize();
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
