package utils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import connection.DBManager;
import connection.PGNotificationHandler.NotificationChannel;
import models.*;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DBUtils {

    public static void dropTables(ConnectionSource conn) throws SQLException {
        TableUtils.dropTable(conn, NotepadHistory.class, true);
        TableUtils.dropTable(conn, CalendarExtraInfo.class, true);
        TableUtils.dropTable(conn, FavouriteSemester.class, true);
        TableUtils.dropTable(conn, FavouriteGroup.class, true);
        TableUtils.dropTable(conn, FavouriteGroupage.class, true);
        TableUtils.dropTable(conn, FavouriteStudent.class, true);
	    TableUtils.dropTable(conn, InviteCode.class, true);
        TableUtils.dropTable(conn, Tardy.class, true);
        TableUtils.dropTable(conn, GroupNotepad.class, true);
        TableUtils.dropTable(conn, GroupageNotepad.class, true);
        TableUtils.dropTable(conn, StudentNotepad.class, true);
        TableUtils.dropTable(conn, Notepad.class, true);
	    TableUtils.dropTable(conn, CalendarEntry.class, true);
	    TableUtils.dropTable(conn, Calendar.class, true);
        TableUtils.dropTable(conn, Student.class, true);
        TableUtils.dropTable(conn, Group.class, true);
        TableUtils.dropTable(conn, Groupage.class, true);
        TableUtils.dropTable(conn, User.class, true);
        TableUtils.dropTable(conn, Person.class, true);
        TableUtils.dropTable(conn, Semester.class, true);
        TableUtils.dropTable(conn, ChatMessage.class, true);
    }

    public static void createTables(ConnectionSource conn) throws SQLException {
        TableUtils.createTable(conn, Semester.class);
        TableUtils.createTable(conn, Person.class);
        TableUtils.createTable(conn, User.class);
        TableUtils.createTable(conn, Groupage.class);
        TableUtils.createTable(conn, Group.class);
        TableUtils.createTable(conn, Student.class);
        TableUtils.createTable(conn, Calendar.class);
        TableUtils.createTable(conn, CalendarEntry.class);
        TableUtils.createTable(conn, ChatMessage.class);
        TableUtils.createTable(conn, Tardy.class);
        TableUtils.createTable(conn, Notepad.class);
        TableUtils.createTable(conn, GroupNotepad.class);
        TableUtils.createTable(conn, GroupageNotepad.class);
        TableUtils.createTable(conn, StudentNotepad.class);
        TableUtils.createTable(conn, InviteCode.class);
        TableUtils.createTable(conn, FavouriteSemester.class);
        TableUtils.createTable(conn, FavouriteGroup.class);
        TableUtils.createTable(conn, FavouriteGroupage.class);
        TableUtils.createTable(conn, FavouriteStudent.class);
        TableUtils.createTable(conn, CalendarExtraInfo.class);
        TableUtils.createTable(conn, NotepadHistory.class);
    }

    public static void createTriggers() throws SQLException{
	    //This function is, currently, way too complicated.

        var dao = DBManager.getInstance().getGroupageDao();

        Map<NotificationChannel, List<Class>> classMap = new HashMap<>();

        for(var channel : NotificationChannel.values()) {
            classMap.put(channel, new LinkedList<>());
        }

        List<Class> dataChannelClasses = classMap.get(NotificationChannel.DATA);
        dataChannelClasses.add(Group.class);
        dataChannelClasses.add(Groupage.class);
        dataChannelClasses.add(Person.class);
        dataChannelClasses.add(Semester.class);
        dataChannelClasses.add(Student.class);
        dataChannelClasses.add(User.class);
        dataChannelClasses.add(FavouriteSemester.class);
        dataChannelClasses.add(FavouriteGroup.class);
        dataChannelClasses.add(FavouriteGroupage.class);
        dataChannelClasses.add(FavouriteStudent.class);

        List<Class> chatChannelClasses = classMap.get(NotificationChannel.CHAT);
        chatChannelClasses.add(ChatMessage.class);


        String triggerSql = "CREATE TRIGGER notification_%s AFTER INSERT OR UPDATE OR DELETE ON \"%s\" EXECUTE PROCEDURE %s();";

        for(var channel : NotificationChannel.values()) {
            dao.executeRaw(channel.getTriggerFunctionSQL());
            for(var clazz : classMap.get(channel)) {
                String tableName = getTableName(clazz);
                dao.executeRaw(String.format(triggerSql, tableName, tableName, channel.getProcedureName()));
            }
        }

    }

    private static String getTableName(Class c) {
	    for(var field : c.getDeclaredFields()) {
	        if(field.getName().matches("TABLE_.+")) {
	            try {
                    return (String) field.get(null);
                } catch(IllegalAccessException e) {
                    throw new IllegalArgumentException("Unable to access %_NAME property of object.", e);
                }
            }
        }
        throw new IllegalArgumentException("Unable to access %_NAME property of object.");
    }

    public static void resetDB(ConnectionSource conn, boolean withDummyData) throws SQLException, IOException {
	    dropTables(conn);
	    createTables(conn);
	    if(withDummyData) {
            insertDummyData(conn);
        }
        createTriggers();
    }

    public static void insertDummyData(ConnectionSource conn) throws SQLException {

        DBManager manager = DBManager.getInstance();

        Semester WS1819 = new Semester("WS18/19", "Wintersemester 2018/2019");
        Semester SS19 = new Semester("SS19", "Sommersemester 2019");
        Semester WS1920 = new Semester("WS19/20", "Wintersemester 2019/2020");
        Semester SS20 = new Semester("SS20", "Sommersemester 2020");

        Dao<Semester, String> semesterDao = manager.getSemesterDao();
        semesterDao.create(WS1819);
        semesterDao.create(SS19);
        semesterDao.create(WS1920);
        semesterDao.create(SS20);

        Groupage one = new Groupage("Klasse A", WS1819);
        Groupage two = new Groupage("Klasse B", SS19);
        Groupage three = new Groupage("Klasse C", WS1920);
        Groupage four = new Groupage("Klasse D", SS20);

        Dao<Groupage, Integer> groupageDao = manager.getGroupageDao();
        groupageDao.create(one);
        groupageDao.create(two);
        groupageDao.create(three);
        groupageDao.create(four);

        Group g1 = new Group("Gruppe A", one);
        Group g2 = new Group("Gruppe B", one);
        Group g3 = new Group("Gruppe C", two);
        Group g4 = new Group("Gruppe D", two);
        Group g5 = new Group("Gruppe E", three);
        Group g6 = new Group("Gruppe F", three);
        Group g7 = new Group("Gruppe G", four);
        Group g8 = new Group("Gruppe H", four);

        Dao<Group, Integer> groupDao = manager.getGroupDao();
        groupDao.create(g1);
        groupDao.create(g2);
        groupDao.create(g3);
        groupDao.create(g4);
        groupDao.create(g5);
        groupDao.create(g6);
        groupDao.create(g7);
        groupDao.create(g8);

        Person p1 = new Person("Aaron", "Arendt", "hans.elektriker@example.com");
        Person p2 = new Person("Berta", "Bregen", "dieter.re@example.com");
        Person p3 = new Person("Charlie", "Chaplin", "1nice.bier@example.com");
        Person p4 = new Person("Dieter", "Dickens", "viereck@example.com");
        Person p5 = new Person("Egon", "Emmers", "djeetapeek@example.com");
        Person p6 = new Person("Franz", "Ferdinand", "nonexistent@example.com");
        Person p7 = new Person("Günther", "Gras", "downinafrica@example.com");
        Person p8 = new Person("Heinz", "Heine", "meep@example.com");
        Person p9 = new Person("Ilsa", "Icke", "bigsord@example.com");
        Person p10 = new Person("Joachim", "Jauch", "sama@example.com");
        Person p11 = new Person("Kalle", "Kanders", "know@example.com");
        Person p12 = new Person("Linda", "Lustig", "iliketrains@example.com");
        Person p13 = new Person("Montag", "Morgen", "event@example.com");
        Person p14 = new Person("Nina", "Nini", "ogre@example.com");

        Dao<Person, Integer> personDao = manager.getPersonDao();
        personDao.create(p1);
        personDao.create(p2);
        personDao.create(p3);
        personDao.create(p4);
        personDao.create(p5);
        personDao.create(p6);
        personDao.create(p7);
        personDao.create(p8);
        personDao.create(p9);
        personDao.create(p10);
        personDao.create(p11);
        personDao.create(p12);
        personDao.create(p13);
        personDao.create(p14);

        User u1 = new User("besttutor", "7A2A74526720526A0E438D027A6AD7B6", "E9506988FBCBBD1414970314455E9091", p12);
        User u2 = new User("moretutor", "7A2A74526720526A0E438D027A6AD7B6", "E9506988FBCBBD1414970314455E9091", p14);

        Dao<User, String> userDao = manager.getUserDao();
        userDao.create(u1);
        userDao.create(u2);

        Student s1 = new Student("0000001", p1, g1);
        Student s2 = new Student("0000002", p2, g1);
        Student s3 = new Student("0000003", p3, g1);
        Student s4 = new Student("0000004", p4, g1);
        Student s5 = new Student("0000005", p5, g2);
        Student s6 = new Student("0000006", p6, g2);
        Student s7 = new Student("0000007", p7, g3);
        Student s8 = new Student("0000008", p8, g4);
        Student s9 = new Student("0000009", p9, g5);
        Student s10 = new Student("0000010", p10, g6);
        Student s11 = new Student("0000011", p11, g7);
        Student s12 = new Student("0000012", p13, g8);

        Dao<Student, Integer> studentDao = manager.getStudentDao();
        studentDao.create(s1);
        studentDao.create(s2);
        studentDao.create(s3);
        studentDao.create(s4);
        studentDao.create(s5);
        studentDao.create(s6);
        studentDao.create(s7);
        studentDao.create(s8);
        studentDao.create(s9);
        studentDao.create(s10);
        studentDao.create(s11);
        studentDao.create(s12);

        Dao<InviteCode, String> inviteCodeDao = manager.getInviteCodeDao();
        InviteCode i1 = new InviteCode();
        inviteCodeDao.create(i1);


        ChatMessage cm1 = new ChatMessage();
        cm1.setContent("Standard testnachricht\n");
        cm1.setSender(u1);
        cm1.setReceiver(u1);
        cm1.setTime(LocalDateTime.now());

        ChatMessage cm2 = new ChatMessage();
        cm2.setContent("Standard Antwort\r\n");
        cm2.setSender(u1);
        cm2.setReceiver(u1);
        cm2.setTime(LocalDateTime.now());

        Dao<ChatMessage, Integer> cmDao = manager.getChatMessageDao();
        cmDao.create(cm1);
        cmDao.create(cm2);

        Calendar c = new Calendar();
        c.setCalendarType(Calendar.CalendarType.WEEK);
        c.setSemester(SS19);
        c.setGroupage(one);

        Dao<Calendar, Integer> calendarDao = manager.getCalendarDao();
        calendarDao.create(c);

        CalendarEntry ce = new CalendarEntry();
        ce.setStartTime(8);
        ce.setDayOfWeek(DayOfWeek.MONDAY);
        ce.setCalendar(c);
        ce.setDescription("TestEintrag");
        ce.setStartTime(LocalDateTime.of(2018, 11, 29, 12, 0));


        Dao<CalendarEntry, Integer> ceDao = manager.getCalendarEntryDao();
        ceDao.create(ce);

        InviteCode code = new InviteCode();
        Dao<InviteCode, String> inviteDao = manager.getInviteCodeDao();
        inviteDao.create(code);


        Tardy t1 = new Tardy();
        t1.setStudent(s1);
        t1.setTimeMissed(15);
        t1.setDateMissed(ce);

        Tardy t2 = new Tardy();
        t2.setStudent(s1);
        t2.setTimeMissed(60);
        t2.setDateMissed(ce);

        Dao<Tardy, Integer> tardyDao = manager.getTardyDao();

        tardyDao.create(t1);
        tardyDao.create(t2);

    }
}
