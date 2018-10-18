package models;

import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;
import java.io.Serializable;

@DatabaseTable(tableName = UserModel.TABLE_NAME_USER) //, daoClass = userDao.class)
public class UserModel implements Serializable {
    public static final String TABLE_NAME_USER = "user";
    private static final String FIELD_USER_USER_NAME = "user_name";
    private static final String FIELD_USER_PERSON_ID = "person_id";
    private static final String FIELD_USER_PASSWORD = "password";

    @DatabaseField(generatedId = true, columnName = FIELD_USER_USER_NAME)
    private String user_name;
    @DatabaseField(columnName = FIELD_USER_PERSON_ID)
    private int person_id;
    @DatabaseField(columnName = FIELD_USER_PASSWORD)
    private String password;

    public UserModel() { }

    public UserModel(String user_name, int person_id, String password) {
        this.user_name = user_name;
        this.person_id = person_id;
        this.password = password;
    }

    public String getUser_name() { return user_name; }

    public void setUser_name(String user_name) { this.user_name = user_name; }

    public int getPerson_id() { return person_id; }

    public void setPerson_id(int person_id) { this.person_id = person_id; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

}