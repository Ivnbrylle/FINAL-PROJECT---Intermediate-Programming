package lifetrack.ui.section;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.util.StringConverter;
import lifetrack.app.Session;
import lifetrack.dao.AchievementDao;
import lifetrack.model.Achievement;
import lifetrack.model.User;
import lifetrack.util.FxIcons;
import lifetrack.util.FxUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AchievementsSection extends HBox {

    private static final List<String> PHASES = List.of(
        "High School", "College", "Vocational / Training", "Work Experience", "Other"
    );

    private final User user = Session.get();
    private final AchievementDao dao = new AchievementDao();
    private final VBox timeline = new VBox(20);
    private final VBox stats = new VBox(10);
    private final FlowPane skillsBox = new FlowPane(8, 8);

    public AchievementsSection() {
        super(20);
        setFillHeight(true);

        ScrollPane sp = new ScrollPane(buildContent());
        sp.setFitToWidth(true);
        sp.getStyleClass().add("scroll-pane");
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox left = new VBox(sp);
        VBox.setVgrow(sp, Priority.ALWAYS);
        HBox.setHgrow(left, Priority.ALWAYS);

        VBox rail = buildRail();
        getChildren().addAll(left, rail);

        refresh();
    }

    private VBox buildContent() {
        VBox col = new VBox(20);

        Label eyebrow = new Label("✦  ACHIEVEMENTS");
        eyebrow.getStyleClass().add("eyebrow");
        Label title = new Label("My Achievements");
        title.getStyleClass().add("h2");
        Label sub = new Label("A journey of milestones, growth, and success throughout my academic and professional life.");
        sub.getStyleClass().add("body");
        sub.setWrapText(true);
        sub.setMaxWidth(620);

        Button add = new Button("+ Add Achievement", FxIcons.plus());
        add.getStyleClass().addAll("btn", "btn-violet");
        ((SVGPath) ((Group) add.getGraphic()).getChildren().get(0))
            .setStyle("-fx-fill: white; -fx-stroke: white; -fx-stroke-width: 2;");
        add.setOnAction(e -> openEditor(null));

        StackPane hero = new StackPane(new HBox(
            new VBox(8, eyebrow, title, sub, add) {{
                setPadding(new Insets(28));
            }},
            fillH(),
            trophyIllustration()
        ));
        hero.getStyleClass().add("hero-banner");

        col.getChildren().addAll(hero, timeline);
        return col;
    }

    private StackPane trophyIllustration() {
        SVGPath cup = new SVGPath();
        cup.setContent("M40 30 H100 V52 C100 70 88 80 70 80 C52 80 40 70 40 52 Z M30 38 H40 V58 H30 Z M100 38 H110 V58 H100 Z M55 80 H85 L82 95 H58 Z M50 95 H90 V102 H50 Z");
        cup.setStyle("-fx-fill: rgba(168, 85, 247, 0.45); -fx-stroke: #c084fc; -fx-stroke-width: 2;");
        SVGPath star = new SVGPath();
        star.setContent("M70 38 l4 9 l9 1 l-7 6 l2 9 l-8 -5 l-8 5 l2 -9 l-7 -6 l9 -1 z");
        star.setStyle("-fx-fill: #fbbf24; -fx-stroke: #fbbf24; -fx-stroke-width: 1.5;");

        Group g = new Group(cup, star);
        StackPane box = new StackPane(g);
        box.setMinSize(200, 130); box.setMaxSize(200, 130);
        box.setPadding(new Insets(14, 28, 14, 0));
        box.setAlignment(Pos.CENTER_RIGHT);
        return box;
    }

    private Region fillH() { Region r = new Region(); HBox.setHgrow(r, Priority.ALWAYS); return r; }

    private VBox buildRail() {
        VBox rail = new VBox(16);
        rail.setMinWidth(280); rail.setMaxWidth(300);
        rail.setPadding(new Insets(0, 0, 0, 16));

        VBox statsCard = new VBox(10);
        statsCard.getStyleClass().add("card");
        Label statsTitle = new Label("📊  ACHIEVEMENT STATS");
        statsTitle.setStyle("-fx-text-fill: #a855f7; -fx-font-size: 11px; -fx-font-weight: 800; -fx-letter-spacing: 0.14em;");
        statsCard.getChildren().add(statsTitle);
        statsCard.getChildren().add(stats);

        VBox skillsCard = new VBox(10);
        skillsCard.getStyleClass().add("card");
        Label skillsTitle = new Label("SKILLS EARNED");
        skillsTitle.setStyle("-fx-text-fill: -bb-text-dim; -fx-font-size: 11px; -fx-font-weight: 800; -fx-letter-spacing: 0.14em;");
        skillsBox.setPrefWrapLength(260);
        skillsCard.getChildren().addAll(skillsTitle, skillsBox);

        // Static quote
        Label quoteTitle = new Label("“");
        quoteTitle.setStyle("-fx-text-fill: rgba(168,85,247,0.6); -fx-font-size: 38px; -fx-font-weight: 900;");
        Label quoteBody = new Label("Success is not final, failure is not fatal: It is the courage to continue that counts.");
        quoteBody.getStyleClass().add("quote-body");
        quoteBody.setWrapText(true);
        Label quoteAuthor = new Label("– Winston Churchill");
        quoteAuthor.getStyleClass().add("quote-author");
        VBox quote = new VBox(6, quoteTitle, quoteBody, quoteAuthor);
        quote.getStyleClass().add("quote-card");

        rail.getChildren().addAll(statsCard, skillsCard, quote);
        return rail;
    }

    private void refresh() {
        timeline.getChildren().clear();
        stats.getChildren().clear();
        skillsBox.getChildren().clear();

        List<Achievement> all = dao.findAll(user.getId());

        // Group by phase, preserving the phase order in PHASES
        Map<String, List<Achievement>> byPhase = new LinkedHashMap<>();
        for (String p : PHASES) byPhase.put(p, new ArrayList<>());
        for (Achievement a : all) {
            String phase = a.getPhase();
            if (!byPhase.containsKey(phase)) byPhase.put(phase, new ArrayList<>());
            byPhase.get(phase).add(a);
        }

        if (all.isEmpty()) {
            Label empty = new Label("No achievements yet. Click \"+ Add Achievement\" to log your first win.");
            empty.getStyleClass().add("body");
            empty.setPadding(new Insets(20, 0, 0, 0));
            timeline.getChildren().add(empty);
        } else {
            for (Map.Entry<String, List<Achievement>> entry : byPhase.entrySet()) {
                if (entry.getValue().isEmpty()) continue;
                timeline.getChildren().add(buildPhaseRow(entry.getKey(), entry.getValue()));
            }
        }

        // Stats
        int total = all.size();
        long certs = all.stream().filter(a -> "Certification".equalsIgnoreCase(a.getCategory())).count();
        long leadership = all.stream().filter(a ->
            a.getCategory().equalsIgnoreCase("Honor") ||
            (a.getSkills() != null && a.getSkills().toLowerCase().contains("leadership"))).count();
        long projects = all.stream().filter(a -> "Project".equalsIgnoreCase(a.getCategory())).count();
        stats.getChildren().addAll(
            statRow("🏆", total       + "+",  "Total Achievements"),
            statRow("📜", certs       + "+",  "Certificates Earned"),
            statRow("👥", leadership  + "+",  "Leadership Roles"),
            statRow("💼", projects    + "+",  "Projects Completed")
        );

        // Skills (union of comma-separated skill tags)
        LinkedHashSet<String> uniqueSkills = new LinkedHashSet<>();
        for (Achievement a : all) {
            if (a.getSkills() == null || a.getSkills().isBlank()) continue;
            for (String s : a.getSkills().split(",")) {
                String tag = s.trim();
                if (!tag.isEmpty()) uniqueSkills.add(tag);
            }
        }
        if (uniqueSkills.isEmpty()) {
            Label hint = new Label("Skills you tag on achievements will show up here.");
            hint.getStyleClass().add("muted");
            hint.setWrapText(true);
            skillsBox.getChildren().add(hint);
        } else {
            for (String s : uniqueSkills) skillsBox.getChildren().add(skillPill(s));
        }
    }

    private HBox buildPhaseRow(String phase, List<Achievement> items) {
        // Year-range chip + icon column
        StackPane icon = new StackPane(iconForPhase(phase));
        icon.getStyleClass().add("phase-icon");
        Label years = new Label(yearRangeFor(items));
        years.setStyle("-fx-text-fill: #a855f7; -fx-font-size: 12px; -fx-font-weight: 700;");
        VBox leftCol = new VBox(8, icon, years);
        leftCol.setAlignment(Pos.TOP_CENTER);
        leftCol.setMinWidth(120); leftCol.setMaxWidth(120);
        leftCol.setPadding(new Insets(8, 12, 0, 0));

        // Right side: phase header + grid of cards
        Label header = new Label(phase.toUpperCase() + " ACHIEVEMENTS");
        header.setStyle("-fx-text-fill: #a855f7; -fx-font-size: 12px; -fx-font-weight: 800; -fx-letter-spacing: 0.14em;");
        FlowPane grid = new FlowPane(12, 12);
        grid.setPrefWrapLength(700);
        for (Achievement a : items) grid.getChildren().add(achievementCard(a));
        VBox right = new VBox(10, header, grid);
        HBox.setHgrow(right, Priority.ALWAYS);

        HBox row = new HBox(leftCol, right);
        row.setFillHeight(true);
        return row;
    }

    private Group iconForPhase(String phase) {
        Group g = switch (phase) {
            case "High School"             -> FxIcons.cap();
            case "College"                 -> FxIcons.cap();
            case "Vocational / Training"   -> FxIcons.code();
            case "Work Experience"         -> FxIcons.briefcase();
            default                        -> FxIcons.star();
        };
        ((SVGPath) g.getChildren().get(0)).setStyle("-fx-fill: white;");
        return g;
    }

    private String yearRangeFor(List<Achievement> items) {
        Integer min = null, max = null;
        for (Achievement a : items) {
            Integer y = parseYear(a.getDateEarned());
            if (y == null) continue;
            if (min == null || y < min) min = y;
            if (max == null || y > max) max = y;
        }
        if (min == null) return "—";
        if (min.equals(max)) return min + "";
        return min + " - " + max;
    }

    private Integer parseYear(String s) {
        if (s == null || s.length() < 4) return null;
        try { return Integer.parseInt(s.substring(0, 4)); }
        catch (NumberFormatException ex) { return null; }
    }

    private VBox achievementCard(Achievement a) {
        StackPane icon = new StackPane(FxIcons.trophy());
        ((SVGPath) ((Group) icon.getChildren().get(0)).getChildren().get(0)).setStyle("-fx-fill: #fbbf24;");
        icon.getStyleClass().add("category-icon");

        Label t = new Label(a.getTitle().isBlank() ? "(untitled)" : a.getTitle());
        t.getStyleClass().add("h4");
        t.setWrapText(true);
        Label d = new Label(a.getDescription());
        d.getStyleClass().add("body");
        d.setWrapText(true);

        Label year = new Label(a.getDateEarned() == null || a.getDateEarned().isBlank() ? ""
                                                                                       : a.getDateEarned());
        year.getStyleClass().add("muted");

        Button menu = new Button("", FxIcons.ellipsis());
        menu.getStyleClass().add("btn-icon");
        ContextMenu cm = new ContextMenu();
        MenuItem edit = new MenuItem("Edit");   edit.setOnAction(e -> openEditor(a));
        MenuItem del  = new MenuItem("Delete"); del.setOnAction(e -> {
            if (FxUtil.confirm("Delete this achievement?")) { dao.delete(a.getId(), user.getId()); refresh(); }
        });
        cm.getItems().addAll(edit, del);
        menu.setOnAction(e -> cm.show(menu, javafx.geometry.Side.BOTTOM, 0, 0));

        HBox header = new HBox(10, icon);
        Region s = new Region(); HBox.setHgrow(s, Priority.ALWAYS);
        header.getChildren().addAll(s, menu);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox box = new VBox(8, header, t, d, year);
        box.getStyleClass().add("catalog-card");
        box.setMinWidth(180); box.setPrefWidth(220); box.setMaxWidth(260);
        return box;
    }

    private HBox statRow(String emoji, String value, String label) {
        Label e = new Label(emoji); e.setStyle("-fx-font-size: 22px;");
        Label v = new Label(value);
        v.setStyle("-fx-text-fill: #a855f7; -fx-font-size: 24px; -fx-font-weight: 900;");
        Label l = new Label(label); l.getStyleClass().add("muted");
        VBox text = new VBox(0, v, l);
        HBox row = new HBox(10, e, text);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Label skillPill(String s) {
        Label l = new Label(s);
        l.getStyleClass().add("tag");
        return l;
    }

    // -------- editor dialog (now with DatePicker for date_earned) --------
    private void openEditor(Achievement existing) {
        Dialog<Achievement> dlg = new Dialog<>();
        dlg.setTitle(existing == null ? "Add Achievement" : "Edit Achievement");
        if (getScene() != null) dlg.getDialogPane().getStylesheets().addAll(getScene().getStylesheets());
        dlg.getDialogPane().setStyle("-fx-background-color: #160a3a;");

        ComboBox<String> phase = new ComboBox<>();
        phase.getItems().addAll(PHASES);
        ComboBox<String> cat   = new ComboBox<>();
        cat.getItems().addAll("Award", "Honor", "Certification", "Project", "Other");
        TextField title = new TextField(); title.setPromptText("Title *");
        DatePicker date = new DatePicker();
        date.setPromptText("Pick a date");
        date.setConverter(isoDateConverter());
        TextArea desc   = new TextArea(); desc.setPromptText("Description"); desc.setPrefRowCount(4); desc.setWrapText(true);
        TextField skills = new TextField(); skills.setPromptText("Skills (comma separated, e.g. Leadership, Programming)");

        if (existing != null) {
            phase.setValue(existing.getPhase());
            cat.setValue(existing.getCategory());
            title.setText(existing.getTitle());
            date.setValue(parseDate(existing.getDateEarned()));
            desc.setText(existing.getDescription());
            skills.setText(existing.getSkills());
        } else {
            phase.setValue("College");
            cat.setValue("Award");
        }

        GridPane g = new GridPane();
        g.setHgap(10); g.setVgap(10); g.setPadding(new Insets(16));
        int r = 0;
        g.add(label("Life phase"), 0, r); g.add(phase, 1, r++);
        g.add(label("Category"),   0, r); g.add(cat,   1, r++);
        g.add(label("Title *"),    0, r); g.add(title, 1, r++);
        g.add(label("Date earned"),0, r); g.add(date,  1, r++);
        g.add(label("Description"),0, r); g.add(desc,  1, r++);
        g.add(label("Skills"),     0, r); g.add(skills,1, r++);
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
            if (title.getText().trim().isEmpty()) { FxUtil.error("Title is required."); return null; }
            Achievement out = existing != null ? existing : new Achievement();
            out.setUserId(user.getId());
            out.setPhase(phase.getValue());
            out.setCategory(cat.getValue());
            out.setTitle(title.getText().trim());
            out.setDateEarned(date.getValue() == null ? "" : date.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE));
            out.setDescription(desc.getText().trim());
            out.setSkills(skills.getText().trim());
            return out;
        });

        dlg.showAndWait().ifPresent(out -> {
            if (existing == null) dao.insert(out); else dao.update(out);
            refresh();
        });
    }

    static StringConverter<LocalDate> isoDateConverter() {
        return new StringConverter<>() {
            final DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
            @Override public String toString(LocalDate d) { return d == null ? "" : d.format(fmt); }
            @Override public LocalDate fromString(String s) {
                try { return s == null || s.isBlank() ? null : LocalDate.parse(s, fmt); }
                catch (Exception e) { return null; }
            }
        };
    }

    static LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE); }
        catch (Exception e) { return null; }
    }

    private Label label(String t) { Label l = new Label(t); l.getStyleClass().add("body"); return l; }
}
