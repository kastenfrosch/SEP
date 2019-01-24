package controller.mail;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import modal.ErrorModal;
import modal.InfoModal;
import models.MailTemplate;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.sql.SQLException;

public class CreateMailTemplateController {
    MailTemplate mailTemp = new MailTemplate();

    private DBManager dbManager;

    {
        try {
            dbManager = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public TextArea contentField;
    public TextField subjectField;

    public void initialize(){
        contentField.clear();
        subjectField.clear();
    }

    public void onCancelBTNClicked(ActionEvent event) {
        SceneManager.getInstance().closeWindow(SceneType.CREATE_MAILTEMPLATES);
    }

    public void onSaveBTNClicked(ActionEvent event) {

        createDraft(subjectField.getText(), contentField.getText());

        MailTemplateController temp = SceneManager.getInstance().getLoaderForScene(SceneType.MAIL_TEMPLATES).getController();
        temp.initialize();
        SceneManager.getInstance().closeWindow(SceneType.CREATE_MAILTEMPLATES);
    }

    public void createDraft(String subject, String content){

        if(content.isBlank()||subject.isBlank()){
            InfoModal.show("Bitte füllen Sie alle Felder aus!");
            return;
        }
        Dao<MailTemplate, Integer> mailTemplateDao = dbManager.getMailTemplateDao();
        mailTemp.setContent(content);
        mailTemp.setSubject(subject);

        try {
            mailTemplateDao.create(mailTemp);
            InfoModal.show("Template wurde erfolgreich gespeichert.");

        } catch (SQLException e) {
            e.printStackTrace();
            ErrorModal.show("Template konnte leider nicht erstellt werden.");
        }
    }
}
