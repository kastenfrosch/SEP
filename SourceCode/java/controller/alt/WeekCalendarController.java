package controller.alt;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import modal.ErrorModal;
import models.CalendarEntry;
import models.Groupage;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.List;

public class WeekCalendarController {

    @FXML
    private AnchorPane anchorPane;

    private CalendarView calendarView;
    private Groupage groupage;

    private Dao<models.Calendar, Integer> calendarDao;
    private Dao<CalendarEntry, Integer> calendarEntryDao;

    private models.Calendar calendar;
    {
        try {
            this.calendarDao = DBManager.getInstance().getCalendarDao();
            this.calendarEntryDao = DBManager.getInstance().getCalendarEntryDao();
        } catch (SQLException ex) {
            ErrorModal.show("Wochenplan konnte nicht geladen werden.");
        }
    }

    public void initialize() {

        this.calendarView = new CalendarView();
        Calendar calendar = new Calendar("Wochenplan");
        calendar.setStyle(Calendar.Style.STYLE1);

        CalendarSource calendarSource = new CalendarSource("Meine Kalender");

        calendarView.setRequestedTime(LocalTime.now());

        calendar.addEventHandler(e -> {

            Entry<?> entry = e.getEntry();

            //bruh
            if(entry == null) {
                return;
            }

            //getting all entries in a calendar like this looks dirty but there is no getEntries method or anything
            if(!calendar.findEntries("").contains(entry)) {
                //entry was deleted because it's not in the calendar anymore so we delete it from the database too
                Entry<CalendarEntry> local = (Entry<CalendarEntry>) entry;
                try {
                    CalendarEntry ce = local.getUserObject();
                    calendarEntryDao.delete(ce);
                } catch(SQLException ex) {
                    ErrorModal.show("Der Eintrag konnte nicht gelöscht werden.");
                }
                return;
            }

            /* TODO: This is called every single time even a single letter of the title is changed
             * which may cause a lot of lag with all of the database calls
             */
            if (entry.getUserObject() == null || entry.getUserObject() instanceof CalendarEntry) {
                //entry was changed in some way
                Entry<CalendarEntry> local = (Entry<CalendarEntry>) entry;
                CalendarEntry ce;
                if(entry.getUserObject() != null) {
                    ce = (CalendarEntry) entry.getUserObject();
                } else {
                    ce = new CalendarEntry();
                    ce.setCalendar(this.calendar);
                    local.setUserObject(ce);
                }

                ce.setEndTime(entry.getEndAsLocalDateTime());
                ce.setStartTime(entry.getStartAsLocalDateTime());
                ce.setDayOfWeek(entry.getStartAsLocalDateTime().getDayOfWeek());
                ce.setDescription(entry.getTitle());

                try {
                    calendarEntryDao.createOrUpdate(ce);
                    calendarEntryDao.refresh(ce);
                } catch (SQLException ex) {
                    ErrorModal.show("Der Eintrag konnte nicht angelegt werden.");
                }
            }
            else
            {
                ErrorModal.show("Unknown UserObject type for CalendarEntry " + entry.getTitle());
            }
        });


        calendarSource.getCalendars().add(calendar);
        calendarView.getCalendarSources().clear();
        calendarView.getCalendarSources().add(calendarSource);
        this.anchorPane.getChildren().add(calendarView);
        calendarView.showWeekPage();
    }

    private void fillCalendar() {
        if (this.groupage == null) {
            ErrorModal.show("Developer Error: Groupage not set (somebody fucked up)");
            return;
        }
        try {
            List<models.Calendar> c0 = calendarDao.query(
                    calendarDao.queryBuilder()
                            .where()
                            .eq(models.Calendar.FIELD_GROUPAGE_ID, this.groupage.getId())
                            .and()
                            .eq(models.Calendar.FIELD_CALENDAR_TYPE, models.Calendar.CalendarType.WEEK)
                            .prepare()
            );
            if (c0.size() == 0) {
                //No calendar exists
                models.Calendar c = new models.Calendar();
                c.setCalendarType(models.Calendar.CalendarType.WEEK);
                c.setGroupage(this.groupage);
                c.setSemester(this.groupage.getSemester());
                calendarDao.create(c);
                this.calendar = c;
            } else {
                this.calendar = c0.get(0);
            }


            List<CalendarEntry> entries = calendarEntryDao.queryForEq(
                    CalendarEntry.FIELD_CALENDAR_ID,
                    calendar.getCalendarId()
            );

            this.calendarView.getCalendars().get(0).clear();
            for (var i : entries) {
                Entry<CalendarEntry> e = new Entry<>(i.getDescription());
                e.setUserObject(i);
                e.setInterval(i.getStartTimeAsLocalDateTime(), (i.getEndTime() == null ? i.getStartTimeAsLocalDateTime().plusMinutes(1) : i.getEndTime()));
                this.calendarView.getCalendars().get(0).addEntries(e);
            }
        } catch (SQLException ex) {
            ErrorModal.show("Wochenplan konnte nicht geladen werden");
        }

    }

    public void setGroupage(Groupage groupage) {
        this.groupage = groupage;
        fillCalendar();
    }
}
