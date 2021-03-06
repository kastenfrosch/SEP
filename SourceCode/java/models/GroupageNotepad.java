package models;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = GroupageNotepad.TABLE_GROUPAGE_NOTEPAD)
public class GroupageNotepad implements INotepadBridge {
    public static final String TABLE_GROUPAGE_NOTEPAD = "notepad_groupage";
    public static final String FIELD_COMBO_ID = "combo_id";
    public static final String FIELD_NOTEPAD_ID = "notepad_id";
    public static final String FIELD_GROUPAGE_ID = "groupage_id";

    @DatabaseField(columnName = FIELD_COMBO_ID, generatedId = true)
    private int id;

    @DatabaseField(columnName = FIELD_NOTEPAD_ID, foreign = true, foreignAutoRefresh = true,
            columnDefinition = "integer not null references notepad(notepad_id) on delete cascade")
    private Notepad notepad;

    @DatabaseField(columnName = FIELD_GROUPAGE_ID, foreign = true, foreignAutoRefresh = true,
            columnDefinition = "integer not null references groupage(groupage_id) on delete cascade")
    private Groupage groupage;

    public Notepad getNotepad() {
        return notepad;
    }

    public void setNotepad(Notepad notepad) {
        this.notepad = notepad;
    }

    public Groupage getGroupage() {
        return groupage;
    }

    public void setGroupage(Groupage groupage) {
        this.groupage = groupage;
    }

}
