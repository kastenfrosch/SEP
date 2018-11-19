package models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@DatabaseTable(tableName=Groupage.TABLE_GROUPAGE)
public class Groupage {
    public static final String FIELD_GROUPAGE_ID = "groupage_id";
    public static final String TABLE_GROUPAGE = "groupage";
    public static final String FIELD_GROUPAGE_DESCRIPTION = "description";
    public static final String FIELD_SEMESTER_ID = "semester_id";
    public static final String FIELD_TIME_START = "time_start";
    public static final String FIELD_TIME_END = "time_end";

    @DatabaseField(generatedId = true, columnName = FIELD_GROUPAGE_ID)
    private int id;

    @DatabaseField(columnName = FIELD_GROUPAGE_DESCRIPTION, canBeNull = false)
    private String description;

    @DatabaseField(columnName = FIELD_TIME_START, dataType = DataType.TIME_STAMP)
    private Timestamp startTime;

    @DatabaseField(columnName = FIELD_TIME_END, dataType = DataType.TIME_STAMP)
    private Timestamp endTime;


    //Note: The column defintions are currently constants. I have not found a way to have them generated yet.
    @DatabaseField(foreign = true, columnName = FIELD_SEMESTER_ID, foreignAutoRefresh = true,
    columnDefinition = "varchar not null references semester(semester_id) on delete restrict")
    private Semester semester;


    public Groupage() {}

    /**
     * @deprecated use {@link Groupage#Groupage(String, Semester, Timestamp, Timestamp)} instead
     */
    @Deprecated
    public Groupage(String description, Semester semester) {
        this.description = description;
        this.semester = semester;
    }

    public Groupage(String description, Semester semester, Timestamp startTime, Timestamp endTime) {
        this.description = description;
        this.semester = semester;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    @Override
    public String toString() {
    	return description;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Groupage)) {
            return false;
        }
        return this.getId() == ((Groupage) other).getId();
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public LocalDateTime getStartTimeAsLocalDateTime() {
        return startTime.toLocalDateTime();
    }

    public void setStartTime(Timestamp t) {
        this.startTime = t;
    }

    public void setStartTime(LocalDateTime t) {
        this.startTime = Timestamp.valueOf(t);
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public LocalDateTime getEndTimeAsLocalDateTime() {
        return endTime.toLocalDateTime();
    }

    public void setEndTime(Timestamp t) {
        this.endTime = t;
    }

    public void setEndTime(LocalDateTime t) {
        this.endTime = Timestamp.valueOf(t);
    }
}
