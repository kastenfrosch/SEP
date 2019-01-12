import connection.DBManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import utils.DBUtils;
import utils.scene.SceneManager;
import utils.scene.SceneType;
import utils.settings.Settings;


public class SEP extends Application {

    public static void main(String[] args) {

        launch(args);

    }

    @Override
    public void start(Stage stage) throws Exception {
        Settings.load();

        //DBUtils.resetDB(DBManager.getInstance().getSemesterDao().getConnectionSource(), true);

        //set primary stage for SceneManager
        SceneManager.getInstance(stage);
        SceneManager.getInstance().switchTo(SceneType.LOGIN);
        stage.show();
    }
}
