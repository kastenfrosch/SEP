package connection;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import models.GroupModel;
import models.GroupageModel;
import models.SemesterModel;
import com.j256.ormlite.dao.Dao;
import models.StudentModel;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBManager {


	private static Connection connection;
    private static DBManager instance;

    private Dao<SemesterModel, String> semesterDao;
    private Dao<GroupageModel, Integer> groupageDao;
    private Dao<GroupModel, Integer> groupDao;
    private Dao<StudentModel, String> studentDao;


    
    
    public static DBManager getInstance() throws SQLException {
        if(instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    private DBManager() throws SQLException {
        //TODO: Add connection source
        JdbcConnectionSource conn = null;
        
    	
    	// **this uses h2 but you can change it to match your database**
    	
      	String databaseUrl = "jdbc:postgresql://hakurei.trashprojects.moe:5432/sep";
   
    	// **create a connection source to our database**
    	conn =
    	     new JdbcConnectionSource(databaseUrl);
    	
    	conn	.setUsername("sep");
    	conn.setPassword("ayy1mao");

    	System.out.println("");
        
        
        
        this.semesterDao = DaoManager.createDao(conn, SemesterModel.class);
        this.groupageDao = DaoManager.createDao(conn, GroupageModel.class);
        this.groupDao = DaoManager.createDao(conn, GroupModel.class);
        this.studentDao = DaoManager.createDao(conn, StudentModel.class);
    }

    public Dao<SemesterModel, String> getSemesterDao() {
        return semesterDao;
    }

    public Dao<GroupageModel, Integer> getGroupageDao() {
        return groupageDao;
    }

    public Dao<GroupModel, Integer> getGroupDao() {
        return groupDao;
    }

    public Dao<StudentModel, String> getStudentDao() {
        return studentDao;
    }
}
