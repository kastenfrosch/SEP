package modal;

import javafx.scene.control.Alert;

public class ErrorModal {

    public static void show(String message) {
        show("ERROR", null, message);
    }

    public static void show(String header, String message) {
        show("ERROR", header, message);
    }

    public static void show(String title, String header, String message) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.show();

    }

}
