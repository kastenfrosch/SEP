package models;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = FavouriteGroup.TABLE_FAV_GROUP)
public class FavouriteGroup {
    public static final String TABLE_FAV_GROUP = "favourite_semester";
    //group_id / user_id would be better but ormlite and multi-field keys dont like each other
    public static final String FIELD_FAVOURITE_ID = "favourite_id";
    public static final String FIELD_GROUP_ID = "group_id";
    public static final String FIELD_USER_ID = "user_id";

    @DatabaseField(columnName = FIELD_FAVOURITE_ID, generatedId = true)
    private int id;

    @DatabaseField(columnName = FIELD_GROUP_ID, foreign = true, foreignAutoRefresh = true,
            columnDefinition = "integer not null references group(group_id)")
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
