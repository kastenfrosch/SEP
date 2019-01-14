package controller.mail;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ListView;
import modal.ConfirmationModal;
import modal.ErrorModal;
import modal.InfoModal;
import models.MailTemplate;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.sql.SQLException;

public class MailTemplateController {
    public ListView<MailTemplate> templateList;


    private DBManager dbManager;

    {
        try {
            dbManager = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {

        try {

            // initializing an ObservableList which is filled with all the existing groupage descriptions
            ObservableList<MailTemplate> tempList = FXCollections.observableArrayList();
            Dao<MailTemplate, Integer> mailtempDao = dbManager.getMailTemplateDao();
            tempList.addAll(mailtempDao.queryForAll());

            // filling the combobox with the ObservableList
            templateList.setItems(tempList);

        } catch (java.sql.SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();


        }
    }

    public void onUseBTNClicked(ActionEvent event) {
        SendMailController sendMailController = SceneManager.getInstance().getLoaderForScene(SceneType.SEND_MAIL).getController();
        String content = templateList.getSelectionModel().getSelectedItem().getContent();
        String subject = templateList.getSelectionModel().getSelectedItem().getSubject();
        sendMailController.setContent(content);
        sendMailController.setSubject(subject);
        SceneManager.getInstance().closeWindow(SceneType.MAIL_TEMPLATES);
    }

    public void onCreatBTNClicked(ActionEvent event) {
        SceneManager.getInstance().showInNewWindow(SceneType.CREATE_MAILTEMPLATES);
    }

    public void onCancelBTNClicked(ActionEvent event) {
        SceneManager.getInstance().closeWindow(SceneType.MAIL_TEMPLATES);
    }

    public void onDeleteBTNClicked(ActionEvent event) {
        Dao<MailTemplate, Integer> mailTemplateDao = dbManager.getMailTemplateDao();
        try {
            if (templateList.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("Bitte wähle ein Template aus");
            return;
        }
            ConfirmationModal.show("Warnung", null, "Soll das ausgewählte Template wirklich gelöscht werden?");
            
            mailTemplateDao.delete(templateList.getSelectionModel().getSelectedItem());
            templateList.getItems().remove(templateList.getSelectionModel().getSelectedItem());
        } catch (SQLException e) {
            InfoModal.show("Das Template konnte nicht gelöscht werden.");
            e.printStackTrace();
        }
        InfoModal.show("Das Template wurde erfolgreich gelöscht");


    }
}
