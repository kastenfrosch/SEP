package controller.form;

import com.sun.mail.iap.ByteArray;
import connection.DBManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import modal.ConfirmationModal;
import utils.DBUtils;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.io.IOException;
import java.sql.SQLException;


public class DangerousStuffController {
    @FXML
    private CheckBox withTestdataCheckbox;


    public void onResetDatabaseBtnClicked(ActionEvent actionEvent) {
        boolean confirm = ConfirmationModal.show(
                "WARNUNG",
                "Datenbank zurücksetzen? ALLE DATEN GEHEN VERLOREN!",
                "Die Anwendung wird sich nach dem Zurücksetzen schließen."
        );

        if(!confirm) {
            return;
        }
        try {
            DBUtils.resetDB(
                    DBManager.getInstance().getSemesterDao().getConnectionSource(),
                    withTestdataCheckbox.isSelected()
            );
            Platform.exit();
        } catch(SQLException | IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public void onCancelBtnClicked(ActionEvent actionEvent) {
        SceneManager.getInstance().closeWindow(SceneType.DANGEROUS_STUFF);
    }
}
