import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SEP extends Application {

    public static void main(String[] args) {

        launch(args);

    }

    @Override
    public void start(Stage stage) throws Exception {

        // set path of fxml/ to your specific view (.fxml)
        Parent root = FXMLLoader.load(getClass().getResource("fxml/AddStudentForm.fxml"));
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();

    }
}
