package models.exam;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import models.Student;

@DatabaseTable(tableName = ExamQuestion.TABLE_EXAM_QUESTION)
public class ExamQuestion {
    public static final String TABLE_EXAM_QUESTION = "exam_question";
    public static final String FIELD_QUESTION_ID = "question_id";
    public static final String FIELD_EXAM_ID = "exam_id";
    public static final String FIELD_QUESTION_STRING = "question_string";
    public static final String FIELD_ANSWER = "answer";
    public static final String FIELD_NOTE = "note";
    public static final String FIELD_SCORE = "score";
    public static final String FIELD_STUDENT = "student";

    @DatabaseField(columnName = FIELD_QUESTION_ID, generatedId = true)
    private int questionId;

    @DatabaseField(columnName = FIELD_EXAM_ID, foreign = true,
            columnDefinition = "integer references exam(exam_id) on delete cascade")
    private Exam exam;

    @DatabaseField(columnName = FIELD_QUESTION_STRING)
    private String questionString;

    @DatabaseField(columnName = FIELD_ANSWER)
    private String answer;

    @DatabaseField(columnName = FIELD_NOTE)
    private String note;

    @DatabaseField(columnName = FIELD_SCORE)
    private float score;

    @DatabaseField(columnName = FIELD_STUDENT, foreign = true,
            columnDefinition = "integer references student(student_id)")
    private Student student;


    public int getQuestionId() {
        return questionId;
    }

    public Exam getExam() {
        return exam;
    }

    public String getAnswer() {
        return answer;
    }

    public String getNote() {
        return note;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getQuestionString() {
        return questionString;
    }

    public void setQuestionString(String questionString) {
        this.questionString = questionString;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    /**
     *
     * @return The student this question is attributed to or <code>null</code> if it's a question for the entire group
     */
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof ExamQuestion)) {
            return false;
        }

        return ((ExamQuestion)other).getQuestionId() == this.getQuestionId();
    }

    @Override
    public String toString() {
        return this.getQuestionString();
    }
}
