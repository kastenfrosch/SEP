
package controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import models.PersonModel;
import models.StudentModel;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class AddStudentController {

    @FXML
    private TextField firstnameInput;

    @FXML
    private TextField idInput;

    @FXML
    private Text statusText;

    @FXML
    private TextField lastnameInput;

    @FXML
    private TextField matNoInput;

    @FXML
    private Button IS_Cancel;

    @FXML
    private Button ID_Create;



    @FXML
    private TextField emailInput;

    @FXML
    public void AddStudentSave(ActionEvent event) {

        StudentModel Student = new StudentModel();
        try {
            Student.setStudent_id(Integer.parseInt(idInput.getText()));
            Student.setMatr_no(matNoInput.getText());

            if (matNoInput.getText().isEmpty()||lastnameInput.getText().isEmpty()|| firstnameInput.getText().isEmpty()||emailInput.getText().isEmpty()) {
                statusText.setText("Please fill in all fields ");
                statusText.setVisible(true);
            }
            if(validateMailAddress(emailInput.getText())){
            //Person.setMailAddress(emailInput)
            }else{
                statusText.setText("Check email address");
                statusText.setVisible(true);
            }


        } catch (NumberFormatException inValidID) {
            statusText.setText("Invalid StudentID");
            statusText.setVisible(true);

        }



    }
    private boolean validateMailAddress(String adr) {
        try {
            InternetAddress a = new InternetAddress(adr);
        } catch(AddressException e) {
            return false;
        }
        return true;
    }

    @FXML
    void AddStudentCancel(ActionEvent event) {


    }
        //go back to HomeScreen
}
