package controller;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.j256.ormlite.dao.Dao;

import connection.DBManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import models.Calendar;
import models.CalendarEntry;
import models.Group;
import models.User;

public class CreateTimetableController {
	
ArrayList<TextField> mon = new ArrayList<TextField>();
ArrayList<TextField> di = new ArrayList<TextField>();
ArrayList<TextField> mi = new ArrayList<TextField>();
ArrayList<TextField> don = new ArrayList<TextField>();
ArrayList<TextField> f  = new ArrayList<TextField>();



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


private DBManager db;

{
    try {
        db = DBManager.getInstance();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

	@FXML
	public void initialize(){

		appendTextfields();
	}
	
	@FXML
	public void CreateTimetable(){
		Calendar calendar = new Calendar();
		Dao<User, String> user = db.getUserDao();
        try {
        	
			User tester = user.queryForId("besttutor");
			calendar.setUser(tester);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		Dao<Calendar, Integer> CalendarDao = db.getCalendarDao();
        try {
			CalendarDao.create(calendar);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        

        
		appendTextfields();
		setMonday(mon,calendar);
		setTuesday(di,calendar);
		setWednesday(mi,calendar);
		setThursday(don,calendar);
		setFriday(f,calendar);

	}
	
	public void setMonday(ArrayList <TextField>array , Calendar calendar) {
		
		
		Iterator<TextField> i = array.iterator();
		for (int hour=8;i.hasNext(); hour+=2) {
			TextField t = i.next();
			CalendarEntry monday = new CalendarEntry();
			monday.setDayOfWeek(DayOfWeek.MONDAY);
			monday.setCalendar(calendar);
			monday.setDescription(t.getText());
			monday.setStartTime(hour);
			Dao<CalendarEntry, Integer> CalendarDaoEntry = db.getCalendarEntryDao();
			try {
				CalendarDaoEntry.create(monday);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void setTuesday(ArrayList <TextField>array , Calendar calendar) {

		Iterator<TextField> i = array.iterator();
		for (int hour=8;i.hasNext(); hour+=2) {
			TextField t = i.next();
			CalendarEntry tuesday = new CalendarEntry();
			tuesday.setDayOfWeek(DayOfWeek.TUESDAY);
			
			tuesday.setCalendar(calendar);
			tuesday.setDescription(t.getText());
			tuesday.setStartTime(hour);
			
		}
	}
		
	public void setWednesday(ArrayList <TextField>array , Calendar calendar) {

			Iterator<TextField> i = array.iterator();
			for (int hour=8;i.hasNext(); hour+=2) {
				TextField t = i.next();
				CalendarEntry wednesday = new CalendarEntry();
				wednesday.setDayOfWeek(DayOfWeek.WEDNESDAY);
				wednesday.setCalendar(calendar);
				wednesday.setDescription(t.getText());
				wednesday.setStartTime(hour);
				
			}
		}
			
	public void setThursday(ArrayList <TextField>array , Calendar calendar) {

				Iterator<TextField> i = array.iterator();
				for (int hour=8;i.hasNext(); hour+=2) {
					TextField t = i.next();
					CalendarEntry thursday = new CalendarEntry();
					thursday.setDayOfWeek(DayOfWeek.THURSDAY);
					thursday.setCalendar(calendar);
					thursday.setDescription(t.getText());
					thursday.setStartTime(hour);
					
				}
			
		
	}
	
	public void setFriday(ArrayList <TextField>array , Calendar calendar) {

		Iterator<TextField> i = array.iterator();
		for (int hour=8;i.hasNext(); hour+=2) {
			TextField t = i.next();
			CalendarEntry friday = new CalendarEntry();
			friday.setDayOfWeek(DayOfWeek.FRIDAY);
			friday.setCalendar(calendar);
			friday.setDescription(t.getText());
			friday.setStartTime(hour);
		  }
		}
	
	

	
	//fertig
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
	

}
