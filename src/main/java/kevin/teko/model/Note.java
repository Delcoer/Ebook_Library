package kevin.teko.model;

public class Note {
    private Integer id;
    private Integer ebookId;
    private String content;
    private int pageNumber;

    /**
     * Konstruktor 1: Für NEUE Notizen (wird in der GUI erstellt).
     * Die ID bleibt vorerst null, da die DB diese später generiert.
     */
    public Note(Integer ebookId, String content, int pageNumber) {
        this.id = null;
        setEbookId(ebookId);
        setContent(content);
        setPageNumber(pageNumber);
    }

    /**
     * Konstruktor 2: Für EXISTIERENDE Notizen (aus der Datenbank geladen).
     * Hier muss die ID zwingend vorhanden sein.
     */
    public Note(int id, Integer ebookId, String content, int pageNumber) {
        setId(id);
        setEbookId(ebookId);
        setContent(content);
        setPageNumber(pageNumber);
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

    public Integer getEbookId() {
        return ebookId;
    }

    public void setEbookId(Integer ebookId) {
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
