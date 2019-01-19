package modal;

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class PasswordModal {

    //modified from
    //https://stackoverflow.com/questions/31556373/javafx-dialog-with-2-input-fields
    public static Optional<String> showAndWait()  {
        Dialog<String> dialog = new Dialog<>();

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setTitle("Passwort");

        GridPane root = new GridPane();
        Label label = new Label("Passwort: ");
        PasswordField input = new PasswordField();

        root.add(label, 0, 0);
        root.add(input, 1, 0);

        dialog.getDialogPane().setContent(root);

        Platform.runLater(input::requestFocus);

        dialog.setResultConverter(button -> {
            if(button == ButtonType.OK) {
                return input.getText();
            }
            return null;
        });

        return dialog.showAndWait();
    }
}
