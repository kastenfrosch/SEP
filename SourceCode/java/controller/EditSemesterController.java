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
	public Button deleteBtn;
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

				// close window
				//SceneManager.getInstance().closeWindow(SceneType.EDIT_SEMESTER);

			} else {
				ErrorModal.show("Semester konnte nicht geändert werden!");
			}

		} catch (java.sql.SQLException e) {
			ErrorModal.show(e.getMessage());
			e.printStackTrace();
		}


	}

	public void onDeleteButtonClicked(ActionEvent actionEvent) {

		// check if sure to delete
		boolean confirmDelete = ConfirmationModal.show("Soll das Semester wirklich gelöscht werden?");

		if (confirmDelete) {

			// delete semester from database
			try {
				Dao<Semester, String> semesterDao = db.getSemesterDao();
				semesterDao.delete(this.semesterToEdit);
			} catch (java.sql.SQLException e) {
				ErrorModal.show("Das Semester konnte nicht gelöscht werden. Bitte stellen Sie sicher, dass dem Semester keine Klassen mehr zugeordnet sind.");
			}

			SceneManager.getInstance().getLoaderForScene(SceneType.HOME).
					<HomeScreenController>getController().setSelectedNode(null);
			// close window
			//SceneManager.getInstance().closeWindow(SceneType.EDIT_SEMESTER);

		}

	}

	public void onCancelButtonClicked(ActionEvent actionEvent) {

		SceneManager.getInstance().getLoaderForScene(SceneType.HOME).
				<HomeScreenController>getController().showTabContent();
		// close semester editing window
		//SceneManager.getInstance().closeWindow(SceneType.EDIT_SEMESTER);

	}

	public void setSemester(Semester semester) {

		// setting up the passed group
		this.semesterToEdit = semester;

		// initializing the text input in the textfield according to the passed semester object
		semesterDescriptionInput.setText(semester.getDescription());

	}

}
