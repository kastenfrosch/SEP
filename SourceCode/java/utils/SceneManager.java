package utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import modal.ErrorModal;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
                SceneInfo info = new SceneInfo(type, loader, p);
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
        SceneInfo info = scenes.get(sceneType);
        Stage stage = info.getStage();
        if (stage == null) {
            stage = new Stage();
            stage.setTitle(stageTitle);
            stage.setScene(new Scene(info.getParent()));
            info.setStage(stage);
        }

        Method[] methods = info.getLoader().getController().getClass().getDeclaredMethods();
        for(Method m : methods) {
            if(m.getName().equals("initialize")) {
                try {
                    m.invoke(info.getLoader().getController());
                } catch(IllegalAccessException |InvocationTargetException ex) {
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

    /**
     * @param sceneType
     */
    public void closeWindow(SceneType sceneType) {
        SceneInfo info = scenes.get(sceneType);
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


    public enum SceneType {

        CREATE_GROUP("/fxml/CreateGroupForm.fxml", "Gruppe erstellen"),
        CREATE_STUDENT("/fxml/CreateStudentForm.fxml", "Studenten erstellen"),
        CREATE_SEMESTER("/fxml/CreateSemesterForm.fxml", "Semester erstellen"),
        CREATE_GROUPAGE("/fxml/CreateGroupageForm.fxml", "Klasse erstellen"),
        EDIT_GROUP("/fxml/EditGroupForm.fxml", "Gruppe bearbeiten"),
        EDIT_STUDENT("/fxml/EditStudentForm.fxml", "Studenten bearbeiten"),
        EDIT_SEMESTER("/fxml/EditSemesterForm.fxml", "Semester bearbeiten"),
        EDIT_GROUPAGE("/fxml/EditGroupageForm.fxml", "Klasse bearbeiten"),
        HOME("/fxml/HomeScreenView.fxml", "SemestervErwaltungsPlan"),
        LOGIN("/fxml/LoginForm.fxml", "SemestervErwaltungsPlan"),
        REGISTER("/fxml/RegisterForm.fxml", "Registrierung"),
        CHAT_TAB("/fxml/ChatTab.fxml", "ChatTab"),
        CHAT_WINDOW("/fxml/ChatWindow.fxml", "ChatWindow"),
        NOTEPAD_WINDOW("/fxml/AddNotepadForm.fxml", "NotepadWindow");




        private String path, title;

        SceneType(String path, String title) {
            this.path = path;
            this.title = title;
        }

        public String getPath() {
            return this.path;
        }

        public String getTitle() { return this.title; }

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
