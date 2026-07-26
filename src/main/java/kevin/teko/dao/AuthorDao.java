package kevin.teko.dao;

import kevin.teko.database.DatabaseManager;
import kevin.teko.model.Author;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AuthorDao implements BaseDao<Author> {

    @Override
    public void save(Author author) {
        if (author == null) {
            throw new IllegalArgumentException("Der zu speichernde Autor darf nicht null sein.");
        }

        String sql = "INSERT INTO Author (first_name, last_name) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, author.getFirstName());
            pstmt.setString(2, author.getLastName());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    author.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Speichern des Autors.", e);
        }
    }

    @Override
    public Author findById(int id) {
        if (id <= 0) {
            return null;
        }

        String sql = "SELECT id, first_name, last_name FROM Author WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Author(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Suchen des Autors mit ID " + id, e);
        }
        return null;
    }

    @Override
    public List<Author> findAll() {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name FROM Author ORDER BY last_name, first_name";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                authors.add(new Author(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden aller Autoren.", e);
        }
        return authors;
    }

    @Override
    public void update(Author author) {
        if (author == null || author.getId() == null) {
            throw new IllegalArgumentException("Der Autor oder dessen ID darf für ein Update nicht null sein.");
        }

        String sql = "UPDATE Author SET first_name = ?, last_name = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, author.getFirstName());
            pstmt.setString(2, author.getLastName());
            pstmt.setInt(3, author.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Aktualisieren des Autors mit ID " + author.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        if (id <= 0) {
            return;
        }

        String sql = "DELETE FROM Author WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Löschen des Autors mit ID " + id, e);
        }
    }
}