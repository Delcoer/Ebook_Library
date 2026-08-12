package kevin.teko.service;

import kevin.teko.dao.AuthorDao;
import kevin.teko.dao.EBookDao;
import kevin.teko.database.DatabaseManager;
import kevin.teko.model.EBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class EBookServiceTest {

    private EBookService eBookService;
    private EBookDao eBookDao;
    private AuthorDao authorDao;
    private int fileFormatId;

    @BeforeEach
    void setUp() throws Exception {
        this.eBookDao = new EBookDao();
        this.authorDao = new AuthorDao();
        this.eBookService = new EBookService(eBookDao, authorDao);

        try (Connection connection = DatabaseManager.getConnection();
             Statement stmt = connection.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS EBook_Author");
            stmt.execute("DROP TABLE IF EXISTS EBook");
            stmt.execute("DROP TABLE IF EXISTS Author");
            stmt.execute("DROP TABLE IF EXISTS FileFormat");
            stmt.execute("DROP TABLE IF EXISTS Publisher");

            stmt.execute("""
                CREATE TABLE Author (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    first_name TEXT,
                    last_name TEXT NOT NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE Publisher (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE
                )
            """);

            stmt.execute("""
                CREATE TABLE FileFormat (
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
                    reading_status TEXT NOT NULL DEFAULT 'NOT_STARTED',
                    rating INTEGER,
                    page_count INTEGER,
                    added_at TEXT NOT NULL,
                    publisher_id INTEGER,
                    FOREIGN KEY (file_format_id) REFERENCES FileFormat(id),
                    FOREIGN KEY (publisher_id) REFERENCES Publisher(id) ON DELETE SET NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE EBook_Author (
                    ebook_id INTEGER NOT NULL,
                    author_id INTEGER NOT NULL,
                    PRIMARY KEY (ebook_id, author_id),
                    FOREIGN KEY (ebook_id) REFERENCES EBook(id) ON DELETE CASCADE,
                    FOREIGN KEY (author_id) REFERENCES Author(id) ON DELETE CASCADE
                )
            """);

            try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO FileFormat (name) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS
            )) {
                pstmt.setString(1, "EPUB");
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    assertTrue(rs.next(), "Es sollte eine FileFormat-ID erzeugt werden.");
                    fileFormatId = rs.getInt(1);
                }
            }
        }
    }

    @Test
    @DisplayName("Erfolgreiches Registrieren: Autor & E-Book werden gespeichert und verknüpft")
    void registerNewEBook_Success() {
        EBook newBook = new EBook(
                "Clean Code in Java",
                null,
                "/path/to/book.epub",
                fileFormatId,
                null,
                "NOT_STARTED",
                0,
                320,
                null
        );
        newBook.setAddedAt("2026-07-27 12:00:00");

        EBook savedBook = eBookService.registerNewEBook(newBook, "Robert", "Martin");

        assertNotNull(savedBook.getId(), "E-Book ID sollte generiert worden sein");
        assertEquals("Clean Code in Java", savedBook.getTitle());
    }

    @Test
    @DisplayName("Validierung schlägt fehl, wenn der Autor-Nachname leer ist")
    void registerNewEBook_BlankAuthorLastName_ThrowsException() {
        EBook validBook = new EBook(
                "Clean Code in Java",
                null,
                "/path/to/book.epub",
                fileFormatId,
                null,
                "NOT_STARTED",
                0,
                320,
                null
        );
        validBook.setAddedAt("2026-07-27 12:00:00");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> eBookService.registerNewEBook(validBook, "Robert", "  ")
        );

        assertEquals("Der Nachname des Autors ist erforderlich!", exception.getMessage());
    }

    @Test
    @DisplayName("E-Book wird erfolgreich aktualisiert")
    void updateEBook_Success() {
        EBook book = new EBook(
                "Old Title",
                null,
                "/path/to/book.epub",
                fileFormatId,
                null,
                "NOT_STARTED",
                0,
                120,
                null
        );
        book.setAddedAt("2026-07-27 12:00:00");
        eBookDao.save(book);

        EBook updated = new EBook(
                book.getId(),
                "Updated Title",
                "1234567890",
                "/path/to/updated.epub",
                fileFormatId,
                "/covers/updated.png",
                "READING",
                3,
                180,
                "2026-07-27 12:30:00",
                null
        );

        EBook result = eBookService.updateEBook(updated);

        assertEquals(book.getId(), result.getId());
        EBook persisted = eBookDao.findById(book.getId());
        assertNotNull(persisted);
        assertEquals("Updated Title", persisted.getTitle());
        assertEquals("1234567890", persisted.getIsbn());
        assertEquals("/path/to/updated.epub", persisted.getFilePath());
        assertEquals("/covers/updated.png", persisted.getCoverPath());
        assertEquals("READING", persisted.getReadingStatus());
        assertEquals(3, persisted.getRating());
        assertEquals(180, persisted.getPageCount());
        assertEquals("2026-07-27 12:30:00", persisted.getAddedAt());
    }

    @Test
    @DisplayName("E-Book wird erfolgreich gelöscht")
    void deleteEBook_Success() {
        EBook book = new EBook(
                "To Delete",
                null,
                "/path/to/delete.epub",
                fileFormatId,
                null,
                "NOT_STARTED",
                0,
                90,
                null
        );
        book.setAddedAt("2026-07-27 12:45:00");
        eBookDao.save(book);

        eBookService.deleteEBook(book.getId());

        assertNull(eBookDao.findById(book.getId()));
    }
}
