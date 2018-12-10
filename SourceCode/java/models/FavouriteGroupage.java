package models;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = FavouriteGroupage.TABLE_FAV_GROUPAGE)
public class FavouriteGroupage {
    public static final String TABLE_FAV_GROUPAGE = "favourite_groupage";
    //same as in FavouriteGroup
    public static final String FIELD_FAVOURITE_ID = "favourite_id";
    public static final String FIELD_GROUPAGE_ID = "groupage_id";
    public static final String FIELD_USER_ID = "user_id";

    @DatabaseField(columnName = FIELD_FAVOURITE_ID, generatedId = true)
    private int id;

    @DatabaseField(columnName = FIELD_GROUPAGE_ID, foreign = true, foreignAutoRefresh = true,
    columnDefinition = "integer not null references groupage(groupage_id) on delete cascade")
    private Groupage groupage;

    @DatabaseField(columnName = FIELD_USER_ID, foreign = true, foreignAutoRefresh = true,
    columnDefinition = "varchar not null references \"user\"(username) on delete cascade")
    private User user;

    public Groupage getGroupage() {
        return groupage;
    }

    public void setGroupage(Groupage groupage) {
        this.groupage = groupage;
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
