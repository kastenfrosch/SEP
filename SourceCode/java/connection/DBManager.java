package connection;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import models.*;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public class DBManager {


    private static DBManager instance;

    private Dao<Semester, String> semesterDao;
    private Dao<Groupage, Integer> groupageDao;
    private Dao<Group, Integer> groupDao;
    private Dao<Student, Integer> studentDao;
    private Dao<Person, Integer> personDao;
    private Dao<User, String> userDao;
    private Dao<Calendar, Integer> calendarDao;
    private Dao<CalendarEntry, Integer> calendarEntryDao;
    private Dao<ChatMessage, Integer> chatMessageDao;
    private Dao<Notepad, Integer> notepadDao;
    private Dao<Tardy, Integer> tardyDao;
    private Dao<InviteCode, String> inviteCodeDao;
    private Dao<FavouriteGroup, Integer> favouriteGroupDao;
    private Dao<FavouriteGroupage, Integer> favouriteGroupageDao;
    private Dao<FavouriteSemester, Integer> favouriteSemesterDao;
    private Dao<FavouriteStudent, Integer> favouriteStudentDao;

    private User loggedInUser = null;

    public static DBManager getInstance() throws SQLException {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    private DBManager() throws SQLException {
        String databaseUrl = "jdbc:postgresql://hakurei.trashprojects.moe:5432/sep";
        JdbcConnectionSource conn = new JdbcConnectionSource(databaseUrl);
        conn.setUsername("sep");
        conn.setPassword("ayy1mao");


        this.semesterDao = DaoManager.createDao(conn, Semester.class);
        this.groupageDao = DaoManager.createDao(conn, Groupage.class);
        this.groupDao = DaoManager.createDao(conn, Group.class);
        this.studentDao = DaoManager.createDao(conn, Student.class);
        this.personDao = DaoManager.createDao(conn, Person.class);
        this.userDao = DaoManager.createDao(conn, User.class);
        this.calendarDao = DaoManager.createDao(conn, Calendar.class);
        this.calendarEntryDao = DaoManager.createDao(conn, CalendarEntry.class);
        this.chatMessageDao = DaoManager.createDao(conn, ChatMessage.class);
        this.notepadDao = DaoManager.createDao(conn, Notepad.class);
        this.tardyDao = DaoManager.createDao(conn, Tardy.class);
        this.inviteCodeDao = DaoManager.createDao(conn, InviteCode.class);
        this.favouriteGroupDao = DaoManager.createDao(conn, FavouriteGroup.class);
        this.favouriteGroupageDao = DaoManager.createDao(conn, FavouriteGroupage.class);
        this.favouriteSemesterDao = DaoManager.createDao(conn, FavouriteSemester.class);
        this.favouriteStudentDao = DaoManager.createDao(conn, FavouriteStudent.class);
    }

    public Dao<Semester, String> getSemesterDao() {
        return semesterDao;
    }

    public Dao<Groupage, Integer> getGroupageDao() {
        return groupageDao;
    }

    public Dao<Group, Integer> getGroupDao() {
        return groupDao;
    }

    public Dao<Student, Integer> getStudentDao() {
        return studentDao;
    }

    public Dao<Person, Integer> getPersonDao() {
        return personDao;
    }

    public Dao<User, String> getUserDao() {
        return userDao;
    }

    public Dao<Calendar, Integer> getCalendarDao() {
        return calendarDao;
    }

    public Dao<ChatMessage, Integer> getChatMessageDao() {
        return chatMessageDao;
    }

    public Dao<CalendarEntry, Integer> getCalendarEntryDao() {
        return calendarEntryDao;
    }

    public Dao<Notepad, Integer> getNotepadDao() {return notepadDao;}

    public Dao<Tardy, Integer> getTardyDao() {
        return tardyDao;
    }

    public Dao<InviteCode, String> getInviteCodeDao(){return inviteCodeDao;}


    public Dao<FavouriteGroup, Integer> getFavouriteGroupDao() {
        return favouriteGroupDao;
    }

    public Dao<FavouriteGroupage, Integer> getFavouriteGroupageDao() {
        return favouriteGroupageDao;
    }

    public Dao<FavouriteSemester, Integer> getFavouriteSemesterDao() {
        return favouriteSemesterDao;
    }

    public Dao<FavouriteStudent, Integer> getFavouriteStudentDao() {
        return favouriteStudentDao;
    }

    public void setLoggedInUser(User user) {
        if (this.loggedInUser != null) {
            throw new IllegalStateException("Logged in user cannot be changed once set");
        }
        this.loggedInUser = user;
    }

    public User getLoggedInUser() {
        return this.loggedInUser;
    }
}
