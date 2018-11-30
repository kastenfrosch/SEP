package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DataType;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@DatabaseTable(tableName = Tardy.TABLE_TARDY)
public class Tardy {
    public static final String TABLE_TARDY = "tardy";
    public static final String FIELD_ID = "tardy_id";
    public static final String FIELD_STUDENT = "student_id";
    public static final String FIELD_PRESENT = "present";
    public static final String FIELD_TIME_MISSED = "time_missed";
    public static final String FIELD_DATE_MISSED = "date_missed";
    public static final String FIELD_EXCUSED = "excused";

    @DatabaseField(generatedId = true, columnName = FIELD_ID)
    private int id;

    @DatabaseField(columnName = FIELD_STUDENT, foreign = true, foreignAutoRefresh = true,
            columnDefinition = "integer not null references student(student_id)")
    private Student student;

    @DatabaseField(columnName = FIELD_TIME_MISSED, canBeNull = false)
    private int timeMissed;

    @DatabaseField(columnName = FIELD_DATE_MISSED, canBeNull = false, foreign = true, foreignAutoRefresh = true,
            columnDefinition = "integer not null references calendar_entry(entry_id)")
    private CalendarEntry dateMissed;

    @DatabaseField(columnName = FIELD_EXCUSED, canBeNull = false, defaultValue = "false")
    private boolean excused;

    @DatabaseField(columnName = FIELD_PRESENT, canBeNull = false, defaultValue = "true")
    private boolean present;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public int getTimeMissed() {
        return timeMissed;
    }

    public void setTimeMissed(int timeMissed) {
        this.timeMissed = timeMissed;
    }

    public CalendarEntry getDateMissed() {
        return dateMissed;
    }

    public void setDateMissed(CalendarEntry dateMissed) {
        this.dateMissed = dateMissed;
    }

    public boolean isExcused() {
        return excused;
    }

    public void setExcused(boolean excused) {
        this.excused = excused;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }
}
