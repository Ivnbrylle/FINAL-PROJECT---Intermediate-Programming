package lifetrack.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import lifetrack.app.Router;
import lifetrack.app.Session;
import lifetrack.auth.AuthService;
import lifetrack.model.User;
import lifetrack.ui.component.BrandPanel;
import lifetrack.util.FxIcons;
import lifetrack.util.FxUtil;

public class LoginView {

    private final AuthService auth = new AuthService();

    public Parent build() {
        HBox root = new HBox();
        root.getStyleClass().add("app-bg");

        VBox left = BrandPanel.build();
        HBox.setHgrow(left, Priority.NEVER);

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
        form.setMaxWidth(440);
        form.setAlignment(Pos.CENTER_LEFT);
        form.setFillWidth(true);

        Label title = new Label("Welcome Back");
        title.getStyleClass().add("h2");

        Label sub = new Label("Sign in to continue your story.");
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

        Button showBtn = new Button("SHOW");
        showBtn.getStyleClass().add("show-toggle");
        showBtn.setOnAction(e -> {
            boolean reveal = !pwdShown.isVisible();
            pwdShown.setVisible(reveal);
            pwd.setVisible(!reveal);
            if (reveal) { pwdShown.setText(pwd.getText()); pwdShown.requestFocus(); }
            else        { pwd.setText(pwdShown.getText()); pwd.requestFocus(); }
            showBtn.setText(reveal ? "HIDE" : "SHOW");
        });
        StackPane pwdSwap = new StackPane(pwd, pwdShown);
        HBox.setHgrow(pwdSwap, Priority.ALWAYS);
        HBox pwdShell = wrapInput(FxIcons.key(), pwdSwap, showBtn);

        Button signIn = new Button("Sign In");
        signIn.getStyleClass().addAll("btn", "btn-primary");
        signIn.setMaxWidth(Double.MAX_VALUE);
        signIn.setStyle("-fx-font-size: 14px; -fx-padding: 14 28 14 28;");
        signIn.setOnAction(e -> doLogin(username.getText(),
            (pwdShown.isVisible() ? pwdShown.getText() : pwd.getText())));

        HBox divider = orDivider();

        Button google = new Button("Continue with Google");
        google.getStyleClass().addAll("btn", "btn-ghost");
        google.setMaxWidth(Double.MAX_VALUE);
        google.setStyle("-fx-font-size: 13px; -fx-padding: 12 28 12 28;");
        google.setOnAction(e -> FxUtil.info(
            "Google sign-in isn't wired up in this build. Use a local account "
            + "(click 'Create Account' below) — your data stays on this machine."));

        HBox bottom = new HBox(4);
        bottom.setAlignment(Pos.CENTER);
        Label q = new Label("Don't have an account?");
        q.getStyleClass().add("muted");
        Button createLink = new Button("Create Account");
        createLink.getStyleClass().add("btn-link");
        createLink.setStyle("-fx-font-weight: 800;");
        createLink.setOnAction(e -> Router.toRegister());
        bottom.getChildren().addAll(q, createLink);

        // Enter to submit
        username.setOnAction(e -> signIn.fire());
        pwd.setOnAction(e -> signIn.fire());
        pwdShown.setOnAction(e -> signIn.fire());

        form.getChildren().addAll(
            title, sub,
            spacer(12),
            userShell, pwdShell,
            spacer(8),
            signIn,
            spacer(4), divider, spacer(4),
            google,
            spacer(8), bottom
        );

        wrap.getChildren().add(form);
        return wrap;
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

    private void doLogin(String username, String password) {
        String u = username == null ? "" : username.trim();
        if (u.isEmpty()) { FxUtil.error("Please enter your username."); return; }
        if (password == null || password.isEmpty()) { FxUtil.error("Please enter your password."); return; }
        try {
            User user = auth.login(u, password.toCharArray());
            Session.set(user);
            Router.toDashboard();
        } catch (AuthService.AuthException ex) {
            FxUtil.error(ex.getMessage());
        }
    }
}
