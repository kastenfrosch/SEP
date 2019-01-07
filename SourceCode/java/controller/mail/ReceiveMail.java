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
import utils.scene.SceneManager;
import utils.scene.SceneType;

import javax.mail.*;
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
        //set imap address, imaps = imap + secure
        properties.put("mail.store.protocol", "imaps");
        //imap host of mail address
        properties.put("mail.imaps.host", "imap.gmail.com");
        //imap port of mail address
        properties.put("mail.imaps.port", "993");
        //set timeout
        properties.put("mail.imaps.timeout", "10000");


        //initialization of the tableview
        TableColumn<Message, String> subject = new TableColumn<>("Betreff:");
        TableColumn<Message, Address> sender = new TableColumn<>("Absender:");
        TableColumn<Message, Date> date = new TableColumn<>("Datum:");
        //set table content
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
            //SET the store for imaps
            Store store = session.getStore("imaps");

            System.out.println("Connection initiated......");
            //trying to connect iamp server
            store.connect(email_id, password);
            System.out.println("Connection is ready :)");


            //get inbox folder
            Folder inbox = store.getFolder("inbox");
            //set readonly format
            inbox.open(Folder.READ_ONLY);


            //inbox email count
            int messageCount = inbox.getMessageCount();
            //keine Ahnung aber ohne das geht es nicht :(
            for (int i = 0; i < messageCount; i++) {
                inbox.getMessage(messageCount - i).getSubject();

            }
            //add all columns
            mailTableView.getColumns().addAll(date, subject, sender);

            mailTableView.setItems(FXCollections.observableArrayList(inbox.getMessages()));

            //close inbox and store
            inbox.close(true);
            store.close();
            //double click on mail to open the mail
            mailTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                    if (t.getClickCount() == 2 && mailTableView.getSelectionModel().getSelectedCells() != null) {
                        //select mail for readmail
                        ReadMail.setMailMessage(mailTableView.getSelectionModel().getSelectedItem());
                        SceneManager.getInstance().showInNewWindow(SceneType.READ_MAIL);

                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void onRefreshBTNClicked(ActionEvent actionEvent) {
        initialize();
    }

}
