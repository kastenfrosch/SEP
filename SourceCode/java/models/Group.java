package models;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Group.TABLE_GROUP)
public class Group {
    public static final String TABLE_GROUP = "group";
    public static final String FIELD_GROUP_ID = "group_id";
    public static final String FIELD_GROUP_NAME = "name";
    public static final String FIELD_SEMESTER_ID = "semester_id";
    public static final String FIELD_GROUPAGE_ID = "groupage_id";

    @DatabaseField(generatedId = true, columnName = FIELD_GROUP_ID)
    private int id;

    @DatabaseField(columnName = FIELD_GROUP_NAME)
    private String name;

    @DatabaseField(foreign = true, columnName = FIELD_SEMESTER_ID, foreignAutoRefresh = true)
    private Semester semester;

    @DatabaseField(foreign = true, columnName = FIELD_GROUPAGE_ID, foreignAutoRefresh = true)
    private Groupage groupage;

    public Group() {}

    public Group(String name, Groupage groupage, Semester semester) {
        this.name = name;
        this.groupage = groupage;
        this.semester = semester;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public Groupage getGroupage() {
        return groupage;
    }

    public void setGroupage(Groupage groupage) {
        this.groupage = groupage;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
    	return name;
    }
}
