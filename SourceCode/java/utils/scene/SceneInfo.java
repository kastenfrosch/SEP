package utils.scene;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

class SceneInfo {
   private SceneType sceneType;
   private FXMLLoader loader;
   private Parent parent;

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
}
