package controller.mail;

import controller.chat.ChatTabController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import modal.InfoModal;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.io.File;


public class MailAttachmentsController {

    private ObservableList<String> attachmentList = FXCollections.observableArrayList();

    public Button addBTN;
    public Button cancelBTN;
    public Button removeBTN;
    public ListView attachmentListView;
    public AnchorPane rootPane;


    public void onAddBTNClicked(ActionEvent actionEvent) {
        // creating filechooser
        FileChooser chooser = new FileChooser();
        // open filechooser
        Stage stage = (Stage) this.rootPane.getScene().getWindow();
        File selectedPath = chooser.showOpenDialog(stage);

        if (selectedPath != null) {
            // getting the path of the file
            String attachmentPath = selectedPath.getAbsolutePath();
            // add it to list 
            this.attachmentList.add(attachmentPath);
            // listView only has to show the file's name
            this.attachmentListView.getItems().add(selectedPath.getName());
        }
    }

    public void onRemoveBTNClicked(ActionEvent actionEvent) {
        // making sure that an attachment is selected
        if (this.attachmentListView.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("ACHTUNG!", null, "Kein Anhang ausgewählt!");
        } else {
            // remove selected attachment from attachmentList
            attachmentList.removeIf(attachment -> attachment.endsWith("\\" + attachmentListView
                    .getSelectionModel().getSelectedItem()));
        // ... and from attachmentListView
        this.attachmentListView.getItems().remove(attachmentListView.getSelectionModel().getSelectedItem());
        this.attachmentListView.refresh();
        }

    }

    public void onCancelBTNClicked(ActionEvent actionEvent) {
        // add attachments to mail and close window
        SendMailController sendMailController = SceneManager.getInstance().getLoaderForScene(SceneType.SEND_MAIL).getController();
        sendMailController.setAttachmentList(this.attachmentList);
        // close window
        SceneManager.getInstance().closeWindow(SceneType.MAIL_ATTACHMENTS);
    }

    public void setAttachmentList(ObservableList<String> attachmentList) {
        this.attachmentList = attachmentList;
    }

    public ObservableList<String> getAttachmentList() {
        return this.attachmentList;
    }

    // TODO: über welchen button muss ich gehen damit die anhänge eingetragen werden?
    // TODO: anhänge müssen gelöscht werden nach dem senden. über den sendemail controller?! (MAYBE FIXED?)
    // TODO:

}
