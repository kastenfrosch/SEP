package controller.mail;

import com.sun.source.tree.Tree;
import connection.DBManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
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
    private CompletableFuture<String> selectedEmail;

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

        selectedEmail = new CompletableFuture<>();

        TreeItem<ITreeItem> root = new TreeItem<>();

        try {

            List<ITreeItem> contacts = new ArrayList<>();

            contacts.addAll(db.getGroupageDao().queryForAll());

            addChildren(root, contacts);

            contactsTreeView.setRoot(root);

        } catch (SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }

    }

    private void addChildren(TreeItem<ITreeItem> parent, List<ITreeItem> objects) {
        if (objects.size() == 0) {
            return;
        }
        for (ITreeItem i : objects) {
            TreeItem<ITreeItem> me = new TreeItem<>(i);
            parent.getChildren().add(me);
            addChildren(me, i.getChildren());
        }
    }

    public void onSelectBTNClicked(ActionEvent actionEvent) {
        List<String> emailList = addEmails(contactsTreeView.getSelectionModel().getSelectedItem());
        selectedEmail.complete(emailList.toString());

        SceneManager.getInstance().closeWindow(SceneType.MAIL_CONTACTS);
    }

    private List<String> addEmails(TreeItem<ITreeItem> parent) {
        List<String> mails = new LinkedList<>();
        if (!parent.getValue().getEmail().equals("")) {
            mails.add(parent.getValue().getEmail());
        }
        for (TreeItem<ITreeItem> item : parent.getChildren()) {
            mails.addAll(addEmails(item));
        }
        return mails;
    }

    public void onCancelBTNClicked(ActionEvent actionEvent) {
        SceneManager.getInstance().closeWindow(SceneType.MAIL_CONTACTS);
    }

    public Future<String> getSelectedEmail() {
        return selectedEmail;
    }
}
