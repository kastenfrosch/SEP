package utils.scene;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class WindowInfo extends SceneInfo {
    private Stage stage = null;

    public WindowInfo(SceneType sceneType, FXMLLoader loader, Parent parent) {
        super(sceneType, loader, parent);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    Stage getStage() {
        return this.stage;
    }
}
