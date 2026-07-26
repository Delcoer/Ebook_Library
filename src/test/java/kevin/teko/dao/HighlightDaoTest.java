package kevin.teko.dao;

import kevin.teko.database.DatabaseManager;
import kevin.teko.model.Highlight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HighlightDaoTest {

    private HighlightDao highlightDao;
    private int ebookId;

    @BeforeEach
    void setUp() throws Exception {
        highlightDao = new HighlightDao();

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS Highlight");
            stmt.execute("DROP TABLE IF EXISTS EBook");

            stmt.execute("""
                CREATE TABLE EBook (
                    id INTEGER PRIMARY KEY AUTOINCREMENT
                )
            """);

            stmt.execute("""
                CREATE TABLE Highlight (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    ebook_id INTEGER NOT NULL,
                    page INTEGER NOT NULL,
                    selected_text TEXT NOT NULL,
                    color TEXT,
                    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (ebook_id) REFERENCES EBook(id) ON DELETE CASCADE
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
        Highlight highlight = new Highlight(ebookId, 7, "Text", "#FFEE00");

        highlightDao.save(highlight);

        assertNotNull(highlight.getId());
        assertTrue(highlight.getId() > 0);

        Highlight fetched = highlightDao.findById(highlight.getId());
        assertNotNull(fetched);
        assertEquals(ebookId, fetched.getEBookId());
        assertEquals(7, fetched.getPage());
        assertEquals("Text", fetched.getSelectedText());
        assertEquals("#FFEE00", fetched.getColor());
        assertNotNull(fetched.getCreatedAt());
    }

    @Test
    void testFindAll() {
        highlightDao.save(new Highlight(ebookId, 1, "A", "#FFEE00"));
        highlightDao.save(new Highlight(ebookId, 2, "B", "#00FF00"));

        List<Highlight> highlights = highlightDao.findAll();

        assertEquals(2, highlights.size());
    }

    @Test
    void testUpdate() {
        Highlight highlight = new Highlight(ebookId, 3, "Alt", "#FFEE00");
        highlightDao.save(highlight);

        highlight = new Highlight(highlight.getId(), ebookId, 4, "Neu", "#00FF00", highlightDao.findById(highlight.getId()).getCreatedAt());
        highlightDao.update(highlight);

        Highlight updated = highlightDao.findById(highlight.getId());
        assertEquals(4, updated.getPage());
        assertEquals("Neu", updated.getSelectedText());
        assertEquals("#00FF00", updated.getColor());
    }

    @Test
    void testDelete() {
        Highlight highlight = new Highlight(ebookId, 5, "Delete", "#FFEE00");
        highlightDao.save(highlight);
        int id = highlight.getId();

        highlightDao.delete(id);

        assertNull(highlightDao.findById(id));
    }

    @Test
    void testFindByEBookId() {
        highlightDao.save(new Highlight(ebookId, 9, "Linked", "#FFEE00"));

        List<Highlight> highlights = highlightDao.findByEBookId(ebookId);

        assertEquals(1, highlights.size());
        assertEquals("Linked", highlights.get(0).getSelectedText());
    }
}