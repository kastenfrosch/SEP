package models;

import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;
import java.io.Serializable;

@DatabaseTable(tableName = StudentModel.TABLE_NAME_STUDENT)
public class StudentModel implements Serializable {
    static final String TABLE_NAME_STUDENT = "student";
    private static final String FIELD_STUDENT_STUDENT_ID = "student_id";
    private static final String FIELD_STUDENT_MATR_NO = "matr_no";
    private static final String FIELD_STUDENT_FIRSTNAME = "firstname";
    private static final String FIELD_STUDENT_LASTNAME = "lastname";
    private static final String FIELD_STUDENT_EMAIL = "email";
    private static final String FIELD_STUDENT_GROUP_ID = "group_id";
    private static final String FIELD_STUDENT_SEMESTER_ID = "semester_id";

    @DatabaseField(generatedId = true, columnName = FIELD_STUDENT_STUDENT_ID)
    private int student_id;
    @DatabaseField(columnName = FIELD_STUDENT_MATR_NO)
    private String matr_no;
    @DatabaseField(columnName = FIELD_STUDENT_FIRSTNAME)
    private String firstname;
    @DatabaseField(columnName = FIELD_STUDENT_LASTNAME)
    private String lastname;
    @DatabaseField(columnName = FIELD_STUDENT_EMAIL)
    private String email;
    @DatabaseField(columnName = FIELD_STUDENT_GROUP_ID)
    private int group_id;
    @DatabaseField(columnName = FIELD_STUDENT_SEMESTER_ID)
    private int semester_id;

    public StudentModel() { }

    public StudentModel(int student_id, String matr_no, String firstname, String lastname, String email, int group_id, int semester_id) {
        this.student_id = student_id;
        this.matr_no = matr_no;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.group_id = group_id;
        this.semester_id = semester_id;
    }

    public int getStudent_id() { return student_id; }

    public void setStudent_id(int student_id) { this.student_id = student_id; }

    public String getMatr_no() { return matr_no; }

    public void setMatr_no(String matr_no) { this.matr_no = matr_no; }

    public String getFirstname() { return firstname; }

    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }

    public void setLastname(String lastname) { this.lastname = lastname; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public int getGroup_id() { return group_id; }

    public void setGroup_id(int group_id) { this.group_id = group_id; }

    public int getSemester_id() { return semester_id; }

    public void setSemester_id(int semester_id) { this.semester_id = semester_id; }

}