package models;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = FavouriteSemester.TABLE_FAV_SEMESTER)
public class FavouriteSemester {
    public static final String TABLE_FAV_SEMESTER = "favourite_semester";
    public static final String FIELD_FAVOURITE_ID = "favourite_id";
    public static final String FIELD_SEMESTER_ID = "semester_id";
    public static final String FIELD_USER_ID = "user_id";

    @DatabaseField(columnName = FIELD_FAVOURITE_ID, generatedId = true)
    private int id;

    @DatabaseField(columnName = FIELD_SEMESTER_ID,foreign = true, foreignAutoRefresh = true,
    columnDefinition = "integer not null references semester(semester_id)")
    private Semester semester;

    @DatabaseField(columnName = FIELD_USER_ID, foreign = true, foreignAutoRefresh = true,
    columnDefinition = "integer not null references \"user\"(user_id)")
    private User user;

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }
}
