package kevin.teko.dao;

import kevin.teko.database.DatabaseManager;
import kevin.teko.model.EBook;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class EBookDao implements BaseDao<EBook> {

    @Override
    public void save(EBook ebook) {
        String sql = "INSERT INTO EBook (title, isbn, file_path, file_format_id, cover_path, reading_status, rating, page_count, added_at, publisher_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, ebook.getTitle());
            pstmt.setString(2, ebook.getIsbn());
            pstmt.setString(3, ebook.getFilePath());
            pstmt.setInt(4, ebook.getFileFormatId());
            pstmt.setString(5, ebook.getCoverPath());
            pstmt.setString(6, ebook.getReadingStatus());
            pstmt.setInt(7, ebook.getRating());
            pstmt.setInt(8, ebook.getPageCount());
            pstmt.setString(9, ebook.getAddedAt());

            if (ebook.getPublisherId() != null) {
                pstmt.setInt(10, ebook.getPublisherId());
            } else {
                pstmt.setNull(10, Types.INTEGER);
            }

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    ebook.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Speichern des E-Books in der Datenbank.", e);
        }
    }

    @Override
    public EBook findById(int id) {
        String sql = "SELECT id, title, isbn, file_path, file_format_id, cover_path, reading_status, rating, page_count, added_at, publisher_id " +
                     "FROM EBook WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEBook(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Suchen des E-Books mit ID " + id, e);
        }
        return null;
    }

    @Override
    public List<EBook> findAll() {
        List<EBook> ebooks = new ArrayList<>();
        String sql = "SELECT id, title, isbn, file_path, file_format_id, cover_path, reading_status, rating, page_count, added_at, publisher_id " +
                     "FROM EBook";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ebooks.add(mapResultSetToEBook(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden aller E-Books.", e);
        }
        return ebooks;
    }

    @Override
    public void update(EBook ebook) {
        String sql = "UPDATE EBook SET title = ?, isbn = ?, file_path = ?, file_format_id = ?, cover_path = ?, " +
                     "reading_status = ?, rating = ?, page_count = ?, added_at = ?, publisher_id = ? " +
                     "WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ebook.getTitle());
            pstmt.setString(2, ebook.getIsbn());
            pstmt.setString(3, ebook.getFilePath());
            pstmt.setInt(4, ebook.getFileFormatId());
            pstmt.setString(5, ebook.getCoverPath());
            pstmt.setString(6, ebook.getReadingStatus());
            pstmt.setInt(7, ebook.getRating());
            pstmt.setInt(8, ebook.getPageCount());
            pstmt.setString(9, ebook.getAddedAt());

            if (ebook.getPublisherId() != null) {
                pstmt.setInt(10, ebook.getPublisherId());
            } else {
                pstmt.setNull(10, Types.INTEGER);
            }

            pstmt.setInt(11, ebook.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Aktualisieren des E-Books mit ID " + ebook.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM EBook WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Löschen des E-Books mit ID " + id, e);
        }
    }

    private EBook mapResultSetToEBook(ResultSet rs) throws SQLException {
        Integer publisherId = (Integer) rs.getObject("publisher_id");

        return new EBook(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("isbn"),
                rs.getString("file_path"),
                rs.getInt("file_format_id"),
                rs.getString("cover_path"),
                rs.getString("reading_status"),
                rs.getInt("rating"),
                rs.getInt("page_count"),
                rs.getString("added_at"),
                publisherId
        );
    }
}