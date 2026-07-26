package kevin.teko.dao;

import kevin.teko.database.DatabaseManager;
import kevin.teko.model.Highlight;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HighlightDao implements BaseDao<Highlight> {

    @Override
    public void save(Highlight highlight) {
        String sql = "INSERT INTO Highlight (ebook_id, page, selected_text, color) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, highlight.getEBookId());
            pstmt.setInt(2, highlight.getPage());
            pstmt.setString(3, highlight.getSelectedText());
            pstmt.setString(4, highlight.getColor());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    highlight.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Speichern des Highlights.", e);
        }
    }

    @Override
    public Highlight findById(int id) {
        String sql = "SELECT id, ebook_id, page, selected_text, color, created_at FROM Highlight WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToHighlight(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Suchen des Highlights mit ID " + id, e);
        }
        return null;
    }

    @Override
    public List<Highlight> findAll() {
        List<Highlight> highlights = new ArrayList<>();
        String sql = "SELECT id, ebook_id, page, selected_text, color, created_at FROM Highlight ORDER BY created_at DESC";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                highlights.add(mapResultSetToHighlight(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden aller Highlights.", e);
        }
        return highlights;
    }

    @Override
    public void update(Highlight highlight) {
        String sql = "UPDATE Highlight SET page = ?, selected_text = ?, color = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, highlight.getPage());
            pstmt.setString(2, highlight.getSelectedText());
            pstmt.setString(3, highlight.getColor());
            pstmt.setInt(4, highlight.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Aktualisieren des Highlights mit ID " + highlight.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Highlight WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Löschen des Highlights mit ID " + id, e);
        }
    }

    public List<Highlight> findByEBookId(int ebookId) {
        List<Highlight> highlights = new ArrayList<>();
        String sql = "SELECT id, ebook_id, page, selected_text, color, created_at FROM Highlight " +
                     "WHERE ebook_id = ? ORDER BY page ASC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ebookId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    highlights.add(mapResultSetToHighlight(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden der Highlights für E-Book ID " + ebookId, e);
        }
        return highlights;
    }

    private Highlight mapResultSetToHighlight(ResultSet rs) throws SQLException {
        return new Highlight(
                rs.getInt("id"),
                rs.getInt("ebook_id"),
                rs.getInt("page"),
                rs.getString("selected_text"),
                rs.getString("color"),
                rs.getString("created_at")
        );
    }
}