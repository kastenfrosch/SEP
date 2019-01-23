package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import modal.ErrorModal;
import modal.InfoModal;
import models.Group;
import models.exam.Exam;
import models.exam.ExamQuestion;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.awt.*;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

public class ExamGroupController {

    public Label timerLbl;
    public Label questionLbl1;
    public Label questionLbl2;
    public Label questionLbl3;
    public Label questionLbl4;

    public ComboBox<Group> groupComboBox;
    public ComboBox answerComboBox1;
    public ComboBox answerComboBox2;
    public ComboBox answerComboBox3;
    public ComboBox answerComboBox4;

    public Button saveBtn;
    public Button questionsStudentBtn;
    public Button commentBtn;

    private Exam exam;
    public ListView<String> commentListView;
    public TextField commentTxtField;

    private DBManager db;

    private Dao<Exam, Integer> examDao;
    private Dao<ExamQuestion, Integer> examQuestionDao;
    private Dao<Group, Integer> groupDao;

    private int min;
    private int startTimeSec, startTimeMin;
    private boolean isRunning;
    private Timeline timeline = new Timeline();

    //TODO: 1. Speichern
    //TODO: 2. Nur 4 Kommentare erlauben -> ListView auf 4 Elemente beschränken
    //TODO: 3. Timer
    //TODO: 4. Code auskomentieren
    public void initialize(){
//
//        try {
//            db = DBManager.getInstance();
//            examDao = db.getExamDao();
//            examQuestionDao = db.getExamQuestionDao();
//            groupDao = db.getGroupDao();
//
//            ObservableList<Group> groups = FXCollections.observableArrayList();
//            groups.addAll(groupDao.queryForAll());
//
//            groupComboBox.setItems(groups);
//            groupComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, group, t1) -> {
//                loadExam();
//            });
//
//        } catch (SQLException e) {
//            ErrorModal.show("Das Bewertungsformular konnte nicht geladen werden!");
//            return;
//        }
//
//        questionLbl1.setText("Wurden alle Dokumente fristgerecht abgegeben?");
//        questionLbl2.setText("Ist das Spezifikationsdokument vollständig?");
//        questionLbl3.setText("Ist die Software funktionstüchtig? ");
//        questionLbl4.setText("Wurde die Präsentationszeit eingehalten?");
//
//        ObservableList <String> yn = FXCollections.observableArrayList();
//        yn.add("Ja");
//        yn.add("Nein");
//
//        answerComboBox1.setItems(yn);
//        answerComboBox2.setItems(yn);
//        answerComboBox3.setItems(yn);
//        answerComboBox4.setItems(yn);
//
//        groupComboBox.getSelectionModel().select(0);
//        answerComboBox1.getSelectionModel().select(0);
//        answerComboBox2.getSelectionModel().select(0);
//        answerComboBox3.getSelectionModel().select(0);
//        answerComboBox4.getSelectionModel().select(0);
//
//        //ListView
//        commentListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void onSaveBtnClicked(ActionEvent event) {

//        List<String> comments = new ArrayList<>();
//
//        for(String c: commentListView.getItems()){
//            comments.add(c);
//        }
//
//        this.exam.setGroup(groupComboBox.getSelectionModel().getSelectedItem());
//        this.exam.setDescription("");
//        ExamQuestion examQuestion1 = new ExamQuestion();
//        examQuestion1.setScore(0.0f);
//        examQuestion1.setQuestionString(questionLbl1.getText());
//        examQuestion1.setNote(comments.get(0));
//        examQuestion1.setExam(this.exam);
//        examQuestion1.setStudent(null);
//        examQuestion1.setAnswer(answerComboBox1.getSelectionModel().getSelectedItem().toString());
//
//        ExamQuestion examQuestion2 = new ExamQuestion();
//        examQuestion2.setScore(0.0f);
//        examQuestion2.setQuestionString(questionLbl2.getText());
//        examQuestion2.setNote(comments.get(1));
//        examQuestion2.setExam(this.exam);
//        examQuestion2.setStudent(null);
//        examQuestion2.setAnswer(answerComboBox2.getSelectionModel().getSelectedItem().toString());
//
//        ExamQuestion examQuestion3 = new ExamQuestion();
//        examQuestion3.setScore(0.0f);
//        examQuestion3.setQuestionString(questionLbl3.getText());
//        examQuestion3.setNote(comments.get(2));
//        examQuestion3.setExam(this.exam);
//        examQuestion3.setStudent(null);
//        examQuestion3.setAnswer(answerComboBox3.getSelectionModel().getSelectedItem().toString());
//
//        ExamQuestion examQuestion4 = new ExamQuestion();
//        examQuestion4.setScore(0.0f);
//        examQuestion4.setQuestionString(questionLbl4.getText());
//        examQuestion4.setNote(comments.get(3));
//        examQuestion4.setExam(this.exam);
//        examQuestion4.setStudent(null);
//        examQuestion4.setAnswer(answerComboBox4.getSelectionModel().getSelectedItem().toString());
//
//        try {
//            examDao.create(this.exam);
//            examQuestionDao.create(examQuestion1);
//            examQuestionDao.create(examQuestion2);
//            examQuestionDao.create(examQuestion3);
//            examQuestionDao.create(examQuestion4);
//        } catch (SQLException e) {
//            ErrorModal.show("Ihre Eingaben konnten nicht gespeichert werden!");
//        }
    }

    public void onQuestionsStudentBtnCLicked() {

//        loadExam();
//
//        if(this.groupComboBox.getSelectionModel().getSelectedItem() == null) {
//            InfoModal.show("Bitte wählen Sie zuerst eine Gruppe aus!");
//        }
//        else{
//            SceneManager.getInstance().getLoaderForScene(SceneType.EXAM_STUDENT)
//                    .<ExamStudentController>getController()
//                    .setArgs(this.exam, groupComboBox.getSelectionModel().getSelectedItem());
//            SceneManager.getInstance().showInNewWindow(SceneType.EXAM_STUDENT);
//        }
    }

    private void loadExam() {
//        try {
//            List<Exam> exams = examDao.query(examDao.queryBuilder()
//                .where().eq(Exam.FIELD_GROUP, this.groupComboBox.getSelectionModel().getSelectedItem().getId()).prepare());
//
//            if (exams.size() == 0) {
//                Exam e = new Exam();
//                e.setDescription("");
//                e.setGroup(this.groupComboBox.getSelectionModel().getSelectedItem());
//                examDao.create(e);
//                examDao.refresh(e);
//                this.exam = e;
//                return;
//            }
//            this.exam = exams.get(0);
//        } catch (SQLException ex) {
//            ErrorModal.show("Das Bewertungsformular konnte nicht geladen werden!");
//        }
    }

    public void onCommentBtnClicked() {
//        commentListView.getItems().add(commentTxtField.getText());
//        commentTxtField.clear();
    }

    public void onDeleteBtnClicked(ActionEvent event) {
//        commentListView.getItems().remove(commentListView.getSelectionModel().getSelectedItem());
    }

    public void startTime(ActionEvent event) {
    }

    public void resetTime(ActionEvent event) {
    }

    public void stopTimer(ActionEvent event) {
    }
}
