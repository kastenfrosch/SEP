package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DataType;

import java.sql.Timestamp;

@DatabaseTable(tableName = NotepadHistory.TABLE_NOTEPAD_HISTORY)
public class NotepadHistory {
    public static final String TABLE_NOTEPAD_HISTORY = "notepad_history";
    public static final String FIELD_HISTORY_ID = "history_id";
    public static final String FIELD_NOTEPAD_ID = "notepad_id";
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_TIME = "time";

    @DatabaseField(generatedId = true, columnName = FIELD_HISTORY_ID)
    private int id;

    @DatabaseField(columnName = FIELD_NOTEPAD_ID, foreign = true, foreignAutoRefresh = true,
            columnDefinition = "integer not null references notepad(notepad_id) on delete cascade")
    private Notepad notepad;

    @DatabaseField(columnName = FIELD_USERNAME, foreign=true,
            columnDefinition = "varchar not null references \"user\"(username) on delete set null")
    private User user;

    @DatabaseField(columnName = FIELD_TIME, dataType = DataType.TIME_STAMP)
    private Timestamp timestamp;

    public int getId() {
        return id;
    }

    public Notepad getNotepad() {
        return notepad;
    }

    public void setNotepad(Notepad notepad) {
        this.notepad = notepad;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
