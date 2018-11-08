package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

@DatabaseTable(tableName=CalendarEntry.TABLE_CALENDAR_ENTRY)
public class CalendarEntry {
    public static final String TABLE_CALENDAR_ENTRY = "calendar_entry";
    public static final String FIELD_ENTRY_ID = "entry_id";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_TIME_START = "time_start";
    public static final String FIELD_TIME_END = "time_end";
    public static final String FIELD_CALENDAR_ID = "calendar_id";


    @DatabaseField(generatedId = true, columnName = FIELD_ENTRY_ID)
    private int entryId;

    @DatabaseField(columnName = FIELD_CALENDAR_ID, canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Calendar calendar;

    @DatabaseField(columnName = FIELD_DESCRIPTION, canBeNull = false)
    private String description;

    @DatabaseField(columnName = FIELD_TIME_START, canBeNull = false)
    private Timestamp startTime;

    @DatabaseField(columnName = FIELD_TIME_END, canBeNull = false)
    private Timestamp endTime;

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


    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }
}

