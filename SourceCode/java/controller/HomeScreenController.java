package controller;

import java.sql.SQLException;
import connection.DBManager;
import connection.DBUtils;
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
import com.j256.ormlite.jdbc.JdbcConnectionSource;

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
	            redrawTreeView();
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
            if(ConfirmationModal.show("Warnung", null, "Soll das ausgewählte Element und ggf. seine untergeordneten Elemente wirklich gelöscht werden?")) {
            	selectedItem.deleteNodeAndAllChildren();
            	treeView.getSelectionModel().clearSelection();
            }
        }
    }

    @FXML
    void onLogoutButtonClicked(ActionEvent event) {
       redrawTreeView();
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
//    	resetDB();
    	treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        drawTreeView(new ArrayList<String>());
    }

    void drawTreeView(List<String> expandedNodeIds) {
    	TreeItem<Object> root = new TreeItem<Object>();
    	TreeItem<Object> selectedTreeItem = treeView.getSelectionModel().getSelectedItem();
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
            root.getChildren().add(semesterNode);
            semesterNode.getChildren().add(new Node(sem.getDescription()));
            for (Groupage groupage : groupageList) {
                if (groupage.getSemester().getId().equals(sem.getId())) {
                    Node groupageNode = new Node(groupage);
                    semesterNode.getChildren().add(groupageNode);
                    for (Group group : groupList) {
                        if (group.getGroupage().getId() == groupage.getId()) {
                            Node groupNode = new Node(group);
                            groupageNode.getChildren().add(groupNode);
                            for (Student student : studentList) {
                                if (student.getGroup().getId() == group.getId()) {
                                    Node studentNode = new Node(student);
                                    groupNode.getChildren().add(studentNode);
                                    studentNode.getChildren().add(new Node(student.getMatrNo()));
                                    studentNode.getChildren().add(new Node(student.getPerson().getEmail()));
                                    studentNode.setExpanded(expandedNodeIds.contains(studentNode.toString()));
                                }
                            }
                            groupNode.setExpanded(expandedNodeIds.contains(groupNode.toString()));
                        }
                    }
                    groupageNode.setExpanded(expandedNodeIds.contains(groupageNode.toString()));
                }
            }
            semesterNode.setExpanded(expandedNodeIds.contains(semesterNode.toString()));
        }
        treeView.setShowRoot(false);
        treeView.setRoot(root);
        treeView.getSelectionModel().select(selectedTreeItem);
    }

    void redrawTreeView() {
    	Node root = new Node(treeView.getRoot().getValue());
    	ArrayList<String> expandedNodeIds = getExpandedNodeIds(root);
    	for (String string : expandedNodeIds) {
			System.out.println(string);
		}
    	drawTreeView(expandedNodeIds);
    }

    public ArrayList<String> getExpandedNodeIds(Node parent) {
		ArrayList<String> ret = new ArrayList<String>();
		ArrayList<Node> expandedNodes = new ArrayList<Node>();
		expandedNodes = getExpandedNodes(expandedNodes, parent);
		for (Node node : expandedNodes) {
			ret.add(node.getId());
		}
		return ret;
	}

	public ArrayList<Node> getExpandedNodes(ArrayList<Node> expandedNodes, Node parent) {
		if (!parent.isExpanded()) {
			return expandedNodes;
		}
        for(TreeItem<Object> child: parent.getChildren()){
        	Node tmp = (Node)child;
            if(tmp.isExpanded()){
            	expandedNodes.add((Node)child);
            	return getExpandedNodes(expandedNodes, (Node)child);
            }
        }
        return expandedNodes;
	}

    // testing
	void resetDB() {
		try {
	    	DBUtils.clearTables(semesterDao.getConnectionSource());
	    	DBUtils.insertDummyData(semesterDao.getConnectionSource());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class Node extends TreeItem<Object> {

	public Node(Object obj) {
		super(obj);
	}

	public String getId() {
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

	public ArrayList<Node> getAllChildren(ArrayList<Node> allChildren, Node parent) {
        for(TreeItem<Object> child: parent.getChildren()){
        	Node tmp = (Node)child;
            if(tmp.getValue() instanceof String){
            	return allChildren;
            } else {
            	allChildren.add((Node)child);
            	return getAllChildren(allChildren, (Node)child);

            }
        }
		return allChildren;
	}

	public void deleteNodeAndAllChildren() {
		ArrayList<Node> allChildren = new ArrayList<Node>();
		allChildren = getAllChildren(allChildren, this);
		for (Node node : allChildren) {
			node.deleteNode();
		}
		deleteNode();
	}

	public void deleteNode() {
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
