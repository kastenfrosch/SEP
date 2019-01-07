package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import modal.ErrorModal;
import models.Student;

import java.sql.SQLException;

public class ExamStudentController {


    public ComboBox<Student> studentComboBox;
    public ProgressBar timeProgressBar;
    public Button evaluationBtn;
    public Button saveBtn;
    public TableView ratingTableView;

    private Dao<Student, Integer> studentDao;
    private DBManager db;

    public void initialize(){

        try {
            db = DBManager.getInstance();
            studentDao = db.getStudentDao();
        } catch (SQLException e) {
            ErrorModal.show("Das Bewertungsformular konnte nicht geladen werden!");
            return;
        }
    }

    public void evaluationBtnClicked(ActionEvent event) {
    }

    public void saveBtnClicked(ActionEvent event) {
    }

    public void setStudent(){

    }
}
