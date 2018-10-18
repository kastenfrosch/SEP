package models;

import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;
import java.io.Serializable;

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
    @DatabaseField(columnName = FIELD_STUDENT_PERSON_ID)
    private int person_id;
    @DatabaseField(columnName = FIELD_STUDENT_MATR_NO)
    private String matr_no;
    @DatabaseField(columnName = FIELD_STUDENT_GROUP_ID)
    private int group_id;
    @DatabaseField(columnName = FIELD_STUDENT_SEMESTER_ID)
    private int semester_id;

    public StudentModel() { }

    public StudentModel(int student_id, int person_id, String matr_no, int group_id, int semester_id) {
        this.student_id = student_id;
        this.person_id = person_id;
        this.matr_no = matr_no;
        this.group_id = group_id;
        this.semester_id = semester_id;
    }

    public int getStudent_id() { return student_id; }

    public void setStudent_id(int student_id) { this.student_id = student_id; }

    public int getPerson_id() { return person_id; }

    public void setPerson_id(int person_id) { this.person_id = person_id; }

    public String getMatr_no() { return matr_no; }

    public void setMatr_no(String matr_no) { this.matr_no = matr_no; }

    public int getGroup_id() { return group_id; }

    public void setGroup_id(int group_id) { this.group_id = group_id; }

    public int getSemester_id() { return semester_id; }

    public void setSemester_id(int semester_id) { this.semester_id = semester_id; }

}