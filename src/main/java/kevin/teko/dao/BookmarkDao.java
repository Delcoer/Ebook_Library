package kevin.teko.dao;

import kevin.teko.database.DatabaseManager;
import kevin.teko.model.Bookmark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BookmarkDao implements BaseDao<Bookmark> {

    @Override
    public void save(Bookmark bookmark) {
        if (bookmark == null) {
            throw new IllegalArgumentException("Das zu speichernde Lesezeichen darf nicht null sein.");
        }

        String sql = "INSERT INTO Bookmark (ebook_id, page_number, title) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, bookmark.getEBookId());
            pstmt.setInt(2, bookmark.getPage());
            pstmt.setString(3, bookmark.getTitle());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    bookmark.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Speichern des Lesezeichens.", e);
        }
    }

    @Override
    public Bookmark findById(int id) {
        if (id <= 0) {
            return null;
        }

        String sql = "SELECT id, ebook_id, page_number, title, created_at FROM Bookmark WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Bookmark(
                        rs.getInt("id"),
                        rs.getInt("ebook_id"),
                        rs.getInt("page_number"),
                        rs.getString("title"),
                        rs.getString("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Suchen des Lesezeichens mit ID " + id, e);
        }
        return null;
    }

    @Override
    public List<Bookmark> findAll() {
        List<Bookmark> bookmarks = new ArrayList<>();
        String sql = "SELECT id, ebook_id, page_number, title, created_at FROM Bookmark ORDER BY ebook_id, page_number";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                bookmarks.add(new Bookmark(
                    rs.getInt("id"),
                    rs.getInt("ebook_id"),
                    rs.getInt("page_number"),
                    rs.getString("title"),
                    rs.getString("created_at")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden aller Lesezeichen.", e);
        }
        return bookmarks;
    }

    @Override
    public void update(Bookmark bookmark) {
        if (bookmark == null || bookmark.getId() == null) {
            throw new IllegalArgumentException("Das Lesezeichen oder dessen ID darf für ein Update nicht null sein.");
        }

        String sql = "UPDATE Bookmark SET ebook_id = ?, page_number = ?, title = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookmark.getEBookId());
            pstmt.setInt(2, bookmark.getPage());
            pstmt.setString(3, bookmark.getTitle());
            pstmt.setInt(4, bookmark.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Aktualisieren des Lesezeichens mit ID " + bookmark.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        if (id <= 0) {
            return;
        }

        String sql = "DELETE FROM Bookmark WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Löschen des Lesezeichens mit ID " + id, e);
        }
    }

    // --- Spezifische Methoden für E-Book-Verknüpfung ---

    public List<Bookmark> findByEBookId(int ebookId) {
        List<Bookmark> bookmarks = new ArrayList<>();
        if (ebookId <= 0) {
            return bookmarks;
        }

        String sql = "SELECT id, ebook_id, page_number, title, created_at FROM Bookmark WHERE ebook_id = ? ORDER BY page_number ASC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ebookId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookmarks.add(new Bookmark(
                        rs.getInt("id"),
                        rs.getInt("ebook_id"),
                        rs.getInt("page_number"),
                        rs.getString("title"),
                        rs.getString("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden der Lesezeichen für E-Book ID " + ebookId, e);
        }
        return bookmarks;
    }

    public void deleteByEBookId(int ebookId) {
        if (ebookId <= 0) {
            return;
        }

        String sql = "DELETE FROM Bookmark WHERE ebook_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ebookId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Löschen der Lesezeichen für E-Book ID " + ebookId, e);
        }
    }
}