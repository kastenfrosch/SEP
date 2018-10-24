package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName=User.TABLE_USER)
public class User {
    public static final String TABLE_USER = "user";
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_PERSON_ID = "person_id";
    public static final String FIELD_PASSWORD = "password";

    @DatabaseField(id = true, columnName = FIELD_USERNAME)
    private String username;

    @DatabaseField(columnName=FIELD_PASSWORD)
    private String password;

    @DatabaseField(foreign=true, columnName=FIELD_PERSON_ID)
    private Person person;

    public User() {}

    public User(String username, String password, Person person) {
        this.username = username;
        this.password = password;
        this.person = person;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
