package lifetrack.util;

import javafx.scene.Group;
import javafx.scene.shape.SVGPath;

/**
 * Tiny SVG-path icon library. Each method returns a fresh node so it can
 * be parented into any layout. Pair with CSS class "icon-svg" / "sidebar-icon"
 * to control fill color.
 */
public final class FxIcons {
    private FxIcons() {}

    public static Group home() {
        return svg("M3 11.5L12 4l9 7.5V20a1 1 0 0 1-1 1h-5v-6h-6v6H4a1 1 0 0 1-1-1v-8.5z");
    }
    public static Group briefcase() {
        return svg("M3 7h18v12a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V7zm5-3h8v3H8V4z");
    }
    public static Group trophy() {
        return svg("M7 4h10v2h3v3a4 4 0 0 1-4 4 5 5 0 0 1-1 .9V16h3v3H6v-3h3v-2.1A5 5 0 0 1 8 13a4 4 0 0 1-4-4V6h3V4z");
    }
    public static Group bolt() {
        return svg("M13 2L4 14h6l-1 8 9-12h-6l1-8z");
    }
    public static Group user() {
        return svg("M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8zm0 2c-4 0-8 2-8 6v2h16v-2c0-4-4-6-8-6z");
    }
    public static Group userLock() {
        return svg("M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8zm0 2c-3.3 0-6.8 1.5-7.7 4.6 0 0 0 0 0 0L4 21h16l-.3-2.4C18.8 15.5 15.3 14 12 14z");
    }
    public static Group key() {
        return svg("M14 7a5 5 0 1 0-4.6 5L12 14l2 2 1.5-1.5L17 16l2-2-1.5-1.5L19 11l-2-2-3 .9z");
    }
    public static Group shield() {
        return svg("M12 2 4 5v7c0 5 3.5 8.7 8 10 4.5-1.3 8-5 8-10V5l-8-3z");
    }
    public static Group sparkle() {
        return svg("M12 2l1.7 4.6L18 8l-4.3 1.4L12 14l-1.7-4.6L6 8l4.3-1.4L12 2zm6 11l1 2.5L21 16l-2 .5L18 19l-1-2.5L15 16l2-.5L18 13zM5 14l.8 1.9L8 17l-2.2.9L5 20l-.8-1.9L2 17l2.2-1.1L5 14z");
    }
    public static Group group() {
        return svg("M8 11a3 3 0 1 0 0-6 3 3 0 0 0 0 6zm8 0a3 3 0 1 0 0-6 3 3 0 0 0 0 6zm-8 2c-2.7 0-7 1.3-7 4v2h10v-2c0-1.1.5-2.1 1.4-3-1.1-.7-2.6-1-4.4-1zm8 0c-1 0-1.9.1-2.7.3 1.6 1 2.7 2.5 2.7 3.7v2h7v-2c0-2.7-4.3-4-7-4z");
    }
    public static Group arrowRight() {
        return svg("M5 12h12m-4-4 4 4-4 4");
    }
    public static Group plus() {
        return svg("M12 5v14M5 12h14");
    }
    public static Group trash() {
        return svg("M6 7h12l-1 13H7L6 7zm3-3h6l1 2H8l1-2z");
    }
    public static Group pencil() {
        return svg("M3 17.25V21h3.75l11-11-3.75-3.75-11 11zm17.7-10.04a1 1 0 0 0 0-1.4l-2.5-2.5a1 1 0 0 0-1.4 0l-1.8 1.8 3.9 3.9 1.8-1.8z");
    }
    public static Group eye() {
        return svg("M12 5c-5 0-9 4-10 7 1 3 5 7 10 7s9-4 10-7c-1-3-5-7-10-7zm0 11a4 4 0 1 1 0-8 4 4 0 0 1 0 8z");
    }
    public static Group code() {
        return svg("M9 7l-5 5 5 5M15 7l5 5-5 5M13 4l-2 16");
    }
    public static Group cap() {
        return svg("M12 3 1 9l11 6 9-4.9V17h2V9L12 3zm-7 9.2v3.6c0 1.7 3.2 3.2 7 3.2s7-1.5 7-3.2v-3.6L12 16l-7-3.8z");
    }
    public static Group star() {
        return svg("M12 2l3 7h7l-5.5 4.5L18 21l-6-4-6 4 1.5-7.5L2 9h7l3-7z");
    }
    public static Group ellipsis() {
        return svg("M6 12a2 2 0 1 1-4 0 2 2 0 0 1 4 0zm8 0a2 2 0 1 1-4 0 2 2 0 0 1 4 0zm8 0a2 2 0 1 1-4 0 2 2 0 0 1 4 0z");
    }
    public static Group dna() {
        return svg("M5 3c0 4 3 6 5 8s5 4 5 8m-10 0c0-4 3-6 5-8s5-4 5-8M6 6h12M6 10h6m0 4h6M6 18h12");
    }

    private static Group svg(String path) {
        SVGPath p = new SVGPath();
        p.setContent(path);
        p.getStyleClass().add("icon-svg");
        Group g = new Group(p);
        return g;
    }
}
