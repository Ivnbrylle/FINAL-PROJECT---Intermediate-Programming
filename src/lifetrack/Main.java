package lifetrack;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lifetrack.app.Router;
import lifetrack.db.Database;

import java.io.InputStream;

public class Main extends Application {

    public static void main(String[] args) {
        try {
            Database.init();
        } catch (RuntimeException e) {
            System.err.println("Database init failed: " + e.getMessage());
            return;
        }
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("BioByte — Real People, Real Stories");
        try (InputStream is = getClass().getResourceAsStream("/lifetrack/ui/icon.png")) {
            if (is != null) stage.getIcons().add(new Image(is));
        } catch (Exception ignored) {}
        Router.install(stage);
        Router.toLanding();
        stage.show();
    }
}
