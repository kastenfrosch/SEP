package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import utils.scene.SceneManager;
import utils.scene.SceneType;

public class PasswordResetController {
    @FXML
    private TextField newPasswordTwo;

    @FXML
    private TextField newPassword;

    @FXML
    private TextField formerPassword;

    public void onCancelBtnClicked(ActionEvent event) {
        SceneManager.getInstance().closeWindow(SceneType.PASSWORD_RESET);
    }

    public void onSaveBtnClicked(ActionEvent event) {
        if()
    }
}
