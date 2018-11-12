package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

// TODO: 12.11.2018  Field_Notepad_Priority ergänzen

@DatabaseTable(tableName = Notepad.TABLE_NOTEPAD)
public class Notepad {

    public static final String TABLE_NOTEPAD = "Notepad";
    public static final String FIELD_NOTEPAD_ID = "Notepad_id";
  //  public static final String FIELD_NOTEPAD_PRIORITY = "Notepad_Priority";
    public static final String FIELD_USERNAME = User.FIELD_USERNAME;

    @DatabaseField(generatedId = true, columnName = FIELD_NOTEPAD_ID)
    private int notepadId;

    @DatabaseField(columnName = FIELD_USERNAME, foreign = true, foreignAutoRefresh = true,
            columnDefinition = "varchar not null references \"user\"(username)")
    private User user;

    public int getNotepadId() {return notepadId;}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
