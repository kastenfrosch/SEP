package utils.scene;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.stage.Stage;
import modal.ErrorModal;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {
    private static SceneManager instance;

    private Map<SceneType, WindowInfo> scenes = new HashMap<>();
    private Stage root;

    /**
     * @return The applications SceneManager
     * @throws UnsupportedOperationException Thrown when getInstance() is called before getInstance(stage) was called
     */
    public static SceneManager getInstance() {
        if (instance == null) {
            throw new UnsupportedOperationException("Stage is not set!");
        }
        return instance;
    }

    /**
     * @param s The stage of the primary window
     * @return The applications SceneManager
     * @throws UnsupportedOperationException Thrown when getInstance(stage) is called twice. Use getInstance() on
     *                                       the second call instead.
     */
    public static SceneManager getInstance(Stage s) {
        if (instance == null) {
            instance = new SceneManager(s);
            return instance;
        }
        throw new UnsupportedOperationException("Stage is already set!");
    }

    private SceneManager(Stage s) {
        this.root = s;
        //pre-load all scenes
        for (SceneType type : SceneType.values()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(type.getPath()));
                Parent p = loader.load();
                WindowInfo info = new WindowInfo(type, loader, p);
                scenes.put(type, info);
            } catch (IOException ex) {
                ErrorModal.show("ERROR", "A fatal exception has occured", ex.getLocalizedMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * @param sceneType The to the FXMLLoader corresponding scene
     * @return The FXMLLoader of the corresponding scene
     */
    public FXMLLoader getLoaderForScene(SceneType sceneType) {
        return scenes.get(sceneType).getLoader();
    }

    /**
     * Switches the main stages scene
     *
     * @param sceneType The scene to switch to
     */
    public void switchTo(SceneType sceneType) {
        SceneInfo info = scenes.get(sceneType);
        if (root.getScene() == null) {
            root.setScene(new Scene(info.getParent()));
        } else {
            root.getScene().setRoot(info.getParent());
        }
        root.setTitle(sceneType.getTitle());
    }

    /**
     * Opens a scene in a new window
     *
     * @param sceneType The scene to open
     * @param stageTitle The title of the new window
     */
    public void showInNewWindow(SceneType sceneType, String stageTitle) {
        WindowInfo info = scenes.get(sceneType);
        Stage stage = info.getStage();
        if (stage == null) {
            stage = new Stage();
            stage.setTitle(stageTitle);
            stage.setScene(new Scene(info.getParent()));
            info.setStage(stage);
        }

        if(stage.getScene().getRoot() == null) {
            stage.getScene().setRoot(info.getParent());
        }

        Method[] methods = info.getLoader().getController().getClass().getDeclaredMethods();
        for(Method m : methods) {
            if(m.getName().equals("initialize")) {
                try {
                    m.invoke(info.getLoader().getController());
                } catch(IllegalAccessException | InvocationTargetException ex) {
                    ErrorModal.show("Unable to reset controller for " + info.getSceneType().toString());
                    ex.printStackTrace();
                }
            }
        }
        stage.showAndWait();
    }

    public void showInNewWindow(SceneType sceneType) {
        showInNewWindow(sceneType, sceneType.getTitle());
    }

    public WindowInfo getRawWindow(SceneType sceneType) {
        return this.scenes.get(sceneType);
    }

    /**
     * @param sceneType
     */
    public void closeWindow(SceneType sceneType) {
        WindowInfo info = scenes.get(sceneType);
        if (info.getSceneType() == SceneType.HOME) {
            root.close();
        } else {
            try {
                info.getStage().close();
            } catch (IllegalStateException ex) {
                //window is not open
            }
        }
    }


    public TabInfo createNewTab(SceneType sceneType) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneType.getPath()));
        try {
            Parent p = loader.load();
            Tab tab = new Tab();
            tab.setContent(p);

            return new TabInfo(sceneType, loader, p);
        } catch (IOException e) {
            ErrorModal.show("ERROR","A fatal exception has occured", e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    public WindowInfo getWindow(SceneType sceneType) {
        WindowInfo info = this.scenes.get(sceneType);
        //need to remove the element from scene because it cannot have two parents
        if(info.getStage().isShowing()) {
            throw new IllegalStateException("Cannot load dynamic window while static window is open");
        }
        if(info.getParent().getScene() != null) {
            info.getParent().getScene().setRoot(null);
        }
        return info;
    }
}
