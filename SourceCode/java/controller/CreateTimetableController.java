package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import modal.ErrorModal;
import modal.InfoModal;
import models.*;
import org.w3c.dom.Text;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Iterator;


public class CreateTimetableController {

    public ComboBox<Semester> cbs;
    public ComboBox <Groupage>cbk;
    public Button loadtimetable;
    public ComboBox <Calendar>cbg;

    ArrayList<TextField> mon = new ArrayList<TextField>();
    ArrayList<TextField> di = new ArrayList<TextField>();
    ArrayList<TextField> mi = new ArrayList<TextField>();
    ArrayList<TextField> don = new ArrayList<TextField>();
    ArrayList<TextField> f = new ArrayList<TextField>();

    ArrayList<Label> monl = new ArrayList<Label>();
    ArrayList<Label> dil = new ArrayList<Label>();
    ArrayList<Label> mil = new ArrayList<Label>();
    ArrayList<Label> donl = new ArrayList<Label>();
    ArrayList<Label> fl = new ArrayList<Label>();

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
    public int retryCreate =0;
    public Semester semester;
    private Groupage groupage;


    private DBManager db;

    {
        try {
            db = DBManager.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Delete TWC is using this method
    public void CalendarComboBox(ComboBox<Calendar> calendarComboBox){
        CalendarEntry calendarEntry = new CalendarEntry();
        Calendar calendar = new Calendar();
        Dao<Calendar, Integer> CalendarDao = db.getCalendarDao();
        Dao<CalendarEntry, Integer> CalendarEntryDao = db.getCalendarEntryDao();


        try {
            Calendar cal =CalendarDao.queryForId(calendarComboBox.getSelectionModel().getSelectedItem().getCalendarId());
            CalendarEntryDao.delete(cal.getCalendarEntries());
            CalendarDao.delete(cal);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //Append TextFields
    public void initialize() {
        Semester semester;

        appendTextfields();

                loadboxes();
    }

    public void loadboxes(){
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


            ObservableList<Calendar> CalendarList = FXCollections.observableArrayList();
            Dao<Calendar, Integer> CalendarDao = db.getCalendarDao();
            CalendarList.addAll(CalendarDao.queryForAll());
            cbg.setItems(CalendarList);

        } catch (java.sql.SQLException e) {
            ErrorModal.show(e.getMessage());
            e.printStackTrace();
        }
    }

    public void getGroupageForSemester(){

        Groupage group = new Groupage();
        group.setSemester(cbs.getSelectionModel().getSelectedItem());
try{
        // creat an observableList with all groups
        ObservableList<Groupage> GroupageList = FXCollections.observableArrayList();
        Dao<Groupage, Integer> GroupageDao = db.getGroupageDao();
        GroupageList.addAll(GroupageDao.queryForMatching(group));
        cbk.setItems(GroupageList);

} catch (SQLException e) {
            e.printStackTrace();
        }

        //set semester combobox with the semester from the observableList


    }

    public void LoadTimetable(){

        Calendar calendar;

        Dao<Calendar,Integer>calendarDao  = db.getCalendarDao();

        try {
            calendar = calendarDao.queryForId(cbg.getSelectionModel().getSelectedItem().getCalendarId());
            this.calendar = calendar;
        } catch (SQLException e) {
            e.printStackTrace();
        }


        getMonday(mon,this.calendar);
        getTuesday(di,this.calendar);
        getWednesday(mi,this.calendar);
        getThursday(don,this.calendar);
        getFriday(f,this.calendar);

    }

    public void deleteEntries(){
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
//////////////////////////////////////////////////////////////////////////////////////////////


//löschen eines einzigen calendars
        ////////////////////////////////////////////////////
//        CalendarEntry calendarEntry2 = new CalendarEntry();
//        Calendar calendar2 = new Calendar();
//        Dao<Calendar, Integer> CalendarDao2 = db.getCalendarDao();
//        Dao<CalendarEntry, Integer> CalendarEntryDao2 = db.getCalendarEntryDao();
//
//
//        try {
//            Calendar cal =CalendarDao2.queryForId(9);   // hier muss später dass von der comboBox ausgewählt hin (CalendarName)
//            calendarEntry2.setCalendar(cal);
//            CalendarEntryDao.delete(calendarEntry2);
//            CalendarDao2.delete(cal);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
////////////////////////////////////////////////////

    }

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

                //IS CALENDAR ALREADY CREATED??





            //Use  Groupage ans Semester id's from the Combo Boxes and set the Informations to this.calendar

            Dao<Semester, String> SemesterDao = db.getSemesterDao();
            try {
                semester = SemesterDao.queryForId(cbs.getSelectionModel().getSelectedItem().toString());
                calendar.setSemester(semester);
                calendar.setCalendarType(Calendar.CalendarType.WEEK);
                System.out.print(semester.getDescription());

                // System.out.print(semester.getDescription());
            } catch (SQLException e) {
                e.printStackTrace();
            }


           Dao<Groupage, Integer> groupageDao = db.getGroupageDao();

            try {
                groupage = groupageDao.queryForId(cbk.getSelectionModel().getSelectedItem().getId());
                calendar.setGroupage(groupage);
             System.out.print(groupage.getDescription());
            } catch (SQLException e) {
                e.printStackTrace();
            }

        Dao<Calendar, Integer> CalendarDao = db.getCalendarDao();
        try {


            CalendarDao.createIfNotExists(calendar);



                //CalendarDao.create(calendar);

                if(calendar.getCalendarId()!=0){
                    InfoModal.show("ACHTUNG!", null, "StundenPlan kontne  ertstellt werden!");

                }


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            InfoModal.show("ACHTUNG!", null, "StundenPlan kontne nicht ertstellt werden es existierbereits!");
           e.printStackTrace();
           return;
        }

       // String Calendarname = calendar.getSemester().getDescription()+calendar.getGroupage().getDescription();

        setMonday(mon, calendar);
        setTuesday(di, calendar);
        setWednesday(mi, calendar);
        setThursday(don, calendar);
        setFriday(f, calendar);

    }

    public void UpdateTimetable(){

        UpdateMonday(mon,this.calendar);
        UpdateTuesday(mon,this.calendar);
        UpdateWednesday(mon,this.calendar);
        UpdateThursday(mon,this.calendar);
        UpdateFriday(mon,this.calendar);

    }


// Create new Timtable
    public void setMonday(ArrayList<TextField> array, Calendar calendar) {



        Iterator<TextField> i = array.iterator();
        for (int hour = 8; i.hasNext(); hour += 2) {
            TextField t = i.next();
            CalendarEntry monday = new CalendarEntry();
            monday.setDayOfWeek(DayOfWeek.MONDAY);
            monday.setCalendar(calendar);
            monday.setDescription(t.getText());
            monday.setStartTime(hour);
            Dao<CalendarEntry, Integer> CalendarDaoEntry = db.getCalendarEntryDao();
            try {


                CalendarDaoEntry.create(monday);
                System.out.print("MONDAY" + hour);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                System.out.println("fuckml");
                e.printStackTrace();

            }
        }


    }
    public void setTuesday(ArrayList<TextField> array, Calendar calendar) {

        Iterator<TextField> i = array.iterator();
        for (int hour = 8; i.hasNext(); hour += 2) {
            TextField t = i.next();
            CalendarEntry tuesday = new CalendarEntry();
            tuesday.setDayOfWeek(DayOfWeek.TUESDAY);

            tuesday.setCalendar(calendar);
            tuesday.setDescription(t.getText());
            tuesday.setStartTime(hour);
            Dao<CalendarEntry, Integer> CalendarDaoEntry = db.getCalendarEntryDao();
            try {

                CalendarDaoEntry.create(tuesday);
                System.out.print("TUESDAY"+hour);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                System.out.println("fuckml");
                e.printStackTrace();

            }
        }
    }
    public void setWednesday(ArrayList<TextField> array, Calendar calendar) {

        Iterator<TextField> i = array.iterator();
        for (int hour = 8; i.hasNext(); hour += 2) {
            TextField t = i.next();
            CalendarEntry wednesday = new CalendarEntry();
            wednesday.setDayOfWeek(DayOfWeek.WEDNESDAY);
            wednesday.setCalendar(calendar);
            wednesday.setDescription(t.getText());
            wednesday.setStartTime(hour);
            Dao<CalendarEntry, Integer> CalendarDaoEntry = db.getCalendarEntryDao();
            try {

                CalendarDaoEntry.create(wednesday);
                System.out.print("WEDNESDAY"+hour);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                System.out.println("fuckml");
                e.printStackTrace();

            }
        }
    }
    public void setThursday(ArrayList<TextField> array, Calendar calendar) {

        Iterator<TextField> i = array.iterator();
        for (int hour = 8; i.hasNext(); hour += 2) {
            TextField t = i.next();
            CalendarEntry thursday = new CalendarEntry();
            thursday.setDayOfWeek(DayOfWeek.THURSDAY);
            thursday.setCalendar(calendar);
            thursday.setDescription(t.getText());
            thursday.setStartTime(hour);
            Dao<CalendarEntry, Integer> CalendarDaoEntry = db.getCalendarEntryDao();
            try {

                CalendarDaoEntry.create(thursday);
                System.out.print("THURSDAY"+hour);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                System.out.println("fuckml");
                e.printStackTrace();

            }

        }


    }
    public void setFriday(ArrayList<TextField> array, Calendar calendar) {

        Iterator<TextField> i = array.iterator();
        for (int hour = 8; i.hasNext(); hour += 2) {
            TextField t = i.next();
            CalendarEntry friday = new CalendarEntry();
            friday.setDayOfWeek(DayOfWeek.FRIDAY);
            friday.setCalendar(calendar);
            friday.setDescription(t.getText());
            friday.setStartTime(hour);
            Dao<CalendarEntry, Integer> CalendarDaoEntry = db.getCalendarEntryDao();
            try {

                CalendarDaoEntry.create(friday);
                System.out.print("FRIDAY"+hour);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                System.out.println("fuckml");
                e.printStackTrace();

            }
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
//Get Timetable infos
    public void getMonday(ArrayList<TextField> array,Calendar calendar){

        Iterator<TextField> i = array.iterator();
        int currentmonday = 0;
        for (int hour = 8; i.hasNext(); hour += 2) {
            CalendarEntry mon = new CalendarEntry();
            mon.setDayOfWeek(DayOfWeek.MONDAY);
            mon.setStartTime(hour);
            mon.setCalendar(calendar);


            Dao<CalendarEntry, Integer> CalendarDaoEntry = db.getCalendarEntryDao();
            try {

                CalendarEntry e = CalendarDaoEntry.queryForMatching(mon).get(0);
                array.get(currentmonday).setText(e.getDescription());

                System.out.print("MONDAY"+hour);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                System.out.println("fuckml");
                e.printStackTrace();

            }
            currentmonday++;
            TextField l = i.next();
        }


    }
    public void getTuesday(ArrayList<TextField> array,Calendar calendar){

        Iterator<TextField> i = array.iterator();
        int currentTuesday = 0;
        for (int hour = 8; i.hasNext(); hour += 2) {
            CalendarEntry tues = new CalendarEntry();
            tues.setDayOfWeek(DayOfWeek.TUESDAY);
            tues.setStartTime(hour);
            tues.setCalendar(calendar);

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
            TextField l = i.next();
        }

        currentTuesday=0;
    }
    public void getWednesday(ArrayList<TextField> array,Calendar calendar){

        Iterator<TextField> i = array.iterator();
        int currentwednesday = 0;
        for (int hour = 8; i.hasNext(); hour += 2) {
            CalendarEntry wed = new CalendarEntry();
            wed.setDayOfWeek(DayOfWeek.WEDNESDAY);
            wed.setStartTime(hour);
            wed.setCalendar(calendar);


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
            TextField l = i.next();
        }

        currentwednesday=0;
    }
    public void getThursday(ArrayList<TextField> array,Calendar calendar){

        Iterator<TextField> i = array.iterator();
        int currentThursday = 0;
        for (int hour = 8; i.hasNext(); hour += 2) {
            CalendarEntry don = new CalendarEntry();
            don.setDayOfWeek(DayOfWeek.THURSDAY);
            don.setStartTime(hour);
            don.setCalendar(calendar);

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
            TextField l = i.next();
        }

        currentThursday=0;
    }
    public void getFriday(ArrayList<TextField> array,Calendar calendar){

        Iterator<TextField> i = array.iterator();
        int currentFriday = 0;
        for (int hour = 8; i.hasNext(); hour += 2) {
            CalendarEntry f = new CalendarEntry();
            f.setDayOfWeek(DayOfWeek.FRIDAY);
            f.setStartTime(hour);
            f.setCalendar(calendar);

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
            TextField l = i.next();
        }

        currentFriday=0;

    }
//Update Timetable with the infos of getDAY
   public void UpdateMonday(ArrayList<TextField>array,Calendar calendar){

        Iterator<TextField> i = array.iterator();

       int currentmonday =0;
       int it = 0;
       int zeit=8;
       for(TextField textField:array){
           System.out.println("----------------------------------------------------------------------------");
           System.out.println(array.get(it).toString());
           System.out.println("----------------------------------------------------------------------------");
           CalendarEntry monday = new CalendarEntry();
           monday.setDayOfWeek(DayOfWeek.MONDAY);
           monday.setCalendar(calendar);
           monday.setStartTime(zeit);
           Dao<CalendarEntry, Integer> CalendarDaoEntry = db.getCalendarEntryDao();
           try {

               CalendarEntry e = CalendarDaoEntry.queryForMatching(monday).get(0);
               e.setDescription(textField.getText());
               CalendarDaoEntry.update(e);
               System.out.print("MONDAY" + zeit);

           } catch (SQLException e) {
               // TODO Auto-generated catch block
               System.out.println("fuckml");
               e.printStackTrace();

           }
           it++;
           zeit = zeit+2;
       }






   }
    public void UpdateTuesday(ArrayList<TextField>array,Calendar calendar){

        int hour=8;
        for(TextField textField : array){
            CalendarEntry tuesday = new CalendarEntry();
            tuesday.setDayOfWeek(DayOfWeek.TUESDAY);
            tuesday.setCalendar(calendar);
            tuesday.setStartTime(hour);
            Dao<CalendarEntry, Integer> CalendarDaoEntry = db.getCalendarEntryDao();
            try {

                CalendarEntry e = CalendarDaoEntry.queryForMatching(tuesday).get(0);
                e.setDescription(textField.getText());
                CalendarDaoEntry.update(e);
                System.out.print("TUESDAY" + hour);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                System.out.println("fuckml");
                e.printStackTrace();

            }
            hour = hour+2;
        }

    }
    public void UpdateWednesday(ArrayList<TextField>array,Calendar calendar){


        int hour=8;
        for(TextField textField : array){
            CalendarEntry wednesday = new CalendarEntry();
            wednesday.setDayOfWeek(DayOfWeek.WEDNESDAY);
            wednesday.setCalendar(calendar);
            wednesday.setStartTime(hour);
            Dao<CalendarEntry, Integer> CalendarDaoEntry = db.getCalendarEntryDao();
            try {

                CalendarEntry e = CalendarDaoEntry.queryForMatching(wednesday).get(0);
                e.setDescription(textField.getText());
                CalendarDaoEntry.update(e);
                System.out.print("WEDNESDAY" + hour);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                System.out.println("fuckml");
                e.printStackTrace();

            }
            hour = hour+2;
        }
    }
    public void UpdateThursday(ArrayList<TextField>array,Calendar calendar){


        int hour=8;
        for(TextField textField : array){
            CalendarEntry thursday = new CalendarEntry();
            thursday.setDayOfWeek(DayOfWeek.THURSDAY);
            thursday.setCalendar(calendar);
            thursday.setStartTime(hour);
            Dao<CalendarEntry, Integer> CalendarDaoEntry = db.getCalendarEntryDao();
            try {

                CalendarEntry e = CalendarDaoEntry.queryForMatching(thursday).get(0);
                e.setDescription(textField.getText());
                CalendarDaoEntry.update(e);
                System.out.print("THURSDAY" + hour);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                System.out.println("fuckml");
                e.printStackTrace();

            }
            hour = hour+2;
        }

    }
    public void UpdateFriday(ArrayList<TextField>array,Calendar calendar){


        int hour=8;
        for(TextField textField : array){
            CalendarEntry friday = new CalendarEntry();
            friday.setDayOfWeek(DayOfWeek.FRIDAY);
            friday.setCalendar(calendar);
            friday.setStartTime(hour);
            Dao<CalendarEntry, Integer> CalendarDaoEntry = db.getCalendarEntryDao();
            try {

                CalendarEntry e = CalendarDaoEntry.queryForMatching(friday).get(0);
                e.setDescription(textField.getText());
                CalendarDaoEntry.update(e);
                System.out.print("FRIDAY" + hour);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                System.out.println("fuckml");
                e.printStackTrace();

            }
            hour = hour+2;
        }

    }

   public void Reload(){
        loadboxes();
   }

   public void setSemester(Semester semester){
    this.semester = semester;
    }

    public Semester getSemester(){
        return this.semester;
    }
}
