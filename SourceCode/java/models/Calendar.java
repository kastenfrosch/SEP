package models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Calendar.TABLE_CALENDAR)
public class Calendar {
    public static final String TABLE_CALENDAR = "calendar";
    public static final String FIELD_CALENDAR_ID = "calendar_id";
    public static final String FIELD_CALENDAR_TYPE = "calendar_type";
    public static final String FIELD_USERNAME = User.FIELD_USERNAME;


    @DatabaseField(generatedId = true, columnName = FIELD_CALENDAR_ID)
    private int calendarId;

    @DatabaseField(columnName = FIELD_CALENDAR_TYPE, dataType = DataType.ENUM_INTEGER, canBeNull = false)
    private CalendarType calendarType;

    @DatabaseField(columnName = FIELD_USERNAME, foreign = true, foreignAutoRefresh = true,
            columnDefinition = "varchar not null references \"user\"(username)")
    private User user;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<CalendarEntry> calendarEntries;

    public enum CalendarType {
        SEMESTER,
        WEEK
    }

    public int getCalendarId() {
        return calendarId;
    }

    public CalendarType getCalendarType() {
        return calendarType;
    }

    public void setCalendarType(CalendarType calendarType) {
        this.calendarType = calendarType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ForeignCollection<CalendarEntry> getCalendarEntries() {
        return calendarEntries;
    }

    public void setCalendarEntries(ForeignCollection<CalendarEntry> calendarEntries) {
        this.calendarEntries = calendarEntries;
    }
}