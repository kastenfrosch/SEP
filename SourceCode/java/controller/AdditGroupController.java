package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import modal.InfoModal;
import models.GroupModel;
import models.GroupageModel;
import models.SemesterModel;

public class AdditGroupController {

    @FXML
    public Text titleText;
    @FXML
    public Button createGroupBtn;
    @FXML
    public Button cancelBtn;
    @FXML
    public Label groupnameLbl;
    @FXML
    public TextField groupnameInput;
    @FXML
    public Label semesterLbl;
    @FXML
    public Label groupageLbl;
    @FXML
    public ComboBox semesterComboBox;
    @FXML
    public ComboBox groupageComboBox;

    @FXML
    public void initialize() {

        // this is to set up some testing data

        models.SemesterModel testsem1 = new SemesterModel(1, "WS1819");
        models.SemesterModel testsem2 = new SemesterModel(2, "SS19");
        models.SemesterModel testsem3 = new SemesterModel(3, "WS1920");
        models.SemesterModel testsem4 = new SemesterModel(4, "SS20");

        ObservableList<String> semList = FXCollections.observableArrayList();
        semList.addAll(testsem1.getDescription(),
                       testsem2.getDescription(),
                       testsem3.getDescription(),
                       testsem4.getDescription());

        semesterComboBox.setItems(semList);

        models.GroupageModel testgrpg1 = new GroupageModel(1, "grpg1", 1);
        models.GroupageModel testgrpg2 = new GroupageModel(2, "grpg2",1);
        models.GroupageModel testgrpg3 = new GroupageModel(3, "grpg3", 2);
        models.GroupageModel testgrpg4 = new GroupageModel(4, "grpg4", 2);
        models.GroupageModel testgrpg5 = new GroupageModel(5, "grpg5", 3);
        models.GroupageModel testgrpg6 = new GroupageModel(6, "grpg6",3);
        models.GroupageModel testgrpg7 = new GroupageModel(7, "grpg7", 4);
        models.GroupageModel testgrpg8 = new GroupageModel(8, "grpg8", 4);

        ///////////////////////////////////////////////////////////////////////
        // to do:
        // check which semester is selected and show possible grpgs accordingly



        ///////////////////////////////////////////////////////////////////////

        ObservableList<String> grpgList = FXCollections.observableArrayList();
        grpgList.addAll(testgrpg1.getDescription(),
                        testgrpg2.getDescription(),
                        testgrpg3.getDescription(),
                        testgrpg4.getDescription(),
                        testgrpg5.getDescription(),
                        testgrpg6.getDescription(),
                        testgrpg7.getDescription(),
                        testgrpg8.getDescription());

        groupageComboBox.setItems(grpgList);

    }


    public void groupnameInput(ActionEvent actionEvent) {
    }

    public void chooseGroupageComboBox(ActionEvent actionEvent) {
    }

    public void chooseSemesterComboBox(ActionEvent actionEvent) {
    }

    public void additGroupCreate(ActionEvent actionEvent) {

        // use all selections and text inputs to create a group.

        // making sure that groupname is not empty.
        // if not, set groupname to the input.
        String name;
        if (groupnameInput.getText() == null || groupnameInput.getText().isBlank()) {
            InfoModal.show("FEHLER!", null, "Kein Gruppenname eingegeben!");
        }
        name = groupnameInput.getText();

        // making sure that groupage is not empty.
        // if not, set groupage to the selection.
        String groupageString;
        if (groupageComboBox.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("FEHLER!", null, "Keine Groupage ausgewählt!");
        }
        groupageString = (String) groupageComboBox.getSelectionModel().getSelectedItem();

        // making sure that semester is not empty.
        // if not, set groupage to the selection.
        String semesterString;
        if (semesterComboBox.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("FEHLER!", null, "Kein Semester ausgewählt");
        }
        semesterString = (String) semesterComboBox.getSelectionModel().getSelectedItem();

        try {

            // setting groupage string to the corresponding groupage_id
            Dao<GroupageModel, Integer> groupage = DBManager.getInstance(null).getGroupageDao();
            groupage.queryForEq("description", groupageString);

            // setting semester string to the corresponding semester_id
            Dao<SemesterModel, String> semester = DBManager.getInstance(null).getSemesterDao();
            semester.queryForEq("description", semesterString);

            // creating new group instance
            models.GroupModel newGroup = new GroupModel();

            // passing variables to the new group instance
            newGroup.setName(name);
            //newGroup.setSemester_id(semester);
            //newGroup.setGroupage_id(groupage);

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        // confirmation message
        InfoModal.show("Group \"" + name + "\" created!");

    }

    public void additGroupCancel(ActionEvent actionEvent) {

        // close window and abort
        InfoModal.show("Testmsg!");

    }

}
