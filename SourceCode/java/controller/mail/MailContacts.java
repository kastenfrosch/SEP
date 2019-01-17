package controller.mail;

import com.sun.source.tree.Tree;
import connection.DBManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import modal.ErrorModal;
import models.Group;
import models.Groupage;
import models.Student;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.swing.text.html.ListView;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class MailContacts {

    private DBManager db;

    {
        try {
            db = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button cancelBTN;
    @FXML
    private Button selectBTN;
    @FXML
    private TreeView<ITreeItem> contactsTreeView;

    public void initialize() {

        // creating root node for treeview
        TreeItem<ITreeItem> root = new TreeItem<>();

        try {
            // creating a list to iterate over
            List<ITreeItem> contacts = new ArrayList<>();
            // append all groupages to the list
            contacts.addAll(db.getGroupageDao().queryForAll());
            // call method to append all children to root node and all other children respectively
            addChildren(root, contacts);
            // set root as root node of the treeview
            contactsTreeView.setRoot(root);

        } catch (SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }

    }

    private void addChildren(TreeItem<ITreeItem> parent, List<ITreeItem> objects) {
        // node has no children, end of recursive function --> return
        if (objects.size() == 0) {
            return;
        }
        // iterate over each item in objects and add it to its parent node
        // call method again for each item as parent and all its children
        for (ITreeItem i : objects) {
            TreeItem<ITreeItem> me = new TreeItem<>(i);
            parent.getChildren().add(me);
            addChildren(me, i.getChildren());
        }
    }

    public void onSelectBTNClicked(ActionEvent actionEvent) {
        // selected item of treeview is a list of strings
        // (for students, that list has only one item)
        List<String> emailList = addEmails(contactsTreeView.getSelectionModel().getSelectedItem());
        // get instance of send mail window and set recipients by calling the method
        SendMailController sendMailController = SceneManager.getInstance().getLoaderForScene(SceneType.SEND_MAIL).getController();
        sendMailController.setRecipients(emailList);
        // ... and close the contacts window
        SceneManager.getInstance().closeWindow(SceneType.MAIL_CONTACTS);
    }



    private List<String> addEmails(TreeItem<ITreeItem> parent) {
        // this method takes the (in the treeview selected) treeitem and searches parent node and
        // all children for a "valid" (read: not "") email address.
        // if found, add it to the list, otherwise call method for all children, add emails to
        // the list and return it once finished.
        List<String> mails = new LinkedList<>();
        if (!parent.getValue().getEmail().equals("")) {
            mails.add(parent.getValue().getEmail());
        }
        for (TreeItem<ITreeItem> item : parent.getChildren()) {
            mails.addAll(addEmails(item));
        }
        return mails;
    }

    public void onTreeviewClicked(MouseEvent mouseEvent) {
        // this eventhandler detects a doubleclick on an item in the contacts treeview
        contactsTreeView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                // detect a double click
                if (click.getClickCount() == 2) {
                    // selecting an item on doubleclick
                    // selected item of treeview is a list of strings
                    // (for students, that list has only one item)
                    List<String> emailList = addEmails(contactsTreeView.getSelectionModel().getSelectedItem());
                    // get instance of send mail window and set recipients by calling the method
                    SendMailController sendMailController = SceneManager.getInstance().getLoaderForScene(SceneType.SEND_MAIL).getController();
                    sendMailController.setRecipients(emailList);
                    // ... and close the contacts window
                    SceneManager.getInstance().closeWindow(SceneType.MAIL_CONTACTS);
                }
            }
        });
    }

    public void onCancelBTNClicked(ActionEvent actionEvent) {
        // close window
        SceneManager.getInstance().closeWindow(SceneType.MAIL_CONTACTS);
    }

}
