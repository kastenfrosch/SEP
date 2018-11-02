package controller;

import java.io.IOException;
import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;

import connection.DBManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import modal.InfoModal;
import models.Semester;

public class CreateSemesterController {

	
	// fx id's
	@FXML
    private Button createbutton;
	
	@FXML
	private TextField idInput;
	
	@FXML
	private TextField desInput;
	
	
	@FXML
    private Button backbutton;
	
	@FXML
    private AnchorPane anchorPane;
	
	//Connection to Database
	private DBManager  dbManager;
	{
		try {
			dbManager = DBManager.getInstance();
			} catch (SQLException e) {
				e.printStackTrace();
    }
	
	}
	
	
	public void CreateSemester(ActionEvent Event) {
	 Semester semester = new Semester();
	 Dao<Semester, String > semesterdao = dbManager.getSemesterDao();
	 
	 if (idInput.getText().isEmpty() || idInput == null) {
         InfoModal.show("FEHLER!", null, "Keine ID angegeben!");
         return;
     }
	 
	 if (desInput.getText().isEmpty() || desInput == null) {
         InfoModal.show("FEHLER!", null, "Keinen Namen angegeben!");
         return;
     }
	 
	 
	 
	 semester.setId(idInput.getText());
	 semester.setDescription(desInput.getText());
	 
    try {
		semesterdao.create(semester);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
	}
	
	@FXML
	public void back(ActionEvent Event) {
        try {
            Parent p = FXMLLoader.load(getClass().getResource("/fxml/HomeScreenView.fxml"));
            anchorPane.getScene().setRoot(p);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
		
		
	}
	

