package kevin.teko.model;

package kevin.teko.database.model;

public class Note {
    private int id;
    private int eBookId;
    private Integer page;
    private String noteText;
    private String createdAt;

    public Note(int eBookId, Integer page, String noteText, String createdAt) {
        this.eBookId = eBookId;
        this.page = page;
        this.noteText = noteText;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEBookId() { return eBookId; }
    public void setEBookId(int eBookId) { this.eBookId = eBookId; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public String getNoteText() { return noteText; }
    public void setNoteText(String noteText) { this.noteText = noteText; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
