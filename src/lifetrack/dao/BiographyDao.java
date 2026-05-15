package lifetrack.dao;

import lifetrack.db.Database;
import lifetrack.model.Biography;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BiographyDao {

    public Biography findByUserId(int userId) {
        String sql = "SELECT * FROM biographies WHERE user_id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Biography b = new Biography();
                    b.setUserId(userId);
                    b.setFullName(rs.getString("full_name"));
                    b.setDateOfBirth(rs.getString("date_of_birth"));
                    b.setPlaceOfBirth(rs.getString("place_of_birth"));
                    b.setGender(rs.getString("gender"));
                    b.setNationality(rs.getString("nationality"));
                    b.setPrimaryEdu(rs.getString("primary_edu"));
                    b.setSecondaryEdu(rs.getString("secondary_edu"));
                    b.setCollegeEdu(rs.getString("college_edu"));
                    b.setFamilyBackground(rs.getString("family_background"));
                    b.setProfilePicture(rs.getString("profile_picture"));
                    b.setOtherInfo(rs.getString("other_info"));
                    b.setEmail(rs.getString("email"));
                    b.setBio(rs.getString("bio"));
                    b.setLocation(rs.getString("location"));
                    b.setWebsite(rs.getString("website"));
                    b.setOccupation(rs.getString("occupation"));
                    b.setSocialLinks(rs.getString("social_links"));
                    return b;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load biography: " + e.getMessage(), e);
        }
        Biography fresh = new Biography();
        fresh.setUserId(userId);
        return fresh;
    }

    public void save(Biography b) {
        String sql = """
            INSERT INTO biographies
                (user_id, full_name, date_of_birth, place_of_birth, gender, nationality,
                 primary_edu, secondary_edu, college_edu, family_background,
                 profile_picture, other_info, email, bio, location, website,
                 occupation, social_links)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(user_id) DO UPDATE SET
                full_name=excluded.full_name,
                date_of_birth=excluded.date_of_birth,
                place_of_birth=excluded.place_of_birth,
                gender=excluded.gender,
                nationality=excluded.nationality,
                primary_edu=excluded.primary_edu,
                secondary_edu=excluded.secondary_edu,
                college_edu=excluded.college_edu,
                family_background=excluded.family_background,
                profile_picture=excluded.profile_picture,
                other_info=excluded.other_info,
                email=excluded.email,
                bio=excluded.bio,
                location=excluded.location,
                website=excluded.website,
                occupation=excluded.occupation,
                social_links=excluded.social_links
        """;
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, b.getUserId());
            ps.setString(2, b.getFullName());
            ps.setString(3, b.getDateOfBirth());
            ps.setString(4, b.getPlaceOfBirth());
            ps.setString(5, b.getGender());
            ps.setString(6, b.getNationality());
            ps.setString(7, b.getPrimaryEdu());
            ps.setString(8, b.getSecondaryEdu());
            ps.setString(9, b.getCollegeEdu());
            ps.setString(10, b.getFamilyBackground());
            ps.setString(11, b.getProfilePicture());
            ps.setString(12, b.getOtherInfo());
            ps.setString(13, b.getEmail());
            ps.setString(14, b.getBio());
            ps.setString(15, b.getLocation());
            ps.setString(16, b.getWebsite());
            ps.setString(17, b.getOccupation());
            ps.setString(18, b.getSocialLinks());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save biography: " + e.getMessage(), e);
        }
    }
}
