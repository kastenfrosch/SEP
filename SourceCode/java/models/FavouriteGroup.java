package models;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = FavouriteGroup.TABLE_FAV_GROUP)
public class FavouriteGroup {
    public static final String TABLE_FAV_GROUP = "favourite_group";
    //group_id / user_id would be better but ormlite and multi-field keys dont like each other
    public static final String FIELD_FAVOURITE_ID = "favourite_id";
    public static final String FIELD_GROUP_ID = "group_id";
    public static final String FIELD_USER_ID = "user_id";

    @DatabaseField(columnName = FIELD_FAVOURITE_ID, generatedId = true)
    private int id;

    @DatabaseField(columnName = FIELD_GROUP_ID, foreign = true, foreignAutoRefresh = true,
            columnDefinition = "integer not null references \"group\"(group_id)")
    private Group group;

    @DatabaseField(columnName = FIELD_USER_ID, foreign = true, foreignAutoRefresh = true,
            columnDefinition = "varchar not null references \"user\"(username)")
    private User user;

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
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
