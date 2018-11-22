package models;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = FavouriteStudent.TABLE_FAV_STUDENT)
public class FavouriteStudent {
    public static final String TABLE_FAV_STUDENT = "favourite_student";
    public static final String FIELD_FAVOURITE_ID = "favourite_id";
    public static final String FIELD_STUDENT_ID = "student_id";
    public static final String FIELD_USER_ID = "user_id";

    @DatabaseField(columnName = FIELD_FAVOURITE_ID, generatedId = true)
    private int id;

    @DatabaseField(columnName = FIELD_STUDENT_ID, foreign = true, foreignAutoRefresh = true,
            columnDefinition = "integer not null references student(student_id)")
    private int groupId;

    @DatabaseField(columnName = FIELD_USER_ID, foreign = true, foreignAutoRefresh = true,
            columnDefinition = "integer not null references \"user\"(user_id)")
    private int userId;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }
}
