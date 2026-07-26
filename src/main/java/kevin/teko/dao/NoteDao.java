package kevin.teko.dao;

import kevin.teko.database.DatabaseManager;
import kevin.teko.model.Note;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class NoteDao implements BaseDao<Note> {

    @Override
    public void save(Note note) {
        String sql = "INSERT INTO Note (ebook_id, page, content) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, note.getEBookId());
            pstmt.setInt(2, note.getPage());
            pstmt.setString(3, note.getContent());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    note.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Speichern der Notiz.", e);
        }
    }

    @Override
    public Note findById(int id) {
        String sql = "SELECT id, ebook_id, page, content, created_at FROM Note WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNote(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Suchen der Notiz mit ID " + id, e);
        }
        return null;
    }

    @Override
    public List<Note> findAll() {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT id, ebook_id, page, content, created_at FROM Note ORDER BY created_at DESC";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                notes.add(mapResultSetToNote(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden aller Notizen.", e);
        }
        return notes;
    }

    @Override
    public void update(Note note) {
        String sql = "UPDATE Note SET page = ?, content = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, note.getPage());
            pstmt.setString(2, note.getContent());
            pstmt.setInt(3, note.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Aktualisieren der Notiz mit ID " + note.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Note WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Löschen der Notiz mit ID " + id, e);
        }
    }

    public List<Note> findByEBookId(int ebookId) {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT id, ebook_id, page, content, created_at FROM Note " +
                     "WHERE ebook_id = ? ORDER BY page ASC, created_at ASC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ebookId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notes.add(mapResultSetToNote(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden der Notizen für E-Book ID " + ebookId, e);
        }
        return notes;
    }

    private Note mapResultSetToNote(ResultSet rs) throws SQLException {
        return new Note(
                rs.getInt("id"),
                rs.getInt("ebook_id"),
                rs.getInt("page"),
                rs.getString("content"),
                rs.getString("created_at")
        );
    }
}