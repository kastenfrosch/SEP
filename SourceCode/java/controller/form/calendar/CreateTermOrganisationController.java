package controller.form.calendar;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedUpdate;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import modal.ErrorModal;
import modal.InfoModal;
import models.Calendar;
import models.CalendarEntry;
import models.CalendarExtraInfo;
import utils.TimeUtils;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javafx.scene.control.cell.TextFieldTableCell.*;
import static utils.TimeUtils.localDateFromString;
import static utils.TimeUtils.toSimpleString;

//TODO: Alles in IntelliJ übertragen
//TODO: Semesterplan in der Datenbank speichern
//TODO: Wer macht CheckAttendance? Geht genauso wie Semesterplan nur mit anderen Spaltennamen
//TODO: Semesterplan zu Beginn leer gestalten (Benutzer muss ihn selbst anlegen und diesen dann speichern können)
//TODO: Änderung/Bearbeitung speichern und nicht nach dem nächsten hinzugefügten Eintrag löschen
//TODO: Dynamik: Termine festlegen, KW filtern, SEP-Termine aufaddieren
//TODO: fehlerhafte Spalten in Ordnung bringen (bei manchen wird nichts eingetragen)
//TODO: gespeichertes muss wieder aufgerufen werden könnnen

public class CreateTermOrganisationController {

    @FXML
    public TableColumn<CalendarExtraInfo, LocalDate> colSEPdates;
    @FXML
    public TableColumn<CalendarExtraInfo, Integer> colCalendarWeek;
    @FXML
    public TableColumn<CalendarExtraInfo, Integer> colMeetingNo;
    @FXML
    public TableColumn<CalendarExtraInfo, String> colIteration;
    @FXML
    public TableColumn<CalendarExtraInfo, String> colLectureContent;
    @FXML
    public TableColumn<CalendarExtraInfo, String> colWorkingPhase;
    @FXML
    public TableView<CalendarExtraInfo> tableViewTermOrganisation;
    @FXML
    public DatePicker sepDatePicker;
    @FXML
    public TextField txtKW;
    @FXML
    public TextField txtMeetingNo;
    @FXML
    public TextField txtIteration;
    @FXML
    public TextField txtLectureContent;
    @FXML
    public TextField txtWorkingPhase;
    @FXML
    public Button btnAdd;
    @FXML
    public Button btnSave;
    @FXML
    public Button btnShow;

    private Dao<CalendarExtraInfo, Integer> calendarExtraInfoDao;

    private DBManager db;

