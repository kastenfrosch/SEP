package models;

import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * @deprecated
 * use {@link User} instead
 */
@Deprecated
@DatabaseTable(tableName = UserModel.TABLE_NAME_USER) //, daoClass = userDao.class)
public class UserModel implements Serializable {
    public static final String TABLE_NAME_USER = "user";
    private static final String FIELD_USER_USER_NAME = "user_name";
    private static final String FIELD_USER_PERSON_ID = "person_id";
    private static final String FIELD_USER_PASSWORD = "password";

    @DatabaseField(id = true, columnName = FIELD_USER_USER_NAME)
    private String user_name;
    @DatabaseField(columnName = FIELD_USER_PERSON_ID, foreign = true, foreignAutoRefresh = true, foreignColumnName = "person_id")
    private PersonModel person;
    @DatabaseField(columnName = FIELD_USER_PASSWORD)
    private String password;

    public UserModel() {
    }

    public UserModel(String user_name, PersonModel person, String password) {
        this.user_name = user_name;
        this.person = person;
        this.password = password;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public PersonModel getPerson() {
        return person;
    }

    public void setPerson_id(PersonModel person_id) {
        this.person = person;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}