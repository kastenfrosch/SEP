package models;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = FavouriteGroupage.TABLE_FAV_GROUPAGE)
public class FavouriteGroupage {
    public static final String TABLE_FAV_GROUPAGE = "favourite_semester";
    public static final String FIELD_GROUPAGE_ID = "groupage_id";
    public static final String FIELD_USER_ID = "user_id";

    @DatabaseField(columnName = FIELD_GROUPAGE_ID, id = true, foreign = true, foreignAutoRefresh = true,
    columnDefinition = "integer not null references groupage(groupage_id)")
    private int groupageId;

    @DatabaseField(columnName = FIELD_USER_ID, id = true, foreign = true, foreignAutoRefresh = true,
    columnDefinition = "integer not null references \"user\"(user_id)")
    private int userId;

    public int getGroupageId() {
        return groupageId;
    }

    public void setGroupageId(int groupageId) {
        this.groupageId = groupageId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
