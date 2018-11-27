package controller;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import connection.DBManager;
import connection.PGNotificationHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import modal.ConfirmationModal;
import modal.InfoModal;
import models.*;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class HomeScreenController {

    private boolean running = false;
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
    void onAddSemesterButtonClicked(ActionEvent event) {
        tabPane.getSelectionModel().select(detailsTab);
        selectedTab.setContent(SceneManager.getInstance().getLoaderForScene(SceneType.CREATE_SEMESTER).getRoot());
    }

    @FXML
    void onAddGroupageButtonClicked(ActionEvent event) {
        tabPane.getSelectionModel().select(detailsTab);
        selectedTab.setContent(SceneManager.getInstance().getLoaderForScene(SceneType.CREATE_GROUPAGE).getRoot());
    }

    @FXML
    void onAddGroupButtonClicked(ActionEvent event) {
        tabPane.getSelectionModel().select(detailsTab);
        selectedTab.setContent(SceneManager.getInstance().getLoaderForScene(SceneType.CREATE_GROUP).getRoot());
    }

    @FXML
    void onAddStudentButtonClicked(ActionEvent event) {
        tabPane.getSelectionModel().select(detailsTab);
        selectedTab.setContent(SceneManager.getInstance().getLoaderForScene(SceneType.CREATE_STUDENT).getRoot());
    }

    @FXML
    void onChatButtonClicked(ActionEvent event) {
        SceneManager.getInstance().showInNewWindow(SceneType.CHAT_WINDOW_TAB_PANE_TEST);
    }

    @FXML
    void onUserAdministrationButtonClicked(ActionEvent event) {
        SceneManager.getInstance().showInNewWindow(SceneType.USER_ADMIN);
    }

    @FXML
    void onLogoutButtonClicked(ActionEvent event) {
        try {
            user.setLastItem(((Node) treeView.getSelectionModel().getSelectedItem()).getUniqueId());
            // TODO lasttab as scenetype hurts
            dbm.getUserDao().update(user);

            ConnectionSource conn = dbm.getFavouriteSemesterDao().getConnectionSource();
            TableUtils.clearTable(conn, FavouriteSemester.class);
            TableUtils.clearTable(conn, FavouriteGroupage.class);
            TableUtils.clearTable(conn, FavouriteGroup.class);
            TableUtils.clearTable(conn, FavouriteStudent.class);

            ArrayList<Node> expandedNodes = getExpandedNodes();
            for (Node tmp : expandedNodes) {
                if(tmp.getValue()instanceof Semester) {
                    FavouriteSemester fav = new FavouriteSemester();
                    fav.setUser(user);
                    fav.setSemester((Semester) tmp.getValue());
                    dbm.getFavouriteSemesterDao().createIfNotExists(fav);
                } else if(tmp.getValue()instanceof Groupage) {
                    FavouriteGroupage fav = new FavouriteGroupage();
                    fav.setUser(user);
                    fav.setGroupage((Groupage) tmp.getValue());
                    dbm.getFavouriteGroupageDao().createIfNotExists(fav);
                } else if(tmp.getValue()instanceof Group) {
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
    }

    @FXML
    public void initialize() {
        getData();
        selectedTab = detailsTab;
        treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        treeView.getSelectionModel().selectedItemProperty().addListener((ov, oldNode, newNode) -> {
            selectedNode = (Node) newNode;
            showTabContent();
        });
        tabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
            selectedTab = newTab;
            showTabContent();
        });
        drawTreeView();
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
            groupList    = dbm.getGroupDao().queryForAll();
            studentList  = dbm.getStudentDao().queryForAll();
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
                case "notesTab" : sceneType = SceneType.EDIT_NOTEPAD_WINDOW;
                    break;
                    // TODO complete precise naming needed
                case "weekPlanTab" : ;
                    break;
                case "semesterPlanTab" : ;
                    break;
                default : ;
                    break;
            }
            if(selectedNode.getValue() instanceof Semester) {
                if (sceneType == null) {
                    sceneType = SceneType.EDIT_SEMESTER;
                    sm.getLoaderForScene(sceneType).<EditSemesterController>getController()
                            .setSemester((Semester) selectedNode.getValue());
                }
            } else if(selectedNode.getValue() instanceof Groupage) {
                if (sceneType == null) {
                    sceneType = SceneType.EDIT_GROUPAGE;
                    sm.getLoaderForScene(sceneType).<EditGroupageController>getController()
                            .setGroupage((Groupage) selectedNode.getValue());
                }
            } else if(selectedNode.getValue() instanceof Group) {
                if (sceneType == null) {
                    sceneType = SceneType.EDIT_GROUP;
                    sm.getLoaderForScene(sceneType).<EditGroupController>getController()
                            .setGroup((Group) selectedNode.getValue());
                }
            } else {
                if (sceneType == null) {
                    sceneType = SceneType.EDIT_STUDENT;
                    sm.getLoaderForScene(sceneType).<EditStudentController>getController()
                            .setStudent((Student) selectedNode.getValue());
                }
            }
            selectedTab.setContent(sm.getLoaderForScene(sceneType).getRoot());
        } else {
            Text selectNotification = new Text("Bitte wählen Sie ein Element links aus der Baumstruktur.");
            selectedTab.setContent(selectNotification);
        }
    }

    void drawTreeView() {
        ArrayList<Node> expandedNodes = null;
        expandedNodes = getExpandedNodes();
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
        treeView.setShowRoot(false);
        treeView.setRoot(treeViewRoot);
        if (expandedNodes != null) {
            treeViewRoot.expandChildren(expandedNodes);
        }
        if (!running) {
            if (user != null) {
                selectedNode = treeViewRoot.getNode(user.getLastItem());
                running = true;
            }
        }
        treeView.getSelectionModel().select(selectedNode);
    }

    ArrayList<Node> getExpandedNodes() {
        if (running) {
            return treeViewRoot.getExpandedChildren();
        } else {
            List<FavouriteSemester> favSemesterList = null;
            List<FavouriteGroupage> favGroupageList = null;
            List<FavouriteGroup>    favGroupList    = null;
            List<FavouriteStudent>  favStudentList  = null;
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
        treeView.getSelectionModel().select(new Node(obj));
    }
}

class Node extends TreeItem<Object> {

    Node() {}

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
        for (TreeItem treeItem: this.getChildren()) {
            Node child = (Node) treeItem;
            Node result =  getNode(child, uniqueId);
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
        if(getValue() instanceof Semester) {
            return (Semester) getValue();
        } else if(getValue()  instanceof Groupage) {
            return ((Groupage) getValue()).getSemester();
        } else if(getValue() instanceof Group) {
            return ((Group) getValue()).getGroupage().getSemester();
        } else {
            return ((Student) getValue()).getGroup().getGroupage().getSemester();
        }
    }

    void deleteNode() {
        deleteAllChildren();
        deleteCorresbondingDBObject();
    }

    void deleteAllChildren() {
        for (TreeItem<Object> child : getChildren()) {
            ((Node) child).deleteNode();
        }
    }

    void deleteCorresbondingDBObject() {
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