package controller.gitlab;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;

public class GitlabLoginController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button loginBtn;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    void onLoginBtnClicked(ActionEvent event) {
        GitLabApi gitLabApi;
        try {
           gitLabApi = GitLabApi.oauth2Login("https://git.uni-due.de", username.getText(), password.getText());
        } catch (GitLabApiException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) {
            this.loginBtn.fire();
            keyEvent.consume();
        }
    }
}
