package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName=Student.TABLE_STUDENT)
public class Student {
    public static final String TABLE_STUDENT = "student";
    public static final String FIELD_STUDENT_ID = "student_id";
    public static final String FIELD_MATR_NO = "mat_no";
    public static final String FIELD_PERSON_ID = "person_id";
    public static final String FIELD_SEMESTER_ID = "semester_id";
    public static final String FIELD_GROUP_ID = "group_id";

    @DatabaseField(generatedId = true, columnName = FIELD_STUDENT_ID)
    private int id;

    @DatabaseField(columnName = FIELD_MATR_NO)
    private String matrNo;


    //Note: The column defintions are currently constants. I have not found a way to have them generated yet.
    @DatabaseField(foreign = true, columnName = FIELD_PERSON_ID, foreignAutoRefresh = true,
    columnDefinition = "integer references person(person_id) on delete restrict")
    private Person person;

    @DatabaseField(foreign = true, columnName = FIELD_SEMESTER_ID, foreignAutoRefresh = true,
    columnDefinition = "varchar references semester(semester_id) on delete restrict")
    private Semester semester;

    @DatabaseField(foreign = true, columnName = FIELD_GROUP_ID, foreignAutoRefresh = true,
    columnDefinition = "integer references \"group\"(group_id) on delete restrict")
    private Group group;

    public Student() {}

    public Student(String matrNo, Person person, Group group, Semester semester) {
        this.matrNo = matrNo;
        this.person = person;
        this.semester = semester;
        this.group = group;
    }

    public String getMatrNo() {
        return matrNo;
    }

    public void setMatrNo(String matrNo) {
        this.matrNo = matrNo;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
    	return person.toString();
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Student)) {
            return false;
        }

        return this.getId() == ((Student)other).getId();
    }
}
