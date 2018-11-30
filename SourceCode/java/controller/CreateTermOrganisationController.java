package controller;

import com.j256.ormlite.dao.Dao;
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
    public TableColumn<CalendarExtraInfo, CalendarEntry> colSEPdates;
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

        CalendarEntry calendarEntry = new CalendarEntry();
        ObservableList<CalendarExtraInfo> ersteZeile =
                FXCollections.observableArrayList(new CalendarExtraInfo(calendarEntry, "V-Kick-Off", "Einführungsveranstaltung", "Gruppenanmeldung", 41, 0));

        // colSEPdates.setCellValueFactory(new PropertyValueFactory<CalendarExtraInfo, CalendarEntry>("calendarEntry"));
        colCalendarWeek.setCellValueFactory(new PropertyValueFactory<CalendarExtraInfo, Integer>("calendarWeek"));
        colMeetingNo.setCellValueFactory(new PropertyValueFactory<CalendarExtraInfo, Integer>("meetingNo"));
        colIteration.setCellValueFactory(new PropertyValueFactory<CalendarExtraInfo, String>("iterationInfo"));
        colLectureContent.setCellValueFactory(new PropertyValueFactory<CalendarExtraInfo, String>("lectureInfo"));
        colWorkingPhase.setCellValueFactory(new PropertyValueFactory<CalendarExtraInfo, String>("workphaseInfo"));

        tableViewTermOrganisation.setItems(ersteZeile);

        //Make the table editable
        tableViewTermOrganisation.setEditable(true);

