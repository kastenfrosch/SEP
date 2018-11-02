import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.SceneManager;

public class SEP extends Application {

    public static void main(String[] args) {

        launch(args);

    }

    @Override
    public void start(Stage stage) throws Exception {

        //set primary stage for SceneManager
        SceneManager.getInstance(stage);
        SceneManager.getInstance().switchTo(SceneManager.SceneType.HOME);
        stage.show();
    }
}
