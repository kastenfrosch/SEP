package modal;

import javafx.scene.control.Alert;

public class InfoModal {

    // overwrites the methods below, shows popup with "INFO" title and custom message, header is null
    // most commonly used.
    public static void show(String message) {
        show("INFO", null, message);
    }

    // overwrites the method below, shows popup with "INFO" title and custom header/message
    public static void show(String header, String message) {
        show("INFO", header, message);
    }

    // this method shows a popup window with a custom title/header/message
    public static void show(String title, String header, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.show();

    }

}
