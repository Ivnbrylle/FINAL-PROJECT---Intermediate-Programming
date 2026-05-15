package lifetrack.app;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lifetrack.ui.DashboardView;
import lifetrack.ui.LandingView;
import lifetrack.ui.LoginView;
import lifetrack.ui.RegisterView;

/**
 * Single source of truth for switching the visible view.
 * Each view is created fresh on navigation so state always starts clean.
 */
public final class Router {

    private static Stage stage;
    private static Scene scene;

    private Router() {}

    public static void install(Stage s) {
        stage = s;
        // Build initial empty scene with the stylesheet attached once.
        scene = new Scene(new javafx.scene.layout.StackPane(), 1240, 760);
        scene.getStylesheets().add(
            Router.class.getResource("/lifetrack/ui/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setMinWidth(1080);
        stage.setMinHeight(680);
    }

    public static Stage stage() { return stage; }

    public static void toLanding() { swap(new LandingView().build()); }
    public static void toLogin()   { swap(new LoginView().build()); }
    public static void toRegister(){ swap(new RegisterView().build()); }
    public static void toDashboard() { swap(new DashboardView().build()); }

    private static void swap(Parent root) {
        scene.setRoot(root);
    }
}
