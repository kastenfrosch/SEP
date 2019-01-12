package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import modal.ErrorModal;
import models.Group;
import models.Groupage;
import models.Student;
import models.User;
import models.exam.Exam;
import models.exam.ExamQuestion;
import java.sql.SQLException;
import java.util.List;

public class ExamStudentController {


    public ComboBox<Student> studentComboBox;
    public ProgressBar timeProgressBar;
    public Button evaluationBtn;
    public Button saveBtn;
    public TableView<ExamQuestion> ratingTableView;
    public Label averageLabel;
    public Label passLabel;

    private Group group;
    private Student student;
    private Groupage groupage;

    private DBManager db;

    private Dao<Exam, Integer> examDao;
    private Dao<ExamQuestion, Integer> examQuestionDao;
    private Dao<Student, Integer> studentDao;
    private Dao<Group, Integer> groupDao;
    private Dao<Groupage, Integer> groupageDao;




    //TODO: Timer
    //TODO: AddButton hinzufügen; exam.setGroup eine richtige Gruppe setzen - bisher ist die Group_ID nicht existent
    //TODO: SaveButton separat; fehlt komplett
    //TODO: StudentenComboBox befüllen
    public void initialize(){

        try {
            db = DBManager.getInstance();
            examDao = db.getExamDao();
            examQuestionDao = db.getExamQuestionDao();
            studentDao = db.getStudentDao();
            groupDao = db.getGroupDao();
            groupageDao = db.getGroupageDao();


        } catch (SQLException e) {
            ErrorModal.show("Das Bewertungsformular konnte nicht geladen werden!");
            return;
        }

        TableColumn<ExamQuestion, String> questionCol = new TableColumn<>("Fragen");
        TableColumn<ExamQuestion, Float> ratingCol = new TableColumn<>("Bewertung");

        questionCol.setCellValueFactory(new PropertyValueFactory<>("questionString"));
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("score"));

        questionCol.setCellFactory(TextFieldTableCell.forTableColumn());
        ratingCol.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));

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

        questionCol.setEditable(true);
        ratingCol.setEditable(true);

        ratingTableView.getColumns().clear();
        ratingTableView.getColumns().addAll(questionCol, ratingCol);
    }

    public void addBtnClicked(ActionEvent event) {
          Exam exam = new Exam();
//      Todo: Sollte User nicht eigentlich Student sein? Jedem Studenten sollte quasi ein Exam zugeordnet werden
        exam.setGroup(this.group);
        exam.setDescription("");

        ExamQuestion question = new ExamQuestion();
        question.setExam(exam);
        question.setAnswer("");
        question.setNote("");
        question.setQuestionString("");
        question.setScore(0.0f);

        try {
            examDao.create(exam);
            examQuestionDao.create(question);
            ratingTableView.getItems().add(question);
            ratingTableView.getSelectionModel().select(question);
            ratingTableView.edit(ratingTableView.getSelectionModel().getSelectedIndex(), ratingTableView.getColumns().get(0));

        } catch (SQLException ex) {
            ErrorModal.show("Ihre Änderung konnte nicht gespeichert werden!");
        }
    }

    private void setGroupage(Groupage groupage){
        this.groupage = groupage;
    }

    private void loadGroupsAndStudents(){
        try {
            ObservableList<Group> groups = FXCollections.observableArrayList();
            groups.addAll(groupDao.queryForAll());

            if (groups.size() == 0) {
                Group g = new Group();
                g.setGroupage(groupage);
                g.setName("");
                groupDao.create(g);
                groupDao.refresh(g);
                this.group = g;
                return;
            }

            List<Student> studentsInGroup = studentDao.query(studentDao.queryBuilder()
            .where().eq(Group.FIELD_GROUP_ID, Student.FIELD_GROUP_ID).prepare());
//            studentComboBox.setItems(studentsInGroup);

//            ObservableList<Student> studentenInGruppe = FXCollections.observableArrayList();
//
//            studentenInGruppe.addAll((Student) groupDao.queryForEq(Group.FIELD_GROUP_ID, student.getGroup().getId()));
//
//            this.studentComboBox.setItems(studentenInGruppe);

        } catch (SQLException ex) {
            ErrorModal.show("Das Bewertungsformular konnte nicht geladen werden!");
        }
    }

    public void evaluationBtnClicked(ActionEvent event) {

        int anzahlScores = ratingTableView.getItems().size();

        float durchschnitt = ratingTableView.getSelectionModel().getSelectedItem().getScore() / anzahlScores;
        String avg =Float.toString(durchschnitt);
        averageLabel.setText(avg);
    }

    public void saveBtnClicked(ActionEvent event) {
    }


}
