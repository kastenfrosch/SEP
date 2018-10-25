package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;

public class LoginController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Label passwordLbl;

    @FXML
    private TextField usernameInput;

    @FXML
    private Text titleText;

    @FXML
    private Text subtitleText;

    @FXML
    private Label usernameLbl;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private Text statusText;

    @FXML
    public void onLoginBtnClicked(ActionEvent event) {
        //Add logic to handle the button click here
        //Example: here I would grab the data the user submitted from the input fields
        //and verify them, then either display the main application or an error
        /*

        String username = usernameInput.getText();
        String password = passwordInput.getText();


         if(isValidUser(username, password)) {
            //close login form, display application
         }
         else
         {
            statusText.setText("Invalid username / password!");
            statusText.setVisible(true);
         }

        */

        try {
            Parent p = FXMLLoader.load(getClass().getResource("/fxml/AddStudentForm.fxml"));
            rootPane.getScene().setRoot(p);
        } catch(IOException ex) {
            //TODO: shit broke yo
        }
    }

}
