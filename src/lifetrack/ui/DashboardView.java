package lifetrack.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import lifetrack.app.Router;
import lifetrack.app.Session;
import lifetrack.model.User;
import lifetrack.ui.section.AchievementsSection;
import lifetrack.ui.section.ChallengesSection;
import lifetrack.ui.section.ExperiencesSection;
import lifetrack.ui.section.HomeSection;
import lifetrack.ui.section.ProfileSection;
import lifetrack.util.FxIcons;
import lifetrack.util.FxUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DashboardView {

    private final StackPane contentHost = new StackPane();
    private final StringProperty breadcrumb = new SimpleStringProperty("Dashboard");
    private final Map<String, Button> navItems = new LinkedHashMap<>();

    public Parent build() {
        User user = Session.get();
        if (user == null) { Router.toLogin(); return new StackPane(); }

        BorderPane root = new BorderPane();
        root.getStyleClass().add("dashboard-aura");

        root.setLeft(buildSidebar(user));
        root.setCenter(buildMainArea());
        return root;
    }

    private VBox buildSidebar(User user) {
        VBox side = new VBox(4);
        side.getStyleClass().add("sidebar");

        Label logo = new Label("BIOBYTE", brandMark());
        logo.setGraphicTextGap(8);
        logo.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: 900; -fx-letter-spacing: 0.05em;");
        Label tag = new Label("REAL PEOPLE, REAL STORIES");
        tag.setStyle("-fx-text-fill: #8a7fb0; -fx-font-size: 9px; -fx-font-weight: 800; -fx-letter-spacing: 0.18em;");
        VBox brand = new VBox(2, logo, tag);
        brand.setPadding(new Insets(0, 0, 22, 6));

        Button dash = navItem("Dashboard",   FxIcons.home(),      HomeSection::new);
        Button exp  = navItem("Experiences", FxIcons.briefcase(), ExperiencesSection::new);
        Button ach  = navItem("Achievements", FxIcons.trophy(),   AchievementsSection::new);
        Button chal = navItem("Challenges",  FxIcons.bolt(),      ChallengesSection::new);
        Button prof = navItem("Profile",     FxIcons.user(),      ProfileSection::new);

        navItems.put("Dashboard", dash);
        navItems.put("Experiences", exp);
        navItems.put("Achievements", ach);
        navItems.put("Challenges", chal);
        navItems.put("Profile", prof);

        Region grow = new Region(); VBox.setVgrow(grow, Priority.ALWAYS);

        StackPane avatar = new StackPane(new Label(initials(user.getUsername())));
        avatar.getStyleClass().add("avatar-circle");
        Label name  = new Label(user.getUsername());
        name.setStyle("-fx-text-fill: -bb-text; -fx-font-weight: 700; -fx-font-size: 13px;");
        Label email = new Label("Signed in");
        email.getStyleClass().add("muted");
        VBox userText = new VBox(0, name, email);
        Button logout = new Button("", FxIcons.ellipsis());
        logout.getStyleClass().add("btn-icon");
        logout.setOnAction(e -> doLogout());
        HBox chip = new HBox(10, avatar, userText, fill(), logout);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.getStyleClass().add("user-chip");

        side.getChildren().addAll(brand, dash, exp, ach, chal, prof, grow, chip);
        select("Dashboard");
        return side;
    }

    private Region fill() { Region r = new Region(); HBox.setHgrow(r, Priority.ALWAYS); return r; }

    private Button navItem(String label, Group icon, Supplier<Parent> contentFactory) {
        SVGPath path = (SVGPath) icon.getChildren().get(0);
        path.getStyleClass().add("sidebar-icon");
        Button b = new Button(label, icon);
        b.getStyleClass().add("sidebar-item");
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setOnAction(e -> {
            select(label);
            breadcrumb.set(label);
            Parent c = contentFactory.get();
            contentHost.getChildren().setAll(c);
        });
        return b;
    }

    private void select(String label) {
        for (Map.Entry<String, Button> e : navItems.entrySet()) {
            e.getValue().getStyleClass().remove("active");
            if (e.getKey().equals(label)) e.getValue().getStyleClass().add("active");
        }
        if (label.equals("Dashboard")) {
            contentHost.getChildren().setAll(new HomeSection());
        }
    }

    private BorderPane buildMainArea() {
        BorderPane wrap = new BorderPane();
        wrap.setPadding(new Insets(20, 28, 28, 28));

        Label home = new Label("Home");
        home.getStyleClass().add("muted");
        Label sep = new Label("›"); sep.getStyleClass().add("muted");
        Label section = new Label();
        section.textProperty().bind(breadcrumb);
        section.setStyle("-fx-text-fill: -bb-text; -fx-font-size: 13px; -fx-font-weight: 600;");
        HBox crumbs = new HBox(8, home, sep, section);
        crumbs.setAlignment(Pos.CENTER_LEFT);

        Button view = new Button("View My Story  →");
        view.getStyleClass().addAll("btn", "btn-violet");
        view.setOnAction(e -> navItems.get("Dashboard").fire());
        Region s = new Region(); HBox.setHgrow(s, Priority.ALWAYS);
        HBox top = new HBox(12, crumbs, s, view);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(0, 0, 16, 0));

        contentHost.setPadding(new Insets(0));
        contentHost.setAlignment(Pos.TOP_LEFT);

        wrap.setTop(top);
        wrap.setCenter(contentHost);
        return wrap;
    }

    private void doLogout() {
        if (!FxUtil.confirm("Log out?")) return;
        Session.clear();
        Router.toLanding();
    }

    private static String initials(String s) {
        if (s == null || s.isBlank()) return "?";
        return String.valueOf(Character.toUpperCase(s.charAt(0)));
    }

    private Group brandMark() {
        Group g = FxIcons.dna();
        ((SVGPath) g.getChildren().get(0)).setStyle("-fx-fill: white;");
        return g;
    }
}
