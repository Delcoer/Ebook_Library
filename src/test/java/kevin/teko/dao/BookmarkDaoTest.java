package kevin.teko.dao;

import kevin.teko.database.DatabaseManager;
import kevin.teko.model.Bookmark;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookmarkDaoTest {

	private BookmarkDao bookmarkDao;
	private int ebookId;

	@BeforeEach
	void setUp() throws Exception {
		bookmarkDao = new BookmarkDao();

		try (Connection conn = DatabaseManager.getConnection();
			 Statement stmt = conn.createStatement()) {

			stmt.execute("DROP TABLE IF EXISTS Bookmark");
			stmt.execute("DROP TABLE IF EXISTS EBook");

			stmt.execute("""
				CREATE TABLE EBook (
					id INTEGER PRIMARY KEY AUTOINCREMENT
				)
			""");

			stmt.execute("""
				CREATE TABLE Bookmark (
					id INTEGER PRIMARY KEY AUTOINCREMENT,
					ebook_id INTEGER NOT NULL,
					page_number INTEGER NOT NULL,
					title TEXT,
					created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
					FOREIGN KEY (ebook_id) REFERENCES EBook(id) ON DELETE CASCADE
				)
			""");

			try (PreparedStatement pstmt = conn.prepareStatement(
				"INSERT INTO EBook DEFAULT VALUES",
				Statement.RETURN_GENERATED_KEYS
			)) {
				pstmt.executeUpdate();

				try (ResultSet rs = pstmt.getGeneratedKeys()) {
					assertTrue(rs.next(), "Es sollte eine EBook-ID erzeugt werden.");
					ebookId = rs.getInt(1);
				}
			}
		}
	}

	@Test
	void testSaveAndFindById() {
		Bookmark bookmark = new Bookmark(ebookId, 12, "Kapitel 1");

		bookmarkDao.save(bookmark);

		assertNotNull(bookmark.getId(), "Die ID sollte nach dem Speichern nicht null sein.");
		assertTrue(bookmark.getId() > 0, "Die ID sollte größer als 0 sein.");

		Bookmark fetchedBookmark = bookmarkDao.findById(bookmark.getId());
		assertNotNull(fetchedBookmark, "Das Lesezeichen sollte in der Datenbank gefunden werden.");
		assertEquals(ebookId, fetchedBookmark.getEBookId());
		assertEquals(12, fetchedBookmark.getPage());
		assertEquals("Kapitel 1", fetchedBookmark.getTitle());
		assertNotNull(fetchedBookmark.getCreatedAt(), "createdAt sollte beim Laden gesetzt sein.");
	}

	@Test
	void testFindAll() {
		bookmarkDao.save(new Bookmark(ebookId, 5, "Eintrag 1"));
		bookmarkDao.save(new Bookmark(ebookId, 10, "Eintrag 2"));

		List<Bookmark> bookmarks = bookmarkDao.findAll();

		assertEquals(2, bookmarks.size(), "Es sollten genau 2 Lesezeichen in der DB sein.");
	}

	@Test
	void testUpdate() {
		Bookmark bookmark = new Bookmark(ebookId, 8, "Alt");
		bookmarkDao.save(bookmark);

		bookmark.setTitle("Neu");
		bookmarkDao.update(bookmark);

		Bookmark updated = bookmarkDao.findById(bookmark.getId());
		assertEquals("Neu", updated.getTitle());
	}

	@Test
	void testDelete() {
		Bookmark bookmark = new Bookmark(ebookId, 3, "Zum Löschen");
		bookmarkDao.save(bookmark);
		int id = bookmark.getId();

		bookmarkDao.delete(id);

		Bookmark deleted = bookmarkDao.findById(id);
		assertNull(deleted, "Nach dem Löschen sollte findById null zurückliefern.");
	}

	@Test
	void testSaveNullThrowsException() {
		assertThrows(IllegalArgumentException.class, () -> bookmarkDao.save(null));
	}
}
