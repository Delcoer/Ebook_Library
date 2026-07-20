package kevin.teko.model;

public class Bookmark {
    private Integer id;
    private Integer ebookId;
    private int pageNumber;

    /**
     * Konstruktor 1: Für NEUE Lesezeichen (wird in der GUI erstellt).
     * Die ID bleibt vorerst null, da die DB diese später generiert.
     */
    public Bookmark(Integer ebookId, int pageNumber) {
        this.id = null;
        setEbookId(ebookId);
        setPageNumber(pageNumber);
    }

    /**
     * Konstruktor 2: Für EXISTIERENDE Lesezeichen (aus der Datenbank geladen).
     * Hier muss die ID zwingend vorhanden sein.
     */
    public Bookmark(int id, Integer ebookId, int pageNumber) {
        setId(id);
        setEbookId(ebookId);
        setPageNumber(pageNumber);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("Die Lesezeichen-ID muss größer als 0 sein.");
        }
        this.id = id;
    }

    public Integer getEbookId() {
        return ebookId;
    }

    public void setEbookId(Integer ebookId) {
        if (ebookId == null || ebookId <= 0) {
            throw new IllegalArgumentException("Das Lesezeichen muss einer gültigen E-Book-ID zugeordnet sein.");
        }
        this.ebookId = ebookId;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        if (pageNumber <= 0) {
            throw new IllegalArgumentException("Die Seitenzahl muss mindestens 1 sein.");
        }
        this.pageNumber = pageNumber;
    }
}
