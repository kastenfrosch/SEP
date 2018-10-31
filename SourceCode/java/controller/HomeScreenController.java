package controller;

import java.sql.SQLException;
import connection.DBManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import modal.ConfirmationModal;
import modal.InfoModal;
import models.*;

import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.DaoObserver;

public class HomeScreenController {

    private Dao<Semester, String>  semesterDao;
    private Dao<Groupage, Integer> groupageDao;
    private Dao<Group, Integer>    groupDao;
    private Dao<Student, Integer>  studentDao;

    public HomeScreenController() throws SQLException {
        semesterDao = DBManager.getInstance().getSemesterDao();
        groupageDao = DBManager.getInstance().getGroupageDao();
        groupDao    = DBManager.getInstance().getGroupDao();
        studentDao  = DBManager.getInstance().getStudentDao();
		DaoObserver observer = new DaoObserver() {
	        public void onChange() {
	        	Node root = (Node)treeView.getRoot();
	            drawTreeView(root.getExpandedChildren());
//	            treeView.refresh();
	        }
	    };
		semesterDao.registerObserver(observer);
		groupageDao.registerObserver(observer);
		groupDao.registerObserver(observer);
		studentDao.registerObserver(observer);
    }

    @FXML
    private TreeView<Object> treeView;

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
    	if(treeView.getSelectionModel().isEmpty()) {
            InfoModal.show("Bitte wählen Sie ein Element aus.");
        } else {

        }
    }

    @FXML
    void onDeleteButtonClicked(ActionEvent event) {
        if(treeView.getSelectionModel().isEmpty()) {
            InfoModal.show("Bitte wählen Sie ein Element aus.");
        } else {
            Node selectedItem = (Node)treeView.getSelectionModel().getSelectedItem();
            if (selectedItem.getValue() instanceof String) {
                selectedItem = (Node)selectedItem.getParent();
                treeView.getSelectionModel().select(selectedItem);
            }

            if(ConfirmationModal.show("Warnung", null, "Soll das ausgew�hlte Element und ggf. seine untergeordneten Elemente wirklich gel�scht werden?")) {
            	selectedItem.deleteNode();
            	treeView.getSelectionModel().clearSelection();
            }
        }
    }

    @FXML
    void onLogoutButtonClicked(ActionEvent event) {
    	Node root = (Node)treeView.getRoot();
    	drawTreeView(root.getExpandedChildren());
//    	Platform.exit();
    }

    @FXML
    void onAddSemesterButtonClicked(ActionEvent event) {

    }

    @FXML
    void onAddGroupageButtonClicked(ActionEvent event) {

    }

    @FXML
    void onAddGroupButtonClicked(ActionEvent event) {

    }

    @FXML
    void onAddStudentButtonClicked(ActionEvent event) {

    }

    @FXML
    public void initialize() {
    	treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        drawTreeView(new ArrayList<String>());
    }

    void drawTreeView(List<String> expandedNodes) {
    	Node root = new Node();
    	Node selectedTreeItem = (Node)treeView.getSelectionModel().getSelectedItem();
        List<Semester> semesterList = null;
        List<Groupage> groupageList = null;
        List<Group> groupList = null;
        List<Student> studentList = null;
        try {
            semesterList = semesterDao.queryForAll();
            groupageList = groupageDao.queryForAll();
            groupList    = groupDao.queryForAll();
            studentList  = studentDao.queryForAll();
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
		for(TreeItem<Object> child: parent.getChildren()){
        	Node tmp = (Node)child;
            if(!tmp.isLeaf() && tmp.isExpanded()){
            	list.add(tmp.getId());
            	list.addAll(getExpandedChildren(list, tmp));
            }
        }
		return list;
	}

	String getId() {
		if (getValue() instanceof Semester) {
			Semester tmp = (Semester)getValue();
			return "Semester: " + tmp.getId();
		} else if (getValue() instanceof Groupage) {
			Groupage tmp = (Groupage)getValue();
			return "Groupage: " + Integer.toString(tmp.getId());
		} else if (getValue() instanceof Group) {
			Group tmp = (Group)getValue();
			return "Group: " + Integer.toString(tmp.getId());
		} else {
			Student tmp = (Student)getValue();
			return "Student: " + Integer.toString(tmp.getId());
		}
	}

	void deleteNode() {
		deleteAllChildren(this);
		deleteCorresbondingDBObject();
	}

	void deleteAllChildren(Node parent) {
        for(TreeItem<Object> child: parent.getChildren()){
        	Node tmp = (Node)child;
            if(!tmp.isLeaf()){
            	tmp.deleteNode();
            }
        }
	}

	void deleteCorresbondingDBObject() {
		try {
            if(getValue() instanceof Semester) {
                DBManager.getInstance().getSemesterDao().delete((Semester)getValue());
            } else if(getValue() instanceof Groupage) {
                DBManager.getInstance().getGroupageDao().delete((Groupage)getValue());
            } else if(getValue() instanceof Group) {
                DBManager.getInstance().getGroupDao().delete((Group)getValue());
            } else {
                DBManager.getInstance().getStudentDao().delete((Student)getValue());
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
}
