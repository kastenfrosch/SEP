package controller;

import connection.DBManager;
import connection.PGNotificationHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modal.ConfirmationModal;
import modal.InfoModal;
import models.Group;
import models.Groupage;
import models.Semester;
import models.Student;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class HomeScreenController {

    private Node root;
    private PGNotificationHandler handler;

    @FXML
    private TreeView<Object> treeView;

    @FXML
    private Tab timeTableSemester;

    @FXML
    private Tab timeTableWeek;

    @FXML
    private Tab notes;

    @FXML
    private Tab userAdministration;

    @FXML
    private Button deleteButton;

    @FXML
    private Button addSemesterButton;

    @FXML
    private Button addStudentButton;

    @FXML
    private Button addGroupButton;

    @FXML
    private Button editButton;

    @FXML
    private Button addGroupageButton;

    @FXML
    private Button logoutButton;

    @FXML
    void onEditButtonClicked(ActionEvent event) {
        if (treeView.getSelectionModel().isEmpty()) {
            InfoModal.show("Bitte wählen Sie ein Element aus.");
        } else {
            Node selectedItem = (Node) treeView.getSelectionModel().getSelectedItem();
            if (selectedItem.getValue() instanceof String) {
                selectedItem = (Node) selectedItem.getParent();
                treeView.getSelectionModel().select(selectedItem);
            }
            if (selectedItem.getValue() instanceof Semester) {
                SceneManager.getInstance()
                        .getLoaderForScene(SceneType.EDIT_SEMESTER)
                        .<EditSemesterController>getController()
                        .setSemester((Semester) selectedItem.getValue());
                SceneManager.getInstance().showInNewWindow(SceneType.EDIT_SEMESTER);
            } else if (selectedItem.getValue() instanceof Groupage) {
                SceneManager.getInstance()
                        .getLoaderForScene(SceneType.EDIT_GROUPAGE)
                        .<EditGroupageController>getController()
                        .setGroupage((Groupage) selectedItem.getValue());
                SceneManager.getInstance().showInNewWindow(SceneType.EDIT_GROUPAGE);
            } else if (selectedItem.getValue() instanceof Group) {
                SceneManager.getInstance()
                        .getLoaderForScene(SceneType.EDIT_GROUP)
                        .<EditGroupController>getController()
                        .setGroup((Group) selectedItem.getValue());
                SceneManager.getInstance().showInNewWindow(SceneType.EDIT_GROUP);
            } else {
                SceneManager.getInstance()
                        .getLoaderForScene(SceneType.EDIT_STUDENT)
                        .<EditStudentController>getController()
                        .setStudent((Student) selectedItem.getValue());
                SceneManager.getInstance().showInNewWindow(SceneType.EDIT_STUDENT);
            }
        }
//        drawTreeView();
    }

    @FXML
    void onDeleteButtonClicked(ActionEvent event) {
        if (treeView.getSelectionModel().isEmpty()) {
            InfoModal.show("Bitte wählen Sie ein Element aus.");
        } else {
            Node selectedItem = (Node) treeView.getSelectionModel().getSelectedItem();
            if (selectedItem.getValue() instanceof String) {
                selectedItem = (Node) selectedItem.getParent();
                treeView.getSelectionModel().select(selectedItem);
            }

            if (ConfirmationModal.show("Warnung", null, "Soll das ausgewählte Element und ggf. seine untergeordneten Elemente wirklich gelöscht werden?")) {
                selectedItem.deleteNode();
//                drawTreeView();
            }
        }
    }

    @FXML
    void onLogoutButtonClicked(ActionEvent event) {
        SceneManager.getInstance().closeWindow(SceneType.HOME);
    }

    @FXML
    void onAddSemesterButtonClicked(ActionEvent event) {
        SceneManager.getInstance().showInNewWindow(SceneType.CREATE_SEMESTER);
//        drawTreeView();
    }

    @FXML
    void onAddGroupageButtonClicked(ActionEvent event) {
        SceneManager.getInstance().showInNewWindow(SceneType.CREATE_GROUPAGE);
//        drawTreeView();
    }

    @FXML
    void onAddGroupButtonClicked(ActionEvent event) {
        SceneManager.getInstance().showInNewWindow(SceneType.CREATE_GROUP);
//        drawTreeView();
    }

    @FXML
    void onAddStudentButtonClicked(ActionEvent event) {
        SceneManager.getInstance().showInNewWindow(SceneType.CREATE_STUDENT);
//        drawTreeView();
    }

    @FXML
    public void initialize() {
        Callable c = () -> {
            Platform.runLater(() -> {
                drawTreeView();
            });
            return null;
        };
        PGNotificationHandler.getInstance().registerListener(PGNotificationHandler.NotificationChannel.DATA, c);
        treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        drawTreeView();
    }

    void drawTreeView() {
        ArrayList<String> expandedNodes = new ArrayList<String>();
        if (root != null) {
            expandedNodes = new ArrayList<String>(root.getExpandedChildren());
        }
        root = new Node();
        Node selectedTreeItem = (Node) treeView.getSelectionModel().getSelectedItem();
        List<Semester> semesterList = null;
        List<Groupage> groupageList = null;
        List<Group> groupList = null;
        List<Student> studentList = null;
        try {
            semesterList = DBManager.getInstance().getSemesterDao().queryForAll();
            groupageList = DBManager.getInstance().getGroupageDao().queryForAll();
            groupList = DBManager.getInstance().getGroupDao().queryForAll();
            studentList = DBManager.getInstance().getStudentDao().queryForAll();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (Semester sem : semesterList) {
            Node semesterNode = new Node(sem);
            semesterNode.setExpanded(expandedNodes.contains(semesterNode.getId()));
            root.getChildren().add(semesterNode);
            semesterNode.getChildren().add(new Node(sem.getDescription()));
            for (Groupage groupage : groupageList) {
                Node groupageNode = new Node(groupage);
                groupageNode.setExpanded(expandedNodes.contains(groupageNode.getId()));
                if (groupage.getSemester().getId().equals(sem.getId())) {
                    semesterNode.getChildren().add(groupageNode);
                    for (Group group : groupList) {
                        Node groupNode = new Node(group);
                        groupNode.setExpanded(expandedNodes.contains(groupNode.getId()));
                        if (group.getGroupage().getId() == groupage.getId()) {
                            groupageNode.getChildren().add(groupNode);
                            for (Student student : studentList) {
                                if (student.getGroup().getId() == group.getId()) {
                                    Node studentNode = new Node(student);
                                    groupNode.getChildren().add(studentNode);
                                    studentNode.getChildren().add(new Node(student.getMatrNo()));
                                    studentNode.getChildren().add(new Node(student.getPerson().getEmail()));
                                    studentNode.setExpanded(expandedNodes.contains(studentNode.getId()));
                                }
                            }
                        }
                    }
                }
            }
        }
        treeView.setShowRoot(false);
        treeView.setRoot(root);
        treeView.getSelectionModel().select(selectedTreeItem);
    }
}

class Node extends TreeItem<Object> {

    Node() {

    }

    Node(Object obj) {
        super(obj);
    }

    ArrayList<String> getExpandedChildren() {
        ArrayList<String> expandedChildren = new ArrayList<String>();
        expandedChildren = getExpandedChildren(expandedChildren, this);
        return expandedChildren;
    }

    ArrayList<String> getExpandedChildren(ArrayList<String> list, Node parent) {
        for (TreeItem<Object> child : parent.getChildren()) {
            Node tmp = (Node) child;
            if (!tmp.isLeaf() && tmp.isExpanded()) {
                list.add(tmp.getId());
                list.addAll(getExpandedChildren(list, tmp));
            }
        }
        return list;
    }

    String getId() {
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

    void deleteNode() {
        deleteAllChildren(this);
        deleteCorresbondingDBObject();
    }

    void deleteAllChildren(Node parent) {
        for (TreeItem<Object> child : parent.getChildren()) {
            Node tmp = (Node) child;
            if (!tmp.isLeaf()) {
                tmp.deleteNode();
            }
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