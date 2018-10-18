package models;

import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;
import java.io.Serializable;

@DatabaseTable(tableName = PersonModel.TABLE_NAME_PERSON) //, daoClass = personDao.class)
public class PersonModel implements Serializable {
    public static final String TABLE_NAME_PERSON = "person";
    private static final String FIELD_PERSON_PERSON_ID = "person_id";
    private static final String FIELD_PERSON_FIRSTNAME = "firstname";
    private static final String FIELD_PERSON_LASTNAME = "lastname";
    private static final String FIELD_PERSON_EMAIL = "email";

    @DatabaseField(generatedId = true, columnName = FIELD_PERSON_PERSON_ID)
    private int person_id;
    @DatabaseField(columnName = FIELD_PERSON_FIRSTNAME)
    private String firstname;
    @DatabaseField(columnName = FIELD_PERSON_LASTNAME)
    private String lastname;
    @DatabaseField(columnName = FIELD_PERSON_EMAIL)
    private String email;

    public PersonModel() { }

    public PersonModel(int person_id, String firstname, String lastname, String email) {
        this.person_id = person_id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public int getPerson_id() { return person_id; }

    public void setPerson_id(int person_id) { this.person_id = person_id; }

    public String getFirstname() { return firstname; }

    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }

    public void setLastname(String lastname) { this.lastname = lastname; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

}