package controller.form.calendar;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import modal.ErrorModal;
import modal.InfoModal;
import models.*;


import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Iterator;


public class CreateTimetableController {

    public ComboBox<Semester> cbs;
    public ComboBox<Groupage> cbk;
    public ComboBox<Calendar> cbg;

    private ArrayList<TextField> mon = new ArrayList<>();
    private ArrayList<TextField> di = new ArrayList<>();
    private ArrayList<TextField> mi = new ArrayList<>();
    private ArrayList<TextField> don = new ArrayList<>();
    private ArrayList<TextField> f = new ArrayList<>();


    public TextField m1;
    public TextField m2;
    public TextField m3;
    public TextField m4;
    public TextField m5;
    public TextField m6;

    public TextField di1;
    public TextField di2;
    public TextField di3;
    public TextField di4;
    public TextField di5;
    public TextField di6;

    public TextField mi1;
    public TextField mi2;
    public TextField mi3;
    public TextField mi4;
    public TextField mi5;
    public TextField mi6;

    public TextField do1;
    public TextField do2;
    public TextField do3;
    public TextField do4;
    public TextField do5;
    public TextField do6;

    public TextField f1;
    public TextField f2;
    public TextField f3;
    public TextField f4;
    public TextField f5;
    public TextField f6;
    public Calendar calendar;
    public Semester semester;
    private Groupage groupage;
    private int append = 0;


    private DBManager db;

