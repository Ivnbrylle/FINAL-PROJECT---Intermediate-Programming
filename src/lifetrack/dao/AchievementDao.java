package lifetrack.dao;

import lifetrack.db.Database;
import lifetrack.model.Achievement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AchievementDao {

    public List<Achievement> findAll(int userId) {
        List<Achievement> list = new ArrayList<>();
        String sql = "SELECT * FROM achievements WHERE user_id = ? ORDER BY id";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Achievement(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("category"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("date_earned"),
                        rs.getString("phase"),
                        rs.getString("skills")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load achievements: " + e.getMessage(), e);
        }
        return list;
    }

    public int insert(Achievement a) {
        String sql = """
            INSERT INTO achievements (user_id, category, title, description, date_earned, phase, skills)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getUserId());
            ps.setString(2, a.getCategory());
            ps.setString(3, a.getTitle());
            ps.setString(4, a.getDescription());
            ps.setString(5, a.getDateEarned());
            ps.setString(6, a.getPhase());
            ps.setString(7, a.getSkills());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    a.setId(id);
                    return id;
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to insert achievement: " + ex.getMessage(), ex);
        }
        return -1;
    }

    public void update(Achievement a) {
        String sql = """
            UPDATE achievements SET
                category = ?, title = ?, description = ?, date_earned = ?,
                phase = ?, skills = ?
            WHERE id = ? AND user_id = ?
        """;
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, a.getCategory());
            ps.setString(2, a.getTitle());
            ps.setString(3, a.getDescription());
            ps.setString(4, a.getDateEarned());
            ps.setString(5, a.getPhase());
            ps.setString(6, a.getSkills());
            ps.setInt(7, a.getId());
            ps.setInt(8, a.getUserId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to update achievement: " + ex.getMessage(), ex);
        }
    }

    public void delete(int id, int userId) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "DELETE FROM achievements WHERE id = ? AND user_id = ?")) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to delete achievement: " + ex.getMessage(), ex);
        }
    }
}
