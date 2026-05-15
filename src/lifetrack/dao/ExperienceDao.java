package lifetrack.dao;

import lifetrack.db.Database;
import lifetrack.model.Experience;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExperienceDao {

    public List<Experience> findAll(int userId) {
        List<Experience> list = new ArrayList<>();
        String sql = "SELECT * FROM experiences WHERE user_id = ? ORDER BY id";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Experience(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("type"),
                        rs.getString("title"),
                        rs.getString("organization"),
                        rs.getString("start_date"),
                        rs.getString("end_date"),
                        rs.getString("responsibilities"),
                        rs.getString("notes")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load experiences: " + e.getMessage(), e);
        }
        return list;
    }

    public int insert(Experience e) {
        String sql = """
            INSERT INTO experiences
                (user_id, type, title, organization, start_date, end_date, responsibilities, notes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, e.getUserId());
            ps.setString(2, e.getType());
            ps.setString(3, e.getTitle());
            ps.setString(4, e.getOrganization());
            ps.setString(5, e.getStartDate());
            ps.setString(6, e.getEndDate());
            ps.setString(7, e.getResponsibilities());
            ps.setString(8, e.getNotes());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    e.setId(id);
                    return id;
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to insert experience: " + ex.getMessage(), ex);
        }
        return -1;
    }

    public void update(Experience e) {
        String sql = """
            UPDATE experiences SET
                type = ?, title = ?, organization = ?, start_date = ?,
                end_date = ?, responsibilities = ?, notes = ?
            WHERE id = ? AND user_id = ?
        """;
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getType());
            ps.setString(2, e.getTitle());
            ps.setString(3, e.getOrganization());
            ps.setString(4, e.getStartDate());
            ps.setString(5, e.getEndDate());
            ps.setString(6, e.getResponsibilities());
            ps.setString(7, e.getNotes());
            ps.setInt(8, e.getId());
            ps.setInt(9, e.getUserId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to update experience: " + ex.getMessage(), ex);
        }
    }

    public void delete(int id, int userId) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                 "DELETE FROM experiences WHERE id = ? AND user_id = ?")) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to delete experience: " + ex.getMessage(), ex);
        }
    }
}
