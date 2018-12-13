package controller.form.calendar;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.util.converter.IntegerStringConverter;
import modal.ErrorModal;
import modal.InfoModal;
import models.*;
import utils.TimeUtils;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class SemesterplanController {

    @FXML
    public TableView<CalendarExtraInfo> semesterplan;
    @FXML
    public Button addButton;
    @FXML
    public Button deleteBtn;
    @FXML
    public Text titleText;
    @FXML
    private Button tardyBtn;
    @FXML
    private ComboBox<Groupage> groupageComboBox;

    private Dao<CalendarExtraInfo, Integer> calendarExtraInfoDao;
    private Dao<CalendarEntry, Integer> calendarEntryDao;
    private Dao<Calendar, Integer> calendarDao;
    private Dao<Groupage, Integer> groupageDao;

    private Semester semester;
    private Calendar calendar;

    private DBManager db;

    public void initialize() {
        try {
            db = DBManager.getInstance();
            calendarExtraInfoDao = db.getCalendarExtraInfoDao();
            calendarEntryDao = db.getCalendarEntryDao();
            calendarDao = db.getCalendarDao();
            groupageDao = db.getGroupageDao();
        } catch (SQLException e) {
            ErrorModal.show("Der Semesterplan konnten nicht geladen werden!");
            return;
        }

        TableColumn<CalendarExtraInfo, String> sepDatesCol = new TableColumn<>("SEP-Termine");
        TableColumn<CalendarExtraInfo, Integer> cwCol = new TableColumn<>("KW");
        TableColumn<CalendarExtraInfo, Integer> meetingNoCol = new TableColumn<>("TerminNr.");
        TableColumn<CalendarExtraInfo, String> iterationCol = new TableColumn<>("Iteration");
        TableColumn<CalendarExtraInfo, String> lectureContentCol = new TableColumn<>("Vorlesungsinhalte");
        TableColumn<CalendarExtraInfo, String> workingPhaseCol = new TableColumn<>("Arbeitsphase");


        sepDatesCol.setCellValueFactory(s ->
                new SimpleStringProperty(TimeUtils.toSimpleDateString(s.getValue().getCalendarEntry().getStartTimeAsLocalDateTime()))
        );
        cwCol.setCellValueFactory(new PropertyValueFactory<>("calendarWeek"));
        meetingNoCol.setCellValueFactory(new PropertyValueFactory<>("meetingNo"));
        iterationCol.setCellValueFactory(new PropertyValueFactory<>("iterationInfo"));
        lectureContentCol.setCellValueFactory(new PropertyValueFactory<>("lectureInfo"));
        workingPhaseCol.setCellValueFactory(new PropertyValueFactory<>("workphaseInfo"));


        sepDatesCol.setCellFactory(TextFieldTableCell.forTableColumn());
        cwCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        meetingNoCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        iterationCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lectureContentCol.setCellFactory(TextFieldTableCell.forTableColumn());
        workingPhaseCol.setCellFactory(TextFieldTableCell.forTableColumn());

        cwCol.setOnEditCommit(event -> {
            try {
                CalendarExtraInfo table = semesterplan.getSelectionModel().getSelectedItem();
                table.setCalendarWeek(event.getNewValue());

                calendarExtraInfoDao.update(table);
            } catch (SQLException e) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
            }
        });
        meetingNoCol.setOnEditCommit(event -> {
            try {
                CalendarExtraInfo table = semesterplan.getSelectionModel().getSelectedItem();
                table.setMeetingNo(event.getNewValue());

                calendarExtraInfoDao.update(table);
            } catch (SQLException e) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
            }
        });
        iterationCol.setOnEditCommit(event -> {
            try {
                CalendarExtraInfo table = semesterplan.getSelectionModel().getSelectedItem();
                table.setIterationInfo(event.getNewValue());

                calendarExtraInfoDao.update(table);
            } catch (SQLException e) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
            }
        });
        lectureContentCol.setOnEditCommit(event -> {
            try {
                CalendarExtraInfo table = semesterplan.getSelectionModel().getSelectedItem();
                table.setLectureInfo(event.getNewValue());

                calendarExtraInfoDao.update(table);
            } catch (SQLException e) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
            }
        });
        workingPhaseCol.setOnEditCommit(event -> {
            try {
                CalendarExtraInfo table = semesterplan.getSelectionModel().getSelectedItem();
                table.setWorkphaseInfo(event.getNewValue());

                calendarExtraInfoDao.update(table);
            } catch (SQLException e) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
            }
        });

        sepDatesCol.setOnEditCommit(event -> {
            try {
                CalendarExtraInfo t = semesterplan.getSelectionModel().getSelectedItem();
                t.getCalendarEntry().setStartTime(
                        LocalDateTime.of(
                                TimeUtils.localDateFromString(event.getNewValue()),
                                LocalTime.of(0, 0, 0))
                );

                calendarEntryDao.update(t.getCalendarEntry());
            } catch (SQLException ex) {
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
            } catch (DateTimeException ex) {
                ErrorModal.show("Bitte ein gültiges Dattum (yyyy-MM-dd) eingeben!");
                semesterplan.refresh();
            }
        });

        sepDatesCol.setEditable(true);
        cwCol.setEditable(true);
        meetingNoCol.setEditable(true);
        iterationCol.setEditable(true);
        lectureContentCol.setEditable(true);
        workingPhaseCol.setEditable(true);

        sepDatesCol.setMinWidth(100);
        sepDatesCol.setMaxWidth(100);
        cwCol.setMinWidth(30);
        cwCol.setMaxWidth(30);
        meetingNoCol.setMinWidth(80);
        meetingNoCol.setMaxWidth(80);


        semesterplan.getColumns().clear();
        semesterplan.getColumns().addAll(sepDatesCol, cwCol, meetingNoCol, iterationCol, lectureContentCol, workingPhaseCol);

    }

    public void onAddBtnClicked(ActionEvent event) {
        CalendarExtraInfo c = new CalendarExtraInfo();
        CalendarEntry calendarEntry = new CalendarEntry();
        calendarEntry.setCalendar(this.calendar);
        calendarEntry.setStartTime(LocalDateTime.of(2018, 10, 8, 0, 0));
        calendarEntry.setDescription("");
        calendarEntry.setDayOfWeek(LocalDate.now().getDayOfWeek());

        c.setCalendarEntry(calendarEntry);
        c.setCalendarWeek(41);
        c.setMeetingNo(0);
        c.setIterationInfo("V-Kick-Off");
        c.setLectureInfo("Einführungsveranstaltung");
        c.setWorkphaseInfo("Gruppenanmeldung");

        try {
            calendarEntryDao.create(calendarEntry);
            calendarExtraInfoDao.create(c);
            semesterplan.getItems().add(c);
            semesterplan.getSelectionModel().select(c);
            semesterplan.edit(semesterplan.getSelectionModel().getSelectedIndex(), semesterplan.getColumns().get(0));

        } catch (SQLException ex) {
            ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
        }

    }

    public void onDeleteBtnClicked(ActionEvent event) {
        CalendarExtraInfo table = semesterplan.getSelectionModel().getSelectedItem();
        try {
            calendarExtraInfoDao.delete(table);
            semesterplan.getItems().remove(table);
        } catch (SQLException e) {
            ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
        }
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
        loadCalendar();
    }

    private void loadCalendar() {
        try {

            List<Calendar> calendars = calendarDao.query(calendarDao.queryBuilder()
                    .where()
                    .eq(Calendar.FIELD_CALENDAR_TYPE, Calendar.CalendarType.SEMESTER)
                    .and()
                    .eq(Calendar.FIELD_SEMESTER_ID, this.semester.getId()).prepare());

            if (calendars.size() == 0) {
                Calendar c = new Calendar();
                c.setSemester(this.semester);
                c.setCalendarType(Calendar.CalendarType.SEMESTER);
                calendarDao.create(c);
                calendarDao.refresh(c);
                this.calendar = c;
                return;
            }

            this.calendar = calendars.get(0);

            ObservableList<CalendarExtraInfo> extraInfo = FXCollections.observableArrayList();

            for (var entry : calendar.getCalendarEntries()) {
                extraInfo.addAll(entry.getExtraInfo());
            }

            this.semesterplan.setItems(extraInfo);

            ObservableList<Groupage> groupagesInSemester = FXCollections.observableArrayList();

            groupagesInSemester.addAll(groupageDao.queryForEq(Groupage.FIELD_SEMESTER_ID, this.semester.getId()));

            this.groupageComboBox.setItems(groupagesInSemester);


        } catch (SQLException ex) {
            ErrorModal.show("Der Semesterplan konnte nicht geladen werden!");
        }
    }

    public void onTardyBtnClicked(ActionEvent event) {
        CalendarExtraInfo selectedItem = semesterplan.getSelectionModel().getSelectedItem();
        if(selectedItem == null) {
            InfoModal.show("Bitte wählen Sie ein Element aus.");
            return;
        }
        if(this.groupageComboBox.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("Bitte wählen Sie eine Klasse aus.");
            return;
        }

        SceneManager.getInstance().getLoaderForScene(SceneType.TARDY_VIEW)
                .<TardyController>getController()
                .setArgs(selectedItem.getCalendarEntry(), groupageComboBox.getSelectionModel().getSelectedItem());
        SceneManager.getInstance().showInNewWindow(SceneType.TARDY_VIEW);
    }
}
