package lifetrack.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import lifetrack.app.Router;
import lifetrack.auth.AuthService;
import lifetrack.ui.component.BrandPanel;
import lifetrack.util.FxIcons;
import lifetrack.util.FxUtil;

import java.util.Arrays;

public class RegisterView {

    private final AuthService auth = new AuthService();

    public Parent build() {
        HBox root = new HBox();
        root.getStyleClass().add("app-bg");

        VBox left = BrandPanel.build();
        Region right = buildRight();
        HBox.setHgrow(right, Priority.ALWAYS);

        root.getChildren().addAll(left, right);
        return root;
    }

    private Region buildRight() {
        StackPane wrap = new StackPane();
        wrap.getStyleClass().add("split-right");
        wrap.setPadding(new Insets(40, 80, 40, 80));

        VBox form = new VBox(16);
        form.setMaxWidth(460);
        form.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Create Your Account");
        title.getStyleClass().add("h2");

        Label sub = new Label("Start your journey. It only takes a minute.");
        sub.getStyleClass().add("muted");

        TextField username = new TextField();
        username.setPromptText("Username");
        HBox userShell = wrapInput(FxIcons.user(), username, null);

        PasswordField pwd = new PasswordField();
        pwd.setPromptText("Password");
        TextField pwdShown = new TextField();
        pwdShown.setPromptText("Password");
        pwdShown.managedProperty().bind(pwdShown.visibleProperty());
        pwd.managedProperty().bind(pwd.visibleProperty());
        pwdShown.setVisible(false);
        Button showPwd = new Button("SHOW");
        showPwd.getStyleClass().add("show-toggle");
        showPwd.setOnAction(e -> toggleReveal(pwd, pwdShown, showPwd));
        StackPane pwdSwap = new StackPane(pwd, pwdShown);
        HBox pwdShell = wrapInput(FxIcons.key(), pwdSwap, showPwd);

        PasswordField confirm = new PasswordField();
        confirm.setPromptText("Confirm Password");
        TextField confirmShown = new TextField();
        confirmShown.setPromptText("Confirm Password");
        confirmShown.managedProperty().bind(confirmShown.visibleProperty());
        confirm.managedProperty().bind(confirm.visibleProperty());
        confirmShown.setVisible(false);
        Button showConfirm = new Button("SHOW");
        showConfirm.getStyleClass().add("show-toggle");
        showConfirm.setOnAction(e -> toggleReveal(confirm, confirmShown, showConfirm));
        StackPane confirmSwap = new StackPane(confirm, confirmShown);
        HBox confirmShell = wrapInput(FxIcons.key(), confirmSwap, showConfirm);

        Button create = new Button("Create Account");
        create.getStyleClass().addAll("btn", "btn-primary");
        create.setMaxWidth(Double.MAX_VALUE);
        create.setStyle("-fx-font-size: 14px; -fx-padding: 14 28 14 28;");
        create.setOnAction(e -> doCreate(
            username.getText(),
            (pwdShown.isVisible() ? pwdShown.getText() : pwd.getText()),
            (confirmShown.isVisible() ? confirmShown.getText() : confirm.getText())
        ));

        HBox divider = orDivider();

        Button google = new Button("Continue with Google");
        google.getStyleClass().addAll("btn", "btn-ghost");
        google.setMaxWidth(Double.MAX_VALUE);
        google.setStyle("-fx-font-size: 13px; -fx-padding: 12 28 12 28;");
        google.setOnAction(e -> FxUtil.info(
            "Google sign-up isn't wired up in this build. Create a local account above — "
            + "your data stays on this machine."));

        HBox bottom = new HBox(4);
        bottom.setAlignment(Pos.CENTER);
        Label q = new Label("Already have an account?");
        q.getStyleClass().add("muted");
        Button signInLink = new Button("Sign In");
        signInLink.getStyleClass().add("btn-link");
        signInLink.setStyle("-fx-font-weight: 800;");
        signInLink.setOnAction(e -> Router.toLogin());
        bottom.getChildren().addAll(q, signInLink);

        // Submit on enter
        username.setOnAction(e -> create.fire());
        pwd.setOnAction(e -> create.fire());
        confirm.setOnAction(e -> create.fire());

        form.getChildren().addAll(
            title, sub,
            spacer(12),
            userShell, pwdShell, confirmShell,
            spacer(6),
            create,
            spacer(4), divider, spacer(4),
            google,
            spacer(8), bottom
        );

        wrap.getChildren().add(form);
        return wrap;
    }

    private void toggleReveal(PasswordField pf, TextField tf, Button toggle) {
        boolean reveal = !tf.isVisible();
        tf.setVisible(reveal);
        pf.setVisible(!reveal);
        if (reveal) { tf.setText(pf.getText()); tf.requestFocus(); }
        else        { pf.setText(tf.getText()); pf.requestFocus(); }
        toggle.setText(reveal ? "HIDE" : "SHOW");
    }

    private HBox wrapInput(Group icon, javafx.scene.Node field, Button trailing) {
        ((javafx.scene.shape.SVGPath) icon.getChildren().get(0)).setStyle("-fx-fill: #c5b9e6;");
        HBox shell = new HBox(10);
        shell.getStyleClass().add("input-shell");
        shell.setAlignment(Pos.CENTER_LEFT);
        shell.getChildren().add(icon);
        shell.getChildren().add(field);
        if (field instanceof Region r) HBox.setHgrow(r, Priority.ALWAYS);
        if (trailing != null) shell.getChildren().add(trailing);
        return shell;
    }

    private HBox orDivider() {
        Region l = new Region(); l.getStyleClass().add("divider"); HBox.setHgrow(l, Priority.ALWAYS);
        Region r = new Region(); r.getStyleClass().add("divider"); HBox.setHgrow(r, Priority.ALWAYS);
        Label or = new Label("OR"); or.getStyleClass().add("muted");
        HBox box = new HBox(12, l, or, r);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private Region spacer(double h) {
        Region r = new Region();
        r.setMinHeight(h); r.setMaxHeight(h);
        return r;
    }

    private void doCreate(String username, String pwd, String confirm) {
        String u = username == null ? "" : username.trim();
        if (u.isEmpty())           { FxUtil.error("Username is required."); return; }
        if (u.length() < 3)        { FxUtil.error("Username must be at least 3 characters."); return; }
        if (pwd == null || pwd.length() < 6) {
            FxUtil.error("Password must be at least 6 characters."); return;
        }
        if (!pwd.equals(confirm))  { FxUtil.error("Passwords do not match."); return; }
        try {
            char[] p = pwd.toCharArray();
            auth.register(u, p);
            Arrays.fill(p, '\0');
            FxUtil.info("Account created! Please sign in.");
            Router.toLogin();
        } catch (AuthService.AuthException ex) {
            FxUtil.error(ex.getMessage());
        }
    }
}
