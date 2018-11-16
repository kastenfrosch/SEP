package models;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = FavouriteGroup.TABLE_FAV_GROUP)
public class FavouriteGroup {
    public static final String TABLE_FAV_GROUP = "favourite_semester";
    public static final String FIELD_GROUP_ID = "group_id";
    public static final String FIELD_USER_ID = "user_id";

    @DatabaseField(columnName = FIELD_GROUP_ID, id = true, foreign = true, foreignAutoRefresh = true,
            columnDefinition = "integer not null references group(group_id)")
    private int groupId;

    @DatabaseField(columnName = FIELD_USER_ID, id = true, foreign = true, foreignAutoRefresh = true,
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
}
