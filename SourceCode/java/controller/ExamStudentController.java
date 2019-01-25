package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.converter.FloatStringConverter;
import modal.ErrorModal;
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
    public Button startBtn;
    public Button pauseBtn;
    public Label timerLbl;
    public Button deleteBtn;
    public Button addBtn;
    public Button setBtn;
    public TextField setmin;
    public TextField setsec;

    private Group group;
    private Exam exam;

    private DBManager db;

    private Dao<Exam, Integer> examDao;
    private Dao<ExamQuestion, Integer> examQuestionDao;
    private Dao<Student, Integer> studentDao;

    private int timerMin;
    private int timerSec;
    private int min;
    private int startSec;
    private int startMin;
    private boolean timeIsRunning;
    private boolean zeroSeconds;
    private boolean timeIsUp;
    private Timeline timeline = new Timeline();

    //settings from the start
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

        TableColumn<ExamQuestion, String> questionCol = new TableColumn<>("Fragen");
        TableColumn<ExamQuestion, Float> ratingCol = new TableColumn<>("Bewertung");
        TableColumn<ExamQuestion, String> commentCol = new TableColumn<>("Kommentare");

        questionCol.setCellValueFactory(new PropertyValueFactory<>("questionString"));
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        commentCol.setCellValueFactory(new PropertyValueFactory<>("note"));

        questionCol.setCellFactory(TextFieldTableCell.forTableColumn());
        ratingCol.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));
        commentCol.setCellFactory(TextFieldTableCell.forTableColumn());

        questionCol.setOnEditCommit(event -> {
            try {
                ExamQuestion item = ratingTableView.getSelectionModel().getSelectedItem();
                item.setQuestionString(event.getNewValue());

                examQuestionDao.update(item);
            } catch (SQLException e) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
            }
        });
        ratingCol.setOnEditCommit(event -> {
            try {
                ExamQuestion item = ratingTableView.getSelectionModel().getSelectedItem();
                item.setScore(event.getNewValue());

                examQuestionDao.update(item);
            } catch (SQLException e) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
            }
        });

        commentCol.setOnEditCommit(event -> {
            try {
                ExamQuestion item = ratingTableView.getSelectionModel().getSelectedItem();
                item.setNote(event.getNewValue());

                examQuestionDao.update(item);
            } catch (SQLException e) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
            }
        });

        questionCol.setMinWidth(400);
        questionCol.setMaxWidth(400);
        ratingCol.setMinWidth(80);
        ratingCol.setMaxWidth(80);
        commentCol.setMinWidth(400);
        commentCol.setMaxWidth(400);

        questionCol.setEditable(true);
        ratingCol.setEditable(true);
        commentCol.setEditable(true);

        ratingTableView.getColumns().clear();
        ratingTableView.getColumns().addAll(questionCol, ratingCol, commentCol);

        studentComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            ratingTableView.getItems().clear();
            loadQuestions();
            basicReset();
        });

        startBtn.setDisable(true);
        pauseBtn.setDisable(true);
    }

    //populating tableview with questions and scores
    public void addBtnClicked(ActionEvent event) {

        if(this.studentComboBox.getSelectionModel().getSelectedItem() == null){
            ErrorModal.show("Bitte wählen Sie zuerst einen Studenten aus.");
        }

        this.exam.setGroup(this.group);
        this.exam.setDescription("");

        ExamQuestion question = new ExamQuestion();
        question.setExam(this.exam);
        question.setAnswer("");
        question.setNote("");
        question.setQuestionString("");
        question.setScore(0.0f);
        question.setStudent(this.studentComboBox.getSelectionModel().getSelectedItem());

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

        List<Float> scoresList = new ArrayList<>();

        for(ExamQuestion s: ratingTableView.getItems()){
            scoresList.add(s.getScore());
        }
        if(scoresList.size() == 0){
            ErrorModal.show("Sie müssen Einträge tätigen bevor eine Auswertung möglich ist!");
        }

        else{
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
                averageLabel.setTextFill(Color.GREEN);
                passLabel.setTextFill(Color.GREEN);
                passLabel.setText("Bestanden");
            }
            else{
                averageLabel.setTextFill(Color.RED);
                passLabel.setTextFill(Color.RED);
                passLabel.setText("Durchgefallen");
            }
        }
    }

    //create exam for student of a certain group
    public void setArgs(Exam exam, Group group) {
        this.exam = exam;
        this.group = group;

        try {
            var x = FXCollections.observableArrayList(studentDao.queryForEq(Student.FIELD_GROUP_ID, group.getId()));
            studentComboBox.setItems(x);

        } catch (Exception e) {
            ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
        }
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

    public void loadQuestions(){

        try {
            ObservableList<ExamQuestion> examq = FXCollections.observableArrayList(examQuestionDao
                    .queryForEq(ExamQuestion.FIELD_STUDENT, studentComboBox.getSelectionModel().getSelectedItem().getId()));

            ratingTableView.setItems(examq);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void startTime() {
        if (timeIsRunning) {
            timeline.play();
        } else {
            if (startMin < 0) {
                return;
            }
            KeyFrame keyframe = new KeyFrame(Duration.seconds(1), event -> {
                startSec--;
                if (startSec == 0){
                    zeroSeconds = true;
                }
                else{
                    zeroSeconds = false;
                }
                if (startSec == 0 && startMin == 0){
                    timeIsUp = true;
                }
                else{
                    timeIsUp = false;
                }

                if (zeroSeconds) {
                    startMin--;
                    startSec = 60;
                }
                if (timeIsUp) {
                    timeline.stop();
                    startMin = 0;
                    startSec = 0;
                    timerLbl.setTextFill(Color.RED);
                    timerLbl.setText("Die Zeit ist abgelaufen!");
                }

                timerLbl.setText(String.format("Minuten: " + startMin + " " + "Sekunden: " + startSec));

            });
            startSec = timerSec;
            startMin = timerMin - min;
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.getKeyFrames().add(keyframe);
            timeline.playFromStart();
            timeIsRunning = true;
        }

    }

    public void pauseTime() {
        timeline.pause();
    }

    private void resetTime() {
        timeline.stop();
        startSec = timerSec;
        startMin = timerMin - min;
        timerLbl.setText(String.format("Minuten: " + startMin + " " + "Sekunden: " + startSec));
    }

    private void basicReset(){
        timeline.stop();
        startSec = 0;
        startMin = 0;
        timerLbl.setTextFill(Color.BLACK);
        timerLbl.setText(String.format("Minuten: " + startMin + " " + "Sekunden: " + startSec));
    }
    public void setTimer() {
        if(setsec.getText().isEmpty() == false ){
            if (Integer.parseInt(setsec.getText()) < 0 || Integer.parseInt(setsec.getText()) > 60) {
                ErrorModal.show("Tragen Sie eine Zahl zwischen 0 und 60 ein.");
                setsec.setText("");
            }
        }
        try {
            pauseTime();
            resetTime();
            timerMin = Integer.parseInt(setmin.getText());
            timerSec = Integer.parseInt(setsec.getText());
            timerLbl.setTextFill(Color.BLACK);
            startBtn.setDisable(false);
            pauseBtn.setDisable(false);
        }catch(NumberFormatException n){
            ErrorModal.show("Tragen Sie zuerst die gewünschten Minuten und Sekunden ein");

        }
        resetTime();

    }
}
