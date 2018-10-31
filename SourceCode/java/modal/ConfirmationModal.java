package modal;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class ConfirmationModal {

	public static boolean show(String message) {
		return show("WARNUNG", null, message);
	}

	public static boolean show(String header, String message) {
		return show("WARNUNG", header, message);
	}

	public static boolean show(String title, String header, String message) {

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(message);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
		    return true;
		} else {
		    return false;
		}

    }

}
