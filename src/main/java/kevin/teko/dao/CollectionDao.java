package kevin.teko.dao;

import kevin.teko.database.DatabaseManager;
import kevin.teko.model.Collection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CollectionDao implements BaseDao<Collection> {

    @Override
    public void save(Collection collection) {
        if (collection == null) {
            throw new IllegalArgumentException("Die zu speichernde Sammlung darf nicht null sein.");
        }

        String sql = "INSERT INTO Collection (name, description) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, collection.getName());
            pstmt.setString(2, collection.getDescription());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    collection.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Speichern der Sammlung.", e);
        }
    }

    @Override
    public Collection findById(int id) {
        if (id <= 0) {
            return null;
        }

        String sql = "SELECT id, name, description FROM Collection WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Collection(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Suchen der Sammlung mit ID " + id, e);
        }
        return null;
    }

    @Override
    public List<Collection> findAll() {
        List<Collection> collections = new ArrayList<>();
        String sql = "SELECT id, name, description FROM Collection ORDER BY name";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                collections.add(new Collection(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden aller Sammlungen.", e);
        }
        return collections;
    }

    @Override
    public void update(Collection collection) {
        if (collection == null || collection.getId() == null) {
            throw new IllegalArgumentException("Die Sammlung oder deren ID darf für ein Update nicht null sein.");
        }

        String sql = "UPDATE Collection SET name = ?, description = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, collection.getName());
            pstmt.setString(2, collection.getDescription());
            pstmt.setInt(3, collection.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Aktualisieren der Sammlung mit ID " + collection.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        if (id <= 0) {
            return;
        }

        String sql = "DELETE FROM Collection WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Löschen der Sammlung mit ID " + id, e);
        }
    }

    // --- N:M Verknüpfungsmethoden (EBook <-> Collection) ---

    public void addEBookToCollection(int ebookId, int collectionId) {
        if (ebookId <= 0 || collectionId <= 0) {
            throw new IllegalArgumentException("Ungültige E-Book- oder Sammlungs-ID.");
        }

        String sql = "INSERT OR IGNORE INTO EBook_Collection (ebook_id, collection_id) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ebookId);
            pstmt.setInt(2, collectionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Hinzufügen von E-Book ID " + ebookId + " zu Sammlung ID " + collectionId, e);
        }
    }

    public void removeEBookFromCollection(int ebookId, int collectionId) {
        if (ebookId <= 0 || collectionId <= 0) {
            return;
        }

        String sql = "DELETE FROM EBook_Collection WHERE ebook_id = ? AND collection_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ebookId);
            pstmt.setInt(2, collectionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Entfernen von E-Book ID " + ebookId + " aus Sammlung ID " + collectionId, e);
        }
    }

    public List<Collection> findCollectionsByEBookId(int ebookId) {
        List<Collection> collections = new ArrayList<>();
        if (ebookId <= 0) {
            return collections;
        }

        String sql = "SELECT c.id, c.name, c.description FROM Collection c " +
                     "JOIN EBook_Collection ec ON c.id = ec.collection_id " +
                     "WHERE ec.ebook_id = ? ORDER BY c.name";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ebookId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    collections.add(new Collection(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Laden der Sammlungen für E-Book ID " + ebookId, e);
        }
        return collections;
    }
}