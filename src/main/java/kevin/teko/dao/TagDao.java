package kevin.teko.dao;

import kevin.teko.database.DatabaseManager;
import kevin.teko.model.Tag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TagDao implements BaseDao<Tag> {

    @Override
    public void save(Tag tag) {
        String sql = "INSERT INTO Tag (name) VALUES (?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, tag.getName());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    tag.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Speichern des Tags.", e);
        }
    }

    @Override
    public Tag findById(int id) {
        String sql = "SELECT id, name FROM Tag WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Tag(rs.getInt("id"), rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Suchen des Tags mit ID " + id, e);
        }
        return null;
    }

    @Override
    public List<Tag> findAll() {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT id, name FROM Tag ORDER BY name";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tags.add(new Tag(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden aller Tags.", e);
        }
        return tags;
    }

    @Override
    public void update(Tag tag) {
        String sql = "UPDATE Tag SET name = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tag.getName());
            pstmt.setInt(2, tag.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Aktualisieren des Tags mit ID " + tag.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Tag WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Löschen des Tags mit ID " + id, e);
        }
    }

    // N:M Verknüpfungsmethoden
    public void addTagToEBook(int ebookId, int tagId) {
        String sql = "INSERT OR IGNORE INTO EBook_Tag (ebook_id, tag_id) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ebookId);
            pstmt.setInt(2, tagId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Verknüpfen von E-Book ID " + ebookId + " mit Tag ID " + tagId, e);
        }
    }

    public void removeTagFromEBook(int ebookId, int tagId) {
        String sql = "DELETE FROM EBook_Tag WHERE ebook_id = ? AND tag_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ebookId);
            pstmt.setInt(2, tagId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Entfernen des Tags ID " + tagId + " von E-Book ID " + ebookId, e);
        }
    }

    public List<Tag> findTagsByEBookId(int ebookId) {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT t.id, t.name FROM Tag t " +
                     "JOIN EBook_Tag et ON t.id = et.tag_id " +
                     "WHERE et.ebook_id = ? ORDER BY t.name";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ebookId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tags.add(new Tag(rs.getInt("id"), rs.getString("name")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden der Tags für E-Book ID " + ebookId, e);
        }
        return tags;
    }
}