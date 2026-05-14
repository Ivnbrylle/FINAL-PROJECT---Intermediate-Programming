package lifetrack.ui.section;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import lifetrack.app.Session;
import lifetrack.dao.AchievementDao;
import lifetrack.dao.BiographyDao;
import lifetrack.dao.ChallengeDao;
import lifetrack.dao.ExperienceDao;
import lifetrack.model.User;
import lifetrack.util.FxIcons;

import java.util.List;
import java.util.Random;

public class HomeSection extends HBox {

    public HomeSection() {
        super(20);
        User user = Session.get();
        if (user == null) return;
        setFillHeight(true);

        VBox left  = buildLeft(user);
        VBox right = buildRail(user);
        HBox.setHgrow(left, Priority.ALWAYS);
        getChildren().addAll(left, right);
    }

    private VBox buildLeft(User user) {
        Label eyebrow = new Label("✦  DASHBOARD");
        eyebrow.getStyleClass().add("eyebrow");

        String bioName = new BiographyDao().findByUserId(user.getId()).getFullName();
        String first   = bioName.isBlank() ? user.getUsername() : bioName.split(" ")[0];
        Label title = new Label("Welcome back, " + first + ".");
        title.getStyleClass().add("h2");

        Label sub = new Label("A quick look at the story you're building. Use the sidebar to dive into any section.");
        sub.getStyleClass().add("body");
        sub.setWrapText(true);
        sub.setMaxWidth(560);

        int experiences  = new ExperienceDao().findAll(user.getId()).size();
        int achievements = new AchievementDao().findAll(user.getId()).size();
        int challenges   = new ChallengeDao().findAll(user.getId()).size();

        HBox stats = new HBox(14,
            statCard("Experiences",  experiences,  "💼"),
            statCard("Achievements", achievements, "🏆"),
            statCard("Challenges",   challenges,   "⚡")
        );
        for (var n : stats.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);

        VBox tips = new VBox(8);
        tips.getStyleClass().add("card");
        Label tipsTitle = new Label("Next steps");
        tipsTitle.getStyleClass().add("h4");
        tips.getChildren().add(tipsTitle);
        if (experiences == 0)  tips.getChildren().add(bullet("Add an experience to build your timeline."));
        if (achievements == 0) tips.getChildren().add(bullet("Log an achievement — every win matters."));
        if (challenges == 0)   tips.getChildren().add(bullet("Capture a challenge and what you learned."));
        if (bioName.isBlank()) tips.getChildren().add(bullet("Visit Profile to fill in your background."));
        if (experiences > 0 && achievements > 0 && challenges > 0 && !bioName.isBlank())
            tips.getChildren().add(bullet("Looking good! Click 'View My Story' for a quote refresh."));

        VBox content = new VBox(18, eyebrow, title, sub, stats, tips);
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.getStyleClass().add("scroll-pane");
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox box = new VBox(sp);
        VBox.setVgrow(sp, Priority.ALWAYS);
        return box;
    }

    private VBox buildRail(User user) {
        VBox rail = new VBox(16, buildQuoteCard(user), buildGrowthCard(user));
        rail.setMinWidth(300); rail.setMaxWidth(320);
        rail.setPadding(new Insets(0, 0, 0, 16));
        return rail;
    }

    private VBox buildQuoteCard(User user) {
        Label title = new Label("Journey Reflection");
        title.getStyleClass().add("h4");
        Label mark  = new Label("“"); mark.getStyleClass().add("quote-mark");

        // Pick a quote from the user's own data (lessons / solutions / descriptions)
        java.util.List<String[]> pool = new java.util.ArrayList<>();
        for (var c : new ChallengeDao().findAll(user.getId())) {
            if (!c.getLesson().isBlank())   pool.add(new String[]{ c.getLesson(),   user.getUsername() });
            if (!c.getSolution().isBlank()) pool.add(new String[]{ c.getSolution(), user.getUsername() });
        }
        for (var a : new AchievementDao().findAll(user.getId())) {
            if (!a.getDescription().isBlank()) pool.add(new String[]{ a.getDescription(), user.getUsername() });
        }
        String[] pick = pool.isEmpty()
            ? new String[]{
                "Every step, challenge, and lesson has been a part of my story. And I'm just getting started.",
                "BioByte"
            }
            : pool.get(new Random().nextInt(pool.size()));

        Label body  = new Label(pick[0]);
        body.getStyleClass().add("quote-body");
        body.setWrapText(true);
        Label author = new Label("— " + pick[1]);
        author.getStyleClass().add("quote-author");

        VBox box = new VBox(8, title, mark, body, author);
        box.getStyleClass().add("quote-card");
        return box;
    }

    private VBox buildGrowthCard(User user) {
        StackPane icon = new StackPane(FxIcons.bolt());
        ((SVGPath) ((Group) icon.getChildren().get(0)).getChildren().get(0)).setStyle("-fx-fill: white;");
        icon.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #a855f7, #ec4899);" +
            "-fx-background-radius: 14;" +
            "-fx-min-width: 42; -fx-min-height: 42;" +
            "-fx-max-width: 42; -fx-max-height: 42;"
        );
        Label title = new Label("Keep Growing"); title.getStyleClass().add("h4");
        HBox header = new HBox(12, icon, new VBox(2, title));
        header.setAlignment(Pos.CENTER_LEFT);

        int exp  = new ExperienceDao().findAll(user.getId()).size();
        int ach  = new AchievementDao().findAll(user.getId()).size();
        int chal = new ChallengeDao().findAll(user.getId()).size();
        int total = exp + ach + chal;
        String hint;
        if (total == 0)         hint = "Your story is a blank page. Add your first experience to start a timeline.";
        else if (exp == 0)      hint = "Try adding an experience — a job, internship, or major life event.";
        else if (ach == 0)      hint = "Time to celebrate a win. Add an achievement you're proud of.";
        else if (chal == 0)     hint = "Capture a challenge you overcame — the lessons become quotes here.";
        else                    hint = "You've logged " + total + " moments. Your journey inspires others. Keep sharing, keep growing!";

        Label body = new Label(hint);
        body.getStyleClass().add("body");
        body.setWrapText(true);

        VBox box = new VBox(10, header, body);
        box.getStyleClass().add("card");
        return box;
    }

    private Label bullet(String text) {
        Label l = new Label("•  " + text);
        l.getStyleClass().add("body");
        l.setWrapText(true);
        return l;
    }

    private VBox statCard(String label, int value, String emoji) {
        Label e = new Label(emoji); e.setStyle("-fx-font-size: 22px;");
        Label num = new Label(String.valueOf(value));
        num.setStyle("-fx-text-fill: -bb-text; -fx-font-size: 30px; -fx-font-weight: 900;");
        Label name = new Label(label); name.getStyleClass().add("muted");
        HBox row = new HBox(12, e, new VBox(0, num, name));
        row.setAlignment(Pos.CENTER_LEFT);
        VBox box = new VBox(row);
        box.getStyleClass().add("card");
        box.setPadding(new Insets(18));
        return box;
    }
}
