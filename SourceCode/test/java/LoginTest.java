import com.sun.javafx.fxml.builder.JavaFXSceneBuilder;
import connection.DBManager;
import controller.LoginController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadui.testfx.GuiTest;
import org.loadui.testfx.categories.TestFX;
import utils.scene.SceneType;

import static org.junit.Assert.assertNull;

@Category(TestFX.class)
public class LoginTest extends GuiTest {


    private LoginController controller;
    private DBManager db;

    @Override
    protected Parent getRootNode() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneType.LOGIN.getPath()));
        this.controller = loader.getController();
        return loader.getRoot();
    }
    @After
    public void cleanup() {

    }

    @Test
    public void tryLogin() {
        click((Button) find("loginBtn"), MouseButton.PRIMARY);
        assertNull(db.getLoggedInUser());
    }

}
