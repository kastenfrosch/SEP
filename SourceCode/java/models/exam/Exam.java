package models.exam;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import models.Group;
import models.User;

@DatabaseTable(tableName = Exam.TABLE_EXAM)
public class Exam {

    public static final String TABLE_EXAM = "exam";
    public static final String FIELD_EXAM_ID = "exam_id";
    public static final String FIELD_GROUP = "group_id";
    public static final String FIELD_DESCRIPTION = "description";

    @DatabaseField(columnName = FIELD_EXAM_ID, generatedId = true)
    private int examId;

    @DatabaseField(columnName = FIELD_GROUP, foreign = true, foreignAutoRefresh = true,
            columnDefinition = "integer references \"group\"(group_id) on delete cascade")
    private Group group;

    @DatabaseField(columnName = FIELD_DESCRIPTION)
    private String description;

    @ForeignCollectionField
    private ForeignCollection<ExamQuestion> questions;

    public int getExamId() {
        return examId;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ForeignCollection<ExamQuestion> getQuestions() {
        return questions;
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
