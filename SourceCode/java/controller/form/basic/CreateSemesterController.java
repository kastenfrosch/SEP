package controller.form.basic;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import controller.HomeScreenController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import modal.ErrorModal;
import models.Semester;
import modal.InfoModal;
import utils.scene.SceneManager;
import utils.scene.SceneType;

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
    public ComboBox<String> comboTerm;

    @FXML
    public ComboBox<Integer> comboYear;

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

        //lists to save years for the summer and winter terms
        ObservableList<Integer> years = FXCollections.observableArrayList();


        //adding one year from 2018 to 2050 to the "summeryears"
        for (int i = 2018; i <= 2050; i++) {
            years.add(i);
        }
        //set years for the summer terms into the ComboBox
        comboYear.setItems(years);

        comboTerm.getSelectionModel().select(0);
        comboYear.getSelectionModel().select(0);
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

        switch (checkTerm) {
            case "Wintersemester":
                createWintersemester();
                break;
            case "Sommersemester":
                createSommersemster();
                break;
            default:
                InfoModal.show("Fehler", null, "Es konnte kein Semester erstellt werden. Bitte versuchen sie es erneut!");
        }
    }

    private void createWintersemester() {
        //select item chosen by the user
        int year = comboYear.getSelectionModel().getSelectedItem();
        year = year % 100;
        String yearString = String.valueOf(year) + "/" + String.valueOf(year+1);
        String termString = comboTerm.getSelectionModel().getSelectedItem();

        //ID for each term consisting of the terms short version and the year
        String semesterID;

        if (termString.equals("Wintersemester")) {
            semesterID = "WS";

            semesterID = semesterID + yearString;

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

                InfoModal.show("Semester \"" + semesterID + "\" erstellt!");

                SceneManager.getInstance().getLoaderForScene(SceneType.HOME).
                        <HomeScreenController>getController().setSelectedNode(newSemester);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
                ErrorModal.show("Semester konnte nicht erstellt werden.");
            }
        }

    }

    private void createSommersemster() {
        //select item chosen by the user
        int yearString = comboYear.getSelectionModel().getSelectedItem();
        String termString =  comboTerm.getSelectionModel().getSelectedItem();

        //ID for each term consisting of the terms short version and the year
        String semesterID;

        if (termString.equals("Sommersemester")) {
            semesterID = "SS";

            semesterID = semesterID + (yearString%100);

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
                InfoModal.show("Semester \"" + semesterID + "\" erstellt!");

                SceneManager.getInstance().getLoaderForScene(SceneType.HOME).
                        <HomeScreenController>getController().setSelectedNode(newSemester);

            } catch (java.sql.SQLException e) {
                e.printStackTrace();
                ErrorModal.show("Semester konnte nicht erstellt werden.");
            }
        }

    }

    @FXML
    public void addSemesterCancel(ActionEvent event) {
        SceneManager.getInstance().getLoaderForScene(SceneType.HOME).
                <HomeScreenController>getController().showTabContent();
    }

    public void chooseSemester(ActionEvent event) {
    }

    public void chooseYear(ActionEvent event) {
    }
}
