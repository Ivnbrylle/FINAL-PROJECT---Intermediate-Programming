package lifetrack.ui.section;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import lifetrack.app.Session;
import lifetrack.dao.ChallengeDao;
import lifetrack.model.Challenge;
import lifetrack.model.User;
import lifetrack.util.FxIcons;
import lifetrack.util.FxUtil;

import java.util.List;

/**
 * Challenges page. Follows the supplied mockup:
 *
 *  - hero banner with mountain-climber theme
 *  - a curated catalog of common challenges grouped by School / Work /
 *    Personal / Social
 *  - "What You Gain From Challenges" strip at the bottom
 *  - personal strip at the top so the user can still record their own
 *    stories (a rubric requirement)
 */
public class ChallengesSection extends VBox {

    private final User user = Session.get();
    private final ChallengeDao dao = new ChallengeDao();
    private final FlowPane personalGrid = new FlowPane(12, 12);

    public ChallengesSection() {
        super(20);
        setFillWidth(true);

        ScrollPane sp = new ScrollPane(buildContent());
        sp.setFitToWidth(true);
        sp.getStyleClass().add("scroll-pane");
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(sp, Priority.ALWAYS);
        getChildren().add(sp);
    }

    private VBox buildContent() {
        VBox col = new VBox(22);

        col.getChildren().addAll(
            buildHero(),
            buildPersonalSection(),
            buildCategory("🎓  SCHOOL CHALLENGES",  "#a78bfa", schoolChallenges()),
            buildCategory("💼  WORK CHALLENGES",    "#60a5fa", workChallenges()),
            buildCategory("💜  PERSONAL CHALLENGES","#ec4899", personalChallenges()),
            buildCategory("👥  SOCIAL CHALLENGES",  "#34d399", socialChallenges()),
            buildGainStrip()
        );
        refreshPersonal();
        return col;
    }

    private StackPane buildHero() {
        Label eyebrow = new Label("✦  CHALLENGES");
        eyebrow.getStyleClass().add("eyebrow");

        Label title = new Label("Challenges");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 56px; -fx-font-weight: 900;");

        Label sub = new Label(
            "Real challenges. Real lessons.\n" +
            "Every step forward makes you stronger."
        );
        sub.getStyleClass().add("body");
        sub.setStyle("-fx-text-fill: -bb-text-dim; -fx-font-size: 14px;");

        VBox text = new VBox(8, eyebrow, title, sub);
        text.setAlignment(Pos.CENTER_LEFT);
        text.setPadding(new Insets(28, 28, 28, 28));

        // Decorative mountain on the right
        StackPane illustration = mountainIllustration();
        illustration.setPickOnBounds(false);

        Region grow = new Region(); HBox.setHgrow(grow, Priority.ALWAYS);
        HBox row = new HBox(text, grow, illustration);
        row.setAlignment(Pos.CENTER_LEFT);

        StackPane wrap = new StackPane(row);
        wrap.getStyleClass().add("hero-banner");
        return wrap;
    }

    private StackPane mountainIllustration() {
        // Triangle "mountain" shape with a flag, all in line art
        SVGPath mountain = new SVGPath();
        mountain.setContent("M10 110 L70 30 L100 70 L120 50 L160 110 Z");
        mountain.setStyle(
            "-fx-fill: rgba(168, 85, 247, 0.35);" +
            "-fx-stroke: #c084fc; -fx-stroke-width: 2;"
        );
        SVGPath inner = new SVGPath();
        inner.setContent("M70 30 L82 60 L92 50 L100 70");
        inner.setStyle("-fx-fill: transparent; -fx-stroke: rgba(255,255,255,0.45); -fx-stroke-width: 1.5;");
        SVGPath flag = new SVGPath();
        flag.setContent("M120 50 L120 18 M120 18 L140 22 L120 28 Z");
        flag.setStyle("-fx-fill: #fbbf24; -fx-stroke: #fbbf24; -fx-stroke-width: 1.8;");

        Group g = new Group(mountain, inner, flag);
        StackPane box = new StackPane(g);
        box.setMinSize(200, 130); box.setMaxSize(200, 130);
        box.setPadding(new Insets(14, 28, 14, 0));
        box.setAlignment(Pos.CENTER_RIGHT);
        return box;
    }

