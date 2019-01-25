import connection.DBManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import utils.DBUtils;
import utils.scene.SceneManager;
import utils.scene.SceneType;
import utils.settings.Settings;

import java.io.IOException;
import java.sql.SQLException;


public class SEP extends Application {

    public static void main(String[] args) {

        launch(args);

    }

    @Override
    public void start(Stage stage) {
        Settings.load();

        //make sure we can connect to the db and it exists
        try {
            DBManager.getInstance().getSemesterDao().executeRaw("select 1");
        } catch (SQLException ex) {
            System.out.println("ERROR: Die Verbindung zur Datenbank konnte nicht hergestellt werden.\n" +  ex.getMessage());
            Platform.exit();
            return;
        }

        //install the database if requested
        if(System.getProperty("setup") != null) {
            try {
                DBUtils.resetDB(DBManager.getInstance().getSemesterDao().getConnectionSource(), true);
            } catch(IOException | SQLException ex) {
                System.out.println("ERROR: Die Datenbank konnte nicht installiert werden");
            }
            Platform.exit();
            return;
        }

        //check if the database is installed
        try {
            DBManager.getInstance().getSemesterDao()
                    .executeRawNoArgs("select semester_id from semester limit 1");
        } catch(SQLException ex) {
            System.out.println("ERROR: Die Datenbank ist nicht installiert.\n" + ex.getMessage());
            Platform.exit();
            return;
        }

        //set primary stage for SceneManager
        SceneManager.getInstance(stage);
        SceneManager.getInstance().switchTo(SceneType.NEW_EXAM_GROUP);
        stage.show();
    }

}
