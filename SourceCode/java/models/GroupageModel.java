package models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * @deprecated
 * use {@link Groupage} instead
 */
@Deprecated
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
    @DatabaseField(columnName = FIELD_GROUPAGE_SEMESTER_ID, foreign = true, foreignAutoRefresh = true, foreignColumnName = "semester_id")
    private SemesterModel semester;
    @ForeignCollectionField(foreignFieldName = "groupage")
    private ForeignCollection<GroupModel> groups;

    public GroupageModel() {
    }

    public GroupageModel(int groupage_id, String description, SemesterModel semester) {
        this.groupage_id = groupage_id;
        this.description = description;
        this.semester = semester;
    }

    public int getGroupage_id() {
        return groupage_id;
    }

    public void setGroupage_id(int groupage_id) {
        this.groupage_id = groupage_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SemesterModel getSemester() {
        return semester;
    }

    public void setSemester(SemesterModel semester) {
        this.semester = semester;
    }

    public ForeignCollection<GroupModel> getGroups() {
        return groups;
    }
}