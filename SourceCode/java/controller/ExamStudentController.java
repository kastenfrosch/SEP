package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.FloatStringConverter;
import modal.ErrorModal;
import modal.InfoModal;
import models.Group;
import models.Student;
import models.exam.Exam;
import models.exam.ExamQuestion;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExamStudentController {


    public ComboBox<Student> studentComboBox;
    public Button evaluationBtn;
    public TableView<ExamQuestion> ratingTableView;
    public Label averageLabel;
    public Label passLabel;

    private Group group;
    private Exam exam;

    private DBManager db;

    private Dao<Exam, Integer> examDao;
    private Dao<ExamQuestion, Integer> examQuestionDao;
    private Dao<Student, Integer> studentDao;

    //TODO: 2. Timer
    //TODO: 4. Code auskommentieren
    //TODO: 5. Namen des Stundenten in der Überschrift einblenden
    public void initialize(){

        try {
            db = DBManager.getInstance();
            examDao = db.getExamDao();
            examQuestionDao = db.getExamQuestionDao();
            studentDao = db.getStudentDao();

        } catch (SQLException e) {
            ErrorModal.show("Das Bewertungsformular konnte nicht geladen werden!");
            return;
        }

        //create  table columns
        TableColumn<ExamQuestion, String> questionCol = new TableColumn<>("Fragen");
        TableColumn<ExamQuestion, Float> ratingCol = new TableColumn<>("Bewertung");

        //set cellValueFactory
        questionCol.setCellValueFactory(new PropertyValueFactory<>("questionString"));
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("score"));

        //enable temporary textfields to edit cells
        questionCol.setCellFactory(TextFieldTableCell.forTableColumn());
        ratingCol.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));

        //saving new value after editing cell
        questionCol.setOnEditCommit(event -> {
            try {
                ExamQuestion table = ratingTableView.getSelectionModel().getSelectedItem();
                table.setQuestionString(event.getNewValue());

                examQuestionDao.update(table);
            } catch (SQLException e) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
            }
        });
        ratingCol.setOnEditCommit(event -> {
            try {
                ExamQuestion table = ratingTableView.getSelectionModel().getSelectedItem();
                table.setScore(event.getNewValue());

                examQuestionDao.update(table);
            } catch (SQLException e) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
            }
        });

        //enable editing for table cells
        questionCol.setEditable(true);
        ratingCol.setEditable(true);

        //add columns to tableview
        ratingTableView.getColumns().clear();
        ratingTableView.getColumns().addAll(questionCol, ratingCol);

        //TODO: 1. Wenn der Student in der ComboBox gelöscht wird muss der Inhalt der Tabelle auch gelöscht werden, aber die Einträge für den jeweiligen Studenten müssen drin bleiben
        studentComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> ratingTableView.getItems().clear());
    }

    //populating tableview with questions and scores
    public void addBtnClicked(ActionEvent event) {

        this.exam.setGroup(this.group);
        this.exam.setDescription("");

        ExamQuestion question = new ExamQuestion();
        question.setExam(this.exam);
        question.setAnswer("");
        question.setNote("");
        question.setQuestionString("");
        question.setScore(0.0f);
        question.setStudent(studentComboBox.getSelectionModel().getSelectedItem());

        try {
            examDao.create(this.exam);
            examQuestionDao.create(question);
            ratingTableView.getItems().add(question);
            ratingTableView.getSelectionModel().select(question);
            ratingTableView.edit(ratingTableView.getSelectionModel().getSelectedIndex(), ratingTableView.getColumns().get(0));

        } catch (SQLException ex) {
            ErrorModal.show("Ihre Änderung konnte nicht gespeichert werden!");
        }
    }

    //calculation of the average score
    public void evaluationBtnClicked(ActionEvent event) {
        //TODO: 3. Eine Auswertung macht keinen Sinn wenn die Tabelle leer ist
        if(ratingTableView.getItems() == null){
            InfoModal.show("Sie müssen Einträge tätigen bevor eine Auswertung möglich ist!");
        }

        List<Float> scoresList = new ArrayList<>();

        for(ExamQuestion s: ratingTableView.getItems()){
            scoresList.add(s.getScore());
        }

        float scores = 0.0f;

        for(float f: scoresList){
            scores += f;
        }

        float durchschnitt = Math.round((scores / scoresList.size())*100.0f);
        durchschnitt /= 100.0f;
        String avg = Float.toString(durchschnitt);
        averageLabel.setText(avg);

        //you need to reach at least an average of 2.5 points to pass an exam successfully
        if(durchschnitt >= 2.5f){
            passLabel.setText("Bestanden");
        }
        else{
            passLabel.setText("Durchgefallen");
        }
    }

    //create exam for student of a certain group
    public void setArgs(Exam exam, Group group) {
        this.exam = exam;
        this.group = group;

        try {
            var x = FXCollections.observableArrayList(studentDao.queryForEq(Student.FIELD_GROUP_ID, group.getId()));
            studentComboBox.setItems(x);

        } catch (Exception e) {}
    }

    //delete rows
    public void deleteBtnClicked(ActionEvent event) {

        ExamQuestion eq = ratingTableView.getSelectionModel().getSelectedItem();
        try {
            examQuestionDao.delete(eq);
            ratingTableView.getItems().remove(eq);
        } catch (SQLException e) {
            ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
        }
    }
}
