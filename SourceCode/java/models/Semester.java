package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "semester")
public class Semester {
    public static final String TABLE_NAME_SEMESTER = "semester";
    public static final String FIELD_SEMESTER_ID = "semester_id";
    public static final String FIELD_SEMESTER_DESCRIPTION = "description";

    @DatabaseField(id = true, columnName = FIELD_SEMESTER_ID)
    private String id;

    @DatabaseField(columnName=FIELD_SEMESTER_DESCRIPTION)
    private String description;

    public Semester() {}

    public Semester(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