    // -------- personal section --------
    private VBox buildPersonalSection() {
        Label h = sectionHeader("✦  MY PERSONAL CHALLENGES", "#a855f7");
        Label sub = new Label("Your own stories — what you've faced and what it taught you.");
        sub.getStyleClass().add("muted");

        Button add = new Button("+ Add Personal Challenge", FxIcons.plus());
        add.getStyleClass().addAll("btn", "btn-violet");
        ((SVGPath) ((Group) add.getGraphic()).getChildren().get(0))
            .setStyle("-fx-fill: white; -fx-stroke: white; -fx-stroke-width: 2;");
        add.setOnAction(e -> openEditor(null));

        HBox row = new HBox(12, sub, fill(), add);
        row.setAlignment(Pos.CENTER_LEFT);

        personalGrid.setPrefWrapLength(900);
        VBox box = new VBox(10, h, row, personalGrid);
        return box;
    }

    private void refreshPersonal() {
        personalGrid.getChildren().clear();
        List<Challenge> mine = dao.findAll(user.getId());
        if (mine.isEmpty()) {
            Label empty = new Label("Click '+ Add Personal Challenge' to capture one of your own.");
            empty.getStyleClass().add("muted");
            empty.setPadding(new Insets(8, 0, 0, 0));
            personalGrid.getChildren().add(empty);
            return;
        }
        for (Challenge c : mine) personalGrid.getChildren().add(personalCard(c));
    }

    private VBox personalCard(Challenge c) {
        StackPane icon = new StackPane(FxIcons.bolt());
        ((SVGPath) ((Group) icon.getChildren().get(0)).getChildren().get(0)).setStyle("-fx-fill: #ec4899;");
        icon.getStyleClass().add("category-icon");

        Label cat = new Label(c.getCategory().toUpperCase());
        cat.setStyle("-fx-text-fill: #ec4899; -fx-font-size: 10px; -fx-font-weight: 800; -fx-letter-spacing: 0.1em;");
        Label desc = new Label(c.getDescription());
        desc.getStyleClass().add("h4");
        desc.setWrapText(true);

        VBox details = new VBox(4);
        if (!c.getSolution().isBlank()) {
            Label h = new Label("How I handled it"); h.setStyle("-fx-text-fill: -bb-text; -fx-font-size: 12px; -fx-font-weight: 700;");
            Label v = new Label(c.getSolution()); v.getStyleClass().add("body"); v.setWrapText(true);
            details.getChildren().addAll(h, v);
        }
        if (!c.getLesson().isBlank()) {
            Label h = new Label("Lesson learned");
            h.setStyle("-fx-text-fill: -bb-pink; -fx-font-size: 12px; -fx-font-weight: 700; -fx-padding: 6 0 0 0;");
            Label v = new Label(c.getLesson()); v.getStyleClass().add("body"); v.setWrapText(true);
            details.getChildren().addAll(h, v);
        }

        Button menu = new Button("", FxIcons.ellipsis());
        menu.getStyleClass().add("btn-icon");
        ContextMenu cm = new ContextMenu();
        MenuItem edit = new MenuItem("Edit");  edit.setOnAction(e -> openEditor(c));
        MenuItem del  = new MenuItem("Delete"); del.setOnAction(e -> {
            if (FxUtil.confirm("Delete this challenge?")) { dao.delete(c.getId(), user.getId()); refreshPersonal(); }
        });
        cm.getItems().addAll(edit, del);
        menu.setOnAction(e -> cm.show(menu, javafx.geometry.Side.BOTTOM, 0, 0));

        HBox header = new HBox(10, icon, new VBox(2, cat, desc), fill(), menu);
        header.setAlignment(Pos.TOP_LEFT);

        VBox box = new VBox(10, header, details);
        box.getStyleClass().add("card");
        box.setMinWidth(290); box.setPrefWidth(310); box.setMaxWidth(330);
        return box;
    }

    // -------- catalog category --------
    private VBox buildCategory(String header, String color, List<CatalogEntry> entries) {
        Label h = sectionHeader(header, color);

        FlowPane grid = new FlowPane(12, 12);
        grid.setPrefWrapLength(900);
        for (CatalogEntry e : entries) grid.getChildren().add(catalogCard(e, color));

        VBox v = new VBox(10, h, grid);
        return v;
    }

