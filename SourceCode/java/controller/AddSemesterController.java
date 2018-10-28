package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import models.SemesterModel;
import javafx.scene.input.MouseEvent;



public class AddSemesterController {

    @FXML
    private Text txtCreateTerm;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnCreate;

    @FXML
    private Label lblTerm;

    @FXML
    private Label lblYear;

    @FXML
    private ComboBox comboTerm;

    @FXML
    private ComboBox comboYear;


    /*
        Bezeichnung und Jahr als ComboBox
        Create/Erstellen Methode -> Bezeichnung und Jahr zusammen ausgeben lassen
     */

    public AddSemesterController() {
    }

    /*
    Warnungen
     */

    @FXML
    public void addSemesterSave(ActionEvent event){

        //Button saveButton = new Button();
        SemesterModel semester = new SemesterModel();


    }

    public void addSemesterCancel(ActionEvent event){

        AnchorPane root = new AnchorPane();

        Button cancelButton = new Button();
        cancelButton.setCancelButton(true);
    }

    @FXML
    public void checkTextFieldInput(ActionEvent event){

        String term = comboTerm.getText();
        System.out.println("Bezeichnung: " + term);

        String year = comboYear.getText();
        System.out.println("Jahr: " + year);
    }
}
