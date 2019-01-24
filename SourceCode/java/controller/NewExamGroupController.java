package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
import modal.InfoModal;
import models.Group;
import models.exam.Exam;
import models.exam.ExamQuestion;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NewExamGroupController {


    public ComboBox<Group> groupComboBox;
    public Button studentQuestionsBtn;
    public Button saveBtn;
    public TableView<ExamQuestion> groupTableView;
    public Label timerLbl;
    public TextField questionTxtField;
    public TextField answerTxtField;
    public TextField commentTxtField;
    public Button addBtn;
    public Button pauseBtn;
    public Button startTimeBtn;
    public Button resetBtn;
    public Button setBtn;
    private Exam exam;
    private DBManager db;

    private Dao<Exam, Integer> examDao;
    private Dao<ExamQuestion, Integer> examQuestionDao;
    private Dao<models.Group, Integer> groupDao;

    private Timeline timeline = new Timeline();
    int seconds;
    int minuten;

    public void initialize(){
        try {
            db = DBManager.getInstance();
            examDao = db.getExamDao();
            examQuestionDao = db.getExamQuestionDao();
            groupDao = db.getGroupDao();

            ObservableList<models.Group> groups = FXCollections.observableArrayList();
            groups.addAll(groupDao.queryForAll());

            groupComboBox.setItems(groups);
            groupComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, group, t1) -> {
                loadExam();
            });

        } catch (SQLException e) {
            ErrorModal.show("Das Bewertungsformular konnte nicht geladen werden!");
            return;
        }

        //create table columns
        TableColumn<ExamQuestion, String> groupQuestionCol = new TableColumn<>("Fragen");
        TableColumn<ExamQuestion, String> answerCol = new TableColumn<>("Ja/Nein");
        TableColumn<ExamQuestion, String> groupCommentCol = new TableColumn<>("Kommentare");


        //set cellValueFactory
        groupQuestionCol.setCellValueFactory(new PropertyValueFactory<>("questionString"));
        answerCol.setCellValueFactory(new PropertyValueFactory<>("answer"));
        groupCommentCol.setCellValueFactory(new PropertyValueFactory<>("note"));

        //enable temporary textfields to edit cells
        groupQuestionCol.setCellFactory(TextFieldTableCell.forTableColumn());
        answerCol.setCellFactory(TextFieldTableCell.forTableColumn());
        groupCommentCol.setCellFactory(TextFieldTableCell.forTableColumn());

        //saving new value after editing cell
        groupQuestionCol.setOnEditCommit(event -> {
            try {
                ExamQuestion table = groupTableView.getSelectionModel().getSelectedItem();
                table.setQuestionString(event.getNewValue());

                examQuestionDao.update(table);
            } catch (SQLException e) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
            }
        });
        answerCol.setOnEditCommit(event -> {
            try {
                ExamQuestion table = groupTableView.getSelectionModel().getSelectedItem();
                table.setAnswer(event.getNewValue());

                examQuestionDao.update(table);
            } catch (SQLException e) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
            }
        });
        groupCommentCol.setOnEditCommit(event -> {
            try {
                ExamQuestion table = groupTableView.getSelectionModel().getSelectedItem();
                table.setNote(event.getNewValue());

                examQuestionDao.update(table);
            } catch (SQLException e) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
            }
        });

        groupQuestionCol.setMinWidth(500);
        groupQuestionCol.setMaxWidth(500);
        answerCol.setMinWidth(50);
        answerCol.setMaxWidth(50);
        groupCommentCol.setMinWidth(500);
        groupCommentCol.setMaxWidth(500);

        //enable editing for table cells
        groupQuestionCol.setEditable(true);
        answerCol.setEditable(true);
        groupCommentCol.setEditable(true);

        //add columns to tableview
        groupTableView.getColumns().clear();
        groupTableView.getColumns().addAll(groupQuestionCol, answerCol, groupCommentCol);


        groupComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            groupTableView.getItems().clear();
            loadExam();
            loadQuestions();
        });
    }

    public void populateAndSaveData(ActionEvent event) {
        if(this.groupComboBox.getSelectionModel().getSelectedItem() == null) {
            ErrorModal.show("Bitte wählen Sie zuerst eine Gruppe aus.");
            return;
        }

        this.exam.setGroup(groupComboBox.getSelectionModel().getSelectedItem());
        this.exam.setDescription("");

        ExamQuestion question = new ExamQuestion();
        question.setExam(this.exam);
        question.setAnswer("Nein");
        question.setNote("Die Software funktioniert nicht einwandfrei");
        question.setQuestionString("Ist die Software vollkommen funktionstüchtig?");
        question.setScore(0.0f);
        question.setStudent(null);

        try {
            examDao.update(this.exam);
            examQuestionDao.createOrUpdate(question);
            groupTableView.getItems().add(question);
            groupTableView.getSelectionModel().select(question);
            groupTableView.edit(groupTableView.getSelectionModel().getSelectedIndex(), groupTableView.getColumns().get(0));

        } catch (SQLException ex) {
            ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
        }
    }

    public void deleteRow(ActionEvent event) {
        ExamQuestion eq = groupTableView.getSelectionModel().getSelectedItem();
        try {
            examQuestionDao.delete(eq);
            groupTableView.getItems().remove(eq);
        } catch (SQLException e) {
            ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
        }
    }

    public void openStudentQuestions(ActionEvent event) {
        if(groupTableView.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("Befüllen Sie bitte zuerst die Tabelle.");
            return;
        }
            SceneManager.getInstance().getLoaderForScene(SceneType.EXAM_STUDENT)
                    .<ExamStudentController>getController()
                    .setArgs(this.exam, groupComboBox.getSelectionModel().getSelectedItem());
            SceneManager.getInstance().showInNewWindow(SceneType.EXAM_STUDENT);
    }

    private void loadExam() {

        try {
            List<Exam> exams = examDao.query(examDao.queryBuilder()
                    .where().eq(Exam.FIELD_GROUP, this.groupComboBox.getSelectionModel().getSelectedItem().getId())
                    .prepare());
//            List<ExamQuestion> eq = new ArrayList<>();
//
//            for(Exam ex: exams){
//                eq.addAll(ex.getQuestions());
//            }

            if (exams.size() == 0) {
                Exam e = new Exam();
                e.setDescription("");
                e.setGroup(this.groupComboBox.getSelectionModel().getSelectedItem());
                examDao.create(e);
                examDao.refresh(e);
                this.exam = e;
                return;
            }
            this.exam = exams.get(0);
        } catch (SQLException ex) {
            ErrorModal.show("Das Bewertungsformular konnte nicht geladen werden!");
        }
    }

    public void loadQuestions(){
        try {
            ObservableList<Exam> e = FXCollections.observableArrayList();
            e.addAll(examDao
                    .queryForEq(Exam.FIELD_GROUP, this.groupComboBox.getSelectionModel().getSelectedItem().getId()));

            //neue observable List aus der anderen
            groupTableView.setItems(FXCollections.observableArrayList(e.get(0).getQuestions()));

        } catch (SQLException e) {
            ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
        }
    }

    public void startTime(ActionEvent event) {
        long startTime = System.currentTimeMillis();

        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    int sek = (int) ((startTime / 1000) % 60);
                    int min = (int) ((startTime / 1000) % 60);
                    sek++;
                    min++;
                        timerLbl.setText("Minuten: " + min + "Sekunden: " + sek);
//                        timerLbl.setTextFill(Color.RED);

                });
            }
        }, 0, 1000);

    }

    public void pauseTime(ActionEvent event) {
        timeline.pause();
    }

    public void resetTime(ActionEvent event) {

    }

    public void setTimer(ActionEvent event) {
    }
}
