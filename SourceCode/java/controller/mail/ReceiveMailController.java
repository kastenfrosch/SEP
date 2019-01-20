package controller.mail;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import modal.ErrorModal;
import modal.PasswordModal;
import models.User;
import utils.HashUtils;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;

public class ReceiveMailController {

    private User currentUser;
    private DBManager db;
    private Dao<User, String> userDao;
    private Folder inbox;

    {
        try {
            db = DBManager.getInstance();
            userDao = db.getUserDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //private static final String email_id = "MailForSEP@gmail.com";
    //private static final String password = "changeme123!";
    @FXML
    private TableView<Message> mailTableView;
    //set properties
    private String mailPassword;

    //this should NOT be called initialize because it is not supposed to be called by the FXMLLoader, only by the SceneManager
    public void init() {

        currentUser = db.getLoggedInUser();
        //user data may have changed since the last call
        try {
            userDao.refresh(currentUser);
        } catch (SQLException ex) {
            ErrorModal.show("Fehler", "Ein unbekannter Fehler ist aufgetreten", ex.getMessage());
            return;
        }

        Optional<String> appPasswordOpt = PasswordModal.showAndWait();

        String applicationPassword;

        if (appPasswordOpt.isPresent()) {
            applicationPassword = appPasswordOpt.get();
        } else {
            ErrorModal.show("Ungültiges Anwendungskennwort!");
            Platform.runLater(() -> SceneManager.getInstance().closeWindow(SceneType.RECEIVE_MAIL));
            return;
        }

        if (currentUser.getMailUser() == null || currentUser.getMailUser().isBlank()) {
            //no mail settings have been set yet
            SceneManager sm = SceneManager.getInstance();
            sm.getLoaderForScene(SceneType.MAIL_CREDENTIALS).<AddEmailCredentials>getController()
                    .setUserPassword(appPasswordOpt.get());
            sm.showInNewWindow(SceneType.MAIL_CREDENTIALS, true);
        }

        //user needs to be refreshed to match the updated db entry
        try {
            userDao.refresh(currentUser);
        } catch (SQLException ex) {
            ErrorModal.show("Unknown Database Error", ex.getMessage());
            SceneManager.getInstance().closeWindow(SceneType.RECEIVE_MAIL);
        }

        String mailPassword;

        try {
            mailPassword = HashUtils.decryptAES(currentUser.getMailPassword(), applicationPassword);
            if (!mailPassword.startsWith("VALID")) {
                throw new IllegalArgumentException("Invalid application password");
            }
        } catch (Exception e) {
            ErrorModal.show("Ungültiges Anwendungskennwort!");
            Platform.runLater(() -> SceneManager.getInstance().closeWindow(SceneType.RECEIVE_MAIL));
            return;
        }

        this.mailPassword = mailPassword.substring(5);

        //initialization of the tableview
        TableColumn<Message, String> subject = new TableColumn<>("Betreff:");
        TableColumn<Message, Address> sender = new TableColumn<>("Absender:");
        TableColumn<Message, Date> date = new TableColumn<>("Datum:");
        //set table content
        subject.setCellValueFactory(new PropertyValueFactory<>("subject"));
        sender.setCellValueFactory(c -> {
            try {
                return new SimpleObjectProperty<>(c.getValue().getFrom()[0]);
            } catch (MessagingException | IndexOutOfBoundsException ex) {
                return null;
            }
        });
        date.setCellValueFactory(c -> {
            try {
                return new SimpleObjectProperty<>(c.getValue().getSentDate());
            } catch (MessagingException ex) {
                return new SimpleObjectProperty<>(Date.from(Instant.EPOCH));
            }
        });

        Properties properties = new Properties();

        //enable ssl
        //set timeout
        properties.put("mail.debug", "true");
        //set imap address
        properties.put("mail.store.protocol", "imaps");
        //imap host of mail address
        properties.put("mail.imaps.host", currentUser.getMailImapHost());
        //imap port of mail address
        properties.put("mail.imaps.port", currentUser.getMailImapPort());
        properties.put("mail.imaps.timeout", "10000");

        try {

            //create a session
            Session session = Session.getInstance(properties, null);
            //SET the store for imaps
            Store store = session.getStore("imaps");

            System.out.println("Connection initiated......");
            //trying to connect iamp server
            try {
                store.connect(currentUser.getMailUser(), this.mailPassword);
            } catch (AuthenticationFailedException ex) {
                ErrorModal.show("Anmeldung Fehlgeschlagen", "Bitte stellen Sie sicher dass Benutzername und Passwort korrekt sind.");
                Platform.runLater(() -> SceneManager.getInstance().closeWindow(SceneType.RECEIVE_MAIL));
                System.out.println("Login failed with user " + currentUser.getMailUser() + " pw " + this.mailPassword);
                ex.printStackTrace();
                System.out.println(ex.getMessage());
                return;
            }
            System.out.println("Connection is ready :)");


            //get inbox folder
            switch (currentUser.getMailImapHost()) {
                case "imap.gmail.com":
                    inbox = store.getFolder("inbox");
                    break;
                case "imap.gmx.de":
                case "mailbox.uni-duisburg-essen.de":
                    inbox = store.getFolder("INBOX");

                    break;
                case "imap.mail.yahoo.com":
                case "imap.mail.yahoo.de":
                    inbox = store.getFolder("Inbox");
                    break;

                default:
                    inbox = store.getFolder("inbox");
                    break;
            }


            //set readonly format
            inbox.open(Folder.READ_ONLY);

            //inbox email count
            int messageCount = inbox.getMessageCount();
            //keine Ahnung aber ohne das geht es nicht :( jetzt doch ahnung aber ich lass das mal lieber so stehen, bevor alles nicht mehr geht
            for (int i = 0; i < messageCount; i++) {
                inbox.getMessage(messageCount - i).getSubject();

            }
            //add all columns

            date.prefWidthProperty().bind(mailTableView.widthProperty().divide(3));
            subject.prefWidthProperty().bind(mailTableView.widthProperty().divide(3));
            sender.prefWidthProperty().bind(mailTableView.widthProperty().divide(3));

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
                        ReadMailController.setMailMessage(mailTableView.getSelectionModel().getSelectedItem());
                        SceneManager.getInstance().showInNewWindow(SceneType.READ_MAIL);

                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onRefreshBTNClicked(ActionEvent actionEvent) {
        init();
    }


    public void onSendMailClicked(ActionEvent event) {
        SceneManager.getInstance().getLoaderForScene(SceneType.SEND_MAIL).<SendMailController>getController().setPass(mailPassword);
        SceneManager.getInstance().showInNewWindow(SceneType.SEND_MAIL);
    }

    public void onCancelBTNClicked(ActionEvent event) {
        SceneManager.getInstance().closeWindow(SceneType.RECEIVE_MAIL);
    }

    public void onReplyBTNClicked(ActionEvent event) {
        try {
            inbox.open(Folder.READ_ONLY);
            for (int i = 0; i < inbox.getMessageCount(); i++) {
                inbox.getMessage(inbox.getMessageCount() - i).getSubject();
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        SendMailController sendMail = SceneManager.getInstance().getLoaderForScene(SceneType.SEND_MAIL).getController();
        try {
            if (mailTableView.getSelectionModel().getSelectedItem().getReplyTo() == null) {
                ErrorModal.show("Bitte eine Mail auswählen.");
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        SceneManager.getInstance().showInNewWindow(SceneType.SEND_MAIL);
        Message message = mailTableView.getSelectionModel().getSelectedItem();
        String from = "";
        Address[] froms = new Address[0];
        try {
            froms = message.getFrom();
        } catch (MessagingException e) {
            e.printStackTrace();
        }


        InternetAddress address = (InternetAddress) froms[0];
        String person = address.getPersonal();

        if (person != null) {
            try {
                person = MimeUtility.decodeText(person) + " ";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            person = "";
        }

        from = person + "<" + address.getAddress() + ">";


        try {
            sendMail.setTo(from);
            sendMail.setSubject("Awd: "+message.getSubject());
            sendMail.setContent("\n"+"\n"+"\n" +
                    "-------------------------------------------------------------------------------------------"+"\n"
            + message.getContent().toString());
        } catch (IOException | MessagingException e) {
            e.printStackTrace();
        }
        try {
            inbox.close(true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void onEditCredentialsBTN(ActionEvent event) {
        SceneManager.getInstance().showInNewWindow(SceneType.EDIT_MAILCREDENTIALS);
    }
}
