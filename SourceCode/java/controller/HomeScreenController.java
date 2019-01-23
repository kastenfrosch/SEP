package controller;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import connection.DBManager;
import connection.PGNotificationHandler;
import controller.alt.NoteViewController;
import controller.alt.WeekCalendarController;
import controller.form.basic.*;
import controller.form.calendar.SemesterplanController;
import controller.form.notepad.NotesTabController;
import controller.gitlab.GitlabChartController;
import controller.gitlab.GitlabLoginController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import modal.ConfirmationModal;
import modal.InfoModal;
import models.*;
import org.gitlab4j.api.GitLabApi;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class HomeScreenController {

    private boolean isRunning = false;
    private DBManager dbm;
    private User user;
    private Node treeViewRoot;
    private Node selectedNode;
    private Tab selectedTab;
    private PGNotificationHandler handler;
    private List<Semester> semesterList;
    private List<Groupage> groupageList;
    private List<Group> groupList;
    private List<Student> studentList;

    @FXML
    private TreeView<Object> treeView;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab detailsTab;

    @FXML
    private Tab notesTab;

    @FXML
    private Tab weekPlanTab;

    @FXML
    private Tab semesterPlanTab;

    @FXML
    private Tab gitlabTab;

    @FXML
    void onDeleteButtonClicked(ActionEvent event) {
        if (treeView.getSelectionModel().isEmpty()) {
            InfoModal.show("Bitte wählen Sie ein Element aus.");
        } else {
            Node selectedItem = (Node) treeView.getSelectionModel().getSelectedItem();

            if (ConfirmationModal.show("Warnung", null, "Soll das ausgewählte Element und ggf. seine untergeordneten Elemente wirklich gelöscht werden?")) {
                selectedItem.deleteNode();
            }
        }
    }

    @FXML
    void onAddGroupageButtonClicked(ActionEvent event) {
        tabPane.getSelectionModel().select(detailsTab);
        FXMLLoader l = SceneManager.getInstance().getLoaderForScene(SceneType.CREATE_GROUPAGE);
        CreateGroupageController ctrl = l.getController();
        ctrl.initialize();
        selectedTab.setContent(l.getRoot());
    }

    @FXML
    void onAddGroupButtonClicked(ActionEvent event) {
        tabPane.getSelectionModel().select(detailsTab);

        FXMLLoader l = SceneManager.getInstance().getLoaderForScene(SceneType.CREATE_GROUP);
        ((CreateGroupController) l.getController()).initialize();
        selectedTab.setContent(l.getRoot());
    }

    @FXML
    void onAddSemesterButtonClicked(ActionEvent event) {
        tabPane.getSelectionModel().select(detailsTab);
        selectedTab.setContent(SceneManager.getInstance().getLoaderForScene(SceneType.CREATE_SEMESTER).getRoot());
    }

    @FXML
    void onAddStudentButtonClicked(ActionEvent event) {
        tabPane.getSelectionModel().select(detailsTab);
        FXMLLoader l = SceneManager.getInstance().getLoaderForScene(SceneType.CREATE_STUDENT);
        ((CreateStudentController) l.getController()).initialize();
        selectedTab.setContent(l.getRoot());
    }

    @FXML
    void onChatButtonClicked(ActionEvent event) {
        SceneManager.getInstance().showInNewWindow(SceneType.CHAT_WINDOW);
    }

    @FXML
    void onUserAdministrationButtonClicked(ActionEvent event) {
        SceneManager.getInstance().showInNewWindow(SceneType.USER_ADMIN);
    }

    @FXML
    void onLogoutButtonClicked(ActionEvent event) {
        try {
            if (!treeView.getSelectionModel().isEmpty()) {
                user.setLastItem(((Node) treeView.getSelectionModel().getSelectedItem()).getUniqueId());
            }
            user.setLastTab(tabPane.getSelectionModel().getSelectedItem().getId());
            dbm.getUserDao().update(user);

            ConnectionSource conn = dbm.getFavouriteSemesterDao().getConnectionSource();
            dbm.getFavouriteGroupageDao().deleteBuilder().where().eq(FavouriteGroupage.FIELD_USER_ID, user.getUsername());
            dbm.getFavouriteGroupDao().deleteBuilder().where().eq(FavouriteGroup.FIELD_USER_ID, user.getUsername());
            dbm.getFavouriteSemesterDao().deleteBuilder().where().eq(FavouriteSemester.FIELD_USER_ID, user.getUsername());
            dbm.getFavouriteStudentDao().deleteBuilder().where().eq(FavouriteStudent.FIELD_USER_ID, user.getUsername());

            ArrayList<Node> expandedNodes = getExpandedNodes();
            for (Node tmp : expandedNodes) {
                if (tmp.getValue() instanceof Semester) {
                    FavouriteSemester fav = new FavouriteSemester();
                    fav.setUser(user);
                    fav.setSemester((Semester) tmp.getValue());
                    dbm.getFavouriteSemesterDao().createIfNotExists(fav);
                } else if (tmp.getValue() instanceof Groupage) {
                    FavouriteGroupage fav = new FavouriteGroupage();
                    fav.setUser(user);
                    fav.setGroupage((Groupage) tmp.getValue());
                    dbm.getFavouriteGroupageDao().createIfNotExists(fav);
                } else if (tmp.getValue() instanceof Group) {
                    FavouriteGroup fav = new FavouriteGroup();
                    fav.setUser(user);
                    fav.setGroup((Group) tmp.getValue());
                    dbm.getFavouriteGroupDao().createIfNotExists(fav);
                } else {
                    FavouriteStudent fav = new FavouriteStudent();
                    fav.setUser(user);
                    fav.setStudent((Student) tmp.getValue());
                    dbm.getFavouriteStudentDao().createIfNotExists(fav);
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SceneManager.getInstance().closeWindow(SceneType.HOME);
        //TODO: temp fix, window doesn't closee
        Platform.exit();
    }

    @FXML
    public void initialize() {
        selectedTab = detailsTab;

        getData();
        drawTreeView();

        treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        treeView.getSelectionModel().selectedItemProperty().addListener((ov, oldNode, newNode) -> {
            selectedNode = (Node) newNode;
            showTabContent();
        });

        tabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            selectedTab = newTab;
            showTabContent();
        });

        Callable c = () -> {
            Platform.runLater(() -> {
                getData();
                drawTreeView();
            });
            return null;
        };
        PGNotificationHandler.getInstance().registerListener(PGNotificationHandler.NotificationChannel.DATA, c);
    }

    void getData() {
        try {
            dbm = DBManager.getInstance();
            semesterList = dbm.getSemesterDao().queryForAll();
            groupageList = dbm.getGroupageDao().queryForAll();
            groupList = dbm.getGroupDao().queryForAll();
            studentList = dbm.getStudentDao().queryForAll();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        user = dbm.getLoggedInUser();
    }

    public void showTabContent() {
        if (selectedNode != null) {
            SceneManager sm = SceneManager.getInstance();
            SceneType sceneType = null;
            switch (selectedTab.getId()) {
                case "notesTab":
                    if (System.getProperty("sep.alt.notepad") != null) {
                        sceneType = SceneType.NOTEPAD_VIEW;
                        if (selectedNode.getValue() instanceof Semester) {
                            Text selectNotification = new Text("Bitte wählen Sie ein anderes Element links aus der Baumstruktur.");
                            selectedTab.setContent(selectNotification);
                            return;
                        }
                        sm.getLoaderForScene(sceneType).<NoteViewController>getController()
                                .setEntity((INotepadEntity) selectedNode.getValue());
                    } else {
                        sceneType = SceneType.NOTESTAB_WINDOW;
                        if (selectedNode.getValue() instanceof Semester) {
                            Text selectNotification = new Text("Bitte wählen Sie ein anderes Element links aus der Baumstruktur.");
                            selectedTab.setContent(selectNotification);
                            return;
                        }
                        sm.getLoaderForScene(sceneType).<NotesTabController>getController()
                                .setObject(selectedNode.getValue());
                    }
                    break;
                case "weekPlanTab":
                    if (System.getProperty("sep.alt.weekcalendar") == null) {
                        if (selectedNode.getValue() instanceof Groupage) {
                            sceneType = SceneType.WEEK_CALENDAR;
                            sm.getLoaderForScene(sceneType).<WeekCalendarController>getController()
                                    .setGroupage((Groupage) selectedNode.getValue());
                        } else {
                            Text selectNotification = new Text("Bitte wählen Sie eine Klasse links aus der Baumstruktur.");
                            selectedTab.setContent(selectNotification);
                            return;
                        }
                    } else {
                        sceneType = SceneType.TIMETABLE_WINDOW;
                    }
                    break;
                case "semesterPlanTab":
                    if (selectedNode.getValue() instanceof Semester) {
                        sceneType = SceneType.EDIT_AND_CREATE_SEMESTERPLAN;
                        sm.getLoaderForScene(sceneType).<SemesterplanController>getController()
                                .setSemester((Semester) selectedNode.getValue());
                    } else {
                        Text selectNotification = new Text("Bitte wählen Sie ein Semester links aus der Baumstruktur.");
                        selectedTab.setContent(selectNotification);
                        return;
                    }
                    break;
                case "gitlabTab":
                    GitLabApi api = sm.getLoaderForScene(SceneType.GITLAB_LOGIN)
                            .<GitlabLoginController>getController()
                            .getApi();
                    if (api == null) {
                        sceneType = SceneType.GITLAB_LOGIN;
                    } else {
                        if (selectedNode.getValue() instanceof Semester) {
                            Text notification = new Text("Bitte wählen Sie ein anderes Element aus der Baumstruktur.");
                            selectedTab.setContent(notification);
                        } else {
                            sm.getLoaderForScene(SceneType.GITLAB_CHART_VIEW)
                                    .<GitlabChartController>getController()
                                    .setValues(selectedNode.getValue(), api);
                            sceneType = SceneType.GITLAB_CHART_VIEW;
                        }
                    }

                    break;
                default:
                    if (selectedNode.getValue() instanceof Semester) {
                        sceneType = SceneType.EDIT_SEMESTER;
                        sm.getLoaderForScene(sceneType).<EditSemesterController>getController()
                                .setSemester((Semester) selectedNode.getValue());

                    } else if (selectedNode.getValue() instanceof Groupage) {
                        sceneType = SceneType.EDIT_GROUPAGE;
                        sm.getLoaderForScene(sceneType).<EditGroupageController>getController()
                                .setGroupage((Groupage) selectedNode.getValue());

                        CreateGroupageController createGroupageController = SceneManager.getInstance()
                                .getLoaderForScene(SceneType.CREATE_GROUPAGE).getController();
                        createGroupageController.initialize();

                        EditGroupageController editGroupageController = SceneManager.getInstance()
                                .getLoaderForScene(SceneType.EDIT_GROUPAGE).getController();
                        editGroupageController.initialize();

                    } else if (selectedNode.getValue() instanceof Group) {
                        sceneType = SceneType.EDIT_GROUP;
                        sm.getLoaderForScene(sceneType).<EditGroupController>getController()
                                .setGroup((Group) selectedNode.getValue());

                        CreateGroupController createGroupController = SceneManager.getInstance()
                                .getLoaderForScene(SceneType.CREATE_GROUP).getController();
                        createGroupController.initialize();

                        EditGroupController editGroupController = SceneManager.getInstance()
                                .getLoaderForScene(SceneType.EDIT_GROUP).getController();
                        editGroupController.initialize();
                    } else {
                        sceneType = SceneType.EDIT_STUDENT;
                        sm.getLoaderForScene(sceneType).<EditStudentController>getController()
                                .setStudent((Student) selectedNode.getValue());

                        CreateStudentController createStudentController = SceneManager.getInstance()
                                .getLoaderForScene(SceneType.CREATE_STUDENT).getController();
                        createStudentController.initialize();

                        EditStudentController editStudentController = SceneManager.getInstance()
                                .getLoaderForScene(SceneType.EDIT_STUDENT).getController();
                        editStudentController.initialize();
                    }
                    break;
            }
            //TODO: temp fix
            Parent p = sm.getLoaderForScene(sceneType).getRoot();
            p.maxWidth(980);
            selectedTab.setContent(sm.getLoaderForScene(sceneType).getRoot());
        } else {
            Text selectNotification = new Text("Bitte wählen Sie ein Element links aus der Baumstruktur.");
            selectedTab.setContent(selectNotification);
        }
    }

    void drawTreeView() {
        ArrayList<Node> expandedNodes = null;
        expandedNodes = getExpandedNodes();

        Node selectedNodeTmp = selectedNode;

        treeViewRoot = new Node();

        for (Semester sem : semesterList) {
            Node semesterNode = new Node(sem);
            treeViewRoot.getChildren().add(semesterNode);
            for (Groupage groupage : groupageList) {
                Node groupageNode = new Node(groupage);
                if (groupage.getSemester().getId().equals(sem.getId())) {
                    semesterNode.getChildren().add(groupageNode);
                    for (Group group : groupList) {
                        Node groupNode = new Node(group);
                        if (group.getGroupage().getId() == groupage.getId()) {
                            groupageNode.getChildren().add(groupNode);
                            for (Student student : studentList) {
                                if (student.getGroup().getId() == group.getId()) {
                                    Node studentNode = new Node(student);
                                    groupNode.getChildren().add(studentNode);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (expandedNodes != null) {
            treeViewRoot.expandChildren(expandedNodes);
        }

        if (!isRunning) {
            if (user != null) {

                String lastTab = user.getLastTab();
                for (Tab tab : tabPane.getTabs()) {
                    if (tab.getId().equals(lastTab)) {
                        tabPane.getSelectionModel().select(tab);
                    }
                }

                selectedNodeTmp = treeViewRoot.getNode(user.getLastItem());

                isRunning = true;
            }
        } else {
            if (selectedNodeTmp != null) {
                selectedNodeTmp = treeViewRoot.getNode(selectedNodeTmp.getUniqueId());
            }
        }

        treeView.setShowRoot(false);
        treeView.setRoot(treeViewRoot);

        treeView.getSelectionModel().select(selectedNodeTmp);
    }

    ArrayList<Node> getExpandedNodes() {
        if (isRunning) {
            return treeViewRoot.getExpandedChildren();
        } else {
            List<FavouriteSemester> favSemesterList = null;
            List<FavouriteGroupage> favGroupageList = null;
            List<FavouriteGroup> favGroupList = null;
            List<FavouriteStudent> favStudentList = null;
            ArrayList<Node> favNodes = new ArrayList<>();
            if (user != null) {
                try {
                    favSemesterList = dbm.getFavouriteSemesterDao().queryForEq(FavouriteSemester.FIELD_USER_ID, user.getUsername());
                    favGroupageList = dbm.getFavouriteGroupageDao().queryForEq(FavouriteGroupage.FIELD_USER_ID, user.getUsername());
                    favGroupList = dbm.getFavouriteGroupDao().queryForEq(FavouriteGroup.FIELD_USER_ID, user.getUsername());
                    favStudentList = dbm.getFavouriteStudentDao().queryForEq(FavouriteStudent.FIELD_USER_ID, user.getUsername());
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                for (FavouriteSemester fav : favSemesterList) {
                    favNodes.add(new Node(fav.getSemester()));
                }
                for (FavouriteGroupage fav : favGroupageList) {
                    favNodes.add(new Node(fav.getGroupage()));
                }
                for (FavouriteGroup fav : favGroupList) {
                    favNodes.add(new Node(fav.getGroup()));
                }
                for (FavouriteStudent fav : favStudentList) {
                    favNodes.add(new Node(fav.getStudent()));
                }
            }
            return favNodes;
        }
    }

    public void setSelectedNode(Object obj) {
        selectedNode = new Node(obj);
    }

    public void refreshTabContent() {
        showTabContent();
    }

    public void onMailBtnClicked(ActionEvent event) {
        SceneManager.getInstance().showInNewWindow(SceneType.RECEIVE_MAIL);
    }

    public void onExamBtnClicked(ActionEvent actionEvent) {
        SceneManager.getInstance().showInNewWindow(SceneType.EXAM_GROUP);
    }
}

class Node extends TreeItem<Object> {

    Node() {
    }

    Node(Object obj) {
        super(obj);
    }

    ArrayList<Node> getExpandedChildren() {
        ArrayList<Node> expandedChildren = new ArrayList<>();
        expandedChildren = getExpandedChildren(expandedChildren);
        return expandedChildren;
    }

    ArrayList<Node> getExpandedChildren(ArrayList<Node> expandedChildren) {
        for (TreeItem<Object> child : getChildren()) {
            Node tmp = (Node) child;
            if (!tmp.isLeaf() && tmp.isExpanded()) {
                expandedChildren.add(tmp);
                tmp.getExpandedChildren(expandedChildren);
            }
        }
        return expandedChildren;
    }

    void expandChildren(List<Node> expandedChildren) {
        for (TreeItem<Object> child : getChildren()) {
            Node tmp = (Node) child;
            if (!tmp.isLeaf()) {
                for (Node expandedChild : expandedChildren) {
                    if (tmp.getUniqueId().equals(expandedChild.getUniqueId())) {
                        tmp.setExpanded(true);
                        tmp.expandChildren(expandedChildren);
                    }
                }
            }
        }
    }


    String getUniqueId() {
        if (getValue() instanceof Semester) {
            Semester tmp = (Semester) getValue();
            return "Semester: " + tmp.getId();
        } else if (getValue() instanceof Groupage) {
            Groupage tmp = (Groupage) getValue();
            return "Groupage: " + Integer.toString(tmp.getId());
        } else if (getValue() instanceof Group) {
            Group tmp = (Group) getValue();
            return "Group: " + Integer.toString(tmp.getId());
        } else {
            Student tmp = (Student) getValue();
            return "Student: " + Integer.toString(tmp.getId());
        }
    }

    Node getNode(String uniqueId) {
        for (TreeItem treeItem : this.getChildren()) {
            Node child = (Node) treeItem;
            Node result = getNode(child, uniqueId);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    Node getNode(Node node, String uniqueId) {
        if (node.getUniqueId().equals(uniqueId)) {
            return node;
        } else {
            return node.getNode(uniqueId);
        }
    }

    Semester getSemester() {
        if (getValue() instanceof Semester) {
            return (Semester) getValue();
        } else if (getValue() instanceof Groupage) {
            return ((Groupage) getValue()).getSemester();
        } else if (getValue() instanceof Group) {
            return ((Group) getValue()).getGroupage().getSemester();
        } else {
            return ((Student) getValue()).getGroup().getGroupage().getSemester();
        }
    }

    void deleteNode() {
        deleteAllChildren();
        deleteCorrespondingDBObject();
    }

    void deleteAllChildren() {
        for (TreeItem<Object> child : getChildren()) {
            ((Node) child).deleteNode();
        }
    }

    void deleteCorrespondingDBObject() {
        try {
            if (getValue() instanceof Semester) {
                DBManager.getInstance().getSemesterDao().delete((Semester) getValue());
            } else if (getValue() instanceof Groupage) {
                DBManager.getInstance().getGroupageDao().delete((Groupage) getValue());
            } else if (getValue() instanceof Group) {
                DBManager.getInstance().getGroupDao().delete((Group) getValue());
            } else {
                DBManager.getInstance().getStudentDao().delete((Student) getValue());
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}