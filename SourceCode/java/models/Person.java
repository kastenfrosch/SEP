package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName=Person.TABLE_PERSON)
public class Person {
    public static final String TABLE_PERSON = "person";
    public static final String FIELD_PERSON_ID = "person_id";
    public static final String FIELD_FIRSTNAME = "firstname";
    public static final String FIELD_LASTNAME = "lastname";
    public static final String FIELD_EMAIL = "email";

    @DatabaseField(generatedId = true, columnName = FIELD_PERSON_ID)
    private int id;

    @DatabaseField(columnName=FIELD_FIRSTNAME)
    private String firstname;

    @DatabaseField(columnName=FIELD_LASTNAME)
    private String lastname;

    @DatabaseField(columnName=FIELD_EMAIL)
    private String email;

    public Person() {}

    public Person(String firstname, String lastname, String email) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return this.firstname + " " + this.lastname;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Person)) {
            return false;
        }

        return this.getId() == ((Person) other).getId();
    }
}
