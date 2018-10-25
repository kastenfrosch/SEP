package modal;

import javafx.scene.control.Alert;

public class InfoModal {

    public static void show(String message) {
        show("INFO", null, message);
    }

    public static void show(String header, String message) {
        show("INFO", header, message);
    }

    public static void show(String title, String header, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.show();

    }

}
