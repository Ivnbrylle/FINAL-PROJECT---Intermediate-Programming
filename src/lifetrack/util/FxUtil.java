package lifetrack.util;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Region;
import lifetrack.app.Router;

import java.util.Optional;

public final class FxUtil {
    private FxUtil() {}

    public static void error(String message) { alert(Alert.AlertType.ERROR, "Error", message); }
    public static void info (String message) { alert(Alert.AlertType.INFORMATION, "Info", message); }

    public static boolean confirm(String message) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        styleAlert(a);
        Optional<ButtonType> r = a.showAndWait();
        return r.isPresent() && r.get() == ButtonType.YES;
    }

    private static void alert(Alert.AlertType type, String title, String message) {
        Alert a = new Alert(type, message, ButtonType.OK);
        a.setHeaderText(null);
        a.setTitle(title);
        styleAlert(a);
        a.showAndWait();
    }

    private static void styleAlert(Alert a) {
        DialogPane pane = a.getDialogPane();
        if (Router.stage() != null && Router.stage().getScene() != null) {
            pane.getStylesheets().addAll(Router.stage().getScene().getStylesheets());
        }
        pane.getStyleClass().add("app-bg");
        pane.setStyle("-fx-background-color: #160a3a;");
        Region content = (Region) pane.lookup(".content.label");
        if (content != null) content.setStyle("-fx-text-fill: #f4f1ff;");
    }

    public static void hookHand(Node n) { n.setStyle((n.getStyle() == null ? "" : n.getStyle()) + ";-fx-cursor: hand;"); }
}
