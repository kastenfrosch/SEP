package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = CalendarExtraInfo.TABLE_CALENDAR_EXTRA_INFO)
public class CalendarExtraInfo {
    public static final String TABLE_CALENDAR_EXTRA_INFO = "calendar_extra_info";
    public static final String FIELD_EXTRA_INFO_ID = "extra_info_id";
    public static final String FIELD_CALENDAR_ENTRY_ID = "calendar_entry_id";
    public static final String FIELD_INFO_ITERATION = "info_iteration";
    public static final String FIELD_INFO_LECTURE = "info_lecture";
    public static final String FIELD_INFO_WORKPHASE = "info_workphase";
    public static final String FIELD_CALENDAR_WEEK = "calendar_week";
    public static final String FIELD_MEETING_NO = "meeting_no";

    @DatabaseField(columnName = FIELD_EXTRA_INFO_ID, generatedId = true)
    private int id;

    @DatabaseField(columnName = FIELD_CALENDAR_ENTRY_ID, foreign = true, foreignAutoRefresh = true, unique = true,
            columnDefinition = "integer not null references calendar_entry(entry_id) on delete cascade")
    private CalendarEntry calendarEntry;

    @DatabaseField(columnName=FIELD_INFO_ITERATION)
    private String iterationInfo;

    @DatabaseField(columnName = FIELD_INFO_LECTURE)
    private String lectureInfo;

    @DatabaseField(columnName = FIELD_INFO_WORKPHASE)
    private String workphaseInfo;

    @DatabaseField(columnName = FIELD_CALENDAR_WEEK)
    private int calendarWeek;

    @DatabaseField(columnName = FIELD_MEETING_NO)
    private int meetingNo;


    public CalendarExtraInfo(CalendarEntry calendarEntry, String iterationInfo, String lectureInfo, String workphaseInfo, int calendarWeek, int meetingNo) {
        this.calendarEntry = calendarEntry;
        this.iterationInfo = iterationInfo;
        this.lectureInfo = lectureInfo;
        this.workphaseInfo = workphaseInfo;
        this.calendarWeek = calendarWeek;
        this.meetingNo = meetingNo;
    }

    public CalendarExtraInfo() {

    }

    public CalendarEntry getCalendarEntry() {
        return calendarEntry;
    }

    public void setCalendarEntry(CalendarEntry calendarEntry) {
        this.calendarEntry = calendarEntry;
    }

    public String getIterationInfo() {
        return iterationInfo;
    }

    public void setIterationInfo(String iterationInfo) {
        this.iterationInfo = iterationInfo;
    }

    public String getLectureInfo() {
        return lectureInfo;
    }

    public void setLectureInfo(String lectureInfo) {
        this.lectureInfo = lectureInfo;
    }

    public String getWorkphaseInfo() {
        return workphaseInfo;
    }

    public void setWorkphaseInfo(String workphaseInfo) {
        this.workphaseInfo = workphaseInfo;
    }

    public int getCalendarWeek() {
        return calendarWeek;
    }

    public void setCalendarWeek(int calendarWeek) {
        this.calendarWeek = calendarWeek;
    }

    public int getMeetingNo() {
        return meetingNo;
    }

    public void setMeetingNo(int meetingNo) {
        this.meetingNo = meetingNo;
    }

    public int getId() {
        return id;
    }
}
