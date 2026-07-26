package kevin.teko.model;

public class Highlight {
    private Integer id;
    private Integer ebookId;
    private int pageNumber;
    private String selectedText;
    private String color;
    private String createdAt;

    /**
     * Konstruktor 1: Für NEUE Highlights (wird in der GUI erstellt).
     * Die ID bleibt vorerst null, da die DB diese später generiert.
     */
    public Highlight(int ebookId, int page, String selectedText, String color) {
        setId(null);
        setEBookId(ebookId);
        setPageNumber(page);
        setSelectedText(selectedText);
        setColor(color);
        setCreatedAt(null);
    }
    /**
     * Konstruktor 2: Für EXISTIERENDE Highlights (aus der Datenbank geladen).
     * Hier muss die ID zwingend vorhanden sein.
     */
    public Highlight(int id, int ebookId, int page, String selectedText, String color, String createdAt) {
        setId(id);
        setEBookId(ebookId);
        setPageNumber(page);
        setSelectedText(selectedText);
        setColor(color);
        setCreatedAt(createdAt);
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

    public Integer getEBookId() {
        return ebookId;
    }

    public void setEBookId(Integer ebookId) {
        if (ebookId == null || ebookId <= 0) {
            throw new IllegalArgumentException("Das Highlight muss einer gültigen E-Book-ID zugeordnet sein.");
        }
        this.ebookId = ebookId;
    }

    public int getPage() {
        return pageNumber;
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

    public String getSelectedText() {
        return selectedText;
    }

    public void setSelectedText(String selectedText) {
        if (selectedText == null || selectedText.trim().isEmpty()) {
            throw new IllegalArgumentException("Der hervorgehobene Text darf nicht leer sein.");
        }
        this.selectedText = selectedText.trim();
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        // Fallback auf Standardfarbe (z.B. gelb), falls keine Farbe gewählt wurde
        if (color == null || color.trim().isEmpty()) {
            this.color = "#FFFF00"; // Standard Gelb
        } else {
            this.color = color.trim();
        }
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}