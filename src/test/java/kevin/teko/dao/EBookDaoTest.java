package kevin.teko.dao;

import kevin.teko.database.DatabaseManager;
import kevin.teko.model.EBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EBookDaoTest {

    private EBookDao ebookDao;

    @BeforeEach
    void setUp() throws Exception {
        ebookDao = new EBookDao();

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS EBook");
            stmt.execute("DROP TABLE IF EXISTS Publisher");

            stmt.execute("""
                CREATE TABLE Publisher (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE
                )
            """);

            stmt.execute("""
                CREATE TABLE EBook (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    isbn TEXT UNIQUE,
                    file_path TEXT NOT NULL UNIQUE,
                    file_format_id INTEGER NOT NULL,
                    cover_path TEXT,
                    reading_status TEXT NOT NULL,
                    rating INTEGER,
                    page_count INTEGER,
                    added_at TEXT NOT NULL,
                    publisher_id INTEGER,
                    FOREIGN KEY (publisher_id) REFERENCES Publisher(id)
                )
            """);
        }
    }

    @Test
    void testSaveAndFindById() {
        EBook ebook = new EBook(
            "Title",
            "123456789X",
            "/books/title.epub",
            1,
            null,
            "NOT_STARTED",
            4,
            320,
            null
        );
        ebook.setAddedAt("2026-07-26 12:00:00");

        ebookDao.save(ebook);

        assertNotNull(ebook.getId());
        assertTrue(ebook.getId() > 0);

        EBook fetched = ebookDao.findById(ebook.getId());
        assertNotNull(fetched);
        assertEquals("Title", fetched.getTitle());
        assertEquals("123456789X", fetched.getIsbn());
        assertEquals("/books/title.epub", fetched.getFilePath());
        assertEquals(1, fetched.getFileFormatId());
        assertEquals("NOT_STARTED", fetched.getReadingStatus());
        assertEquals(4, fetched.getRating());
        assertEquals(320, fetched.getPageCount());
        assertEquals("2026-07-26 12:00:00", fetched.getAddedAt());
        assertNull(fetched.getPublisherId());
    }

    @Test
    void testFindAll() {
        EBook first = new EBook("A", "123456789X", "/books/a.epub", 1, null, "NOT_STARTED", 0, 10, null);
        first.setAddedAt("2026-07-26 12:00:00");
        ebookDao.save(first);

        EBook second = new EBook("B", "1234567890", "/books/b.epub", 1, null, "READING", 1, 20, null);
        second.setAddedAt("2026-07-26 12:00:01");
        ebookDao.save(second);

        List<EBook> ebooks = ebookDao.findAll();

        assertEquals(2, ebooks.size());
    }

    @Test
    void testUpdate() {
        EBook ebook = new EBook("Old", "123456789X", "/books/old.epub", 1, null, "NOT_STARTED", 0, 10, null);
        ebook.setAddedAt("2026-07-26 12:00:00");
        ebookDao.save(ebook);

        ebook = new EBook(
            ebook.getId(),
            "New",
            "1234567890",
            "/books/new.epub",
            2,
            "/covers/new.png",
            "COMPLETED",
            5,
            100,
            "2026-07-26 12:05:00",
            null
        );
        ebookDao.update(ebook);

        EBook updated = ebookDao.findById(ebook.getId());
        assertEquals("New", updated.getTitle());
        assertEquals("1234567890", updated.getIsbn());
        assertEquals("/books/new.epub", updated.getFilePath());
        assertEquals(2, updated.getFileFormatId());
        assertEquals("/covers/new.png", updated.getCoverPath());
        assertEquals("COMPLETED", updated.getReadingStatus());
        assertEquals(5, updated.getRating());
        assertEquals(100, updated.getPageCount());
        assertEquals("2026-07-26 12:05:00", updated.getAddedAt());
    }

    @Test
    void testDelete() {
        EBook ebook = new EBook("Delete", "123456789X", "/books/delete.epub", 1, null, "NOT_STARTED", 0, 10, null);
        ebook.setAddedAt("2026-07-26 12:00:00");
        ebookDao.save(ebook);
        int id = ebook.getId();

        ebookDao.delete(id);

        assertNull(ebookDao.findById(id));
    }
}