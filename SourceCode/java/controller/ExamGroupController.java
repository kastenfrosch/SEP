package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import modal.ErrorModal;
import models.Group;
import models.exam.Exam;
import models.exam.ExamQuestion;

import java.sql.SQLException;

public class ExamGroupController {
    public ComboBox groupComboBox;
    public Button saveBtn;

    public Label questionLbl1;
    public Label questionLbl2;
    public Label questionLbl3;
    public Label questionLbl4;

    public ComboBox answerComboBox1;
    public ComboBox answerComboBox2;
    public ComboBox answerComboBox3;
    public ComboBox answerComboBox4;
    public ComboBox succededComboBox;

    private DBManager db;

    private Dao<Exam, Integer> examDao;
    private Dao<ExamQuestion, Integer> examQuestionDao;
    private Dao<Group, Integer> groupDao;

    public void initialize(){

        try {
            db = DBManager.getInstance();
            examDao = db.getExamDao();
            examQuestionDao = db.getExamQuestionDao();
            groupDao = db.getGroupDao();

            ObservableList<Group> groups = FXCollections.observableArrayList();
            Dao<Group, Integer> groupDao = db.getGroupDao();
            groups.addAll(groupDao.queryForAll());
            groupComboBox.setItems(groups);

        } catch (SQLException e) {
            ErrorModal.show("Das Bewertungsformular konnte nicht geladen werden!");
            return;
        }


        questionLbl1.setText("Wurden alle Dokumente fristgerecht abgegeben?");
        questionLbl2.setText("Ist das Spezifikationsdokument vollständig?");
        questionLbl3.setText("Ist die Software funktionstüchtig? ");
        questionLbl4.setText("Wurde die Präsentationszeit eingehalten?");

        String yes = "Ja";
        String no = "Nein";

        ObservableList <String> yn = FXCollections.observableArrayList();
        yn.add(yes);
        yn.add(no);

        answerComboBox1.setItems(yn);
        answerComboBox2.setItems(yn);
        answerComboBox3.setItems(yn);
        answerComboBox4.setItems(yn);
        succededComboBox.setItems(yn);

        answerComboBox1.getSelectionModel().select(0);
        answerComboBox2.getSelectionModel().select(0);
        answerComboBox3.getSelectionModel().select(0);
        answerComboBox4.getSelectionModel().select(0);
        succededComboBox.getSelectionModel().select(0);
    }

    public void saveBtnClicked(){

    }

}
