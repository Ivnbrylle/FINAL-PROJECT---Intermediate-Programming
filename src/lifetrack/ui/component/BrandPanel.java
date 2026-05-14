package lifetrack.ui.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import lifetrack.util.FxIcons;

/**
 * The purple-hero left-hand pane shown on the login + register screens.
 * Identical content on both — extracted here to avoid duplication.
 */
public class BrandPanel {

    public static VBox build() {
        VBox root = new VBox(22);
        root.setPadding(new Insets(56, 56, 56, 56));
        root.setAlignment(Pos.TOP_LEFT);
        root.getStyleClass().add("hero-pane");
        root.setMinWidth(440);
        root.setPrefWidth(540);

        root.getChildren().addAll(
            brandLogoBlock(),
            brandTitle(),
            brandTag(),
            blurb(),
            featureList(),
            footerHint()
        );
        return root;
    }

    private static StackPane brandLogoBlock() {
        StackPane logo = new StackPane();
        logo.setMinSize(170, 170);
        logo.setMaxSize(170, 170);
        logo.setAlignment(Pos.CENTER);
        logo.setStyle(
            "-fx-background-color: #0e0a1f;" +
            "-fx-background-radius: 18;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.55), 18, 0.3, 0, 4);"
        );

        // DNA-ish svg
        SVGPath dna = new SVGPath();
        dna.setContent("M14 4c0 6-8 8-8 14M14 4l2 2-2 2M6 4c0 6 8 8 8 14M9 8h6M9 12h6M9 16h6");
        dna.setStyle("-fx-stroke: white; -fx-stroke-width: 1.6; -fx-fill: transparent;");

        Label tag = new Label("BioByte");
        tag.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: 800;");

        VBox box = new VBox(8, new Group(dna), tag);
        box.setAlignment(Pos.CENTER);
        logo.getChildren().add(box);
        return logo;
    }

    private static Label brandTitle() {
        Label l = new Label("BIOBYTE");
        l.getStyleClass().add("brand-title");
        return l;
    }

    private static Label brandTag() {
        Label l = new Label("REAL PEOPLE, REAL STORIES");
        l.getStyleClass().add("brand-subtitle");
        return l;
    }

    private static Label blurb() {
        Label l = new Label(
            "BioByte is a platform for real people to share real stories. " +
            "We store personal life stories as small digital units that together " +
            "build a complete biography."
        );
        l.getStyleClass().add("brand-tagline");
        l.setWrapText(true);
        l.setMaxWidth(420);
        return l;
    }

    private static VBox featureList() {
        VBox list = new VBox(10);
        list.getChildren().addAll(
            FeatureCard.build("S", 1, "Private & Secure",
                "Encrypted stories protect your most personal moments."),
            FeatureCard.build("O", 2, "Your Life, Organized",
                "Meaningful chapters and milestones in one clear timeline."),
            FeatureCard.build("G", 3, "Built for Generations",
                "Preserve your legacy so future generations can remember.")
        );
        list.setPadding(new Insets(6, 0, 6, 0));
        list.setMaxWidth(460);
        return list;
    }

    private static HBox footerHint() {
        Group lock = FxIcons.shield();
        ((SVGPath) lock.getChildren().get(0)).setStyle("-fx-fill: white; -fx-opacity: 0.7;");
        Label l = new Label("Your data is secure and private.");
        l.setStyle("-fx-text-fill: white; -fx-opacity: 0.85; -fx-font-size: 12px;");
        HBox box = new HBox(8, lock, l);
        box.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(box, new Insets(20, 0, 0, 0));
        // push to bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        VBox vb = new VBox(spacer, box);
        // We return an HBox but caller adds a Region for spacing instead.
        return box;
    }
}
