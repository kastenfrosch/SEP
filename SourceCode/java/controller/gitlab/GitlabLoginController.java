package controller.gitlab;

import controller.HomeScreenController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import modal.ErrorModal;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import utils.scene.SceneManager;
import utils.scene.SceneType;

public class GitlabLoginController {

    private GitLabApi api;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button loginBtn;

    @FXML
    private TextField usernameInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    public void onLoginBtnClicked(ActionEvent event) {
        try {
           this.api = GitLabApi.oauth2Login("https://git.uni-due.de", usernameInput.getText(), passwordInput.getText());

            SceneManager.getInstance().getLoaderForScene(SceneType.HOME).<HomeScreenController>getController().refreshTabContent();
        } catch (GitLabApiException e) {
            ErrorModal.show("Login failed", e.getMessage());
            return;
        }
    }

    @FXML
    public void onKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) {
            this.loginBtn.fire();
            keyEvent.consume();
        }
    }

    public GitLabApi getApi() {
        return api;
    }
}
