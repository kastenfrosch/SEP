package models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * @deprecated
 * use {@link Semester} instead
 */
@Deprecated
@DatabaseTable(tableName = SemesterModel.TABLE_NAME_SEMESTER) //, daoClass = semesterDao.class)
public class SemesterModel implements Serializable {
    public static final String TABLE_NAME_SEMESTER = "semester";
    private static final String FIELD_SEMESTER_SEMESTER_ID = "semester_id";
    private static final String FIELD_SEMESTER_DESCRIPTION = "description";

    @DatabaseField(generatedId = true, columnName = FIELD_SEMESTER_SEMESTER_ID)
    private int semester_id;
    @DatabaseField(columnName = FIELD_SEMESTER_DESCRIPTION)
    private String description;
    @ForeignCollectionField(foreignFieldName = "semester")
    private ForeignCollection<StudentModel> students;
    @ForeignCollectionField(foreignFieldName = "semester")
    private ForeignCollection<GroupageModel> groupages;
    @ForeignCollectionField(foreignFieldName = "semester")
    private ForeignCollection<GroupModel> groups;

    public SemesterModel() {
    }

    public SemesterModel(int semester_id, String description) {
        this.semester_id = semester_id;
        this.description = description;
    }

    public int getSemester_id() {
        return semester_id;
    }

    public void setSemester_id(int semester_id) {
        this.semester_id = semester_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public ForeignCollection<StudentModel> getStudents() {
        return students;
    }

    public ForeignCollection<GroupageModel> getGroupages() {
        return groupages;
    }

    public ForeignCollection<GroupModel> getGroups() {
        return groups;
    }
}