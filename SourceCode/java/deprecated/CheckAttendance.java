package deprecated;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import modal.InfoModal;
import models.Group;
import models.Groupage;
import models.Student;

import java.sql.SQLException;

public class CheckAttendance {
    @FXML
    public ComboBox comboBoxGroupage;
    @FXML
    public ComboBox comboBoxGroup;
    @FXML
    public Label lblGroupage;
    @FXML
    public Label lblGroup;
    @FXML
    public Label lblStudents;
    @FXML
    public ComboBox <String> comboBoxStudent1;
    @FXML
    public ComboBox <String> comboBoxStudent2;
    @FXML
    public ComboBox <String> comboBoxStudent3;
    @FXML
    public ComboBox <String> comboBoxStudent4;
    @FXML
    public ComboBox <String> comboBoxStudent5;
    @FXML
    public ComboBox <String> comboBoxStudent6;
    @FXML
    public ComboBox <String> comboBoxStudent7;
    @FXML
    public Button btnCancel;
    @FXML
    public CheckBox checkBoxPerfectAttendance;
    @FXML
    public Label lblPerfectAttendance;
    @FXML
    public ComboBox <String> comboBoxAttendanceStudent1;
    @FXML
    public ComboBox <String> comboBoxAttendanceStudent2;
    @FXML
    public ComboBox <String> comboBoxAttendanceStudent3;
    @FXML
    public ComboBox <String> comboBoxAttendanceStudent4;
    @FXML
    public ComboBox <String> comboBoxAttendanceStudent5;
    @FXML
    public ComboBox <String> comboBoxAttendanceStudent6;
    @FXML
    public ComboBox <String> comboBoxAttendanceStudent7;
    @FXML
    public Text textAttendanceStatus;
    @FXML
    public Text textMissedTime;
    @FXML
    public TextField textFieldMissedTimeStudent1;
    @FXML
    public TextField textFieldMissedTimeStudent2;
    @FXML
    public TextField textFieldMissedTimeStudent3;
    @FXML
    public TextField textFieldMissedTimeStudent4;
    @FXML
    public TextField textFieldMissedTimeStudent5;
    @FXML
    public TextField textFieldMissedTimeStudent6;
    @FXML
    public TextField textFieldMissedTimeStudent7;
    @FXML
    public Button btnSave;

    private DBManager db;

