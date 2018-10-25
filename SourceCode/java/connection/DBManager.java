package connection;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import models.*;
import com.j256.ormlite.dao.Dao;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBManager {


	private static Connection connection;
    private static DBManager instance;

    private Dao<Semester, String> semesterDao;
    private Dao<Groupage, Integer> groupageDao;
    private Dao<Group, Integer> groupDao;
    private Dao<Student, Integer> studentDao;
    private Dao<Person, Integer> personDao;
    private Dao<User, String> userDao;

    
    
    public static DBManager getInstance() throws SQLException {
        if(instance == null) {
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
}
