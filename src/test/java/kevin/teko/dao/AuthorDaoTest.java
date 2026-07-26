package kevin.teko.dao; 

import kevin.teko.database.DatabaseManager;
import kevin.teko.model.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuthorDaoTest {

    private AuthorDao authorDao;

    @BeforeEach
    void setUp() throws Exception {
        authorDao = new AuthorDao();

        // 1. Tabelle im RAM vor jedem einzelnen Test neu erstellen
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Falls die Tabelle existiert, für eine saubere Testumgebung löschen
            stmt.execute("DROP TABLE IF EXISTS Author");

            // Tabelle neu anlegen
            stmt.execute("""
                CREATE TABLE Author (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    first_name TEXT,
                    last_name TEXT NOT NULL
                )
            """);
        }
    }

    @Test
    void testSaveAndFindById() {
        // 1. Arrange (Vorbereiten)
        Author author = new Author("Stephen", "King");

        // 2. Act (Ausführen)
        authorDao.save(author);

        // 3. Assert (Prüfen)
        assertNotNull(author.getId(), "Die ID sollte nach dem Speichern nicht null sein.");
        assertTrue(author.getId() > 0, "Die ID sollte größer als 0 sein.");

        // Aus Datenbank zurücklesen und Werte abgleichen
        Author fetchedAuthor = authorDao.findById(author.getId());
        assertNotNull(fetchedAuthor, "Der Autor sollte in der Datenbank gefunden werden.");
        assertEquals("Stephen", fetchedAuthor.getFirstName());
        assertEquals("King", fetchedAuthor.getLastName());
    }

    @Test
    void testFindAll() {
        // Arrange
        authorDao.save(new Author("J.K.", "Rowling"));
        authorDao.save(new Author("George R.R.", "Martin"));

        // Act
        List<Author> authors = authorDao.findAll();

        // Assert
        assertEquals(2, authors.size(), "Es sollten genau 2 Autoren in der DB sein.");
    }

    @Test
    void testUpdate() {
        // Arrange
        Author author = new Author("Walter", "Moers");
        authorDao.save(author);

        // Act: Nachname korrigieren
        author.setFirstName("Walter Scott");
        authorDao.update(author);

        // Assert
        Author updated = authorDao.findById(author.getId());
        assertEquals("Walter Scott", updated.getFirstName());
    }

    @Test
    void testDelete() {
        // Arrange
        Author author = new Author("John", "Doe");
        authorDao.save(author);
        int id = author.getId();

        // Act
        authorDao.delete(id);

        // Assert
        Author deleted = authorDao.findById(id);
        assertNull(deleted, "Nach dem Löschen sollte findById null zurückliefern.");
    }

    @Test
    void testSaveNullThrowsException() {
        // Überprüft, ob unsere Guard Clause ordnungsgemäß eine Exception wirft
        assertThrows(IllegalArgumentException.class, () -> {
            authorDao.save(null);
        });
    }
}