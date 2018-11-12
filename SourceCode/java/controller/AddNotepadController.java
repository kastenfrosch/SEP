package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import modal.InfoModal;
import utils.SceneManager;

public class AddNotepadController {

    @FXML
    public TextArea addNotepad;
    @FXML
    public Button saveButton;
    @FXML
    public ComboBox<String> priorityComboBox;
    @FXML
    public TextArea notepadTextarea;
    @FXML
    public Button cancelButton;

    // TODO: 12.11.2018 Datenbankmodel zur Speicherfunktion

    public void initializeComboBox() {
        ObservableList<String> prioritaet = FXCollections.observableArrayList();
        prioritaet.add("Hohe Priorität");
        prioritaet.add("Mittlere Priorität");
        prioritaet.add("Geringe Priorität");
        prioritaet.add("Keine Priorität");

        for(String s : prioritaet) {
            priorityComboBox.setItems(prioritaet);
        }
        priorityComboBox.getSelectionModel().select(0);
    }

    public void setPriority(ActionEvent actionEvent) {
        if (priorityComboBox.getSelectionModel().getSelectedItem() == null) {
            InfoModal.show("FEHLER!", null, "Bitte Priorität bestimmen!");
            return;
        }
        else if(priorityComboBox.getSelectionModel().getSelectedItem() == "Hohe Priorität") {
            addNotepad.setStyle("-fx-background-color: red");
        }
        else if(priorityComboBox.getSelectionModel().getSelectedItem() == "Mittlere Priorität") {
            addNotepad.setStyle("-fx-background-color: yellow");
        }
        else if(priorityComboBox.getSelectionModel().getSelectedItem() == "niedrige Priorität") {
            addNotepad.setStyle("-fx-background-color: green");
        }
        else if(priorityComboBox.getSelectionModel().getSelectedItem() == "Keine Priorität") {
            addNotepad.setStyle("-fx-background-color: grey");
        }
    }

    public void saveButton(ActionEvent actionEvent) {
    }

    public void cancelButton(ActionEvent actionEvent) {
        SceneManager.getInstance().closeWindow(SceneManager.SceneType.NOTEPAD_WINDOW);
    }
}
