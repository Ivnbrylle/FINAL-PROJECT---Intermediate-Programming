package lifetrack.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {

    private static final String DB_PATH = "data" + File.separator + "lifetrack.db";
    private static final String URL = "jdbc:sqlite:" + DB_PATH;

    private Database() {}

    public static Connection getConnection() throws SQLException {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();
        Connection c = DriverManager.getConnection(URL);
        try (Statement s = c.createStatement()) {
            s.execute("PRAGMA foreign_keys = ON");
        }
        return c;
    }

    public static void init() {
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id            INTEGER PRIMARY KEY AUTOINCREMENT,
                    username      TEXT NOT NULL UNIQUE,
                    password_hash TEXT NOT NULL,
                    salt          TEXT NOT NULL,
                    created_at    TEXT NOT NULL DEFAULT (datetime('now'))
                )
            """);
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS biographies (
                    user_id            INTEGER PRIMARY KEY,
                    full_name          TEXT,
                    date_of_birth      TEXT,
                    place_of_birth     TEXT,
                    gender             TEXT,
                    nationality        TEXT,
                    primary_edu        TEXT,
                    secondary_edu      TEXT,
                    college_edu        TEXT,
                    family_background  TEXT,
                    profile_picture    TEXT,
                    other_info         TEXT,
                    email              TEXT,
                    bio                TEXT,
                    location           TEXT,
                    website            TEXT,
                    occupation         TEXT,
                    social_links       TEXT,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                )
            """);
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS experiences (
                    id               INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id          INTEGER NOT NULL,
                    type             TEXT,
                    title            TEXT,
                    organization     TEXT,
                    start_date       TEXT,
                    end_date         TEXT,
                    responsibilities TEXT,
                    notes            TEXT,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                )
            """);
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS achievements (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id     INTEGER NOT NULL,
                    category    TEXT,
                    title       TEXT,
                    description TEXT,
                    date_earned TEXT,
                    phase       TEXT,
                    skills      TEXT,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                )
            """);
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS challenges (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id     INTEGER NOT NULL,
                    category    TEXT,
                    description TEXT,
                    solution    TEXT,
                    lesson      TEXT,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                )
            """);

            // Lightweight migration for users who installed an earlier build.
            addColumnIfMissing(c, "biographies", "email",        "TEXT");
            addColumnIfMissing(c, "biographies", "bio",          "TEXT");
            addColumnIfMissing(c, "biographies", "location",     "TEXT");
            addColumnIfMissing(c, "biographies", "website",      "TEXT");
            addColumnIfMissing(c, "biographies", "occupation",   "TEXT");
            addColumnIfMissing(c, "biographies", "social_links", "TEXT");
            addColumnIfMissing(c, "achievements", "phase",  "TEXT");
            addColumnIfMissing(c, "achievements", "skills", "TEXT");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database: " + e.getMessage(), e);
        }
    }

    private static void addColumnIfMissing(Connection c, String table, String column, String type) throws SQLException {
        try (java.sql.PreparedStatement ps = c.prepareStatement(
                "SELECT 1 FROM pragma_table_info(?) WHERE name = ?")) {
            ps.setString(1, table);
            ps.setString(2, column);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return;
            }
        }
        try (Statement s = c.createStatement()) {
            s.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + column + " " + type);
        }
    }
}
