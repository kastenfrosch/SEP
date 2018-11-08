package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName=User.TABLE_USER)
public class User {
    public static final String TABLE_USER = "user";
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_PERSON_ID = "person_id";
    public static final String FIELD_PASSWORD_HASH = "password_hash";
    public static final String FIELD_SALT = "salt";

    @DatabaseField(id = true, columnName = FIELD_USERNAME)
    private String username;

    @DatabaseField(columnName = FIELD_PASSWORD_HASH)
    private String passwordHash;


    //Note: The column defintions are currently constants. I have not found a way to have them generated yet.
    @DatabaseField(foreign=true, columnName=FIELD_PERSON_ID, foreignAutoRefresh = true, foreignAutoCreate = true,
    columnDefinition = "integer not null references person(person_id) on delete restrict")
    private Person person;

    @DatabaseField(columnName=FIELD_SALT)
    private String salt;

    public User() {}


    public User(String username, String passwordHash, String salt, Person person) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.person = person;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public String toString() {
        return this.getUsername();
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof User)) {
            return false;
        }

        return this.username.equals(((User) other).getUsername());
    }
}
