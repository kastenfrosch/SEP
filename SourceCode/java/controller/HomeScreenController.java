package controller;

import java.io.IOException;
import java.sql.SQLException;
import connection.DBManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modal.ConfirmationModal;
import modal.InfoModal;
import models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.DaoObserver;

public class HomeScreenController {

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
            Node selectedItem = (Node)treeView.getSelectionModel().getSelectedItem();
            if (selectedItem.getValue() instanceof String) {
                selectedItem = (Node)selectedItem.getParent();
                treeView.getSelectionModel().select(selectedItem);
            }
            showEditForm(null);
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
            	selectedItem.deleteNode();
            	treeView.getSelectionModel().clearSelection();
            }
        }
    }

    @FXML
    void onLogoutButtonClicked(ActionEvent event) {
    	Platform.exit();
    }

    @FXML
    void onAddSemesterButtonClicked(ActionEvent event) {
        showEditForm("/fxml/EditSemesterForm.fxml");
    }

    @FXML
    void onAddGroupageButtonClicked(ActionEvent event) {
        showEditForm("/fxml/EditGroupageForm.fxml");
    }

    @FXML
    void onAddGroupButtonClicked(ActionEvent event) {
        showEditForm("/fxml/EditGroupForm.fxml");
    }

    @FXML
    void onAddStudentButtonClicked(ActionEvent event) {
       showEditForm("/fxml/EditStudentForm.fxml");
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
            semesterList = DBManager.getInstance().getSemesterDao().queryForAll();
            groupageList = DBManager.getInstance().getGroupageDao().queryForAll();
            groupList    = DBManager.getInstance().getGroupDao().queryForAll();
            studentList  = DBManager.getInstance().getStudentDao().queryForAll();
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

    void showEditForm(String fxmlResource) {
        Pane pane = null;
        FXMLLoader loader = null;
        try {
            if (fxmlResource == null) {
                if(treeView.getSelectionModel().getSelectedItem().getValue() instanceof Semester) {
                    loader = new FXMLLoader(getClass().getResource("/fxml/EditSemesterForm.fxml"));
                    pane = loader.load();
                } else if(treeView.getSelectionModel().getSelectedItem().getValue() instanceof Groupage) {
                    loader = new FXMLLoader(getClass().getResource("/fxml/EditGroupageForm.fxml"));
                    pane = loader.load();
                } else if(treeView.getSelectionModel().getSelectedItem().getValue() instanceof Group) {
                    loader = new FXMLLoader(getClass().getResource("/fxml/EditGroupForm.fxml"));
                    pane = loader.load();
                    EditGroupController controller = loader.<EditGroupController>getController();
                    controller.setSelectedGroup((Group)treeView.getSelectionModel().getSelectedItem().getValue());
                } else {
                    loader = new FXMLLoader(getClass().getResource("/fxml/EditStudentForm.fxml"));
                    pane = loader.load();
                }
            } else {
                loader = new FXMLLoader(getClass().getResource(fxmlResource));
                pane = loader.load();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage popupStage = new Stage();
        Scene popupScene = new Scene(pane);
        popupStage.setScene(popupScene);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
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
