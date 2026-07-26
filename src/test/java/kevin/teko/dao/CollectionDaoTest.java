package kevin.teko.dao;

import kevin.teko.database.DatabaseManager;
import kevin.teko.model.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CollectionDaoTest {

    private CollectionDao collectionDao;
    private int ebookId;

    @BeforeEach
    void setUp() throws Exception {
        collectionDao = new CollectionDao();

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS EBook_Collection");
            stmt.execute("DROP TABLE IF EXISTS Collection");
            stmt.execute("DROP TABLE IF EXISTS EBook");

            stmt.execute("""
                CREATE TABLE EBook (
                    id INTEGER PRIMARY KEY AUTOINCREMENT
                )
            """);

            stmt.execute("""
                CREATE TABLE Collection (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    description TEXT
                )
            """);

            stmt.execute("""
                CREATE TABLE EBook_Collection (
                    ebook_id INTEGER NOT NULL,
                    collection_id INTEGER NOT NULL,
                    PRIMARY KEY (ebook_id, collection_id),
                    FOREIGN KEY (ebook_id) REFERENCES EBook(id) ON DELETE CASCADE,
                    FOREIGN KEY (collection_id) REFERENCES Collection(id) ON DELETE CASCADE
                )
            """);

            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO EBook DEFAULT VALUES", Statement.RETURN_GENERATED_KEYS)) {
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    assertTrue(rs.next());
                    ebookId = rs.getInt(1);
                }
            }
        }
    }

    @Test
    void testSaveAndFindById() {
        Collection collection = new Collection("Science Fiction", "Genre Sammlung");

        collectionDao.save(collection);

        assertNotNull(collection.getId());
        assertTrue(collection.getId() > 0);

        Collection fetched = collectionDao.findById(collection.getId());
        assertNotNull(fetched);
        assertEquals("Science Fiction", fetched.getName());
        assertEquals("Genre Sammlung", fetched.getDescription());
    }

    @Test
    void testFindAll() {
        collectionDao.save(new Collection("A", "Alpha"));
        collectionDao.save(new Collection("B", "Beta"));

        List<Collection> collections = collectionDao.findAll();

        assertEquals(2, collections.size());
    }

    @Test
    void testUpdate() {
        Collection collection = new Collection("Alt", "Alt desc");
        collectionDao.save(collection);

        collection = new Collection(collection.getId(), "Neu", "Neu desc");
        collectionDao.update(collection);

        Collection updated = collectionDao.findById(collection.getId());
        assertEquals("Neu", updated.getName());
        assertEquals("Neu desc", updated.getDescription());
    }

    @Test
    void testDelete() {
        Collection collection = new Collection("ToDelete", "desc");
        collectionDao.save(collection);
        int id = collection.getId();

        collectionDao.delete(id);

        assertNull(collectionDao.findById(id));
    }

    @Test
    void testAddRemoveAndFindByEBookId() {
        Collection collection = new Collection("Linked", "desc");
        collectionDao.save(collection);

        collectionDao.addEBookToCollection(ebookId, collection.getId());

        List<Collection> linked = collectionDao.findCollectionsByEBookId(ebookId);
        assertEquals(1, linked.size());
        assertEquals("Linked", linked.get(0).getName());

        collectionDao.removeEBookFromCollection(ebookId, collection.getId());
        assertTrue(collectionDao.findCollectionsByEBookId(ebookId).isEmpty());
    }
}