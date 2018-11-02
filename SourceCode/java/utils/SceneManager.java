package utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import modal.ErrorModal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {
    private static SceneManager instance;

    private Map<SceneType, SceneInfo> scenes = new HashMap<>();
    private Stage root;

    /**
     * @return The applications SceneManager
     * @throws UnsupportedOperationException Thrown when getInstance() is called before getInstance(stage) was called
     */
    public static SceneManager getInstance() {
        if(instance == null) {
            throw new UnsupportedOperationException("Stage is not set!");
        }
        return instance;
    }

    /**
     *
     * @param s The stage of the primary window
     * @return The applications SceneManager
     * @throws UnsupportedOperationException Thrown when getInstance(stage) is called twice. Use getInstance() on
     * the second call instead.
     */
    public static SceneManager getInstance(Stage s) {
        if(instance == null) {
            instance = new SceneManager(s);
            return instance;
        }
        throw new UnsupportedOperationException("Stage is already set!");
    }

    private SceneManager(Stage s) {
        this.root = s;
        //pre-load all scenes
        for(SceneType type : SceneType.values()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(type.getPath()));
                Parent p = loader.load();
                SceneInfo info = new SceneInfo(type, loader, p);
                scenes.put(type, info);
            } catch(IOException ex) {
                ErrorModal.show("ERROR", "A fatal exception has occured", ex.getLocalizedMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     *
     * @param sceneType The to the FXMLLoader corresponding scene
     * @return The FXMLLoader of the corresponding scene
     */
    public FXMLLoader getLoaderForScene(SceneType sceneType) {
        return scenes.get(sceneType).getLoader();
    }

    /**
     * Switches the main stages scene
     * @param sceneType The scene to switch to
     */
    public void switchTo(SceneType sceneType) {
        SceneInfo info = scenes.get(sceneType);
        if(root.getScene() == null) {
            root.setScene(new Scene(info.getParent()));
        }
        else {
            root.getScene().setRoot(info.getParent());
        }
    }

    /**
     * Opens a scene in a new window
     * @param sceneType The scene to open
     */
    public void showInNewWindow(SceneType sceneType) {
        SceneInfo info = scenes.get(sceneType);
        Stage stage = info.getStage();
        if(stage == null) {
            stage = new Stage();
            stage.setTitle("TODO: Add title");
            stage.setScene(new Scene(info.getParent()));
            info.setStage(stage);
        }
        stage.showAndWait();
    }

    /**
     *
     * @param sceneType
     */
    public void closeWindow(SceneType sceneType) {
        SceneInfo info = scenes.get(sceneType);
        try {
            info.getStage().close();
        } catch(IllegalStateException ex) {
            //window is not open
        }
    }



    public enum SceneType {

        ADD_GROUPAGE("/fxml/CreateGroupageForm.fxml"),
        //TODO EditGroupage??
        //TODO Create/Edit Semester??
        CREATE_GROUP("/fxml/CreateGroupForm.fxml"),
        CREATE_STUDENT("/fxml/CreateStudentForm.fxml"),
        EDIT_GROUP("/fxml/EditGroupForm.fxml"),
        EDIT_STUDENT("/fxml/EditStudentForm.fxml"),
        HOME("/fxml/HomeScreenView.fxml"),
        LOGIN("/fxml/LoginForm.fxml");


        private String path;
        SceneType(String path) {
            this.path = path;
        }

        public String getPath() {
            return this.path;
        }

    }

    private class SceneInfo {
        private SceneType sceneType;
        private FXMLLoader loader;
        private Parent parent;
        private Stage stage = null;

        public SceneInfo(SceneType sceneType, FXMLLoader loader, Parent parent) {
            this.sceneType = sceneType;
            this.loader = loader;
            this.parent = parent;
            this.stage = stage;
        }

        public SceneType getSceneType() {
            return sceneType;
        }

        public FXMLLoader getLoader() {
            return loader;
        }

        public Parent getParent() {
            return parent;
        }

        public Stage getStage() {
            return stage;
        }
        public void setStage(Stage stage) {
            this.stage = stage;
        }
    }
}
