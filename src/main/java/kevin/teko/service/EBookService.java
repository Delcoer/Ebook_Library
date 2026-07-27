package kevin.teko.service;

import kevin.teko.dao.AuthorDao;
import kevin.teko.dao.EBookDao;
import kevin.teko.model.Author;
import kevin.teko.model.EBook;

import java.util.List;
import java.util.Objects;

public class EBookService {

    private final EBookDao eBookDao;
    private final AuthorDao authorDao;

    // Dependency Injection: Wir übergeben die benötigten DAOs von außen
    public EBookService(EBookDao eBookDao, AuthorDao authorDao) {
        this.eBookDao = Objects.requireNonNull(eBookDao, "EBookDao darf nicht null sein");
        this.authorDao = Objects.requireNonNull(authorDao, "AuthorDao darf nicht null sein");
    }

    /**
     * Nimmt ein neues E-Book auf. 
     * Validiert die Daten, legt den Autor an und verknüpft beide in der Datenbank.
     */
    public EBook registerNewEBook(EBook eBook, String authorVorname, String authorNachname) {
        
        // 1. VALIDIERUNG (Ungültige Eingaben sofort blockieren)
        if (eBook.getTitle() == null || eBook.getTitle().isBlank()) {
            throw new IllegalArgumentException("E-Book Titel darf nicht leer sein!");
        }
        if (eBook.getFilePath() == null || eBook.getFilePath().isBlank()) {
            throw new IllegalArgumentException("Dateipfad muss angegeben werden!");
        }
        if (authorNachname == null || authorNachname.isBlank()) {
            throw new IllegalArgumentException("Der Nachname des Autors ist erforderlich!");
        }

        // 2. ORCHESTRIERUNG (Ablauf koordinieren)
        
        // Schritt A: Autor in der Datenbank anlegen
        Author newAuthor = new Author(authorVorname, authorNachname);
        authorDao.save(newAuthor);

        // Schritt B: E-Book in der Datenbank anlegen
        eBookDao.save(eBook);

        // Schritt C: Die Verknüpfung in der m:n Tabelle (EBook_Autor) herstellen
        if (newAuthor.getId() == null || eBook.getId() == null) {
            throw new IllegalStateException("Die IDs für Autor oder E-Book wurden nach dem Speichern nicht gesetzt.");
        }
        eBookDao.linkAuthorToEBook(eBook.getId(), newAuthor.getId());

        // Das fertige Objekt mit generierter ID zurückgeben
        return eBook;
    }

    public List<EBook> getAllEBooks() {
        return eBookDao.findAll();
    }
}