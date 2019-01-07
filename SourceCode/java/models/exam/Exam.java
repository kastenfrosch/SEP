package models.exam;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import models.User;

@DatabaseTable(tableName = Exam.TABLE_EXAM)
public class Exam {

    public static final String TABLE_EXAM = "exam";
    public static final String FIELD_EXAM_ID = "exam_id";
    public static final String FIELD_USER = "username";
    public static final String FIELD_DESCRIPTION = "description";

    @DatabaseField(columnName = FIELD_EXAM_ID, generatedId = true)
    private int examId;

    @DatabaseField(columnName = FIELD_USER, foreign = true, foreignAutoRefresh = false,
            columnDefinition = "varchar references \"user\"(username)")
    private User user;

    @DatabaseField(columnName = FIELD_DESCRIPTION)
    private String description;

    public int getExamId() {
        return examId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Exam)) {
            return false;
        }

        return ((Exam)other).getExamId() == this.getExamId();
    }

    @Override
    public String toString() {
        return this.description;
    }
}
