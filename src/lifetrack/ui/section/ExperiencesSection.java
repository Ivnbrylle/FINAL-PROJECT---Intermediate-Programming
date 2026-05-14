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
import lifetrack.dao.ExperienceDao;
import lifetrack.model.Experience;
import lifetrack.model.User;
import lifetrack.util.FxIcons;
import lifetrack.util.FxUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExperiencesSection extends VBox {

    private final User user = Session.get();
    private final ExperienceDao dao = new ExperienceDao();
    private final VBox timelineBox = new VBox(0);

    public ExperiencesSection() {
        super(18);
        setFillWidth(true);

        Label eyebrow = new Label("✦  EXPERIENCES");
        eyebrow.getStyleClass().add("eyebrow");
        Label title = new Label("My Experiences");
        title.getStyleClass().add("h2");
        Label sub = new Label(
            "A timeline of my professional journey and personal growth. " +
            "Each experience shaped me into who I am today."
        );
        sub.getStyleClass().add("body");
        sub.setWrapText(true);
        sub.setMaxWidth(620);

        Button add = new Button("+ Add Experience", FxIcons.plus());
        add.getStyleClass().addAll("btn", "btn-violet");
        ((SVGPath) ((Group) add.getGraphic()).getChildren().get(0))
            .setStyle("-fx-fill: white; -fx-stroke: white; -fx-stroke-width: 2;");
        add.setOnAction(e -> openEditor(null));

        ScrollPane sp = new ScrollPane(timelineBox);
        sp.setFitToWidth(true);
        sp.getStyleClass().add("scroll-pane");
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(sp, Priority.ALWAYS);

        getChildren().addAll(eyebrow, title, sub, add, sp);
        refresh();
    }

    private void refresh() {
        timelineBox.getChildren().clear();
        List<Experience> list = dao.findAll(user.getId());
        if (list.isEmpty()) {
            Label empty = new Label("No experiences yet. Click \"+ Add Experience\" to start your timeline.");
            empty.getStyleClass().add("body");
            empty.setPadding(new Insets(40, 0, 0, 0));
            timelineBox.getChildren().add(empty);
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            timelineBox.getChildren().add(buildRow(list.get(i), i == list.size() - 1));
        }
    }

    private HBox buildRow(Experience e, boolean last) {
        Label dates = new Label(formatDates(e));
        dates.setStyle("-fx-text-fill: #a855f7; -fx-font-weight: 700; -fx-font-size: 13px;");
        Label loc = new Label(e.getOrganization().isBlank() ? " " : e.getOrganization());
        loc.getStyleClass().add("muted");
        VBox dateCol = new VBox(2, dates, loc);
        dateCol.setMinWidth(140); dateCol.setMaxWidth(140);
        dateCol.setAlignment(Pos.TOP_RIGHT);
        dateCol.setPadding(new Insets(20, 20, 0, 0));

        Region dot = new Region();
        dot.getStyleClass().add("timeline-dot");
        Region rail = new Region();
        rail.getStyleClass().add("timeline-rail");
        rail.setMinHeight(120);
        VBox.setVgrow(rail, Priority.ALWAYS);
        VBox railCol = new VBox(4, dot, rail);
        railCol.setAlignment(Pos.TOP_CENTER);
        railCol.setMinWidth(28); railCol.setMaxWidth(28);
        railCol.setPadding(new Insets(28, 0, 0, 0));
        if (last) rail.setVisible(false);

        StackPane iconBox = new StackPane(FxIcons.briefcase());
        ((SVGPath) ((Group) iconBox.getChildren().get(0)).getChildren().get(0)).setStyle("-fx-fill: #a855f7;");
        iconBox.getStyleClass().add("timeline-icon");

        Label title = new Label(e.getTitle());
        title.getStyleClass().add("h4");
        Label org = new Label(e.getOrganization());
        org.setStyle("-fx-text-fill: #a855f7; -fx-font-weight: 700;");
        Label desc = new Label(e.getResponsibilities());
        desc.getStyleClass().add("body");
        desc.setWrapText(true);

        HBox tags = new HBox(8);
        tags.setPadding(new Insets(6, 0, 0, 0));
        addTag(tags, e.getType());
        if (!e.getStartDate().isBlank() || !e.getEndDate().isBlank()) addTag(tags, "Timeline");
        if (!e.getNotes().isBlank()) addTag(tags, "Notes");

        Button menu = new Button("", FxIcons.ellipsis());
        menu.getStyleClass().add("btn-icon");
        ContextMenu cm = new ContextMenu();
        MenuItem edit = new MenuItem("Edit"); edit.setOnAction(a -> openEditor(e));
        MenuItem del  = new MenuItem("Delete"); del.setOnAction(a -> doDelete(e));
        cm.getItems().addAll(edit, del);
        menu.setOnAction(a -> cm.show(menu, javafx.geometry.Side.BOTTOM, 0, 0));

        VBox textCol = new VBox(4, title, org, desc, tags);
        HBox.setHgrow(textCol, Priority.ALWAYS);

        HBox card = new HBox(14, iconBox, textCol, menu);
        card.setAlignment(Pos.TOP_LEFT);
        card.getStyleClass().add("timeline-card");
        VBox cardCol = new VBox(card);
        cardCol.setPadding(new Insets(10, 0, 22, 16));
        HBox.setHgrow(cardCol, Priority.ALWAYS);

        HBox row = new HBox(dateCol, railCol, cardCol);
        row.setFillHeight(true);
        return row;
    }

    private void addTag(HBox tags, String text) {
        if (text == null || text.isBlank()) return;
        Label t = new Label(text);
        t.getStyleClass().add("tag");
        tags.getChildren().add(t);
    }

    private String formatDates(Experience e) {
        String s = e.getStartDate(), end = e.getEndDate();
        if (s.isBlank() && end.isBlank()) return "—";
        if (end.isBlank()) return s + " - Present";
        return s + " - " + end;
    }

    private void doDelete(Experience e) {
        if (!FxUtil.confirm("Delete this experience? This cannot be undone.")) return;
        dao.delete(e.getId(), user.getId());
        refresh();
    }

    private void openEditor(Experience existing) {
        Dialog<Experience> dlg = new Dialog<>();
        dlg.setTitle(existing == null ? "Add Experience" : "Edit Experience");
        if (getScene() != null) dlg.getDialogPane().getStylesheets().addAll(getScene().getStylesheets());
        dlg.getDialogPane().setStyle("-fx-background-color: #160a3a;");

        ComboBox<String> type = new ComboBox<>();
        type.getItems().addAll("Job", "Internship", "Training", "Major Life Event", "Other");
        TextField title = new TextField();   title.setPromptText("Title / Role *");
        TextField org   = new TextField();   org.setPromptText("Organization");
        DatePicker start = new DatePicker(); start.setPromptText("Pick a start date"); start.setConverter(isoDateConverter());
        DatePicker end   = new DatePicker(); end.setPromptText("Pick an end date");    end.setConverter(isoDateConverter());
        CheckBox  presentBox = new CheckBox("Currently here (Present)");
        presentBox.setStyle("-fx-text-fill: -bb-text;");
        presentBox.selectedProperty().addListener((obs, was, isNow) -> {
            end.setDisable(isNow);
            if (isNow) end.setValue(null);
        });
        TextArea resp   = new TextArea(); resp.setPromptText("Responsibilities / description"); resp.setPrefRowCount(4); resp.setWrapText(true);
        TextArea notes  = new TextArea(); notes.setPromptText("Notes"); notes.setPrefRowCount(2); notes.setWrapText(true);

        if (existing != null) {
            type.setValue(existing.getType());
            title.setText(existing.getTitle());
            org.setText(existing.getOrganization());
            start.setValue(parseDate(existing.getStartDate()));
            end.setValue(parseDate(existing.getEndDate()));
            presentBox.setSelected(existing.getEndDate().isBlank() && !existing.getStartDate().isBlank());
            resp.setText(existing.getResponsibilities());
            notes.setText(existing.getNotes());
        } else {
            type.setValue("Job");
        }

        HBox endRow = new HBox(10, end, presentBox);
        endRow.setAlignment(Pos.CENTER_LEFT);

        GridPane g = new GridPane();
        g.setHgap(10); g.setVgap(10); g.setPadding(new Insets(16));
        int r = 0;
        g.add(label("Type"), 0, r);         g.add(type,   1, r++);
        g.add(label("Title *"), 0, r);      g.add(title,  1, r++);
        g.add(label("Organization"), 0, r); g.add(org,    1, r++);
        g.add(label("Start date"), 0, r);   g.add(start,  1, r++);
        g.add(label("End date"), 0, r);     g.add(endRow, 1, r++);
        g.add(label("Description"), 0, r);  g.add(resp,   1, r++);
        g.add(label("Notes"), 0, r);        g.add(notes,  1, r++);
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
            if (title.getText().trim().isEmpty()) {
                FxUtil.error("Title is required."); return null;
            }
            Experience out = existing != null ? existing : new Experience();
            out.setUserId(user.getId());
            out.setType(type.getValue());
            out.setTitle(title.getText().trim());
            out.setOrganization(org.getText().trim());
            out.setStartDate(start.getValue() == null ? "" : start.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE));
            if (presentBox.isSelected())              out.setEndDate("");
            else if (end.getValue() == null)          out.setEndDate("");
            else                                      out.setEndDate(end.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE));
            out.setResponsibilities(resp.getText().trim());
            out.setNotes(notes.getText().trim());
            return out;
        });

        dlg.showAndWait().ifPresent(out -> {
            if (existing == null) dao.insert(out);
            else dao.update(out);
            refresh();
        });
    }

    private Label label(String s) { Label l = new Label(s); l.getStyleClass().add("body"); return l; }

    private static StringConverter<LocalDate> isoDateConverter() {
        return new StringConverter<>() {
            final DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
            @Override public String toString(LocalDate d) { return d == null ? "" : d.format(fmt); }
            @Override public LocalDate fromString(String s) {
                try { return s == null || s.isBlank() ? null : LocalDate.parse(s, fmt); }
                catch (Exception e) { return null; }
            }
        };
    }

    private static LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE); }
        catch (Exception e) { return null; }
    }
}
