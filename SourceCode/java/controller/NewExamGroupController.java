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
import modal.ErrorModal;
import models.Group;
import models.exam.Exam;
import models.exam.ExamQuestion;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.sql.SQLException;
import java.util.List;

public class NewExamGroupController {


    public ComboBox<Group> groupComboBox;
    public Button studentQuestionsBtn;
    public TableView<ExamQuestion> groupTableView;
    public Label timerLbl;
    public Button addBtn;
    public Button pauseBtn;
    public Button startTimeBtn;
    public Button setBtn;
    public TextField setmin;
    public TextField setsec;
    private Exam exam;
    private DBManager db;

    private Dao<Exam, Integer> examDao;
    private Dao<ExamQuestion, Integer> examQuestionDao;
    private Dao<Group, Integer> groupDao;

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
    public void initialize() {
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

        TableColumn<ExamQuestion, String> groupQuestionCol = new TableColumn<>("Fragen");
        TableColumn<ExamQuestion, String> answerCol = new TableColumn<>("Ja/Nein");
        TableColumn<ExamQuestion, String> groupCommentCol = new TableColumn<>("Kommentare");

        groupQuestionCol.setCellValueFactory(new PropertyValueFactory<>("questionString"));
        answerCol.setCellValueFactory(new PropertyValueFactory<>("answer"));
        groupCommentCol.setCellValueFactory(new PropertyValueFactory<>("note"));

        groupQuestionCol.setCellFactory(TextFieldTableCell.forTableColumn());
        answerCol.setCellFactory(TextFieldTableCell.forTableColumn());
        groupCommentCol.setCellFactory(TextFieldTableCell.forTableColumn());

        groupQuestionCol.setOnEditCommit(event -> {
            try {
                ExamQuestion item = groupTableView.getSelectionModel().getSelectedItem();
                item.setQuestionString(event.getNewValue());

                examQuestionDao.update(item);
            } catch (SQLException e) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
            }
        });
        answerCol.setOnEditCommit(event -> {
            try {
                ExamQuestion item = groupTableView.getSelectionModel().getSelectedItem();
                item.setAnswer(event.getNewValue());

                examQuestionDao.update(item);
            } catch (SQLException e) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
            }
        });
        groupCommentCol.setOnEditCommit(event -> {
            try {
                ExamQuestion item = groupTableView.getSelectionModel().getSelectedItem();
                item.setNote(event.getNewValue());

                examQuestionDao.update(item);
            } catch (SQLException e) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
            }
        });

        groupQuestionCol.setMinWidth(600);
        groupQuestionCol.setMaxWidth(600);
        answerCol.setMinWidth(50);
        answerCol.setMaxWidth(50);
        groupCommentCol.setMinWidth(600);
        groupCommentCol.setMaxWidth(600);

        groupQuestionCol.setEditable(true);
        answerCol.setEditable(true);
        groupCommentCol.setEditable(true);

        groupTableView.getColumns().clear();
        groupTableView.getColumns().addAll(groupQuestionCol, answerCol, groupCommentCol);

        groupComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            groupTableView.getItems().clear();
            loadExam();
            loadQuestions();
        });


        startTimeBtn.setDisable(true);
        pauseBtn.setDisable(true);

    }
    //populate tableview
    public void populateAndSaveData(ActionEvent event) {
        if (this.groupComboBox.getSelectionModel().getSelectedItem() == null) {
            ErrorModal.show("Bitte wählen Sie zuerst eine Gruppe aus.");
            return;
        }

        this.exam.setGroup(groupComboBox.getSelectionModel().getSelectedItem());
        this.exam.setDescription("");

        ExamQuestion question = new ExamQuestion();
        question.setExam(this.exam);
        question.setAnswer("");
        question.setNote("");
        question.setQuestionString("");
        question.setScore(0.0f);
        question.setStudent(null);

        try {
            examDao.createIfNotExists(this.exam);
            examQuestionDao.createOrUpdate(question);
            groupTableView.getItems().add(question);
            groupTableView.getSelectionModel().select(question);
            groupTableView.edit(groupTableView.getSelectionModel().getSelectedIndex(), groupTableView.getColumns().get(0));

        } catch (SQLException ex) {
            ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
        }
    }
    //delete tableview row
    public void deleteRow(ActionEvent event) {
        ExamQuestion eq = groupTableView.getSelectionModel().getSelectedItem();
        try {
            examQuestionDao.delete(eq);
            groupTableView.getItems().remove(eq);
        } catch (SQLException e) {
            ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
        }
    }
    //open ExamStudentController
    public void openStudentQuestions(ActionEvent event) {
        if (groupComboBox.getSelectionModel().getSelectedItem() == null) {
            ErrorModal.show("Bitte wählen Sie zuerst eine Gruppe aus.");
            return;
        }
        SceneManager.getInstance().getLoaderForScene(SceneType.EXAM_STUDENT)
                .<ExamStudentController>getController()
                .setArgs(this.exam, groupComboBox.getSelectionModel().getSelectedItem());
        SceneManager.getInstance().showInNewWindow(SceneType.EXAM_STUDENT);
    }
    //load exams of a group
    private void loadExam() {

        try {
            List<Exam> exams = examDao.query(examDao.queryBuilder()
                    .where().eq(Exam.FIELD_GROUP, this.groupComboBox.getSelectionModel().getSelectedItem().getId())
                    .prepare());

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
    //load questions of a group
    private void loadQuestions() {
        try {
            ObservableList<Exam> e = FXCollections.observableArrayList();
            e.addAll(examDao
                    .queryForEq(Exam.FIELD_GROUP, this.groupComboBox.getSelectionModel().getSelectedItem().getId()));

            groupTableView.setItems(FXCollections.observableArrayList(e.get(0).getQuestions()));

        } catch (SQLException e) {
            ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden!");
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

    public void setTimer(ActionEvent event) {
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
            startTimeBtn.setDisable(false);
            pauseBtn.setDisable(false);
        }catch(NumberFormatException n){
            ErrorModal.show("Tragen Sie zuerst die gewünschten Minuten und Sekunden ein");
            timerLbl.setTextFill(Color.BLACK);

        }
        resetTime();

    }
}