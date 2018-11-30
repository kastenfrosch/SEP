package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = GroupNotepad.TABLE_GROUP_NOTEPAD
)
public class GroupNotepad {
    public static final String TABLE_GROUP_NOTEPAD = "notepad_group";
    public static final String FIELD_COMBO_ID = "combo_id";
    public static final String FIELD_NOTEPAD_ID = "notepad_id";
    public static final String FIELD_GROUP_ID = "group_id";

    @DatabaseField(columnName = FIELD_COMBO_ID, generatedId = true)
    private int id;

    @DatabaseField(columnName = FIELD_NOTEPAD_ID, foreign = true, foreignAutoRefresh = true,
            columnDefinition = "integer not null references notepad(notepad_id)", uniqueCombo = true)
    private Notepad notepad;

    @DatabaseField(columnName = FIELD_GROUP_ID, foreign = true, foreignAutoRefresh = true,
            columnDefinition = "integer not null references \"group\"(group_id)", uniqueCombo = true)
    private Group group;

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Notepad getNotepad() {
        return notepad;
    }

    public void setNotepad(Notepad notepad) {
        this.notepad = notepad;
    }

    public int getId() {
        return id;
    }

}