//        colSEPdates.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<CalendarEntry>() {
//            @Override
//            public String toString(CalendarEntry calendarEntry) {
//                return null;
//            }
//
//            @Override
//            public CalendarEntry fromString(String s) {
//               return null;
//            }
//        }));
        colCalendarWeek.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colMeetingNo.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colIteration.setCellFactory(TextFieldTableCell.forTableColumn());
        colLectureContent.setCellFactory(TextFieldTableCell.forTableColumn());
        colWorkingPhase.setCellFactory(TextFieldTableCell.forTableColumn());


    }

    public void addDates(ActionEvent event) {


        //TODO:If-Bedingungen erzeugen Error da es zu konflikten zwischen den Datentypen kommt
        if(txtKW.getText().isEmpty() == true || txtKW.getText() == null){
            InfoModal.show("Fehler", null, "Es wurde keine Kalenderwoche eingetragen!");
        }
        if(txtMeetingNo.getText().isEmpty() == true || txtMeetingNo.getText() == null){
            InfoModal.show("Fehler", null, "Es wurde keien Veranstaltungsnummer eingetragen!");
        }

        CalendarExtraInfo entry = new CalendarExtraInfo(calendarEntry ,txtIteration.getText(), txtLectureContent.getText(),txtWorkingPhase.getText(), Integer.parseInt(txtKW.getText()), Integer.parseInt(txtMeetingNo.getText()));
        ersteZeile.add(entry);
        tableViewTermOrganisation.setItems(ersteZeile);

        clearForm();
    }

    private void clearForm() {

        txtKW.clear();
        txtMeetingNo.clear();
        txtIteration.clear();
        txtLectureContent.clear();
        txtWorkingPhase.clear();
    }

    public void saveData(ActionEvent event) throws SQLException {

        models.CalendarExtraInfo newTermOrganisation= new CalendarExtraInfo();
//        CalendarExtraInfo sepDates = new CalendarExtraInfo();

        //set description and id for a new term
//        newTermOrganisation.setCalendarEntry(sepDates.getCalendarEntry());
        newTermOrganisation.setCalendarWeek(tableViewTermOrganisation.getSelectionModel().getSelectedItem().getCalendarWeek());
        newTermOrganisation.setMeetingNo(tableViewTermOrganisation.getSelectionModel().getSelectedItem().getMeetingNo());
        newTermOrganisation.setIterationInfo(tableViewTermOrganisation.getSelectionModel().getSelectedItem().getIterationInfo());
        newTermOrganisation.setLectureInfo(tableViewTermOrganisation.getSelectionModel().getSelectedItem().getLectureInfo());
        newTermOrganisation.setWorkphaseInfo(tableViewTermOrganisation.getSelectionModel().getSelectedItem().getWorkphaseInfo());

        //put term details into the databaase
        Dao<CalendarExtraInfo, Integer> calendarExtraInfoDao = db.getCalendarExtraInfoDao();
        calendarExtraInfoDao.update(newTermOrganisation);

        InfoModal.show("Semesterplan wurde erstellt!");

//        CalendarExtraInfo calenderobject = new CalendarExtraInfo();
//        calenderobject.setCalendarEntry(calendarEntry);
//        calenderobject.setCalendarWeek(tableViewTermOrganisation.getSelectionModel().getSelectedItem().getCalendarWeek());
//        calenderobject.setMeetingNo(tableViewTermOrganisation.getSelectionModel().getSelectedItem().getMeetingNo());
//        calenderobject.setIterationInfo(tableViewTermOrganisation.getSelectionModel().getSelectedItem().getIterationInfo());
//        calenderobject.setLectureInfo(tableViewTermOrganisation.getSelectionModel().getSelectedItem().getLectureInfo());
//        calenderobject.setWorkphaseInfo(tableViewTermOrganisation.getSelectionModel().getSelectedItem().getWorkphaseInfo());
//        ObservableList<CalendarExtraInfo> terms = FXCollections.observableArrayList();
//        terms.add(calenderobject);
//        tableViewTermOrganisation.setItems(terms);
//        tableViewTermOrganisation.getItems();
    }

    //TODO: nur eine Zelle soll geändert werden, nicht direkt alle
    @FXML
    //Edit table cells containing Calendar Entrys
    public void editTableCell(CellEditEvent<CalendarExtraInfo, CalendarEntry> calendarExtraInfoCalendarEntryCellEditEvent) {
        calendarExtraInfoCalendarEntryCellEditEvent.getTableView().getItems().get(calendarExtraInfoCalendarEntryCellEditEvent.getTablePosition().getRow()).setCalendarEntry(calendarExtraInfoCalendarEntryCellEditEvent.getNewValue());
    }
    @FXML
    //Edit table cells containing integer values
    public void editTableCellInteger(CellEditEvent<CalendarExtraInfo, Integer> calendarExtraInfoIntegerCellEditEvent) {

        calendarExtraInfoIntegerCellEditEvent.getTableView().getItems().get(calendarExtraInfoIntegerCellEditEvent.getTablePosition().getRow()).setCalendarWeek(calendarExtraInfoIntegerCellEditEvent.getNewValue());
        calendarExtraInfoIntegerCellEditEvent.getTableView().getItems().get(calendarExtraInfoIntegerCellEditEvent.getTablePosition().getRow()).setMeetingNo(calendarExtraInfoIntegerCellEditEvent.getNewValue());
    }

    @FXML
    //edit table cells containing string values
    public void editTableCellString(CellEditEvent<CalendarExtraInfo, String> calendarExtraInfoStringCellEditEvent) {

        calendarExtraInfoStringCellEditEvent.getTableView().getItems().get(calendarExtraInfoStringCellEditEvent.getTablePosition().getRow()).setIterationInfo(calendarExtraInfoStringCellEditEvent.getNewValue());
        calendarExtraInfoStringCellEditEvent.getTableView().getItems().get(calendarExtraInfoStringCellEditEvent.getTablePosition().getRow()).setLectureInfo(calendarExtraInfoStringCellEditEvent.getNewValue());
        calendarExtraInfoStringCellEditEvent.getTableView().getItems().get(calendarExtraInfoStringCellEditEvent.getTablePosition().getRow()).setWorkphaseInfo(calendarExtraInfoStringCellEditEvent.getNewValue());
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
}

