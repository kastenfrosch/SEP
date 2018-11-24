package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import models.Calendar;
import models.CalendarEntry;
import models.User;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Iterator;

public class TimetableWindowController {


    ArrayList<Label> mon = new ArrayList<>();
    ArrayList<Label> di = new ArrayList<>();
    ArrayList<Label> mi = new ArrayList<>();
    ArrayList<Label> don = new ArrayList<>();
    ArrayList<Label> f = new ArrayList<>();
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




    private DBManager db;

    {
        try {
            db = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
//	
//	
	@FXML
	public void initialize() {
        Calendar calendar = new Calendar();
        Dao<User, String> user = db.getUserDao();
        calendar.setCalendarType(Calendar.CalendarType.WEEK);
        try {

            User tester = user.queryForId("besttutor");
            calendar.setUser(tester);

        } catch (
                SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        CalendarEntry entry  = new CalendarEntry();
        entry.setStartTime(10);
        entry.setDayOfWeek(DayOfWeek.MONDAY);

        Dao<CalendarEntry, Integer> CalendarDaoEntry = db.getCalendarEntryDao();
        try {

            //append();
            CalendarEntry e =CalendarDaoEntry.queryForMatching(entry).get(0);
           // mon.get(5).setText(e.getDescription());


        } catch (SQLException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
    }

    append();
        getMonday(mon);
        getTuesday(di);
        getWednesday(mi);
        getThursday(don);
        getFriday(f);


	}

    public void getMonday(ArrayList<Label> array){

        Iterator<Label> i = array.iterator();
        int currentmonday = 0;
        for (int hour = 8; i.hasNext(); hour += 2) {
            CalendarEntry mon = new CalendarEntry();
            mon.setDayOfWeek(DayOfWeek.MONDAY);
            mon.setStartTime(hour);

            Dao<CalendarEntry, Integer> CalendarDaoEntry = db.getCalendarEntryDao();
            try {

                CalendarEntry e =CalendarDaoEntry.queryForMatching(mon).get(0);
                array.get(currentmonday).setText(e.getDescription());


                System.out.print("MONDAY"+hour);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                System.out.println("fuckml");
                e.printStackTrace();

            }
            currentmonday++;
            Label l = i.next();
        }


    }
    public void getTuesday(ArrayList<Label> array){

        Iterator<Label> i = array.iterator();
        int currentTuesday = 0;
        for (int hour = 8; i.hasNext(); hour += 2) {
            CalendarEntry tues = new CalendarEntry();
            tues.setDayOfWeek(DayOfWeek.TUESDAY);
            tues.setStartTime(hour);

            Dao<CalendarEntry, Integer> CalendarDaoEntry = db.getCalendarEntryDao();
            try {

                CalendarEntry e =CalendarDaoEntry.queryForMatching(tues).get(0);
                array.get(currentTuesday).setText(e.getDescription());


                System.out.print("Tuesday"+hour);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                System.out.println("fuckml");
                e.printStackTrace();

            }
            currentTuesday++;
            Label l = i.next();
        }


    }
    public void getWednesday(ArrayList<Label> array){

        Iterator<Label> i = array.iterator();
        int currentwednesday = 0;
        for (int hour = 8; i.hasNext(); hour += 2) {
            CalendarEntry wed = new CalendarEntry();
            wed.setDayOfWeek(DayOfWeek.WEDNESDAY);
            wed.setStartTime(hour);

            Dao<CalendarEntry, Integer> CalendarDaoEntry = db.getCalendarEntryDao();
            try {

                CalendarEntry e =CalendarDaoEntry.queryForMatching(wed).get(0);
                array.get(currentwednesday).setText(e.getDescription());


                System.out.print("Wednesday"+hour);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                System.out.println("fuckml");
                e.printStackTrace();

            }
            currentwednesday++;
            Label l = i.next();
        }


    }
    public void getThursday(ArrayList<Label> array){

        Iterator<Label> i = array.iterator();
        int currentThursday = 0;
        for (int hour = 8; i.hasNext(); hour += 2) {
            CalendarEntry don = new CalendarEntry();
            don.setDayOfWeek(DayOfWeek.MONDAY);
            don.setStartTime(hour);

            Dao<CalendarEntry, Integer> CalendarDaoEntry = db.getCalendarEntryDao();
            try {

                CalendarEntry e =CalendarDaoEntry.queryForMatching(don).get(0);
                array.get(currentThursday).setText(e.getDescription());


                System.out.print("Thursday"+hour);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                System.out.println("fuckml");
                e.printStackTrace();

            }
            currentThursday++;
            Label l = i.next();
        }


    }
    public void getFriday(ArrayList<Label> array){

        Iterator<Label> i = array.iterator();
        int currentFriday = 0;
        for (int hour = 8; i.hasNext(); hour += 2) {
            CalendarEntry f = new CalendarEntry();
            f.setDayOfWeek(DayOfWeek.FRIDAY);
            f.setStartTime(hour);

            Dao<CalendarEntry, Integer> CalendarDaoEntry = db.getCalendarEntryDao();
            try {

                CalendarEntry e =CalendarDaoEntry.queryForMatching(f).get(0);
                array.get(currentFriday).setText(e.getDescription());


                System.out.print("Friday"+hour);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                System.out.println("fuckml");
                e.printStackTrace();

            }
            currentFriday++;
            Label l = i.next();
        }


    }

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

    }
}
