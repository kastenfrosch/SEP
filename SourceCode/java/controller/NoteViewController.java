package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import modal.ErrorModal;
import modal.InfoModal;
import models.*;

import java.sql.SQLException;

public class NoteViewController {

    @FXML
    private Button newBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private TableView<Notepad> tableView;

    @FXML
    private Text titleText;

    private INotepadEntity entity;

    private Dao<Notepad, Integer> notepadDao;

    private User loggedInUser;

    @FXML
    public void initialize() {

        try {
            notepadDao = DBManager.getInstance().getNotepadDao();
            this.loggedInUser = DBManager.getInstance().getLoggedInUser();
        } catch(SQLException ex) {
            ErrorModal.show("Notizen konnten nicht geladen werden.");
            return;
        }

        TableColumn<Notepad, String> titleCol = new TableColumn<>("Titel");
        TableColumn<Notepad, String> contentCol = new TableColumn<>("Inhalt");
        TableColumn<Notepad, Notepad.Classification> classCol = new TableColumn<>("Klassifizierung");

        titleCol.setCellValueFactory(new PropertyValueFactory<>("notepadName"));
        contentCol.setCellValueFactory(new PropertyValueFactory<>("notepadContent"));
        classCol.setCellValueFactory(new PropertyValueFactory<>("classification"));

        titleCol.setCellFactory(TextFieldTableCell.forTableColumn());
        contentCol.setCellFactory(TextFieldTableCell.forTableColumn());
        classCol.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<>() {
            @Override
            public String toString(Notepad.Classification classification) {
                return classification.toString();
            }

            @Override
            public Notepad.Classification fromString(String s) {
                return Notepad.Classification.valueOf(s);
            }
        }));

        titleCol.setOnEditCommit(e -> {
            Notepad n = tableView.getSelectionModel().getSelectedItem();
            n.setNotepadName(e.getNewValue());
            try {
                notepadDao.update(n);
            } catch (SQLException ex) {
                ErrorModal.show("Änderungen konnten nicht gespeichert werden.");
            }
        });
        contentCol.setOnEditCommit(e -> {
            Notepad n = tableView.getSelectionModel().getSelectedItem();
            n.setNotepadContent(e.getNewValue());
            try {
                notepadDao.update(n);
            } catch (SQLException ex) {
                ErrorModal.show("Änderungen konnten nicht gespeichert werden.");
            }
        });
        classCol.setOnEditCommit(e -> {
            Notepad n = tableView.getSelectionModel().getSelectedItem();
            n.setClassification(e.getNewValue());
            try {
                notepadDao.update(n);
            } catch(SQLException ex) {
                ErrorModal.show("Änderungen konnten nicht gespeichert werden.");
            }
        });
        tableView.getColumns().clear();

        tableView.setEditable(true);
        titleCol.setEditable(true);
        contentCol.setEditable(true);
        classCol.setEditable(true);
        tableView.getColumns().addAll(titleCol, contentCol, classCol);
    }


    private void fillTableView() {
        ObservableList<Notepad> tableItems = FXCollections.observableArrayList();

        for(var n : this.entity.getNotepads()) {
            tableItems.add(n.getNotepad());
        }

        this.tableView.setItems(tableItems);
    }


    public void setEntity(INotepadEntity entity) {
        this.entity = entity;
        this.titleText.setText(String.format(
                this.titleText.getText(),
                entity.toString()
        ));
        fillTableView();
    }

    @FXML
    public void onNewBtnClicked(ActionEvent e) {
        Notepad n = new Notepad();
        n.setNotepadName("Neues Notepad");
        n.setNotepadContent("");
        n.setUser(loggedInUser);
        n.setClassification(Notepad.Classification.NEUTRAL);

        try {
            notepadDao.create(n);
            INotepadBridge.create(entity, n);
        } catch(SQLException ex) {
            ErrorModal.show("Das Notepad konnte nicht erstellt werden.");
        }

        tableView.getItems().add(n);
        tableView.edit(tableView.getItems().size(), tableView.getColumns().get(0));
    }

    @FXML
    public void onDeleteBtnClicked(ActionEvent e) {
        try {
            if(tableView.getSelectionModel().getSelectedItem() == null) {
                InfoModal.show("Bitte einen Eintrag auswählen");
                return;
            }
            notepadDao.delete(tableView.getSelectionModel().getSelectedItem());
            tableView.refresh();
        } catch(SQLException ex) {
            ErrorModal.show("Der Eintrag konnte nicht gelöscht werden: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


}
