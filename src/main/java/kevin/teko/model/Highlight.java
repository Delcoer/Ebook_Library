package kevin.teko.model;

public class Highlight {
    private int id;
    private int eBookId;
    private Integer page;
    private String highlightedText;
    private String noteText;
    private String color;
    private String createdAt;

    public Highlight(int eBookId, Integer page, String highlightedText, String noteText, String color,
            String createdAt) {
        this.eBookId = eBookId;
        this.page = page;
        this.highlightedText = highlightedText;
        this.noteText = noteText;
        this.color = color;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEBookId() {
        return eBookId;
    }

    public void setEBookId(int eBookId) {
        this.eBookId = eBookId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getHighlightedText() {
        return highlightedText;
    }

    public void setHighlightedText(String highlightedText) {
        this.highlightedText = highlightedText;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
