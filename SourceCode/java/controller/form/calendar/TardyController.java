package controller.form.calendar;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import modal.ErrorModal;
import models.CalendarEntry;
import models.Groupage;
import models.Student;
import models.Tardy;

import java.sql.SQLException;
import java.util.stream.Collectors;

public class TardyController {
    @FXML
    private Text titleText;
    @FXML
    private TableView<Tardy> tableView;

    private CalendarEntry calendarEntry;
    private Groupage groupage;

    private Dao<Tardy, Integer> tardyDao;

    @FXML
    public void initialize() {
        try {
            tardyDao = DBManager.getInstance().getTardyDao();
        } catch (SQLException ex) {
            ErrorModal.show("Fehlzeiten konnten nicht geladen werden.");
            return;
        }

        this.tableView.getColumns().clear();
        /**/
        TableColumn<Tardy, String> firstnameCol = new TableColumn<>("Vorname");
        TableColumn<Tardy, String> lastnameCol = new TableColumn<>("Nachname");
        TableColumn<Tardy, Boolean> presentCol = new TableColumn<>("Anwesend");
        TableColumn<Tardy, Integer> timeCol = new TableColumn<>("Fehlzeit (min)");
        TableColumn<Tardy, Boolean> excusedCol = new TableColumn<>("Entschuldigt");

        firstnameCol.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getStudent().getPerson().getFirstname()));
        lastnameCol.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getStudent().getPerson().getLastname()));
        presentCol.setCellValueFactory(new PropertyValueFactory<>("present"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timeMissed"));
        excusedCol.setCellValueFactory(new PropertyValueFactory<>("excused"));

        //student name shouldn't be editable so there is no need for a textfieldtablecell
        presentCol.setCellFactory(TextFieldTableCell.forTableColumn(new YesNoConverter()));
        timeCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        excusedCol.setCellFactory(TextFieldTableCell.forTableColumn(new YesNoConverter()));

        presentCol.setOnEditCommit(event -> {
            try {
                Tardy t = tableView.getSelectionModel().getSelectedItem();
                t.setPresent(event.getNewValue());

                tardyDao.update(t);
            } catch (SQLException ex) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden.");
            }
        });
        timeCol.setOnEditCommit(event -> {
            try {
                Tardy t = tableView.getSelectionModel().getSelectedItem();
                t.setTimeMissed(event.getNewValue());

                tardyDao.update(t);
            } catch (SQLException ex) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden.");
            }
        });
        excusedCol.setOnEditCommit(event -> {
            try {

                Tardy t = tableView.getSelectionModel().getSelectedItem();
                t.setExcused(event.getNewValue());

                tardyDao.update(t);
            } catch (SQLException ex) {
                event.consume();
                ErrorModal.show("Ihre Änderungen konnten nicht gespeichert werden.");
            }
        });

        firstnameCol.setEditable(false);
        lastnameCol.setEditable(false);
        presentCol.setEditable(true);
        timeCol.setEditable(true);
        excusedCol.setEditable(true);

        tableView.getColumns().addAll(firstnameCol, lastnameCol, presentCol, timeCol, excusedCol);
    }


    public void setArgs(CalendarEntry entry, Groupage groupage) {
        this.calendarEntry = entry;
        this.groupage = groupage;
        String titleBase = "Anwesenheit %s";
        this.titleText.setText(
                String.format(
                        titleBase,
                        groupage.getDescription()
                )
        );

        refreshList();
    }

    private void refreshList() {
        try {
            DBManager db = DBManager.getInstance();

            ObservableList<Student> studentsInGroupage = FXCollections.observableArrayList();
            this.groupage.
                    getGroups().
                    forEach(group -> {
                        studentsInGroupage.addAll(group.getStudents());
                    });

            ObservableList<Tardy> listItems = FXCollections.observableArrayList();

            boolean entryExists = tardyDao.query(
                    tardyDao.queryBuilder()
                            .limit(1L)
                            .where()
                            .eq(Tardy.FIELD_DATE_MISSED, this.calendarEntry.getEntryId())
                            .and()
                            .in(Tardy.FIELD_STUDENT, studentsInGroupage.stream().map(Student::getId).collect(Collectors.toList()))
                            .prepare()
            ).size() > 0;

            if (!entryExists) {
                //no list for this date+class exists yet, so we generate one
                for (var s : studentsInGroupage) {
                    Tardy t = new Tardy();
                    t.setDateMissed(this.calendarEntry);
                    t.setExcused(false);
                    t.setPresent(false);
                    t.setStudent(s);
                    t.setTimeMissed(0);
                    tardyDao.create(t);
                    listItems.add(t);
                }
            } else {
                //add all tardies from students in this class and at this calendarentry to the list
                listItems.addAll(
                        tardyDao.query(
                                tardyDao.queryBuilder()
                                        .where()
                                        .in(Tardy.FIELD_STUDENT, studentsInGroupage.stream().map(Student::getId).collect(Collectors.toList()))
                                        .and()
                                        .eq(Tardy.FIELD_DATE_MISSED, this.calendarEntry)
                                        .prepare()
                        )
                );
            }

            this.tableView.setItems(listItems);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private class YesNoConverter extends StringConverter<Boolean> {
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
    }
}
