package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Notepad.TABLE_NOTEPAD)
public class Notepad {

    public static final String TABLE_NOTEPAD = "notepad";
    public static final String FIELD_NOTEPAD_ID = "notepad_id";
    public static final String FIELD_NOTEPAD_NAME = "notepad_name";
    public static final String FIELD_NOTEPAD_PRIORITY = "notepad_priority";
    public static final String FIELD_NOTEPAD_CONTENT = "notepad_content";
    public static final String FIELD_USERNAME = User.FIELD_USERNAME;

    @DatabaseField(generatedId = true, columnName = FIELD_NOTEPAD_ID)
    private int notepadId;

    @DatabaseField(columnName = FIELD_NOTEPAD_NAME)
    private String notepadName;

    @DatabaseField(columnName = FIELD_NOTEPAD_PRIORITY)
    private String notepadPriority;

    @DatabaseField(columnName = FIELD_NOTEPAD_CONTENT)
    private String notepadContent;

    @DatabaseField(columnName = FIELD_USERNAME, foreign = true, foreignAutoRefresh = true,
            columnDefinition = "varchar not null references \"user\"(username)")
    private User user;

    public int getNotepadId() {return notepadId;}
    public void setNotepadId(int notepadId) {this.notepadId = notepadId;}

    public String getNotepadName() {return notepadName;}
    public void setNotepadName(String notepadName) {this.notepadName = notepadName;}

    public String getNotepadPriority() {return notepadPriority;}
    public void setNotepadPriority(String notepadPriority) {this.notepadPriority = notepadPriority;}

    public String getNotepadContent() {return notepadContent;}
    public void setNotepadContent(String notepadContent) {this.notepadContent = notepadContent;}

    public User getUser() {return user;}
    public void setUser(User user) {this.user = user;}
}
