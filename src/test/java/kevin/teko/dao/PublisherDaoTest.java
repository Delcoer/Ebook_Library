package kevin.teko.dao;

import kevin.teko.database.DatabaseManager;
import kevin.teko.model.Publisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PublisherDaoTest {

    private PublisherDao publisherDao;

    @BeforeEach
    void setUp() throws Exception {
        publisherDao = new PublisherDao();

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS Publisher");
            stmt.execute("""
                CREATE TABLE Publisher (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE
                )
            """);
        }
    }

    @Test
    void testSaveAndFindById() {
        Publisher publisher = new Publisher("Penguin");

        publisherDao.save(publisher);

        assertNotNull(publisher.getId());
        assertTrue(publisher.getId() > 0);

        Publisher fetched = publisherDao.findById(publisher.getId());
        assertNotNull(fetched);
        assertEquals("Penguin", fetched.getName());
    }

    @Test
    void testFindAll() {
        publisherDao.save(new Publisher("A"));
        publisherDao.save(new Publisher("B"));

        List<Publisher> publishers = publisherDao.findAll();

        assertEquals(2, publishers.size());
    }

    @Test
    void testUpdate() {
        Publisher publisher = new Publisher("Alt");
        publisherDao.save(publisher);

        publisher.setName("Neu");
        publisherDao.update(publisher);

        Publisher updated = publisherDao.findById(publisher.getId());
        assertEquals("Neu", updated.getName());
    }

    @Test
    void testDelete() {
        Publisher publisher = new Publisher("Delete Me");
        publisherDao.save(publisher);
        int id = publisher.getId();

        publisherDao.delete(id);

        assertNull(publisherDao.findById(id));
    }
}