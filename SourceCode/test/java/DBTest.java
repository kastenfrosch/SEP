import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import models.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.settings.Settings;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class DBTest {


    private DBManager db;


    private Person makePerson() {
        Person p = new Person();
        p.setFirstname("Test");
        p.setLastname("Tester");
        p.setEmail("test@example.com");
        return p;
    }

    private Semester makeSemester() {
        Semester s = new Semester();

        s.setId("SS1233");
        s.setDescription("TestSemester");

        return s;
    }

    private Groupage makeGroupage(Semester s) {
        Groupage g = new Groupage();
        g.setSemester(s);
        g.setDescription("TestGroupage");
        return g;
    }

    @Before
    public void setup() {
        try {
            Settings.load();
            this.db = DBManager.getInstance();
        } catch(SQLException ex) {
            throw new IllegalStateException("Unable to connect to database");
        }
    }

    @After
    public void cleanup() {

    }

    //Tests if semesters can be stored and retrieved to/from the database
    @Test
    public void testSemesterStorage() {

        Semester s = makeSemester();

        Dao<Semester, String> semesterDao = db.getSemesterDao();

        try {
            semesterDao.create(s);
            semesterDao.refresh(s);

            Semester r = semesterDao.queryForId(s.getId());

            assertNotNull(r);
            assertEquals(r.getId(), s.getId());
            assertEquals(r.getDescription(), s.getDescription());

            semesterDao.delete(s);

            r = semesterDao.queryForId(s.getId());
            assertNull(r);

        } catch(SQLException ex) {
            throw new IllegalStateException(ex);
        }
    }

    //Tests if persons can be stored and retrieved to/from the database
    @Test
    public void testPersonStorage() {

        Dao<Person, Integer> personDao = db.getPersonDao();

        try {
            Person p = makePerson();
            personDao.create(p);
            personDao.refresh(p);

            Person r = personDao.queryForId(p.getId());

            assertEquals(r.getFirstname(), p.getFirstname());
            assertEquals(r.getLastname(), p.getLastname());
            assertEquals(r.getEmail(), p.getEmail());
            assertNotEquals(r.getId(), 0);

            personDao.delete(p);

            r = personDao.queryForId(p.getId());
            assertNull(r);

        } catch(SQLException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Test
    public void testGroupageStorage() {
        Semester s = makeSemester();
        Groupage g = makeGroupage(s);

        try {
            db.getSemesterDao().create(s);
            db.getGroupageDao().create(g);

            Groupage r = db.getGroupageDao().queryForId(g.getId());

            assertEquals(r.getId(), g.getId());
            assertEquals(r.getDescription(), g.getDescription());
            assertEquals(r.getSemester(), g.getSemester());

            db.getGroupageDao().delete(g);

            r = db.getGroupageDao().queryForId(g.getId());
            assertNull(r);

            db.getSemesterDao().delete(s);
        } catch(SQLException ex) {

        }

    }
}
