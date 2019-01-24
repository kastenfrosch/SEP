package controller;

import com.j256.ormlite.dao.Dao;
import com.sun.tools.javac.Main;
import connection.DBManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
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
    // sehr wichtig
    //TODO: Fragen aus der Datenbank laden (Gruppenebene)
    //TODO: Timer auf meine eigene Art umschreiben + setTimer wenn noch Zeit übrig bleibt
    //TODO: SQL-Exceptions zum funktionieren bringen
    //TODO: Eine Auswertung macht keinen Sinn wenn die Tabelle leer ist
    //TODO: Gruppen eines einzigen Semesters sollen angezegt werden
    // wenn noch Zeit übrig bleibt
    //TODO: Liste von guten und schlechten Studenten
    //TODO: auskommentieren
    //TODO: Code säubern (evtl. umschreiben)
    //TODO: Namen des Stundenten in der Überschrift einblenden


    public ComboBox<Student> studentComboBox;
    public Button evaluationBtn;
    public TableView<ExamQuestion> ratingTableView;
    public Label averageLabel;
    public Label passLabel;
    public Button startBtn;
    public Button pauseBtn;
    public Button resetBtn;
    public Label timerLbl;
    public Button deleteBtn;
    public Button addBtn;

    private Group group;
    private Exam exam;

    private DBManager db;

    private Dao<Exam, Integer> examDao;
    private Dao<ExamQuestion, Integer> examQuestionDao;
    private Dao<Student, Integer> studentDao;

    private int min;
    private int startTimeSec, startTimeMin;
    private boolean isRunning;
    private Timeline timeline = new Timeline();

    //TODO: 2. Timer
    //TODO: 4. Code auskommentieren
    //TODO: 5. Namen des Stundenten in der Überschrift einblenden
    //TODO: Modale an den entsprechenden Stellen einbauen
    //TODO: Filter von guten und schlechten Studenten
    //TODO: Wieso funktionieren Dinge, die nicht funktionieren sollten? SQL-Exception funktioniert nicht
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
        TableColumn<ExamQuestion, String> commentCol = new TableColumn<>("Kommentare");

        //set cellValueFactory
        questionCol.setCellValueFactory(new PropertyValueFactory<>("questionString"));
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        commentCol.setCellValueFactory(new PropertyValueFactory<>("note"));


        //enable temporary textfields to edit cells
        questionCol.setCellFactory(TextFieldTableCell.forTableColumn());
        ratingCol.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));
        commentCol.setCellFactory(TextFieldTableCell.forTableColumn());

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

        commentCol.setOnEditCommit(event -> {
            try {
                ExamQuestion table = ratingTableView.getSelectionModel().getSelectedItem();
                table.setNote(event.getNewValue());

                examQuestionDao.update(table);
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

        //enable editing for table cells
        questionCol.setEditable(true);
        ratingCol.setEditable(true);
        commentCol.setEditable(true);

        //add columns to tableview
        ratingTableView.getColumns().clear();
        ratingTableView.getColumns().addAll(questionCol, ratingCol, commentCol);

        //TODO: 1. Hierbei taucht ein Fehler auf!
        studentComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            ratingTableView.getItems().clear();
            loadQuestions();
        });
    }

    //TODO: Wieso kann ich eine Frage hinzufügen, wenn kein Student ausgefwählt wurde
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
        //TODO: 3. Eine Auswertung macht keinen Sinn wenn die Tabelle leer ist
        if(ratingTableView.getItems() == null){
            ErrorModal.show("Sie müssen Einträge tätigen bevor eine Auswertung möglich ist!");
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
    //TODO: anpassen!
    public void startTime(ActionEvent event) {
        if (isRunning == false) {
            if (!(startTimeMin < 0)) {
                KeyFrame keyframe = new KeyFrame(Duration.seconds(1), event1 -> {

                    startTimeSec--;
                    boolean isSecondsZero = startTimeSec == 0;
                    boolean timeToChangeBackground = startTimeSec == 0 && startTimeMin == 0;

                    if (isSecondsZero) {
                        startTimeMin--;
                        startTimeSec = 60;
                    }
                    if (timeToChangeBackground) {
                        timeline.stop();
                        startTimeMin = 0;
                        startTimeSec = 0;
                    }

                    timerLbl.setText(String.format("%d min, %02d sec", startTimeMin, startTimeSec));

                });

                startTimeSec = 30;
                startTimeMin = 1-min;
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.getKeyFrames().add(keyframe);
                timeline.playFromStart();
                isRunning = true;
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "You have not entered a time!");
                alert.showAndWait();
            }
        }else {
            timeline.play();
        }
    }

    public void pauseTime(ActionEvent event) {
        timeline.pause();
    }

    public void resetTime(ActionEvent event) {
        timeline.stop();
        startTimeSec = 30;
        startTimeMin = 1-min;
        timerLbl.setText(String.format("%d min, %02d sec", startTimeMin, startTimeSec));
    }

}
