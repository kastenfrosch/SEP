package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import modal.InfoModal;
import models.Groupage;
import models.Semester;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;

import connection.DBManager;

public class EditSemesterController {
	
	private Semester semester;
	
	private DBManager  dbManager;
	{
		try {
			dbManager = DBManager.getInstance();
			} catch (SQLException e) {
				e.printStackTrace();
    }
	
	}
	
	
	@FXML
	private TextField idtextfield;
	
	@FXML
    public AnchorPane anchorPane;

	@FXML
	public ComboBox combobox;
	
	@FXML
	private TextField RenameInput;
	
	@FXML
	private Button SaveButton;
	
	@FXML
	private Button BackButton;
	
	
	@FXML
    public void initialize() {

        // initializing combobox data

        try {

            ObservableList<String> semesterList = FXCollections.observableArrayList();
            Dao<Semester, String> semester = dbManager.getSemesterDao();

            for (Semester s : semester.queryForAll()) {
                semesterList.add(s.getDescription());
            }

            combobox.setItems(semesterList);


           
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        //TODO: check which semester is selected and show possible grpgs accordingly

    }
	 
	 @FXML
	 public void SelectSemester() {
		 

		 
	 }
	
	
	@FXML
	public void SaveRename (ActionEvent Event) {
		
		if (RenameInput.getText().isEmpty()|| RenameInput==null) {
            InfoModal.show("FEHLER!", null, "Ungültiger Name");
            return;
        }
//		if(RenameInput.getText().equals(CurrentSemesterName) {
//			InfoModal.show("FEHLER!",null,"Der name wurd nicht verändert");
//			reutrn;
//		}
		
		 Dao<Semester, String > semesterdao = dbManager.getSemesterDao();
		 
		 Semester selecteditem;
		  try {
			
		        if (combobox.getSelectionModel().getSelectedItem() == null) {
		            InfoModal.show("FEHLER!", null, "Kein Semester ausgewählt!");
		            return;
		        }
		        selecteditem = (Semester) combobox.getSelectionModel().getSelectedItem();//.getSelectedItem();
			  
			Semester semester = semesterdao.queryForId(selecteditem.getId());
		
			if( RenameInput.getText().equals(semester.getDescription()))   {
				InfoModal.show("Fehlgeschlagen, Identisch");
				return;
			}else {
				InfoModal.show(semester.getDescription()+" Renamed to: "+RenameInput.getText());
				
			}
			semester.setDescription(RenameInput.getText());
			semesterdao.update(semester);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

		
	}
	
	public void Cancle (ActionEvent Event) {
		
	       try {
	            Parent p = FXMLLoader.load(getClass().getResource("/fxml/HomeScreenView.fxml"));
	            anchorPane.getScene().setRoot(p);

	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	}
	
}
