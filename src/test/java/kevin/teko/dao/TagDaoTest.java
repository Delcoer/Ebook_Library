package kevin.teko.dao;

import kevin.teko.database.DatabaseManager;
import kevin.teko.model.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TagDaoTest {

    private TagDao tagDao;
    private int ebookId;

    @BeforeEach
    void setUp() throws Exception {
        tagDao = new TagDao();

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS EBook_Tag");
            stmt.execute("DROP TABLE IF EXISTS Tag");
            stmt.execute("DROP TABLE IF EXISTS EBook");

            stmt.execute("""
                CREATE TABLE EBook (
                    id INTEGER PRIMARY KEY AUTOINCREMENT
                )
            """);

            stmt.execute("""
                CREATE TABLE Tag (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE
                )
            """);

            stmt.execute("""
                CREATE TABLE EBook_Tag (
                    ebook_id INTEGER NOT NULL,
                    tag_id INTEGER NOT NULL,
                    PRIMARY KEY (ebook_id, tag_id),
                    FOREIGN KEY (ebook_id) REFERENCES EBook(id) ON DELETE CASCADE,
                    FOREIGN KEY (tag_id) REFERENCES Tag(id) ON DELETE CASCADE
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
        Tag tag = new Tag("Fantasy");

        tagDao.save(tag);

        assertNotNull(tag.getId());
        assertTrue(tag.getId() > 0);

        Tag fetched = tagDao.findById(tag.getId());
        assertNotNull(fetched);
        assertEquals("fantasy", fetched.getName());
    }

    @Test
    void testFindAll() {
        tagDao.save(new Tag("Alpha"));
        tagDao.save(new Tag("Beta"));

        List<Tag> tags = tagDao.findAll();

        assertEquals(2, tags.size());
    }

    @Test
    void testUpdate() {
        Tag tag = new Tag("Old");
        tagDao.save(tag);

        tag.setName("New");
        tagDao.update(tag);

        Tag updated = tagDao.findById(tag.getId());
        assertEquals("new", updated.getName());
    }

    @Test
    void testDelete() {
        Tag tag = new Tag("Delete Me");
        tagDao.save(tag);
        int id = tag.getId();

        tagDao.delete(id);

        assertNull(tagDao.findById(id));
    }

    @Test
    void testAddRemoveAndFindByEBookId() {
        Tag tag = new Tag("Linked");
        tagDao.save(tag);

        tagDao.addTagToEBook(ebookId, tag.getId());

        List<Tag> linked = tagDao.findTagsByEBookId(ebookId);
        assertEquals(1, linked.size());
        assertEquals("linked", linked.get(0).getName());

        tagDao.removeTagFromEBook(ebookId, tag.getId());
        assertTrue(tagDao.findTagsByEBookId(ebookId).isEmpty());
    }
}