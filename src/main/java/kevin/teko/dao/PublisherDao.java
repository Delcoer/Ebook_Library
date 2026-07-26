package kevin.teko.dao;

import kevin.teko.database.DatabaseManager;
import kevin.teko.model.Publisher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PublisherDao implements BaseDao<Publisher> {

    @Override
    public void save(Publisher publisher) {
        String sql = "INSERT INTO Publisher (name) VALUES (?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, publisher.getName());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    publisher.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Speichern des Publishers.", e);
        }
    }

    @Override
    public Publisher findById(int id) {
        String sql = "SELECT id, name FROM Publisher WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Publisher(rs.getInt("id"), rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Suchen des Publishers mit ID " + id, e);
        }
        return null;
    }

    @Override
    public List<Publisher> findAll() {
        List<Publisher> publishers = new ArrayList<>();
        String sql = "SELECT id, name FROM Publisher ORDER BY name";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                publishers.add(new Publisher(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden aller Publisher.", e);
        }
        return publishers;
    }

    @Override
    public void update(Publisher publisher) {
        String sql = "UPDATE Publisher SET name = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, publisher.getName());
            pstmt.setInt(2, publisher.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Aktualisieren des Publishers mit ID " + publisher.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Publisher WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Löschen des Publishers mit ID " + id, e);
        }
    }
}