package kevin.teko.model;

public class Bookmark {
    private Integer id;
    private Integer ebookId;
    private int pageNumber;
    private String title;
    private String createdAt;

    /**
     * Konstruktor 1: Für NEUE Lesezeichen (wird in der GUI erstellt).
     * Die ID bleibt vorerst null, da die DB diese später generiert.
     */
    public Bookmark(int ebookId, int page, String title) {
        setId(null);
        setEBookId(ebookId);
        setPage(page);
        setTitle(title);
        setCreatedAt(null);
    }

    /**
     * Konstruktor 2: Für EXISTIERENDE Lesezeichen (aus der Datenbank geladen).
     * Hier muss die ID zwingend vorhanden sein.
     */
    public Bookmark(int id, int ebookId, int page, String title, String createdAt) {
        setId(id);
        setEBookId(ebookId);
        setPage(page);
        setTitle(title);
        setCreatedAt(createdAt);
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

    public Integer getEBookId() {
        return ebookId;
    }

    public void setTitle(String title) {
        // Titel ist optional – wenn leer, wird er als null gespeichert
        if (title != null && title.trim().isEmpty()) {
            this.title = null;
        } else {
            this.title = title != null ? title.trim() : null;
        }
    }
    
    public String getTitle() {
        return title;
    }

    public void setEBookId(Integer ebookId) {
        if (ebookId == null || ebookId <= 0) {
            throw new IllegalArgumentException("Das Lesezeichen muss einer gültigen E-Book-ID zugeordnet sein.");
        }
        this.ebookId = ebookId;
    }

    public int getPage() {
        return pageNumber;
    }

    public void setPage(int pageNumber) {
        if (pageNumber <= 0) {
            throw new IllegalArgumentException("Die Seitenzahl muss mindestens 1 sein.");
        }
        this.pageNumber = pageNumber;
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
