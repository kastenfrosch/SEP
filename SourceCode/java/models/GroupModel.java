package models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;
import java.io.Serializable;

/**
 * @deprecated
 * use {@link Group} instead
 */
@Deprecated
@DatabaseTable(tableName = GroupModel.TABLE_NAME_GROUP) //, daoClass = groupDao.class)
public class GroupModel implements Serializable {
    public static final String TABLE_NAME_GROUP = "group";
    private static final String FIELD_GROUP_GROUP_ID = "group_id";
    private static final String FIELD_GROUP_NAME = "name";
    private static final String FIELD_GROUP_GROUPAGE_ID = "groupage_id";
    private static final String FIELD_GROUP_SEMESTER_ID = "semester_id";

    @DatabaseField(generatedId = true, columnName = FIELD_GROUP_GROUP_ID)
    private int group_id;
    @DatabaseField(columnName = FIELD_GROUP_NAME)
    private String name;
    @DatabaseField(columnName = FIELD_GROUP_GROUPAGE_ID, foreign = true, foreignAutoRefresh = true, foreignColumnName="groupage_id")
    private GroupageModel groupage;
    @DatabaseField(columnName = FIELD_GROUP_SEMESTER_ID, foreign = true, foreignAutoRefresh = true, foreignColumnName="semester_id")
    private SemesterModel semester;

    @ForeignCollectionField(foreignFieldName="group")
    private ForeignCollection<GroupModel> groups;

    public GroupModel() { }

    public GroupModel(int group_id, String name, GroupageModel groupage, SemesterModel semester) {
        this.group_id = group_id;
        this.name = name;
        this.groupage = groupage;
        this.semester = semester;
    }

    public int getGroup_id() { return group_id; }

    public void setGroup_id(int group_id) { this.group_id = group_id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public GroupageModel getGroupage() { return groupage; }

    public void setGroupage_id(GroupageModel groupage) { this.groupage = groupage; }

    public SemesterModel getSemester() { return semester; }

    public void setSemester(SemesterModel semester) { this.semester = semester; }

    public ForeignCollection<GroupModel> getGroups() {return groups; }
}