    private VBox catalogCard(CatalogEntry e, String color) {
        StackPane icon = new StackPane(e.makeIcon());
        ((SVGPath) ((Group) icon.getChildren().get(0)).getChildren().get(0))
            .setStyle("-fx-fill: " + color + ";");
        icon.getStyleClass().add("category-icon");

        Label t = new Label(e.title);
        t.getStyleClass().add("h4");
        t.setWrapText(true);
        Label d = new Label(e.body);
        d.getStyleClass().add("body");
        d.setWrapText(true);

        VBox box = new VBox(8, icon, t, d);
        box.getStyleClass().add("catalog-card");
        box.setMinWidth(220); box.setPrefWidth(240); box.setMaxWidth(280);
        return box;
    }

    private Label sectionHeader(String text, String color) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 14px; -fx-font-weight: 800; -fx-letter-spacing: 0.18em;");
        return l;
    }

    // -------- gain strip --------
    private VBox buildGainStrip() {
        Label header = sectionHeader("⭐  WHAT YOU GAIN FROM CHALLENGES", "#fbbf24");

        HBox row = new HBox(20,
            gainCard("🛡", "Resilience",     "You learn to bounce back stronger."),
            gainCard("💡", "Problem Solving", "You become better at solving real problems."),
            gainCard("📈", "Growth Mindset",  "You see challenges as opportunities to grow."),
            gainCard("👥", "Confidence",      "You build confidence with every step."),
            gainCard("🏆", "Success",         "You move closer to the life you dream of.")
        );
        for (var n : row.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);
        row.setAlignment(Pos.TOP_CENTER);

        Label quote = new Label(
            "“  Challenges are what make life interesting\n" +
            "      and overcoming them is what makes life meaningful.  ”"
        );
        quote.setStyle("-fx-text-fill: #c5b9e6; -fx-font-style: italic; -fx-font-size: 14px;");
        quote.setWrapText(true);
        quote.setAlignment(Pos.CENTER);
        quote.setMaxWidth(Double.MAX_VALUE);

        VBox box = new VBox(16, header, row, quote);
        box.getStyleClass().add("gain-strip");
        return box;
    }

    private VBox gainCard(String emoji, String title, String body) {
        Label e = new Label(emoji); e.setStyle("-fx-font-size: 28px;");
        Label t = new Label(title); t.getStyleClass().add("h4");
        Label b = new Label(body);  b.getStyleClass().add("body"); b.setWrapText(true);
        VBox v = new VBox(6, e, t, b);
        v.setAlignment(Pos.TOP_CENTER);
        v.setStyle("-fx-alignment: top-center;");
        b.setStyle("-fx-text-alignment: center;");
        t.setStyle("-fx-text-fill: -bb-text;");
        return v;
    }

    // -------- editor dialog --------
    private void openEditor(Challenge existing) {
        Dialog<Challenge> dlg = new Dialog<>();
        dlg.setTitle(existing == null ? "Add Challenge" : "Edit Challenge");
        if (getScene() != null) dlg.getDialogPane().getStylesheets().addAll(getScene().getStylesheets());
        dlg.getDialogPane().setStyle("-fx-background-color: #160a3a;");

        ComboBox<String> cat = new ComboBox<>();
        cat.getItems().addAll("Personal struggle", "Career challenge", "Obstacle overcome",
                              "Failure / lesson", "Other");
        TextArea desc   = new TextArea(); desc.setPromptText("What was the challenge? *"); desc.setPrefRowCount(3); desc.setWrapText(true);
        TextArea sol    = new TextArea(); sol.setPromptText("How did you handle it?");      sol.setPrefRowCount(3); sol.setWrapText(true);
        TextArea lesson = new TextArea(); lesson.setPromptText("Lesson learned");           lesson.setPrefRowCount(2); lesson.setWrapText(true);

        if (existing != null) {
            cat.setValue(existing.getCategory());
            desc.setText(existing.getDescription());
            sol.setText(existing.getSolution());
            lesson.setText(existing.getLesson());
        } else cat.setValue("Personal struggle");

        GridPane g = new GridPane();
        g.setHgap(10); g.setVgap(10); g.setPadding(new Insets(16));
        int r = 0;
        g.add(label("Category"),  0, r); g.add(cat, 1, r++);
        g.add(label("Challenge *"),0, r); g.add(desc, 1, r++);
        g.add(label("How handled"),0, r); g.add(sol, 1, r++);
        g.add(label("Lesson"),    0, r); g.add(lesson, 1, r++);
        ColumnConstraints c1 = new ColumnConstraints(); c1.setMinWidth(110); c1.setHalignment(HPos.RIGHT);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setHgrow(Priority.ALWAYS); c2.setMinWidth(360);
        g.getColumnConstraints().addAll(c1, c2);

        dlg.getDialogPane().setContent(g);
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);
        Button okBtn = (Button) dlg.getDialogPane().lookupButton(saveBtn);
        okBtn.getStyleClass().addAll("btn", "btn-primary");

        dlg.setResultConverter(bt -> {
            if (bt != saveBtn) return null;
            if (desc.getText().trim().isEmpty()) { FxUtil.error("Please describe the challenge."); return null; }
            Challenge out = existing != null ? existing : new Challenge();
            out.setUserId(user.getId());
            out.setCategory(cat.getValue());
            out.setDescription(desc.getText().trim());
            out.setSolution(sol.getText().trim());
            out.setLesson(lesson.getText().trim());
            return out;
        });

        dlg.showAndWait().ifPresent(out -> {
            if (existing == null) dao.insert(out); else dao.update(out);
            refreshPersonal();
        });
    }

    private Label label(String t) { Label l = new Label(t); l.getStyleClass().add("body"); return l; }
    private Region fill() { Region r = new Region(); HBox.setHgrow(r, Priority.ALWAYS); return r; }

    // -------- catalog data --------
    private record CatalogEntry(String title, String body, java.util.function.Supplier<Group> iconSupplier) {
        Group makeIcon() { return iconSupplier.get(); }
    }

    private List<CatalogEntry> schoolChallenges() {
        return List.of(
            new CatalogEntry("Academic Pressure",     "Managing studies, grades, and expectations.", FxIcons::cap),
            new CatalogEntry("Tight Deadlines",       "Handling multiple submissions and back-to-back tasks.", FxIcons::bolt),
            new CatalogEntry("Group Project Conflicts","Different ideas and communication can cause tension.", FxIcons::group),
            new CatalogEntry("Online Class Struggles","Staying focused and motivated during online learning.", FxIcons::code),
            new CatalogEntry("Fear of Failing",       "The fear of not doing enough can be overwhelming.", FxIcons::shield),
            new CatalogEntry("Balancing it All",      "Balancing studies, personal life, and mental well-being.", FxIcons::star)
        );
    }

    private List<CatalogEntry> workChallenges() {
        return List.of(
            new CatalogEntry("First-Job Anxiety",     "Stepping into a new environment can be intimidating.", FxIcons::user),
            new CatalogEntry("Difficult Clients",     "Handling tough situations and managing expectations.", FxIcons::group),
            new CatalogEntry("Work-Life Balance",     "Balancing work responsibilities and personal life.", FxIcons::shield),
            new CatalogEntry("Communication Issues",  "Misunderstandings can affect team productivity.", FxIcons::code),
            new CatalogEntry("Meeting Deadlines",     "High expectations and urgent tasks from managers.", FxIcons::bolt),
            new CatalogEntry("Burnout",               "Long hours and constant pressure can lead to exhaustion.", FxIcons::trophy)
        );
    }

    private List<CatalogEntry> personalChallenges() {
        return List.of(
            new CatalogEntry("Self-Doubt",         "Doubting yourself can stop you from growing.", FxIcons::user),
            new CatalogEntry("Overthinking",       "Too much worry drains energy and happiness.", FxIcons::sparkle),
            new CatalogEntry("Lack of Motivation", "Finding the drive to start can be difficult.", FxIcons::bolt),
            new CatalogEntry("Adapting to Change", "New environments and situations can be challenging.", FxIcons::shield),
            new CatalogEntry("Financial Struggles","Managing expenses while studying or working.", FxIcons::briefcase),
            new CatalogEntry("Setting Goals",      "Staying consistent and focused on long-term goals.", FxIcons::star)
        );
    }

    private List<CatalogEntry> socialChallenges() {
        return List.of(
            new CatalogEntry("Public Speaking Fear","Fear of speaking in front of a crowd.", FxIcons::user),
            new CatalogEntry("Making Friends",      "Hard to connect in new environments.", FxIcons::group),
            new CatalogEntry("Peer Pressure",       "Trying to fit in and making the right choices.", FxIcons::shield),
            new CatalogEntry("Team Communication",  "Different personalities can lead to conflicts.", FxIcons::code),
            new CatalogEntry("Leadership Struggles","Taking responsibility and leading a team is not easy.", FxIcons::trophy),
            new CatalogEntry("Feeling Left Behind", "Seeing others succeed can sometimes be hard.", FxIcons::bolt)
        );
    }
}
