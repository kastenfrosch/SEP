import connection.DBManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import utils.DBUtils;
import utils.scene.SceneManager;
import utils.scene.SceneType;
import utils.settings.Settings;

import java.sql.SQLException;


public class SEP extends Application {

    public static void main(String[] args) {

        launch(args);

    }

    @Override
    public void start(Stage stage) throws Exception {
        Settings.load();

        try {
            DBManager.getInstance();
        } catch (SQLException ex) {
            System.out.println("ERROR: Die Verbindung zur Datenbank konnte nicht hergestellt werden.\n" +  ex.getMessage());
            Platform.exit();
            return;
        }

        if(System.getProperty("setup") != null) {
            try {
                DBUtils.resetDB(DBManager.getInstance().getSemesterDao().getConnectionSource(), true);
            } catch(SQLException ex) {
                System.out.println("ERROR: Die Datenbank konnte nicht installiert werden");
            }
            Platform.exit();
            return;
        }

//        DBUtils.resetDB(DBManage.getInstance().getSemesterDao().getConnectionSource(), true);
        //set primary stage for SceneManager
        SceneManager.getInstance(stage);
        SceneManager.getInstance().switchTo(SceneType.NEW_EXAM_GROUP);
        stage.show();
    }

}