    {
        try {
            db = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cbsOnAction(){
        cbk.getItems().clear();

        if (cbs.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("ACHTUNG!", null, "Kein Stundenplan Ausgewählt!");
            return;
        }
        Groupage group = new Groupage();
        group.setSemester(cbs.getSelectionModel().getSelectedItem());
        try {
            // creat an observableList with all groups
            ObservableList<Groupage> GroupageList = FXCollections.observableArrayList();
            Dao<Groupage, Integer> GroupageDao = db.getGroupageDao();
            GroupageList.addAll(GroupageDao.queryForMatching(group));
            cbk.setItems(GroupageList);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void appendTextfields() {
        mon.add(m1);
        mon.add(m2);
        mon.add(m3);
        mon.add(m4);
        mon.add(m5);
        mon.add(m6);
        di.add(di1);
        di.add(di2);
        di.add(di3);
        di.add(di4);
        di.add(di5);
        di.add(di6);
        mi.add(mi1);
        mi.add(mi2);
        mi.add(mi3);
        mi.add(mi4);
        mi.add(mi5);
        mi.add(mi6);
        don.add(do1);
        don.add(do2);
        don.add(do3);
        don.add(do4);
        don.add(do5);
        don.add(do6);
        f.add(f1);
        f.add(f2);
        f.add(f3);
        f.add(f4);
        f.add(f5);
        f.add(f6);
    }

    //Delete TWC is using this method

    //Append TextFields
    public void initialize() {
        //Append wird beim starten des Programms ausgefüht und für zu dem Fehler dass es dann 60 statt 30 elemnte sind
        if (this.append == 0) {
            appendTextfields();

        }
        this.append = 1;


        loadboxes();
    }

    //Load avaible timetable
    public void loadboxes() {

        cbs.getItems().clear();
        cbg.getItems().clear();

        try {
            // creat an observableList with all groups
            ObservableList<Semester> SemesterList = FXCollections.observableArrayList();
            Dao<Semester, String> SemesterDao = db.getSemesterDao();
            SemesterList.addAll(SemesterDao.queryForAll());
            //set semester combobox with the semester from the observableList
            cbs.setItems(SemesterList);

        } catch (java.sql.SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }

        try {
            Calendar cal = new Calendar();
            ObservableList<Calendar> CalendarList = FXCollections.observableArrayList();
            Dao<Calendar, Integer> CalendarDao = db.getCalendarDao();
            cal.setCalendarType(Calendar.CalendarType.WEEK);
            CalendarList.addAll(CalendarDao.queryForEq(Calendar.FIELD_CALENDAR_TYPE, Calendar.CalendarType.WEEK));
            cbg.setItems(CalendarList);

        } catch (java.sql.SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }

    }
    // Get the Groupages for the selected semester

    // Load Timetable into the textfields
    public void LoadTimetable() {


        if (cbg.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("ACHTUNG!", null, "Kein Stundenplan Ausgewählt!");
            return;
        }

        Calendar calendar;

        Dao<Calendar, Integer> calendarDao = db.getCalendarDao();

        try {
            calendar = calendarDao.queryForId(cbg.getSelectionModel().getSelectedItem().getCalendarId());
            this.calendar = calendar;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getWeekPlan(mon, this.calendar, DayOfWeek.MONDAY);
        getWeekPlan(di, this.calendar, DayOfWeek.TUESDAY);
        getWeekPlan(mi, this.calendar, DayOfWeek.WEDNESDAY);
        getWeekPlan(don, this.calendar, DayOfWeek.THURSDAY);
        getWeekPlan(f, this.calendar, DayOfWeek.FRIDAY);
        System.out.print("");


    }


    public void DeleteWeekPlan(){

        if (cbg.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("ACHTUNG!", null, "Kein Stundenplan Ausgewählt!");
            return;
        }

        CalendarEntry calendarEntry = new CalendarEntry();
        Calendar calendar = new Calendar();
        Dao<Calendar, Integer> CalendarDao = db.getCalendarDao();
        Dao<CalendarEntry, Integer> CalendarEntryDao = db.getCalendarEntryDao();
        System.out.print("");
        try {
            Calendar cal = CalendarDao.queryForId(cbg.getSelectionModel().getSelectedItem().getCalendarId());
            CalendarEntryDao.delete(cal.getCalendarEntries());
            CalendarDao.delete(cal);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.print("");
        }

    }

    //Delete ALL Calendar and Calendar Entries (ONLY FOR TEST)
    public void deleteEntries() {
        //Löschen Aller Calendar für Testzwcke
        /////////////////////////////////////////////////////////////////////////7
        CalendarEntry entry = new CalendarEntry();
        Dao<CalendarEntry, Integer> CalendarEntryDao = db.getCalendarEntryDao();

        try {
            CalendarEntryDao.delete(CalendarEntryDao.queryForAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }


        Calendar calendar = new Calendar();
        Dao<Calendar, Integer> CalendarDao = db.getCalendarDao();

        try {
            CalendarDao.delete(CalendarDao.queryForAll());
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

    }

    //Create
    public void CreateTimetable() {
        Calendar calendar = new Calendar();
        Semester semester;
        Groupage groupage;

        if (cbs.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("ACHTUNG!", null, "Kein Semester ausgewählt!");
            return;
        }

        if (cbk.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("ACHTUNG!", null, "Keine Klasse Ausgewählt!");
            return;
        }

          Dao<Semester, String> SemesterDao = db.getSemesterDao();
        try {
            semester = SemesterDao.queryForId(cbs.getSelectionModel().getSelectedItem().toString());
            calendar.setSemester(semester);
            calendar.setCalendarType(Calendar.CalendarType.WEEK);
            System.out.print(semester.getDescription());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Dao<Groupage, Integer> groupageDao = db.getGroupageDao();

        try {
            groupage = groupageDao.queryForId(cbk.getSelectionModel().getSelectedItem().getId());
            calendar.setGroupage(groupage);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Dao<Calendar, Integer> CalendarDao = db.getCalendarDao();
        try {


            CalendarDao.createIfNotExists(calendar);


            //CalendarDao.create(calendar);

            if (calendar.getCalendarId() != 0) {
                InfoModal.show("ACHTUNG!", null, "StundenPlan kontne  ertstellt werden!");

            }


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            InfoModal.show("ACHTUNG!", null, "StundenPlan kontne nicht ertstellt werden es existierbereits!");
            e.printStackTrace();
            return;
        }
        setWeekPlan(mon, calendar, DayOfWeek.MONDAY);
        setWeekPlan(di, calendar, DayOfWeek.TUESDAY);
        setWeekPlan(mi, calendar, DayOfWeek.WEDNESDAY);
        setWeekPlan(don, calendar, DayOfWeek.THURSDAY);
        setWeekPlan(f, calendar, DayOfWeek.FRIDAY);


        cbk.getItems().clear();
        cbs.getItems().clear();
        loadboxes();
    }

    //Save Timetable
    public void UpdateTimetable() {
        UpdateWeekPlan(mon, this.calendar, DayOfWeek.MONDAY);
        UpdateWeekPlan(di, this.calendar, DayOfWeek.TUESDAY);
        UpdateWeekPlan(mi, this.calendar, DayOfWeek.WEDNESDAY);
        UpdateWeekPlan(don, this.calendar, DayOfWeek.THURSDAY);
        UpdateWeekPlan(f, this.calendar, DayOfWeek.FRIDAY);
    }

    public void setWeekPlan(ArrayList<TextField> array, Calendar calendar, DayOfWeek dayOfWeek) {

        int hour = 8;
        for (TextField textField : array) {
            CalendarEntry mon = new CalendarEntry();
            mon.setDayOfWeek(dayOfWeek);
            mon.setStartTime(hour);
            mon.setCalendar(calendar);
            mon.setDescription(textField.getText());
            Dao<CalendarEntry, Integer> CalendarDaoEntry = db.getCalendarEntryDao();
            try {
                CalendarDaoEntry.create(mon);
            } catch (SQLException e) {
                e.printStackTrace();

            }
            hour = hour + 2;
        }

    }

    //Load WeekPlan into Textfields.
    public void getWeekPlan(ArrayList<TextField> array, Calendar calendar, DayOfWeek dayOfWeek) {

        int hour = 8;
        for (TextField textField : array) {
            CalendarEntry mon = new CalendarEntry();
            mon.setDayOfWeek(dayOfWeek);
            mon.setStartTime(hour);
            mon.setCalendar(calendar);
            Dao<CalendarEntry, Integer> CalendarDaoEntry = db.getCalendarEntryDao();
            try {
                CalendarEntry e = CalendarDaoEntry.queryForMatching(mon).get(0);
                textField.setText(e.getDescription());
            } catch (SQLException e) {
                e.printStackTrace();

            }
            hour = hour + 2;
        }

    }

    //Update WeekPlan with the changes.
    public void UpdateWeekPlan(ArrayList<TextField> array, Calendar calendar, DayOfWeek dayOfWeek) {


        int hour = 8;
        for (TextField textField : array) {
            CalendarEntry monday = new CalendarEntry();
            monday.setDayOfWeek(dayOfWeek);
            monday.setCalendar(calendar);
            monday.setStartTime(hour);
            Dao<CalendarEntry, Integer> CalendarDaoEntry = db.getCalendarEntryDao();
            try {
                CalendarEntry e = CalendarDaoEntry.queryForMatching(monday).get(0);
                e.setDescription(textField.getText());
                CalendarDaoEntry.update(e);

            } catch (SQLException e) {
                e.printStackTrace();

            }
            hour = hour + 2;
        }


    }

    //Clean Textfields
    public void Reload() {
        loadboxes();
        CleanTextfields(mon);
        CleanTextfields(di);
        CleanTextfields(mi);
        CleanTextfields(don);
        CleanTextfields(f);
    }

    //Sub class
    public void CleanTextfields(ArrayList<TextField> array) {

        for (TextField t : array) {
            t.setText("");
        }
        for (TextField t : array) {
            t.setText("");
        }
        for (TextField t : array) {
            t.setText("");
        }
        for (TextField t : array) {
            t.setText("");
        }
        for (TextField t : array) {
            t.setText("");
        }
    }
}


