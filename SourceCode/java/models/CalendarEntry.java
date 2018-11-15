package models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.time.DayOfWeek;

@DatabaseTable(tableName=CalendarEntry.TABLE_CALENDAR_ENTRY)
public class CalendarEntry {
    public static final String TABLE_CALENDAR_ENTRY = "calendar_entry";
    public static final String FIELD_ENTRY_ID = "entry_id";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_TIME_START = "time_start";
    public static final String FIELD_CALENDAR_ID = "calendar_id";
    public static final String FIELD_DAY_OF_WEEK = "day_of_week";

    @DatabaseField(generatedId = true, columnName = FIELD_ENTRY_ID)
    private int entryId;

    @DatabaseField(columnName = FIELD_CALENDAR_ID, canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Calendar calendar;

    @DatabaseField(columnName = FIELD_DESCRIPTION, canBeNull = false)
    private String description;

    @DatabaseField(columnName = FIELD_TIME_START, canBeNull = false)
    private int startTime;

    @DatabaseField(columnName = FIELD_DAY_OF_WEEK, canBeNull = false, dataType = DataType.ENUM_INTEGER)
    private DayOfWeek dayOfWeek;

    public int getEntryId() {
        return entryId;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}

