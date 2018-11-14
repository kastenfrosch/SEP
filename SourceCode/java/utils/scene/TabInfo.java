package utils.scene;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

public class TabInfo extends SceneInfo {
    private Tab tab;

    public TabInfo(SceneType sceneType, FXMLLoader loader, Parent parent) {
        super(sceneType, loader, parent);
        this.tab = new Tab();
        tab.setContent(parent);
    }

    public Tab getTab() {
        return this.tab;
    }

    public <T> T getController() {
        return this.getLoader().getController();
    }
}
