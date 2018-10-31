package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName=Groupage.TABLE_GROUPAGE)
public class Groupage {
    public static final String FIELD_GROUPAGE_ID = "groupage_id";
    public static final String TABLE_GROUPAGE = "groupage";
    public static final String FIELD_GROUPAGE_DESCRIPTION = "description";
    public static final String FIELD_SEMESTER_ID = "semester_id";

    @DatabaseField(generatedId = true, columnName = FIELD_GROUPAGE_ID)
    private int id;

    @DatabaseField(columnName = FIELD_GROUPAGE_DESCRIPTION)
    private String description;


    //Note: The column defintions are currently constants. I have not found a way to have them generated yet.
    @DatabaseField(foreign = true, columnName = FIELD_SEMESTER_ID, foreignAutoRefresh = true,
    columnDefinition = "varchar references semester(semester_id) on delete restrict")
    private Semester semester;


    public Groupage() {}

    public Groupage(String description, Semester semester) {
        this.description = description;
        this.semester = semester;
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
}
