package lifetrack.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lifetrack.app.Router;
import lifetrack.ui.component.FeatureCard;
import lifetrack.util.FxIcons;

import java.util.Random;

public class LandingView {

    public Parent build() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-bg");

        root.setTop(buildTopNav());
        root.setCenter(buildContent());
        root.setBottom(buildFooter());

        // Decorative particles on the right side
        StackPane stack = new StackPane(buildParticleLayer(), root);
        stack.getStyleClass().add("app-bg");
        return stack;
    }

    private HBox buildTopNav() {
        Label brand = brandLockup();

        Button home   = navLink("Home", true);
        Button exp    = navLink("Experiences", false);
        Button ach    = navLink("Achievements", false);
        Button chal   = navLink("Challenges", false);
        Region left   = new Region(); HBox.setHgrow(left, Priority.ALWAYS);
        Region right  = new Region(); HBox.setHgrow(right, Priority.ALWAYS);

        Button signIn = new Button("Sign In", FxIcons.user());
        signIn.getStyleClass().addAll("btn", "btn-ghost");
        signIn.setOnAction(e -> Router.toLogin());

        HBox links = new HBox(34, home, exp, ach, chal);
        links.setAlignment(Pos.CENTER);

        HBox nav = new HBox(20, brand, left, links, right, signIn);
        nav.getStyleClass().add("top-nav");
        nav.setAlignment(Pos.CENTER_LEFT);
        return nav;
    }

    private Label brandLockup() {
        Group icon = FxIcons.dna();
        icon.setStyle("-fx-scale-x: 1.2; -fx-scale-y: 1.2;");
        Label l = new Label("BioByte", icon);
        l.setGraphicTextGap(10);
        l.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: 800;");
        return l;
    }

    private Button navLink(String text, boolean active) {
        Button b = new Button(text);
        b.getStyleClass().add("nav-link");
        if (active) b.getStyleClass().add("active");
        return b;
    }

    private HBox buildContent() {
        VBox left = new VBox(18);
        left.setPadding(new Insets(20, 60, 30, 80));
        left.setAlignment(Pos.TOP_LEFT);
        left.setMaxWidth(640);

        Label welcomePill = new Label("✨  Welcome to BioByte");
        welcomePill.setStyle(
            "-fx-background-color: rgba(168, 85, 247, 0.12);" +
            "-fx-background-radius: 1000;" +
            "-fx-border-color: rgba(168, 85, 247, 0.3);" +
            "-fx-border-radius: 1000;" +
            "-fx-border-width: 1;" +
            "-fx-padding: 6 14 6 14;" +
            "-fx-text-fill: -bb-text;" +
            "-fx-font-size: 12px; -fx-font-weight: 600;"
        );

        Label bigTitle = new Label("BIOBYTE");
        bigTitle.setStyle(
            "-fx-font-size: 88px; -fx-font-weight: 900;" +
            "-fx-text-fill: linear-gradient(to right, white, #c084fc, #a855f7);" +
            "-fx-letter-spacing: 0.1em;"
        );

        Label sub = new Label("REAL PEOPLE, REAL STORIES");
        sub.setStyle("-fx-text-fill: -bb-text-dim; -fx-font-size: 16px; -fx-font-weight: 700; -fx-letter-spacing: 0.4em;");

        Region underline = new Region();
        underline.setMinHeight(2); underline.setMaxHeight(2); underline.setMinWidth(80); underline.setMaxWidth(80);
        underline.setStyle("-fx-background-color: linear-gradient(to right, #ec4899, transparent);");

        Label blurb = new Label(
            "BioByte is a platform for real people to share real stories. " +
            "We store personal life stories as small digital units that " +
            "together build a complete biography."
        );
        blurb.getStyleClass().add("body");
        blurb.setWrapText(true);
        blurb.setMaxWidth(540);

        Button getStarted = new Button("Get Started  →");
        getStarted.getStyleClass().addAll("btn", "btn-primary");
        getStarted.setStyle("-fx-font-size: 14px; -fx-padding: 14 28 14 28;");
        getStarted.setOnAction(e -> Router.toRegister());

        Button learn = new Button("Learn More  ⓘ");
        learn.getStyleClass().addAll("btn", "btn-ghost");
        learn.setStyle("-fx-font-size: 14px; -fx-padding: 14 28 14 28;");
        learn.setOnAction(e -> lifetrack.util.FxUtil.info(
            "BioByte stores your biography as a series of small encrypted records — your background, "
            + "experiences, achievements, and the challenges that shaped you. Sign in or create an "
            + "account to start writing your story."));

        HBox ctas = new HBox(14, getStarted, learn);

        left.getChildren().addAll(welcomePill, bigTitle, sub, underline, blurb, ctas);

        HBox features = new HBox(14,
            FeatureCard.build("P", 1, "Private & Secure",
                "Your stories are encrypted and only you control who sees them."),
            FeatureCard.build("Y", 2, "Your Life, Organized",
                "Break life into meaningful moments and keep them beautifully organized."),
            FeatureCard.build("B", 3, "Built for Generations",
                "Preserve your legacy and share it with the people who matter."),
            FeatureCard.build("R", 4, "Real Stories",
                "From everyday moments to life-changing events — your story, your way.")
        );
        for (var n : features.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);
        features.setPadding(new Insets(20, 80, 20, 80));

        VBox column = new VBox(36, left, features);
        column.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(column, Priority.ALWAYS);

        return new HBox(column);
    }

    private HBox buildFooter() {
        Label dot = new Label("●"); dot.setStyle("-fx-text-fill: #6d28d9; -fx-font-size: 10px;");
        Label secure = new Label("Your data is secure and private.");
        secure.getStyleClass().add("muted");
        Button learn = new Button("Learn more");
        learn.getStyleClass().add("btn-link");
        learn.setOnAction(e -> lifetrack.util.FxUtil.info(
            "All passwords are hashed with PBKDF2-HmacSHA256 + per-user salt. "
            + "Your biography is stored locally in an embedded SQLite database that "
            + "never leaves this machine."));

        Label copyright = new Label("© 2026 BioByte. All rights reserved.");
        copyright.getStyleClass().add("muted");

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox box = new HBox(10, dot, secure, learn, spacer, copyright);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(16, 60, 20, 80));
        return box;
    }

    /** Decorative animated-looking dots layer on the right side, behind content. */
    private Pane buildParticleLayer() {
        Pane pane = new Pane();
        pane.setMouseTransparent(true);
        Random r = new Random(42);
        for (int i = 0; i < 90; i++) {
            double radius = 1 + r.nextDouble() * 2.5;
            Circle c = new Circle(radius);
            double x = 600 + r.nextDouble() * 700;
            double y = 80  + r.nextDouble() * 560;
            c.setLayoutX(x); c.setLayoutY(y);
            double tone = 0.4 + r.nextDouble() * 0.6;
            c.setFill(Color.color(0.85, 0.5, 0.95, tone));
            c.setEffect(new javafx.scene.effect.Glow(0.6));
            pane.getChildren().add(c);
        }
        return pane;
    }
}
