package models;

import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * @deprecated
 * use {@link Student} instead
 */
@Deprecated
@DatabaseTable(tableName = StudentModel.TABLE_NAME_STUDENT) //, daoClass = studentDao.class)
public class StudentModel implements Serializable {
    public static final String TABLE_NAME_STUDENT = "student";
    private static final String FIELD_STUDENT_STUDENT_ID = "student_id";
    private static final String FIELD_STUDENT_PERSON_ID = "person_id";
    private static final String FIELD_STUDENT_MATR_NO = "matr_no";
    private static final String FIELD_STUDENT_GROUP_ID = "group_id";
    private static final String FIELD_STUDENT_SEMESTER_ID = "semester_id";

    @DatabaseField(generatedId = true, columnName = FIELD_STUDENT_STUDENT_ID)
    private int student_id;
    @DatabaseField(columnName = FIELD_STUDENT_PERSON_ID, foreign = true, foreignAutoRefresh = true, foreignColumnName = "person_id")
    private PersonModel person;
    @DatabaseField(columnName = FIELD_STUDENT_MATR_NO)
    private String matr_no;
    @DatabaseField(columnName = FIELD_STUDENT_GROUP_ID, foreign = true, foreignAutoRefresh = true, foreignColumnName = "group_id")
    private GroupModel group;
    @DatabaseField(columnName = FIELD_STUDENT_SEMESTER_ID, foreign = true, foreignAutoRefresh = true, foreignColumnName = "semester_id")
    private SemesterModel semester;

    public StudentModel() {
    }

    public StudentModel(int student_id, PersonModel person, String matr_no, GroupModel group, SemesterModel semester) {
        this.student_id = student_id;
        this.person = person;
        this.matr_no = matr_no;
        this.group = group;
        this.semester = semester;
    }

    public int getStudent_id() {
        return student_id;
    }

    public void setStudent_id(int student_id) {
        this.student_id = student_id;
    }

    public PersonModel getPerson() {
        return person;
    }

    public void setPerson(PersonModel person) {
        this.person = person;
    }

    public String getMatr_no() {
        return matr_no;
    }

    public void setMatr_no(String matr_no) {
        this.matr_no = matr_no;
    }

    public GroupModel getGroup() {
        return group;
    }

    public void setGroup(GroupModel group) {
        this.group = group;
    }

    public SemesterModel getSemester() {
        return semester;
    }

    public void setSemester(SemesterModel semester) {
        this.semester = semester;
    }

}