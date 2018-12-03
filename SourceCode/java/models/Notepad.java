package models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Notepad.TABLE_NOTEPAD)
public class Notepad {

    public static final String TABLE_NOTEPAD = "notepad";
    public static final String FIELD_NOTEPAD_ID = "notepad_id";
    public static final String FIELD_NOTEPAD_NAME = "notepad_name";
    @Deprecated
    public static final String FIELD_NOTEPAD_PRIORITY = "notepad_priority";
    public static final String FIELD_NOTEPAD_CLASSIFICATION = "notepad_classification";
    public static final String FIELD_NOTEPAD_CONTENT = "notepad_content";

    @DatabaseField(generatedId = true, columnName = FIELD_NOTEPAD_ID)
    private int notepadId;

    @DatabaseField(columnName = FIELD_NOTEPAD_NAME)
    private String notepadName;

    @DatabaseField(columnName = FIELD_NOTEPAD_PRIORITY)
    private String notepadPriority;

    @DatabaseField(columnName = FIELD_NOTEPAD_CLASSIFICATION, dataType = DataType.ENUM_INTEGER)
    private Classification classification;

    @DatabaseField(columnName = FIELD_NOTEPAD_CONTENT)
    private String notepadContent;

    public int getNotepadId() {
        return notepadId;
    }

    public void setNotepadId(int notepadId) {
        this.notepadId = notepadId;
    }

    public String getNotepadName() {
        return notepadName;
    }

    public void setNotepadName(String notepadName) {
        this.notepadName = notepadName;
    }

    @Deprecated
    public String getNotepadPriority() {
        return notepadPriority;
    }

    @Deprecated
    public void setNotepadPriority(String notepadPriority) {
        this.notepadPriority = notepadPriority;
    }

    public String getNotepadContent() {
        return notepadContent;
    }

    public void setNotepadContent(String notepadContent) {
        this.notepadContent = notepadContent;
    }

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public enum Classification {
        BAD("Schlecht"), MEDIUM("Mittel"), GOOD("Gut"), NEUTRAL("Keine Zuordnung");

        private String classification;

        Classification(String classification) {
            this.classification = classification;
        }

        public static Classification get(String s) {
            if (s.equalsIgnoreCase("schlecht")) {
                return BAD;
            } else if (s.equalsIgnoreCase("mittel")) {
                return MEDIUM;
            } else if(s.equalsIgnoreCase("gut")) {
                return GOOD;
            }
            else if(s.equalsIgnoreCase("keine zuordnung"))
            {
                return NEUTRAL;
            } else
            {
                return null;
            }
        }

        @Override
        public String toString() {
            return this.classification;
        }
    }
}
