import javafx.application.Application;
import javafx.stage.Stage;
import utils.scene.SceneManager;
import utils.scene.SceneType;


public class SEP extends Application {

    public static void main(String[] args) {

        launch(args);

    }

    @Override
    public void start(Stage stage) throws Exception {

        //set primary stage for SceneManager
        SceneManager.getInstance(stage);
        SceneManager.getInstance().switchTo(SceneType.HOME);
        stage.show();
    }
}
