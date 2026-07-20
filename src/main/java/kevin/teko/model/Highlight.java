package kevin.teko.model;

public class Highlight {
    private Integer id;
    private Integer ebookId;
    private String text;
    private int pageNumber;

    /**
     * Konstruktor 1: Für NEUE Highlights (wird in der GUI erstellt).
     * Die ID bleibt vorerst null, da die DB diese später generiert.
     */
    public Highlight(Integer ebookId, String text, int pageNumber) {
        this.id = null;
        setEbookId(ebookId);
        setText(text);
        setPageNumber(pageNumber);
    }

    /**
     * Konstruktor 2: Für EXISTIERENDE Highlights (aus der Datenbank geladen).
     * Hier muss die ID zwingend vorhanden sein.
     */
    public Highlight(int id, Integer ebookId, String text, int pageNumber) {
        setId(id);
        setEbookId(ebookId);
        setText(text);
        setPageNumber(pageNumber);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("Die Highlight-ID muss größer als 0 sein.");
        }
        this.id = id;
    }

    public Integer getEbookId() {
        return ebookId;
    }

    public void setEbookId(Integer ebookId) {
        if (ebookId == null || ebookId <= 0) {
            throw new IllegalArgumentException("Das Highlight muss einer gültigen E-Book-ID zugeordnet sein.");
        }
        this.ebookId = ebookId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Der markierte Text darf nicht leer sein.");
        }
        this.text = text.trim();
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