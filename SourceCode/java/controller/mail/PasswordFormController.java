package controller.mail;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class PasswordFormController {

    public Button okBTN;
    @FXML

    private TextField passwordField;

    private CompletableFuture<String> passwordFuture;

    public void onOkBTNClicked(ActionEvent actionEvent) {
        passwordFuture.complete(passwordField.getText());
        SceneManager.getInstance().closeWindow(SceneType.PASSWORD_FORM);
    }

    public void onCancelBTNClicked(ActionEvent actionEvent) {
        SceneManager.getInstance().closeWindow(SceneType.PASSWORD_FORM);
    }

    public void onKeyPressed(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER) {
            this.okBTN.fire();
            event.consume();
        }
    }

    public Future<String> getPassword() {
        return this.passwordFuture;
    }
}
