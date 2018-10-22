package connection;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import models.GroupModel;
import models.GroupageModel;
import models.SemesterModel;
import com.j256.ormlite.dao.Dao;
import models.StudentModel;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

public class DBManager {



    private static DBManager instance;

    private Dao<SemesterModel, String> semesterDao;
    private Dao<GroupageModel, Integer> groupageDao;
    private Dao<GroupModel, Integer> groupDao;
    private Dao<StudentModel, String> studentDao;

    public static DBManager getInstance(Path path) throws SQLException {
        if(instance == null) {
            instance = new DBManager(path);
        }
        return instance;
    }

    private DBManager(Path path) throws SQLException {
        //TODO: Add connection source
        ConnectionSource conn = null;
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
