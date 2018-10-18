package models;

import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;
import java.io.Serializable;

@DatabaseTable(tableName = groupModel.TABLE_NAME_GROUP) //, daoClass = groupDao.class) 
public class groupModel implements Serializable {
    public static final String TABLE_NAME_GROUP = "group";
    private static final String FIELD_GROUP_GROUP_ID = "group_id";
    private static final String FIELD_GROUP_NAME = "name";
    private static final String FIELD_GROUP_GROUPAGE_ID = "groupage_id";
    private static final String FIELD_GROUP_SEMESTER_ID = "semester_id";

    @DatabaseField(generatedId = true, columnName = FIELD_GROUP_GROUP_ID)
    private int group_id;
    @DatabaseField(columnName = FIELD_GROUP_NAME)
    private String name;
    @DatabaseField(columnName = FIELD_GROUP_GROUPAGE_ID)
    private int groupage_id;
    @DatabaseField(columnName = FIELD_GROUP_SEMESTER_ID)
    private int semester_id;

    public groupModel() { }

    public groupModel(int group_id,String name,int groupage_id,int semester_id) {     
        this.group_id = group_id;
        this.name = name;
        this.groupage_id = groupage_id;
        this.semester_id = semester_id;
    }

    public int getGroup_id() { return group_id; }

    public void setGroup_id(int group_id) { this.group_id = group_id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getGroupage_id() { return groupage_id; }

    public void setGroupage_id(int groupage_id) { this.groupage_id = groupage_id; }

    public int getSemester_id() { return semester_id; }

    public void setSemester_id(int semester_id) { this.semester_id = semester_id; }

}