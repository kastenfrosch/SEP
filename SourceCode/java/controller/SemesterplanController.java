package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import modal.ErrorModal;
import models.Calendar;
import models.CalendarEntry;
import models.CalendarExtraInfo;
import utils.TimeUtils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SemesterplanController {

    @FXML
    public TableView<CalendarExtraInfo> semesterplan;
    @FXML
    public Button addButton;
    @FXML
    public Button deleteBtn;
    @FXML
    public DatePicker sepDatePicker;
    @FXML
    public Text titleText;

    private CalendarExtraInfo calendarExtraInfo;

    private Dao<CalendarExtraInfo, Integer> calendarExtraInfoDao;
    private DBManager db;

    public void initialize(){
        try {
            calendarExtraInfoDao = db.getInstance().getCalendarExtraInfoDao();
        } catch(SQLException e) {
            ErrorModal.show("Semesterplan konnten nicht geladen werden.");
            return;
        }

        TableColumn<CalendarExtraInfo, LocalDate> sepDatesCol = new TableColumn<>("SEP-Termine");
        TableColumn<CalendarExtraInfo, Integer> cwCol = new TableColumn<>("KW");
        TableColumn<CalendarExtraInfo, Integer> meetingNoCol = new TableColumn<>("TerminNr.");
        TableColumn<CalendarExtraInfo, String> iterationCol = new TableColumn<>("Iteration");
        TableColumn<CalendarExtraInfo, String> lectureContentCol = new TableColumn<>("Vorlesungsinhalte");
        TableColumn<CalendarExtraInfo, String> workingPhaseCol = new TableColumn<>("Arbeitsphase");


        sepDatesCol.setCellValueFactory(new PropertyValueFactory<>("calendarEntry"));
        cwCol.setCellValueFactory(new PropertyValueFactory<>("calendarWeek"));
        meetingNoCol.setCellValueFactory(new PropertyValueFactory<>("meetingNo"));
        iterationCol.setCellValueFactory(new PropertyValueFactory<>("iterationInfo"));
        lectureContentCol.setCellValueFactory(new PropertyValueFactory<>("lectureInfo"));
        workingPhaseCol.setCellValueFactory(new PropertyValueFactory<>("workphaseInfo"));

        //TODO: sepDatesCol.setCellFactory()
        sepDatesCol.setCellFactory(TextFieldTableCell.forTableColumn(sepDatePicker.getConverter()));
        cwCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        meetingNoCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        iterationCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lectureContentCol.setCellFactory(TextFieldTableCell.forTableColumn());
        workingPhaseCol.setCellFactory(TextFieldTableCell.forTableColumn());

        //TODO: sepDatesCol.setOnEditCommit()
        cwCol.setOnEditCommit(event -> {
            try {
                CalendarExtraInfo table = semesterplan.getSelectionModel().getSelectedItem();
                table.setCalendarWeek(event.getNewValue());

                calendarExtraInfoDao.update(table);
            } catch(SQLException e) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden.");
            }
        });
        meetingNoCol.setOnEditCommit(event -> {
            try {
                CalendarExtraInfo table = semesterplan.getSelectionModel().getSelectedItem();
                table.setMeetingNo(event.getNewValue());

                calendarExtraInfoDao.update(table);
            } catch(SQLException e) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden.");
            }
        });
        iterationCol.setOnEditCommit(event -> {
            try {
                CalendarExtraInfo table = semesterplan.getSelectionModel().getSelectedItem();
                table.setIterationInfo(event.getNewValue());

                calendarExtraInfoDao.update(table);
            } catch(SQLException e) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden.");
            }
        });
        lectureContentCol.setOnEditCommit(event -> {
            try {
                CalendarExtraInfo table = semesterplan.getSelectionModel().getSelectedItem();
                table.setLectureInfo(event.getNewValue());

                calendarExtraInfoDao.update(table);
            } catch(SQLException e) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden.");
            }
        });
        workingPhaseCol.setOnEditCommit(event -> {
            try {
                CalendarExtraInfo table = semesterplan.getSelectionModel().getSelectedItem();
                table.setWorkphaseInfo(event.getNewValue());

                calendarExtraInfoDao.update(table);
            } catch(SQLException e) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden.");
            }
        });

        sepDatesCol.setEditable(true);
        cwCol.setEditable(true);
        meetingNoCol.setEditable(true);
        iterationCol.setEditable(true);
        lectureContentCol.setEditable(true);
        workingPhaseCol.setEditable(true);

        semesterplan.getColumns().addAll(sepDatesCol, cwCol, meetingNoCol, iterationCol, lectureContentCol, workingPhaseCol);

        //TODO: refreshList() Methode fehlt
//        refreshList();
    }

    public void chooseDate(ActionEvent event) {

    }

    public void onAddBtnClicked(ActionEvent event) {
        CalendarExtraInfo c = new CalendarExtraInfo();
        CalendarEntry calendarEntry = new CalendarEntry();
        calendarEntry.setStartTime(LocalDateTime.of(2018, 10,8,0,0));

        //TODO: table.setCalendarEntry() hinzufügen bzw. einen richtugen CalendarEntry
        c.setCalendarEntry(calendarEntry);
        c.setCalendarWeek(41);
        c.setMeetingNo(0);
        c.setIterationInfo("V-Kick-Off");
        c.setLectureInfo("Einführungsveranstaltung");
        c.setWorkphaseInfo("Gruppenanmeldung");

        try {
            calendarExtraInfoDao.create(c);
            semesterplan.getItems().add(c);
            semesterplan.getSelectionModel().select(c);
            semesterplan.edit(semesterplan.getSelectionModel().getSelectedIndex(), semesterplan.getColumns().get(0));

        } catch(SQLException ex) {
            ErrorModal.show("Änderungen konnten nicht gespeichert werden.");
        }

    }

    private void refreshList() {
        try {
//            if (calendarExtraInfo == null) {
//                Dao<CalendarExtraInfo, Integer> calendarExtraInfoDao = db.getCalendarExtraInfoDao();
//                this.calendarExtraInfo = calendarExtraInfoDao.queryForId(1);
//            }
            Dao<CalendarExtraInfo, Integer> dao = db.getCalendarExtraInfoDao();
            ObservableList<CalendarExtraInfo> plaene = FXCollections.observableArrayList(
                    dao.queryForEq(CalendarExtraInfo.FIELD_EXTRA_INFO_ID, CalendarEntry.FIELD_ENTRY_ID)
            );
            this.semesterplan.setItems(plaene);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void onDeleteBtnClicked(ActionEvent event) {
        CalendarExtraInfo table = semesterplan.getSelectionModel().getSelectedItem();
        try {
            calendarExtraInfoDao.delete(table);
            semesterplan.getItems().remove(table);
        } catch (SQLException e) {
            ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden.");
        }
    }
}
