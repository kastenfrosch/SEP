package models;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = FavouriteSemester.TABLE_FAV_SEMESTER)
public class FavouriteSemester {
    public static final String TABLE_FAV_SEMESTER = "favourite_semester";
    public static final String FIELD_SEMESTER_ID = "semester_id";
    public static final String FIELD_USER_ID = "user_id";

    @DatabaseField(columnName = FIELD_SEMESTER_ID, id = true, foreign = true, foreignAutoRefresh = true,
    columnDefinition = "integer not null references semester(semester_id)")
    private int semesterId;

    @DatabaseField(columnName = FIELD_USER_ID, id = true, foreign = true, foreignAutoRefresh = true,
    columnDefinition = "integer not null references \"user\"(user_id)")
    private int userId;

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
