package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import modal.ErrorModal;
import models.Student;
import models.Tardy;
import utils.TimeUtils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TardyController {
    @FXML
    private Text titleText;
    @FXML
    private Button newBtn;
    @FXML
    private TableView<Tardy> tableView;
    @FXML
    private Button deleteBtn;

    private Student student;
    private Dao<Tardy, Integer> tardyDao;

    @FXML
    public void initialize()
    {
        try {
            tardyDao = DBManager.getInstance().getTardyDao();
        } catch(SQLException ex) {
            ErrorModal.show("Fehlzeiten konnten nicht geladen werden.");
            return;
        }
        TableColumn<Tardy, LocalDate> dateCol = new TableColumn<>("Datum");
        TableColumn<Tardy, Integer> timeCol = new TableColumn<>("Fehlzeit (min)");
        TableColumn<Tardy, Boolean> excuseCol = new TableColumn<>("Entschuldigt");


        dateCol.setCellValueFactory(new PropertyValueFactory<>("localDateMissed"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timeMissed"));
        excuseCol.setCellValueFactory(new PropertyValueFactory<>("excused"));

        dateCol.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<>() {
            @Override
            public String toString(LocalDate localDate) {
                //TODO why is localDate null when a new Tardy is created
                if(localDate == null) {
                    return "";
                }
                return TimeUtils.toSimpleDateString(LocalDateTime.of(localDate, LocalTime.of(0, 0, 0)));
            }

            @Override
            public LocalDate fromString(String s) {
                return TimeUtils.localDateFromString(s);
            }
        }));
        timeCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        excuseCol.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<>() {
            @Override
            public String toString(Boolean aBoolean) {
                if (aBoolean) {
                    return "Ja";
                }
                return "Nein";
            }

            @Override
            public Boolean fromString(String s) {
                if (s.equals("Ja")) {
                    return true;
                }
                return false;
            }
        }));

        timeCol.setOnEditCommit(event -> {
            try {
                Tardy t = tableView.getSelectionModel().getSelectedItem();
                t.setTimeMissed(event.getNewValue());

                tardyDao.update(t);
            } catch(SQLException ex) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden.");
            }
        });
        excuseCol.setOnEditCommit(event -> {
            try {

                Tardy t = tableView.getSelectionModel().getSelectedItem();
                t.setExcused(event.getNewValue());

                tardyDao.update(t);
            } catch(SQLException ex) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden.");
            }
        });

        dateCol.setEditable(true);
        timeCol.setEditable(true);
        excuseCol.setEditable(true);

        tableView.getColumns().addAll(dateCol, timeCol, excuseCol);

        refreshList();
    }


    public void setStudent(Student student) {
        this.student = student;

        this.titleText.setText(
                String.format(
                        this.titleText.getText(),
                        student.getPerson().getFirstname()
                )
        );

        refreshList();
    }

    private void refreshList() {
        try {
            DBManager db = DBManager.getInstance();
            Dao<Tardy, Integer> dao = db.getTardyDao();
            //TODO: remove before release, debug only
            if (student == null) {
                Dao<Student, Integer> sDao = db.getStudentDao();
                this.student = sDao.queryForId(1);
            }

            ObservableList<Tardy> tardies = FXCollections.observableArrayList(
                    dao.queryForEq(Tardy.FIELD_STUDENT, this.student.getId())
            );


            this.tableView.setItems(tardies);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void onNewBtnClicked(ActionEvent actionEvent) {
        Tardy t = new Tardy();
        t.setLocalDateMissed(LocalDate.now());
        t.setTimeMissed(0);
        t.setExcused(false);
        t.setStudent(student);

        try {
            tardyDao.create(t);
            tableView.getItems().add(t);
            tableView.getSelectionModel().select(t);
            tableView.edit(tableView.getSelectionModel().getSelectedIndex(), tableView.getColumns().get(0));

        } catch(SQLException ex) {
            ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden.");
        }

    }

    public void onDeleteBtnClicked(ActionEvent actionEvent) {
        Tardy t = tableView.getSelectionModel().getSelectedItem();
        try {
            tardyDao.delete(t);
            tableView.getItems().remove(t);
        } catch (SQLException e) {
            ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden.");
        }
    }
}
