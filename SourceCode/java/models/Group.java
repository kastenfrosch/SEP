package models;


import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Group.TABLE_GROUP)
public class Group {
    public static final String TABLE_GROUP = "group";
    public static final String FIELD_GROUP_ID = "group_id";
    public static final String FIELD_GROUP_NAME = "name";
    public static final String FIELD_GROUPAGE_ID = "groupage_id";

    @DatabaseField(generatedId = true, columnName = FIELD_GROUP_ID)
    private int id;

    @DatabaseField(columnName = FIELD_GROUP_NAME)
    private String name;

    //Note: The column defintions are currently constants. I have not found a way to have them generated yet.
    @DatabaseField(foreign = true, columnName = FIELD_GROUPAGE_ID, foreignAutoRefresh = true,
    columnDefinition = "integer not null references groupage(groupage_id)")
    private Groupage groupage;

    @ForeignCollectionField
    private ForeignCollection<Student> students;

    public Group() {}

    public Group(String name, Groupage groupage) {
        this.name = name;
        this.groupage = groupage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public ForeignCollection<Student> getStudents() {
        return students;
    }

    @Override
    public String toString() {
    	return name;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Group)) {
            return false;
        }
        return this.id == ((Group) other).getId();
    }
}
