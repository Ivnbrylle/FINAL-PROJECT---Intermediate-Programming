package lifetrack.app;

import lifetrack.model.User;

public final class Session {
    private static User user;
    private Session() {}
    public static void set(User u) { user = u; }
    public static User get() { return user; }
    public static void clear() { user = null; }
}
