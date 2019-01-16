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

@DatabaseTable(tableName = Group.TABLE_GROUP)
public class Group implements INotepadEntity, ITreeItem {
    public static final String TABLE_GROUP = "group";
    public static final String FIELD_GROUP_ID = "group_id";
    public static final String FIELD_GROUP_NAME = "name";
    public static final String FIELD_GROUPAGE_ID = "groupage_id";
    public static final String FIELD_GITLAB_URL = "gitlab_url";

    @DatabaseField(generatedId = true, columnName = FIELD_GROUP_ID)
    private int id;

    @DatabaseField(columnName = FIELD_GROUP_NAME)
    private String name;

    //Note: The column defintions are currently constants. I have not found a way to have them generated yet.
    @DatabaseField(foreign = true, columnName = FIELD_GROUPAGE_ID, foreignAutoRefresh = true,
    columnDefinition = "integer not null references groupage(groupage_id)")
    private Groupage groupage;

    @DatabaseField(columnName = FIELD_GITLAB_URL)
    private String gitlabUrl;

    @ForeignCollectionField
    private ForeignCollection<Student> students;

    @ForeignCollectionField
    private ForeignCollection<GroupNotepad> notepads;

    public Group() {}

    public Group(String name, Groupage groupage) {
        this.name = name;
        this.groupage = groupage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Groupage getGroupage() {
        return groupage;
    }

    public void setGroupage(Groupage groupage) {
        this.groupage = groupage;
    }

    public int getId() {
        return id;
    }

    public String getGitlabUrl() {
        return gitlabUrl;
    }

    public void setGitlabUrl(String gitlabUrl) {
        this.gitlabUrl = gitlabUrl;
    }

    public ForeignCollection<Student> getStudents() {
        return students;
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
        return getStudents().stream().map(stu -> (ITreeItem) stu).collect(Collectors.toList());
    }

    @Override
    public String toString() {
    	return name;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Group)) {
            return false;
        }
        return this.id == ((Group) other).getId();
    }
}
