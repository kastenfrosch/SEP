package models;

import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;
import java.io.Serializable;

@DatabaseTable(tableName = GroupageModel.TABLE_NAME_GROUPAGE) //, daoClass = groupageDao.class)
public class GroupageModel implements Serializable {
    public static final String TABLE_NAME_GROUPAGE = "groupage";
    private static final String FIELD_GROUPAGE_GROUPAGE_ID = "groupage_id";
    private static final String FIELD_GROUPAGE_DESCRIPTION = "description";
    private static final String FIELD_GROUPAGE_SEMESTER_ID = "semester_id";

    @DatabaseField(generatedId = true, columnName = FIELD_GROUPAGE_GROUPAGE_ID)
    private int groupage_id;
    @DatabaseField(columnName = FIELD_GROUPAGE_DESCRIPTION)
    private String description;
    @DatabaseField(columnName = FIELD_GROUPAGE_SEMESTER_ID)
    private int semester_id;

    public GroupageModel() { }

    public GroupageModel(int groupage_id, String description, int semester_id) {
        this.groupage_id = groupage_id;
        this.description = description;
        this.semester_id = semester_id;
    }

    public int getGroupage_id() { return groupage_id; }

    public void setGroupage_id(int groupage_id) { this.groupage_id = groupage_id; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public int getSemester_id() { return semester_id; }

    public void setSemester_id(int semester_id) { this.semester_id = semester_id; }

}