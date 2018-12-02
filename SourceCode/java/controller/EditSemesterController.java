package controller;

import com.j256.ormlite.dao.Dao;
import connection.DBManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import modal.ConfirmationModal;
import modal.ErrorModal;
import modal.InfoModal;
import models.Semester;
import utils.scene.SceneManager;
import utils.scene.SceneType;

import java.sql.SQLException;

public class EditSemesterController {

	private Semester semesterToEdit;
	
	private DBManager db;

	{
		try {
			db = DBManager.getInstance();
		} catch (SQLException e) {
				e.printStackTrace();
		}
	}

	@FXML
	public Text titleText;
	@FXML
	public Button saveBtn;
	@FXML
	public TextField semesterDescriptionInput;
	@FXML
	public Button cancelBtn;

	public void onSaveButtonClicked(ActionEvent actionEvent) {

		// use text input to edit the group.

		// making sure that semester description is not empty.
		// if not, set semester description to the input.
		String description;
		if (semesterDescriptionInput.getText() == null || semesterDescriptionInput.getText().isBlank()) {
			InfoModal.show("ACHTUNG!", null, "Keine Beschreibung eingegeben!");
			return;
		}
		description = semesterDescriptionInput.getText();

		try {

			// passing variables to the semester instance
			this.semesterToEdit.setDescription(description);

			// save edited semester into database
			Dao<Semester, String> semesterDao = db.getSemesterDao();
			semesterDao.update(this.semesterToEdit);

			// check if semester has been edited
			// an existing semester has an id other than 0
			if (this.semesterToEdit.getId() != null) {
				InfoModal.show("Semester \"" + description + "\" wurde geändert!");
				SceneManager.getInstance().getLoaderForScene(SceneType.HOME).
						<HomeScreenController>getController().setSelectedNode(semesterToEdit);
			} else {
				ErrorModal.show("Semester konnte nicht geändert werden!");
			}

		} catch (java.sql.SQLException e) {
			ErrorModal.show(e.getMessage());
			e.printStackTrace();
		}
	}

	public void onCancelButtonClicked(ActionEvent actionEvent) {
		SceneManager.getInstance().getLoaderForScene(SceneType.HOME).
				<HomeScreenController>getController().showTabContent();
	}

	public void setSemester(Semester semester) {

		// setting up the passed group
		this.semesterToEdit = semester;

		// initializing the text input in the textfield according to the passed semester object
		semesterDescriptionInput.setText(semester.getDescription());

	}

}
