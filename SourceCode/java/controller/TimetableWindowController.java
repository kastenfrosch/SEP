package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import modal.ConfirmationModal;
import modal.ErrorModal;
import modal.InfoModal;
import models.*;
import utils.scene.SceneManager;
import utils.scene.SceneType;


import java.sql.Array;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Iterator;

public class TimetableWindowController {


    public ComboBox <Calendar>cbg;
    private ArrayList<Label> mon = new ArrayList<>();
    private ArrayList<Label> di = new ArrayList<>();
    private ArrayList<Label> mi = new ArrayList<>();
    private ArrayList<Label> don = new ArrayList<>();
    private ArrayList<Label> f = new ArrayList<>();



    @FXML
    public GridPane gridPane;
    public Label m1;
    public Label m2;
    public Label m3;
    public Label m4;
    public Label m5;
    public Label m6;

    public Label di1;
    public Label di2;
    public Label di3;
    public Label di4;
    public Label di5;
    public Label di6;

    public Label mi1;
    public Label mi2;
    public Label mi3;
    public Label mi4;
    public Label mi5;
    public Label mi6;

    public Label do1;
    public Label do2;
    public Label do3;
    public Label do4;
    public Label do5;
    public Label do6;

    public Label f1;
    public Label f2;
    public Label f3;
    public Label f4;
    public Label f5;
    public Label f6;

    public Calendar calendar;
    public Semester semester;
    public Groupage groupage;

    private DBManager db;
    {
        try {
            db = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Load Timetable
    public void LoadTimetable(){

        if (cbg.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("ACHTUNG!", null, "Kein Stundenplan Ausgewählt!");
            return;
        }

        Calendar calendar;

        Dao<Calendar,Integer>calendarDao  = db.getCalendarDao();

        try {
            calendar = calendarDao.queryForId(cbg.getSelectionModel().getSelectedItem().getCalendarId());
            this.calendar = calendar;
        } catch (SQLException e) {
            e.printStackTrace();
        }

//        getMonday(mon,this.calendar);
//        getTuesday(di,this.calendar);
//        getWednesday(mi,this.calendar);
//        getThursday(don,this.calendar);
//        getFriday(f,this.calendar);

        getWeekPlan(mon,this.calendar,DayOfWeek.MONDAY);
        getWeekPlan(di,this.calendar,DayOfWeek.TUESDAY);
        getWeekPlan(mi,this.calendar,DayOfWeek.WEDNESDAY);
        getWeekPlan(don,this.calendar,DayOfWeek.THURSDAY);
        getWeekPlan(f,this.calendar,DayOfWeek.FRIDAY);

    }

    //
    public void initialize() {
        append();
        loadboxes();

    }

    //Load availbe timetable
    public void loadboxes(){
        try {

            Calendar cal = new Calendar();
            cal.setCalendarType(Calendar.CalendarType.WEEK);
            ObservableList<Calendar> CalendarList = FXCollections.observableArrayList();
            Dao<Calendar, Integer> CalendarDao = db.getCalendarDao();

            CalendarList.addAll(CalendarDao.queryForMatching(cal));
            cbg.setItems(CalendarList);

        } catch (java.sql.SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }
    }

    public void getWeekPlan(ArrayList<Label>array,Calendar calendar,DayOfWeek dayOfWeek){
        Iterator<Label> i = array.iterator();
        int currenttextfield = 0;
        for(int hour = 8; i.hasNext(); hour += 2){

            CalendarEntry mon = new CalendarEntry();
            mon.setDayOfWeek(dayOfWeek);
            mon.setStartTime(hour);
            mon.setCalendar(calendar);


            Dao<CalendarEntry, Integer> CalendarDaoEntry = db.getCalendarEntryDao();
            try {

                CalendarEntry e = CalendarDaoEntry.queryForMatching(mon).get(0);
                array.get(currenttextfield).setText(e.getDescription());
                //  }

                System.out.print(dayOfWeek+" "+hour+"   ");

            } catch (SQLException e) {
                e.printStackTrace();

            }
            currenttextfield++;
            Label l = i.next();
        }
    }

    //Delete single Timetable


    public void OpenCreate(){
        SceneManager.getInstance()
                .getLoaderForScene(SceneType.CREATE_TIMETABLE)
                .<CreateTimetableController>getController().initialize();
        SceneManager.getInstance().showInNewWindow(SceneType.CREATE_TIMETABLE);
    }

    //Append texfields to ArrayList<Textfield>
    public void append(){
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
        System.out.print("--------------------------------------------------");
        System.out.println("GRÖ?E  "+mon.size()+di.size()+mi.size()+don.size()+f.size());
        System.out.print("--------------------------------------------------");
    }

    //ReloadCombobox
    public void Reload(){
        loadboxes();
    }
}