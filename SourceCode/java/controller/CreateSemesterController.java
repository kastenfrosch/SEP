package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Semester;
import modal.InfoModal;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;

public class CreateSemesterController {

    @FXML
    public AnchorPane anchorPane;

    @FXML
    public Text txtCreateTerm;

    @FXML
    public Button btnCancel;

    @FXML
    public Button btnCreate;

    @FXML
    public Label lblTerm;

    @FXML
    public Label lblYear;

    @FXML
    public ComboBox comboTerm;

    @FXML
    public ComboBox comboYear;

    private DBManager db;

    {
        try {
            db = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        //Add terms to list
        ObservableList<String> terms = FXCollections.observableArrayList();
        terms.add("Wintersemester");
        terms.add("Sommersemester");

        //set terms for the ComboBox
        comboTerm.setItems(terms);
    }

    public CreateSemesterController() {
    }

    @FXML
    public void addSemesterCreate(ActionEvent event) {
        //Error if no term is selected
        if (comboTerm.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("Fehler", null, "Kein Semester ausgewählt!");
        }
        //Error if no year is selected
        if (comboYear.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("Fehler", null, "Kein Jahr ausgewählt!");
        }
        //variable to check whether winter or summer term is selected
        String checkTerm = comboTerm.getSelectionModel().getSelectedItem().toString();

        switch(checkTerm) {
            case "Wintersemester":
                createWintersemester();
                break;
            case "Sommersemester":
                createSommersemster();
                break;
            default:
                InfoModal.show("Fehler", null, "Es konnte kein Semester erstellt werden. Bitte versuchen sie es erneut.");
        }
    }

    private void createWintersemester() {
        //select item chosen by the user
        String yearString = (String) comboYear.getSelectionModel().getSelectedItem();
        String termString = (String) comboTerm.getSelectionModel().getSelectedItem();

        //ID for each term consisting of the terms short version and the year
        String semesterID;

        if (termString == "Wintersemester") {
            semesterID = "WS";

            semesterID = semesterID + " " + yearString;

            //description for each term consisting of the terms shortform and the year
            String semesterDescription = termString + " " + yearString;

            try {
                //create new term object
                models.Semester newSemester = new Semester();

                //set description and id for a new term
                newSemester.setDescription(semesterDescription);
                newSemester.setId(semesterID);

                //put term details into the databaase
                Dao<Semester, String> semesterDao = db.getSemesterDao();
                semesterDao.create(newSemester);

            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
            InfoModal.show("Semester \"" + semesterID + "\" erstellt!");
        }
    }
    private void createSommersemster() {
        //select item chosen by the user
        int yearString = (int) comboYear.getSelectionModel().getSelectedItem();
        String termString = (String) comboTerm.getSelectionModel().getSelectedItem();

        //ID for each term consisting of the terms short version and the year
        String semesterID;

        if (termString == "Sommersemester") {
            semesterID = "SS";

            semesterID = semesterID + " " + yearString;

            //description for each term consisting of the terms shortform and the year
            String semesterDescription = termString + " " + yearString;

            try {
                //create new term object
                models.Semester newSemester = new Semester();

                //set description and id for a new term
                newSemester.setDescription(semesterDescription);
                newSemester.setId(semesterID);

                //put term details into the databaase
                Dao<Semester, String> semesterDao = db.getSemesterDao();
                semesterDao.create(newSemester);

            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
            InfoModal.show("Semester \"" + semesterID + "\" erstellt!");
        }
    }

    @FXML
    public void addSemesterCancel(ActionEvent event) {

        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public void chooseSemester(ActionEvent event) {

        //lists to save years for the summer and winter terms
        ObservableList<Integer> summerYears = FXCollections.observableArrayList();
        ObservableList<String> winterYears = FXCollections.observableArrayList();

        //select item chosen by the user
        String selectedTerm = comboTerm.getSelectionModel().getSelectedItem().toString();

        //adding one year from 2018 to 2050 to the "summeryears"
        if (selectedTerm.equals("Sommersemester")) {
            for (int i = 2018; i <= 2050; i++) {
                summerYears.add(i);
            }
            //set years for the summer terms into the ComboBox
            comboYear.setItems(summerYears);
            //adding the current as well as the following year from 2018 to 2050 to the "winteryears"
        } else {
            if (selectedTerm.equals("Wintersemester")) {
                for (int i = 2018; i <= 2050; ) {
                    for (int j = 2019; j <= 2051; ) {
                        winterYears.add(i++ + "/" + j++);
                    }
                }
                //set years for the winter terms into the ComboBox
                comboYear.setItems(winterYears);
            }
        }
    }

    public void chooseYear(ActionEvent event) {
    }

    private void checkTextFieldInput(ActionEvent event) {
    }
}
