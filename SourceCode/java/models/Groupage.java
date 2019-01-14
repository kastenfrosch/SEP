package models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import controller.mail.ITreeItem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@DatabaseTable(tableName=Groupage.TABLE_GROUPAGE)
public class Groupage implements INotepadEntity, ITreeItem {
    public static final String FIELD_GROUPAGE_ID = "groupage_id";
    public static final String TABLE_GROUPAGE = "groupage";
    public static final String FIELD_GROUPAGE_DESCRIPTION = "description";
    public static final String FIELD_SEMESTER_ID = "semester_id";

    @DatabaseField(generatedId = true, columnName = FIELD_GROUPAGE_ID)
    private int id;

    @DatabaseField(columnName = FIELD_GROUPAGE_DESCRIPTION, canBeNull = false)
    private String description;

    //Note: The column defintions are currently constants. I have not found a way to have them generated yet.
    @DatabaseField(foreign = true, columnName = FIELD_SEMESTER_ID, foreignAutoRefresh = true,
    columnDefinition = "varchar not null references semester(semester_id) on delete restrict")
    private Semester semester;

    @ForeignCollectionField
    private ForeignCollection<Group> groups;

    @ForeignCollectionField
    private ForeignCollection<GroupageNotepad> notepads;


    public Groupage() {}

    /**
     * @deprecated use {@link Groupage#Groupage(String, Semester)} instead
     */
    @Deprecated
    public Groupage(String description, Semester semester) {
        this.description = description;
        this.semester = semester;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public List<INotepadBridge> getNotepads() {
        return new LinkedList<>(notepads);
    }

    @Override
    public String getEmail() {
        return "";
    }

    @Override
    public List<ITreeItem> getChildren() {
        return getGroups().stream().map(grp -> (ITreeItem) grp).collect(Collectors.toList());
    }

    @Override
    public String toString() {
    	return description;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Groupage)) {
            return false;
        }
        return this.getId() == ((Groupage) other).getId();
    }

    public ForeignCollection<Group> getGroups() {
        return groups;
    }
}
