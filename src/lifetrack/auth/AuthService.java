package lifetrack.auth;

import lifetrack.db.Database;
import lifetrack.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AuthService {

    public static class AuthException extends Exception {
        public AuthException(String msg) { super(msg); }
    }

    /** Register a new user. Returns the created User. */
    public User register(String username, char[] password) throws AuthException {
        validate(username, password);
        if (exists(username)) {
            throw new AuthException("Username already taken. Please choose another.");
        }
        String salt = PasswordUtil.newSalt();
        String hash = PasswordUtil.hash(password, salt);

        String sql = "INSERT INTO users (username, password_hash, salt) VALUES (?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setString(3, salt);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return new User(keys.getInt(1), username);
            }
        } catch (SQLException e) {
            throw new AuthException("Could not create account: " + e.getMessage());
        }
        throw new AuthException("Unknown error creating account.");
    }

    /** Authenticate. Returns the User on success, throws AuthException on failure. */
    public User login(String username, char[] password) throws AuthException {
        validate(username, password);
        String sql = "SELECT id, password_hash, salt FROM users WHERE username = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new AuthException("Invalid username or password.");
                int id = rs.getInt("id");
                String storedHash = rs.getString("password_hash");
                String salt = rs.getString("salt");
                if (!PasswordUtil.matches(password, salt, storedHash)) {
                    throw new AuthException("Invalid username or password.");
                }
                return new User(id, username);
            }
        } catch (SQLException e) {
            throw new AuthException("Login failed: " + e.getMessage());
        }
    }

    private boolean exists(String username) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM users WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void validate(String username, char[] password) throws AuthException {
        if (username == null || username.isBlank()) {
            throw new AuthException("Username is required.");
        }
        if (password == null || password.length == 0) {
            throw new AuthException("Password is required.");
        }
        if (username.length() > 64) {
            throw new AuthException("Username is too long (max 64 characters).");
        }
    }
}
