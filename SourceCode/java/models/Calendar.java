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
    public static final String FIELD_SEMESTER_ID = Semester.FIELD_SEMESTER_ID;
    public static final String FIELD_GROUPAGE_ID = Groupage.FIELD_GROUPAGE_ID;

    @DatabaseField(generatedId = true, columnName = FIELD_CALENDAR_ID)
    private int calendarId;

    @DatabaseField(columnName = FIELD_CALENDAR_TYPE, dataType = DataType.ENUM_INTEGER, canBeNull = false, uniqueCombo = true)
    private CalendarType calendarType;

    @DatabaseField(columnName = FIELD_SEMESTER_ID, foreign = true, foreignAutoRefresh = true, uniqueCombo = true,
            columnDefinition = "varchar not null references \"semester\"(semester_id)")
    private Semester semester;

    @DatabaseField(columnName = FIELD_GROUPAGE_ID, foreign = true, foreignAutoRefresh = true, uniqueCombo = true,
            columnDefinition = "integer references groupage(groupage_id)")
    private Groupage groupage;

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

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public ForeignCollection<CalendarEntry> getCalendarEntries() {
        return calendarEntries;
    }


    public Groupage getGroupage() {
        return groupage;
    }

    public void setGroupage(Groupage groupage) {
        this.groupage = groupage;
    }

    @Override
    public String toString() {
        return this.calendarType.toString() + " " + this.getSemester().getId();
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Calendar)) {
            return false;
        }

        return ((Calendar) other).getCalendarId() == this.calendarId;
    }
}