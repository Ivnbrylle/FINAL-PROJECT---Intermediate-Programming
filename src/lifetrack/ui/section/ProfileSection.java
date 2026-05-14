package lifetrack.ui.section;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import lifetrack.app.Session;
import lifetrack.dao.BiographyDao;
import lifetrack.model.Biography;
import lifetrack.model.User;
import lifetrack.util.FxIcons;
import lifetrack.util.FxUtil;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProfileSection extends VBox {

    private final User user = Session.get();
    private final BiographyDao dao = new BiographyDao();
    private Biography bio;

    // Hero
    private final ImageView avatar = new ImageView();
    private final Label heroName  = new Label();
    private final Label heroOcc   = new Label();
    private final Label heroQuote = new Label();

    // Edit Profile form
    private final TextField fullName   = new TextField();
    private final TextField email      = new TextField();
    private final TextArea  bioField   = new TextArea();
    private final TextField location   = new TextField();
    private final TextField website    = new TextField();
    private final DatePicker dob       = new DatePicker();
    private final ComboBox<String> occupation = new ComboBox<>();

    // Social links (LinkedIn, Twitter, Instagram, YouTube)
    private final Map<String, TextField> socialFields = new LinkedHashMap<>();

    // Completion + Security
    private final ProgressBar completion = new ProgressBar(0);
    private final Label completionLabel  = new Label("0%");
    private final VBox completionItems   = new VBox(6);

    public ProfileSection() {
        super(18);
        setFillWidth(true);
        if (user == null) return;

        bio = dao.findByUserId(user.getId());

        ScrollPane sp = new ScrollPane(buildContent());
        sp.setFitToWidth(true);
        sp.getStyleClass().add("scroll-pane");
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(sp, Priority.ALWAYS);
        getChildren().add(sp);

        load();
    }

    // -------------------------------------------------------------- content

    private VBox buildContent() {
        VBox col = new VBox(20);
        col.getChildren().addAll(buildHero(), buildEditAndSocialRow(), buildCompletionAndSecurityRow());
        return col;
    }

    // ------- hero block -------
    private StackPane buildHero() {
        // Circular avatar with glow
        StackPane avatarRing = new StackPane();
        avatarRing.setMinSize(180, 180); avatarRing.setMaxSize(180, 180);
        Circle ring = new Circle(90, 90, 90);
        ring.setStyle("-fx-fill: linear-gradient(to bottom right, #6d28d9, #ec4899); -fx-effect: dropshadow(gaussian, rgba(168, 85, 247, 0.7), 18, 0.4, 0, 0);");

        Region placeholder = new Region();
        placeholder.setStyle("-fx-background-color: #1a1130; -fx-background-radius: 1000;");
        placeholder.setMinSize(170, 170); placeholder.setMaxSize(170, 170);

        avatar.setFitWidth(170); avatar.setFitHeight(170); avatar.setPreserveRatio(false);
        Circle clip = new Circle(85, 85, 85);
        avatar.setClip(clip);

        // Camera button overlaid
        Button cam = new Button();
        Group camIcon = FxIcons.eye();
        ((SVGPath) camIcon.getChildren().get(0)).setStyle("-fx-fill: white;");
        cam.setGraphic(camIcon);
        cam.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #a855f7, #6d28d9);" +
            "-fx-background-radius: 1000;" +
            "-fx-padding: 8;" +
            "-fx-cursor: hand;"
        );
        cam.setOnAction(e -> pickImage());
        StackPane camWrap = new StackPane(cam);
        camWrap.setAlignment(Pos.BOTTOM_RIGHT);
        camWrap.setMaxSize(180, 180);
        camWrap.setMouseTransparent(false);

        avatarRing.getChildren().addAll(ring, placeholder, avatar, camWrap);

        heroName.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: 900;");
        heroOcc.setStyle("-fx-text-fill: -bb-text-dim; -fx-font-size: 14px;");

        heroQuote.setWrapText(true);
        heroQuote.setStyle(
            "-fx-text-fill: -bb-text;" +
            "-fx-font-style: italic;" +
            "-fx-font-size: 13px;" +
            "-fx-background-color: rgba(168, 85, 247, 0.12);" +
            "-fx-background-radius: 14;" +
            "-fx-padding: 12 16 12 16;"
        );
        heroQuote.setMaxWidth(440);

        VBox text = new VBox(8, heroName, heroOcc, heroQuote);
        text.setAlignment(Pos.CENTER_LEFT);
        text.setPadding(new Insets(20, 28, 20, 12));

        HBox row = new HBox(20, avatarRing, text);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(20, 28, 20, 28));

        StackPane wrap = new StackPane(row);
        wrap.getStyleClass().add("hero-banner");
        wrap.setAlignment(Pos.CENTER_LEFT);
        return wrap;
    }

    private HBox buildEditAndSocialRow() {
        VBox edit = buildEditCard();
        VBox social = buildSocialCard();
        HBox.setHgrow(edit, Priority.ALWAYS);
        HBox row = new HBox(20, edit, social);
        return row;
    }

    private VBox buildEditCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");

        Label title = new Label("Edit Profile");
        title.getStyleClass().add("h4");

        GridPane g = new GridPane();
        g.setHgap(12); g.setVgap(10);

        fullName.setPromptText("Full Name");
        email.setPromptText("name@example.com");
        bioField.setPromptText("A short bio — your story in one paragraph.");
        bioField.setPrefRowCount(3);
        bioField.setWrapText(true);
        location.setPromptText("City, Country");
        website.setPromptText("yourdomain.com");
        dob.setPromptText("Pick a date");
        dob.setConverter(isoDateConverter());
        occupation.getItems().addAll(
            "Content Creator", "Student", "Software Engineer", "Designer",
            "Educator", "Healthcare", "Entrepreneur", "Other"
        );
        occupation.setEditable(true);
        occupation.setMaxWidth(Double.MAX_VALUE);

        int r = 0;
        g.add(formLabel("Full Name"),   0, r); g.add(fullName, 1, r);
        g.add(formLabel("Email Address"), 2, r); g.add(email,  3, r++);
        g.add(formLabel("Bio"),          0, r, 4, 1);  r++;
        g.add(bioField, 0, r, 4, 1); r++;
        g.add(formLabel("Location"),    0, r); g.add(location, 1, r);
        g.add(formLabel("Website"),     2, r); g.add(website,  3, r++);
        g.add(formLabel("Date of Birth"), 0, r); g.add(dob, 1, r);
        g.add(formLabel("Occupation"),  2, r); g.add(occupation, 3, r++);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints c = new ColumnConstraints();
            if (i % 2 == 0) { c.setMinWidth(110); c.setHalignment(HPos.LEFT); }
            else            { c.setHgrow(Priority.ALWAYS); c.setMinWidth(160); c.setFillWidth(true); }
            g.getColumnConstraints().add(c);
        }

        Button cancel = new Button("Cancel");
        cancel.getStyleClass().addAll("btn", "btn-ghost");
        cancel.setOnAction(e -> { if (FxUtil.confirm("Discard unsaved changes?")) load(); });

        Button save = new Button("Save Changes");
        save.getStyleClass().addAll("btn", "btn-primary");
        save.setOnAction(e -> doSave());

        Region s = new Region(); HBox.setHgrow(s, Priority.ALWAYS);
        HBox actions = new HBox(10, cancel, s, save);
        actions.setAlignment(Pos.CENTER_RIGHT);

        // After GridPane add() of bio area — set its column span: already done via g.add(bioField, 0, r, 4, 1)

        card.getChildren().addAll(title, g, actions);
        return card;
    }

    private VBox buildSocialCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setMinWidth(330); card.setMaxWidth(380);

        Label title = new Label("Social Links");
        title.getStyleClass().add("h4");
        Region s = new Region(); HBox.setHgrow(s, Priority.ALWAYS);
        Button add = new Button("+ Add New");
        add.getStyleClass().addAll("btn", "btn-ghost");
        add.setOnAction(e -> FxUtil.info("Add new social platforms by editing the fields below — they're saved with your profile."));
        HBox head = new HBox(8, title, s, add);
        head.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().add(head);

        addSocialField(card, "LinkedIn",  "🔗");
        addSocialField(card, "Twitter",   "🐦");
        addSocialField(card, "Instagram", "📷");
        addSocialField(card, "YouTube",   "▶");
        return card;
    }

    private void addSocialField(VBox card, String name, String emoji) {
        Label e = new Label(emoji); e.setStyle("-fx-font-size: 18px;");
        Label label = new Label(name);
        label.setStyle("-fx-text-fill: -bb-text; -fx-font-weight: 700; -fx-font-size: 13px;");
        TextField f = new TextField();
        f.setPromptText(name.toLowerCase() + ".com/yourname");
        HBox.setHgrow(f, Priority.ALWAYS);
        Button open = new Button("↗");
        open.getStyleClass().add("btn-icon");
        open.setStyle("-fx-text-fill: -bb-primary;");
        open.setOnAction(ev -> {
            String url = f.getText().trim();
            if (url.isBlank()) { FxUtil.info("Enter a URL first."); return; }
            FxUtil.info("Opens externally in a real browser: " + url);
        });
        socialFields.put(name, f);

        VBox text = new VBox(2, label, f);
        HBox.setHgrow(text, Priority.ALWAYS);
        HBox row = new HBox(10, e, text, open);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 0, 8, 0));
        card.getChildren().add(row);
    }

    private HBox buildCompletionAndSecurityRow() {
        VBox compCard = buildCompletionCard();
        VBox secCard  = buildSecurityCard();
        HBox.setHgrow(compCard, Priority.ALWAYS);
        HBox row = new HBox(20, compCard, secCard);
        return row;
    }

    private VBox buildCompletionCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");

        Label title = new Label("Profile Completion");
        title.getStyleClass().add("h4");

        completion.setMaxWidth(Double.MAX_VALUE);
        completion.setStyle("-fx-accent: linear-gradient(to right, #a855f7, #ec4899);");
        HBox barRow = new HBox(10, completion, completionLabel);
        completionLabel.setStyle("-fx-text-fill: -bb-text; -fx-font-weight: 800;");
        HBox.setHgrow(completion, Priority.ALWAYS);
        barRow.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(title, barRow, completionItems);
        return card;
    }

    private VBox buildSecurityCard() {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        card.setMinWidth(330); card.setMaxWidth(380);

        Label title = new Label("Account Security");
        title.getStyleClass().add("h4");
        card.getChildren().add(title);

        card.getChildren().addAll(
            securityRow("🔑", "Change Password", "Update the password used to sign in.",
                () -> FxUtil.info("Change Password isn't wired up in this build, but the back-end uses PBKDF2-HmacSHA256 + per-user salt.")),
            securityRow("🛡", "Two-Factor Authentication", "Extra protection for your account.",
                () -> FxUtil.info("2FA isn't part of this demo. The underlying password store is already hardened.")),
            securityRow("🕘", "Login Activity", "See where you've signed in from.",
                () -> FxUtil.info("Login activity tracking isn't implemented in this demo.")),
            securityRow("💻", "Manage Devices", "Sign out of other devices.",
                () -> FxUtil.info("Device management isn't implemented in this demo."))
        );
        return card;
    }

    private HBox securityRow(String emoji, String title, String body, Runnable onClick) {
        Label e = new Label(emoji); e.setStyle("-fx-font-size: 16px;");
        Label t = new Label(title); t.setStyle("-fx-text-fill: -bb-text; -fx-font-weight: 700; -fx-font-size: 13px;");
        Label b = new Label(body); b.getStyleClass().add("muted");
        VBox text = new VBox(0, t, b);
        Button go = new Button(">"); go.getStyleClass().add("btn-icon"); go.setOnAction(ev -> onClick.run());
        Region s = new Region(); HBox.setHgrow(s, Priority.ALWAYS);
        HBox row = new HBox(10, e, text, s, go);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 0, 6, 0));
        row.setOnMouseClicked(ev -> onClick.run());
        row.setStyle("-fx-cursor: hand;");
        return row;
    }

    private Label formLabel(String s) {
        Label l = new Label(s);
        l.setStyle("-fx-text-fill: -bb-text-dim; -fx-font-size: 11px; -fx-font-weight: 700; -fx-letter-spacing: 0.05em;");
        return l;
    }

    // ------------------------------------------------------- save / load

    private void load() {
        bio = dao.findByUserId(user.getId());
        fullName.setText(bio.getFullName());
        email.setText(bio.getEmail());
        bioField.setText(bio.getBio());
        location.setText(bio.getLocation());
        website.setText(bio.getWebsite());
        dob.setValue(parseDate(bio.getDateOfBirth()));
        occupation.setValue(bio.getOccupation());

        // Apply socials
        Map<String, String> existing = parseSocials(bio.getSocialLinks());
        for (Map.Entry<String, TextField> e : socialFields.entrySet()) {
            e.getValue().setText(existing.getOrDefault(e.getKey(), ""));
        }

        renderHero();
        setPicture(bio.getProfilePicture());
        refreshCompletion();
    }

    private void doSave() {
        if (fullName.getText().trim().isEmpty()) {
            FxUtil.error("Full Name is required."); fullName.requestFocus(); return;
        }
        String em = email.getText().trim();
        if (!em.isEmpty() && !em.contains("@")) {
            FxUtil.error("That doesn't look like a valid email address."); email.requestFocus(); return;
        }
        bio.setFullName(fullName.getText().trim());
        bio.setEmail(em);
        bio.setBio(bioField.getText().trim());
        bio.setLocation(location.getText().trim());
        bio.setWebsite(website.getText().trim());
        bio.setOccupation(occupation.getValue() == null ? "" : occupation.getValue().trim());
        bio.setDateOfBirth(dob.getValue() == null ? "" : dob.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE));

        StringBuilder s = new StringBuilder();
        for (Map.Entry<String, TextField> e : socialFields.entrySet()) {
            String v = e.getValue().getText().trim();
            if (v.isEmpty()) continue;
            s.append(e.getKey()).append("=").append(v).append("\n");
        }
        bio.setSocialLinks(s.toString().trim());

        dao.save(bio);
        FxUtil.info("Profile saved.");
        renderHero();
        refreshCompletion();
    }

    private void renderHero() {
        String name = bio.getFullName().isBlank() ? user.getUsername() : bio.getFullName();
        heroName.setText(name);
        heroOcc.setText(bio.getOccupation().isBlank() ? "Tell the world what you do" : bio.getOccupation());
        String quote = bio.getBio().isBlank()
            ? "\"I don't just collect moments, I turn them into stories that inspire.\""
            : "\"" + bio.getBio() + "\"";
        heroQuote.setText(quote);
    }

    private void refreshCompletion() {
        completionItems.getChildren().clear();
        List<String[]> checks = new ArrayList<>();
        checks.add(new String[]{"Add Profile Picture", bio.getProfilePicture()});
        checks.add(new String[]{"Add Bio",             bio.getBio()});
        checks.add(new String[]{"Add Social Links",    bio.getSocialLinks()});
        checks.add(new String[]{"Add Email",           bio.getEmail()});
        checks.add(new String[]{"Add Date of Birth",   bio.getDateOfBirth()});

        int done = 0;
        for (String[] check : checks) {
            boolean ok = check[1] != null && !check[1].isBlank();
            if (ok) done++;
            Label dot = new Label(ok ? "✓" : "○");
            dot.setStyle((ok ? "-fx-text-fill: #22c55e;" : "-fx-text-fill: -bb-text-muted;") + " -fx-font-weight: 800;");
            Label t = new Label(check[0]);
            t.setStyle("-fx-text-fill: " + (ok ? "-bb-text" : "-bb-text-muted") + ";");
            HBox r = new HBox(10, dot, t);
            r.setAlignment(Pos.CENTER_LEFT);
            completionItems.getChildren().add(r);
        }
        double pct = (double) done / checks.size();
        completion.setProgress(pct);
        completionLabel.setText(Math.round(pct * 100) + "%");
    }

    // ------------------------------------------------------- avatar

    private void pickImage() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose profile picture");
        fc.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));
        File f = fc.showOpenDialog(getScene() == null ? null : getScene().getWindow());
        if (f != null) {
            bio.setProfilePicture(f.getAbsolutePath());
            dao.save(bio);
            setPicture(f.getAbsolutePath());
            refreshCompletion();
        }
    }

    private void setPicture(String path) {
        if (path == null || path.isBlank()) { avatar.setImage(null); return; }
        try (FileInputStream is = new FileInputStream(path)) {
            avatar.setImage(new Image(is));
        } catch (Exception ex) {
            avatar.setImage(null);
        }
    }

    // ------------------------------------------------------- helpers

    private static Map<String, String> parseSocials(String raw) {
        Map<String, String> out = new LinkedHashMap<>();
        if (raw == null || raw.isBlank()) return out;
        for (String line : raw.split("\\r?\\n")) {
            int idx = line.indexOf('=');
            if (idx <= 0) continue;
            out.put(line.substring(0, idx).trim(), line.substring(idx + 1).trim());
        }
        return out;
    }

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