    {
        try {
            db = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initialize() throws SQLException {
        ObservableList<String> groupages = FXCollections.observableArrayList();
        Dao <Groupage, Integer> groupageDao = db.getGroupageDao();
        groupages.addAll(String.valueOf(groupageDao.queryForAll()));
        comboBoxGroupage.setItems(groupages);

        ObservableList<String> groups = FXCollections.observableArrayList();
        Dao<Group, Integer> groupDao = db.getGroupDao();
        groups.addAll(String.valueOf(groupDao.queryForAll()));
        comboBoxGroup.setItems(groups);

        ObservableList<String> students = FXCollections.observableArrayList();
        //TODO: Studenten initialisieren
        Dao<Student, Integer> studentDao = db.getStudentDao();
        students.addAll(String.valueOf(studentDao.queryForAll()));
        comboBoxStudent1.setItems(students);
        comboBoxStudent2.setItems(students);
        comboBoxStudent3.setItems(students);
        comboBoxStudent4.setItems(students);
        comboBoxStudent5.setItems(students);
        comboBoxStudent6.setItems(students);
        comboBoxStudent7.setItems(students);

        comboBoxGroupage.getSelectionModel().select(0);
        comboBoxGroup.getSelectionModel().select(0);
        comboBoxStudent1.getSelectionModel().select(0);
        comboBoxStudent2.getSelectionModel().select(0);
        comboBoxStudent3.getSelectionModel().select(0);
        comboBoxStudent4.getSelectionModel().select(0);
        comboBoxStudent5.getSelectionModel().select(0);
        comboBoxStudent6.getSelectionModel().select(0);
        comboBoxStudent7.getSelectionModel().select(0);



        ObservableList<String> attendanceStatus = FXCollections.observableArrayList();
       //TODO: automatisch 0 Fehlminuten
        attendanceStatus.add("Anwesend");
        attendanceStatus.add("Verspätet");
        //TODO: nicht anwesend = Max. Fehlminuten
        attendanceStatus.add("Nicht anwesend");
        comboBoxAttendanceStudent1.setItems(attendanceStatus);
        comboBoxAttendanceStudent2.setItems(attendanceStatus);
        comboBoxAttendanceStudent3.setItems(attendanceStatus);
        comboBoxAttendanceStudent4.setItems(attendanceStatus);
        comboBoxAttendanceStudent5.setItems(attendanceStatus);
        comboBoxAttendanceStudent6.setItems(attendanceStatus);
        comboBoxAttendanceStudent7.setItems(attendanceStatus);
    }

    public void cancelAttendance(ActionEvent event) {

        //TODO:: richtigen SceneType angeben
        //SceneManager.getInstance().closeWindow(SceneType.Check_Attendance);
    }

    public void saveAttendance(ActionEvent event) {

        //TODO: Anwesend
        if (comboBoxAttendanceStudent1.getSelectionModel().getSelectedItem().equals("Anwesend")) {
            textFieldMissedTimeStudent1.setText("0");
            }
        if (comboBoxAttendanceStudent2.getSelectionModel().getSelectedItem().equals("Anwesend")) {
            textFieldMissedTimeStudent2.setText("0");
        }
        if (comboBoxAttendanceStudent3.getSelectionModel().getSelectedItem().equals("Anwesend")) {
            textFieldMissedTimeStudent3.setText("0");
        }
        if (comboBoxAttendanceStudent4.getSelectionModel().getSelectedItem().equals("Anwesend")) {
            textFieldMissedTimeStudent4.setText("0");
        }
        if (comboBoxAttendanceStudent5.getSelectionModel().getSelectedItem().equals("Anwesend")) {
            textFieldMissedTimeStudent5.setText("0");
        }
        if (comboBoxAttendanceStudent6.getSelectionModel().getSelectedItem().equals("Anwesend")) {
            textFieldMissedTimeStudent6.setText("0");
        }
        if (comboBoxAttendanceStudent7.getSelectionModel().getSelectedItem().equals("Anwesend")) {
            textFieldMissedTimeStudent7.setText("0");
        }

        // TODO: If-Else Bedingung überprüfen
        //TODO: Nicht Anwesend
        else
            if (comboBoxAttendanceStudent1.getSelectionModel().getSelectedItem().equals("Nicht anwesend")){
                {
                    textFieldMissedTimeStudent1.setText("120");
                }
                if(comboBoxAttendanceStudent2.getSelectionModel().getSelectedItem().equals("Nicht anwesend")){
                    textFieldMissedTimeStudent2.setText("120");
                }
                if(comboBoxAttendanceStudent3.getSelectionModel().getSelectedItem().equals("Nicht anwesend")){
                    textFieldMissedTimeStudent3.setText("120");
                }
                if(comboBoxAttendanceStudent4.getSelectionModel().getSelectedItem().equals("Nicht anwesend")){
                    textFieldMissedTimeStudent4.setText("120");
                }
                if(comboBoxAttendanceStudent5.getSelectionModel().getSelectedItem().equals("Nicht anwesend")){
                    textFieldMissedTimeStudent5.setText("120");
                }
                if(comboBoxAttendanceStudent6.getSelectionModel().getSelectedItem().equals("Nicht anwesend")){
                    textFieldMissedTimeStudent6.setText("120");
                }
                if(comboBoxAttendanceStudent7.getSelectionModel().getSelectedItem().equals("Nicht anwesend")){
                    textFieldMissedTimeStudent7.setText("120");
                }
                //TODO: Verspätet
        } else {
                if (comboBoxAttendanceStudent1.getSelectionModel().getSelectedItem().equals("Verspätet")) {
                    textFieldMissedTimeStudent1.setText(textFieldMissedTimeStudent1.getText());
                }
                if (comboBoxAttendanceStudent2.getSelectionModel().getSelectedItem().equals("Verspätet")) {
                    textFieldMissedTimeStudent2.setText(textFieldMissedTimeStudent2.getText());
                }
                if (comboBoxAttendanceStudent3.getSelectionModel().getSelectedItem().equals("Verspätet")) {
                    textFieldMissedTimeStudent3.setText(textFieldMissedTimeStudent3.getText());
                }
                if (comboBoxAttendanceStudent4.getSelectionModel().getSelectedItem().equals("Verspätet")) {
                    textFieldMissedTimeStudent4.setText(textFieldMissedTimeStudent4.getText());
                }
                if (comboBoxAttendanceStudent5.getSelectionModel().getSelectedItem().equals("Verspätet")) {
                    textFieldMissedTimeStudent5.setText(textFieldMissedTimeStudent5.getText());
                }
                if (comboBoxAttendanceStudent6.getSelectionModel().getSelectedItem().equals("Verspätet")) {
                    textFieldMissedTimeStudent6.setText(textFieldMissedTimeStudent6.getText());
                }
                if (comboBoxAttendanceStudent7.getSelectionModel().getSelectedItem().equals("Verspätet")) {
                    textFieldMissedTimeStudent7.setText(textFieldMissedTimeStudent7.getText());
                }
            }
        }
}
