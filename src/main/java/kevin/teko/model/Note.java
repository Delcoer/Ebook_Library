package kevin.teko.model;

public class Note {
    private Integer id;
    private Integer ebookId;
    private String content;
    private int pageNumber;
    private String createdAt;

    /**
     * Konstruktor 1: Für NEUE Notizen (wird in der GUI erstellt).
     * Die ID bleibt vorerst null, da die DB diese später generiert.
     */
    public Note(int ebookId, int page, String content) {
        setId(null);
        setEBookId(ebookId);
        setPageNumber(page);
        setContent(content);
        setCreatedAt(null);
    }

    /**
     * Konstruktor 2: Für EXISTIERENDE Notizen (aus der Datenbank geladen).
     * Hier muss die ID zwingend vorhanden sein.
     */
    public Note(int id, int ebookId, int page, String content, String createdAt) {
        setId(id);
        setEBookId(ebookId);
        setPageNumber(page);
        setContent(content);
        setCreatedAt(createdAt);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("Die Notiz-ID muss größer als 0 sein.");
        }
        this.id = id;
    }

    public Integer getEBookId() {
        return ebookId;
    }

    public void setEBookId(Integer ebookId) {
        if (ebookId == null || ebookId <= 0) {
            throw new IllegalArgumentException("Die Notiz muss einer gültigen E-Book-ID zugeordnet sein.");
        }
        this.ebookId = ebookId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Der Inhalt der Notiz darf nicht leer sein.");
        }
        this.content = content.trim();
    }

    public int getPage() {
        return pageNumber;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setPageNumber(int pageNumber) {
        if (pageNumber <= 0) {
            throw new IllegalArgumentException("Die Seitenzahl muss mindestens 1 sein.");
        }
        this.pageNumber = pageNumber;
    }
}