    {
        try {
            db = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //TODO: Semesterplan in der Datenbank speichern; Änderung/Bearbeitung speichern und nicht nach dem nächsten hinzugefügten Eintrag löschen;gespeichertes muss wieder aufgerufen werden könnnen
//TODO: Semesterplan zu Beginn leer gestalten (Benutzer muss ihn selbst anlegen und diesen dann speichern können)
//TODO: Dynamik: Termine festlegen, KW filtern, SEP-Termine aufaddieren
    CalendarEntry calendarEntry = new CalendarEntry();
    ObservableList<CalendarExtraInfo> ersteZeile =
            FXCollections.observableArrayList(new CalendarExtraInfo(calendarEntry, "V-Kick-Off", "Einführungsveranstaltung", "Gruppenanmeldung", 41, 0));

    public void initialize() {
        //Create first row for the table
        CalendarEntry calendarEntry = new CalendarEntry();
        calendarEntry.setStartTime(LocalDateTime.of(2018, 10, 8, 18, 0));
        ObservableList<CalendarExtraInfo> ersteZeile =
                FXCollections.observableArrayList(new CalendarExtraInfo(calendarEntry, "V-Kick-Off", "Einführungsveranstaltung", "Gruppenanmeldung", 41, 0));

        //Setting of Cell Value Factory
        colSEPdates.setCellValueFactory(new PropertyValueFactory<CalendarExtraInfo, LocalDate>("calendarEntry"));
        colCalendarWeek.setCellValueFactory(new PropertyValueFactory<CalendarExtraInfo, Integer>("calendarWeek"));
        colMeetingNo.setCellValueFactory(new PropertyValueFactory<CalendarExtraInfo, Integer>("meetingNo"));
        colIteration.setCellValueFactory(new PropertyValueFactory<CalendarExtraInfo, String>("iterationInfo"));
        colLectureContent.setCellValueFactory(new PropertyValueFactory<CalendarExtraInfo, String>("lectureInfo"));
        colWorkingPhase.setCellValueFactory(new PropertyValueFactory<CalendarExtraInfo, String>("workphaseInfo"));

        //Add first row to the table
        tableViewTermOrganisation.setItems(ersteZeile);

        //Make the table editable
        tableViewTermOrganisation.setEditable(true);

        //StringConverter with different data types
//        colSEPdates.setCellFactory(TextFieldTableCell.forTableColumn(sepDatePicker.getConverter()));
        colCalendarWeek.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colMeetingNo.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colIteration.setCellFactory(TextFieldTableCell.forTableColumn());
        colLectureContent.setCellFactory(TextFieldTableCell.forTableColumn());
        colWorkingPhase.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    private void clearForm() {
        //clear Textfields
        txtKW.clear();
        txtMeetingNo.clear();
        txtIteration.clear();
        txtLectureContent.clear();
        txtWorkingPhase.clear();
    }

    //TODO: nur eine Zelle soll geändert werden, nicht direkt alle
    @FXML
    //Edit table cells containing Calendar Entrys
    public void editTableCell(CellEditEvent<CalendarExtraInfo, CalendarEntry> calendarExtraInfoCalendarEntryCellEditEvent) {
        // calendarExtraInfoCalendarEntryCellEditEvent.getTableView().getItems().get(calendarExtraInfoCalendarEntryCellEditEvent.getTablePosition().getRow()).setCalendarEntry(calendarExtraInfoCalendarEntryCellEditEvent.getNewValue());
    }
    @FXML
    //Edit table cells containing integer values
    public void editTableCellInteger(CellEditEvent<CalendarExtraInfo, Integer> calendarExtraInfoIntegerCellEditEvent) {

        calendarExtraInfoIntegerCellEditEvent.getTableView().getItems().get(calendarExtraInfoIntegerCellEditEvent.getTablePosition().getRow()).setCalendarWeek(calendarExtraInfoIntegerCellEditEvent.getNewValue());
        calendarExtraInfoIntegerCellEditEvent.getTableView().getItems().get(calendarExtraInfoIntegerCellEditEvent.getTablePosition().getRow()).setMeetingNo(calendarExtraInfoIntegerCellEditEvent.getNewValue());
        //Update in database
//        try {
//            calendarExtraInfoDao.update((PreparedUpdate<CalendarExtraInfo>) calendarExtraInfoIntegerCellEditEvent);
//        } catch (SQLException e) {
//            calendarExtraInfoIntegerCellEditEvent.consume();
//            ErrorModal.show("Die Änderungen wurden nicht gepseichert!");
//        }
    }

    @FXML
    //edit table cells containing string values
    public void editTableCellString(CellEditEvent<CalendarExtraInfo, String> calendarExtraInfoStringCellEditEvent) {

        calendarExtraInfoStringCellEditEvent.getTableView().getItems().get(calendarExtraInfoStringCellEditEvent.getTablePosition().getRow()).setIterationInfo(calendarExtraInfoStringCellEditEvent.getNewValue());
        calendarExtraInfoStringCellEditEvent.getTableView().getItems().get(calendarExtraInfoStringCellEditEvent.getTablePosition().getRow()).setLectureInfo(calendarExtraInfoStringCellEditEvent.getNewValue());
        calendarExtraInfoStringCellEditEvent.getTableView().getItems().get(calendarExtraInfoStringCellEditEvent.getTablePosition().getRow()).setWorkphaseInfo(calendarExtraInfoStringCellEditEvent.getNewValue());
            //Update in database
//        try {
//            calendarExtraInfoDao.update((PreparedUpdate<CalendarExtraInfo>) calendarExtraInfoStringCellEditEvent);
//        } catch (SQLException e) {
//            calendarExtraInfoStringCellEditEvent.consume();
//            ErrorModal.show("Die Änderungen wurden nicht gespeichert!");
//        }

    }

    public void onAddBtnClicked(ActionEvent event) {
        //TODO:If-Bedingungen erzeugen Error da es zu konflikten zwischen den Datentypen kommt
//        if(txtKW.getText().isEmpty() == true || txtKW.getText() == null){
//            InfoModal.show("Fehler", null, "Es wurde keine Kalenderwoche eingetragen!");
//        }
//        if(txtMeetingNo.getText().isEmpty() == true || txtMeetingNo.getText() == null){
//            InfoModal.show("Fehler", null, "Es wurde keien Veranstaltungsnummer eingetragen!");
//        }
        //Adding new Rows to the table where the first row is still fixed
//        calendarEntry = calendarEntry.setStartTime(sepDatePicker.getValue();
        CalendarExtraInfo entry = new CalendarExtraInfo(calendarEntry ,txtIteration.getText(), txtLectureContent.getText(),txtWorkingPhase.getText(), Integer.parseInt(txtKW.getText()), Integer.parseInt(txtMeetingNo.getText()));
        ersteZeile.add(entry);
        tableViewTermOrganisation.setItems(ersteZeile);

        clearForm();

    }

    public void onSaveBtnClicked(ActionEvent event) throws SQLException {
        //1. Methode
 //       models.CalendarExtraInfo newTermOrganisation= new CalendarExtraInfo();
////        CalendarExtraInfo sepDates = new CalendarExtraInfo();
//
//
////        newTermOrganisation.setCalendarEntry(sepDates.getCalendarEntry());
//        newTermOrganisation.setCalendarWeek(tableViewTermOrganisation.getSelectionModel().getSelectedItem().getCalendarWeek());
//        newTermOrganisation.setMeetingNo(tableViewTermOrganisation.getSelectionModel().getSelectedItem().getMeetingNo());
//        newTermOrganisation.setIterationInfo(tableViewTermOrganisation.getSelectionModel().getSelectedItem().getIterationInfo());
//        newTermOrganisation.setLectureInfo(tableViewTermOrganisation.getSelectionModel().getSelectedItem().getLectureInfo());
//        newTermOrganisation.setWorkphaseInfo(tableViewTermOrganisation.getSelectionModel().getSelectedItem().getWorkphaseInfo());
//
//
//        Dao<CalendarExtraInfo, Integer> calendarExtraInfoDao = db.getCalendarExtraInfoDao();
//        calendarExtraInfoDao.update(newTermOrganisation);
//
//        InfoModal.show("Semesterplan wurde erstellt!");

        //2. Methode
        CalendarExtraInfo c = new CalendarExtraInfo();
//        CalendarEntry entry = new CalendarEntry();
//        entry.setStartTime(LocalDateTime.now());
//        c.setCalendarEntry(getCalendarEntries().get(1));
        c.setCalendarWeek(tableViewTermOrganisation.getSelectionModel().getSelectedItem().getCalendarWeek());
        c.setMeetingNo(tableViewTermOrganisation.getSelectionModel().getSelectedItem().getMeetingNo());
        c.setIterationInfo(tableViewTermOrganisation.getSelectionModel().getSelectedItem().getIterationInfo());
        c.setLectureInfo(tableViewTermOrganisation.getSelectionModel().getSelectedItem().getLectureInfo());
        c.setWorkphaseInfo(tableViewTermOrganisation.getSelectionModel().getSelectedItem().getWorkphaseInfo());

        try {
            calendarExtraInfoDao.create(c);
            tableViewTermOrganisation.getItems().add(c);
            tableViewTermOrganisation.getSelectionModel().select(c);
            tableViewTermOrganisation.edit(tableViewTermOrganisation.getSelectionModel().getSelectedIndex(), tableViewTermOrganisation.getColumns().get(0));

        } catch(SQLException e) {
            ErrorModal.show("Die Änderungen wurden nicht gespeichert!");
        }


    }
    public void buttonShow(ActionEvent event) {

        CalendarExtraInfo tableContent = new CalendarExtraInfo();
        List<List<String>>  liste = new ArrayList<>();

        for(int i = 0; i < tableViewTermOrganisation.getItems().size(); i++){
            tableContent = tableViewTermOrganisation.getItems().get(i);
            liste.add(new ArrayList<>());
            liste.get(i).add(String.valueOf(tableContent.getCalendarWeek()));
            liste.get(i).add(String.valueOf(tableContent.getMeetingNo()));
            liste.get(i).add(tableContent.getIterationInfo());
            liste.get(i).add(tableContent.getLectureInfo());
            liste.get(i).add(tableContent.getWorkphaseInfo());
        }

        for(int i = 0; i < liste.size(); i++){
            for(int j = 0; j < liste.get(i).size(); j++){
                System.out.println(liste.get(i).get(j));
            }
        }

    }

//    public ObservableList<CalendarEntry> getCalendarEntries(){
//
//        ObservableList<CalendarEntry> entries = FXCollections.observableArrayList();
//
//        entries.add(new CalendarEntry(localDateFromString("2018-10-08")));
//        entries.add(new CalendarEntry(localDateFromString("2018-10-11")));
//        entries.add(new CalendarEntry(localDateFromString("2018-10-15")));
//        entries.add(new CalendarEntry(localDateFromString("2018-10-18")));
//        entries.add(new CalendarEntry(localDateFromString("2018-10-22")));
//
//
//        return entries;
//    }
}

