package models;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import connection.DBManager;

import java.sql.SQLException;

public interface INotepadBridge {
    Notepad getNotepad();

    void setNotepad(Notepad notepad);

    static INotepadBridge create(INotepadEntity entity, Notepad notepad) throws SQLException {
        INotepadBridge bridge;
        if (entity instanceof Groupage) {
            bridge = new GroupageNotepad();
            ((GroupageNotepad) bridge).setGroupage((Groupage) entity);
            bridge.setNotepad(notepad);

        } else if (entity instanceof Group) {
            bridge = new GroupNotepad();
            ((GroupNotepad) bridge).setGroup((Group) entity);
            bridge.setNotepad(notepad);
        } else if (entity instanceof Student) {
            bridge = new StudentNotepad();
            ((StudentNotepad) bridge).setStudent((Student) entity);
            bridge.setNotepad(notepad);
        } else {
            throw new IllegalArgumentException("Unhandeled INotepadEntity: " + entity);
        }

        Dao dao = DaoManager.lookupDao(DBManager.getInstance().getCalendarDao().getConnectionSource(), bridge.getClass());
        if (dao != null) {
            dao.create(bridge);
            dao.refresh(bridge);
        }
        else
        {
            throw new IllegalArgumentException("Unable to lookup DAO for " + entity.getClass().toString());
        }

        return bridge;
    }
}
