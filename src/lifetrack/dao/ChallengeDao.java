package lifetrack.dao;

import lifetrack.db.Database;
import lifetrack.model.Challenge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ChallengeDao {

    public List<Challenge> findAll(int userId) {
        List<Challenge> list = new ArrayList<>();
        String sql = "SELECT * FROM challenges WHERE user_id = ? ORDER BY id";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Challenge(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getString("solution"),
                        rs.getString("lesson")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load challenges: " + e.getMessage(), e);
        }
        return list;
    }

    public int insert(Challenge ch) {
        String sql = """
            INSERT INTO challenges (user_id, category, description, solution, lesson)
            VALUES (?, ?, ?, ?, ?)
        """;
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, ch.getUserId());
            ps.setString(2, ch.getCategory());
            ps.setString(3, ch.getDescription());
            ps.setString(4, ch.getSolution());
            ps.setString(5, ch.getLesson());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    ch.setId(id);
                    return id;
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to insert challenge: " + ex.getMessage(), ex);
        }
        return -1;
    }

    public void update(Challenge ch) {
        String sql = """
            UPDATE challenges SET
                category = ?, description = ?, solution = ?, lesson = ?
            WHERE id = ? AND user_id = ?
        """;
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, ch.getCategory());
            ps.setString(2, ch.getDescription());
            ps.setString(3, ch.getSolution());
            ps.setString(4, ch.getLesson());
            ps.setInt(5, ch.getId());
            ps.setInt(6, ch.getUserId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to update challenge: " + ex.getMessage(), ex);
        }
    }

    public void delete(int id, int userId) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "DELETE FROM challenges WHERE id = ? AND user_id = ?")) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to delete challenge: " + ex.getMessage(), ex);
        }
    }
}
