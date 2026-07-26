package kevin.teko.dao;

import kevin.teko.database.DatabaseManager;
import kevin.teko.model.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NoteDaoTest {

    private NoteDao noteDao;
    private int ebookId;

    @BeforeEach
    void setUp() throws Exception {
        noteDao = new NoteDao();

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS Note");
            stmt.execute("DROP TABLE IF EXISTS EBook");

            stmt.execute("""
                CREATE TABLE EBook (
                    id INTEGER PRIMARY KEY AUTOINCREMENT
                )
            """);

            stmt.execute("""
                CREATE TABLE Note (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    ebook_id INTEGER NOT NULL,
                    page INTEGER NOT NULL,
                    content TEXT NOT NULL,
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
        Note note = new Note(ebookId, 11, "Inhalt");

        noteDao.save(note);

        assertNotNull(note.getId());
        assertTrue(note.getId() > 0);

        Note fetched = noteDao.findById(note.getId());
        assertNotNull(fetched);
        assertEquals(ebookId, fetched.getEBookId());
        assertEquals(11, fetched.getPage());
        assertEquals("Inhalt", fetched.getContent());
        assertNotNull(fetched.getCreatedAt());
    }

    @Test
    void testFindAll() {
        noteDao.save(new Note(ebookId, 1, "A"));
        noteDao.save(new Note(ebookId, 2, "B"));

        List<Note> notes = noteDao.findAll();

        assertEquals(2, notes.size());
    }

    @Test
    void testUpdate() {
        Note note = new Note(ebookId, 3, "Alt");
        noteDao.save(note);

        note = new Note(note.getId(), ebookId, 4, "Neu", noteDao.findById(note.getId()).getCreatedAt());
        noteDao.update(note);

        Note updated = noteDao.findById(note.getId());
        assertEquals(4, updated.getPage());
        assertEquals("Neu", updated.getContent());
    }

    @Test
    void testDelete() {
        Note note = new Note(ebookId, 5, "Delete");
        noteDao.save(note);
        int id = note.getId();

        noteDao.delete(id);

        assertNull(noteDao.findById(id));
    }

    @Test
    void testFindByEBookId() {
        noteDao.save(new Note(ebookId, 6, "Linked"));

        List<Note> notes = noteDao.findByEBookId(ebookId);

        assertEquals(1, notes.size());
        assertEquals("Linked", notes.get(0).getContent());
    }
}