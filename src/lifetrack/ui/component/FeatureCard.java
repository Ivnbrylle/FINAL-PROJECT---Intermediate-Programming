package lifetrack.ui.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class FeatureCard {

    public static HBox build(String letter, int badgeIndex, String title, String body) {
        StackPane badge = new StackPane(new Label(letter));
        badge.getStyleClass().addAll("feature-badge", "b" + Math.max(1, Math.min(4, badgeIndex)));

        Label t = new Label(title);
        t.getStyleClass().add("h4");
        Label b = new Label(body);
        b.getStyleClass().add("body");
        b.setWrapText(true);

        VBox text = new VBox(2, t, b);
        text.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(text, Priority.ALWAYS);

        HBox row = new HBox(14, badge, text);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("feature-pill");
        return row;
    }
}
