package kevin.teko.model;

public class Bookmark {
    private int id;
    private int eBookId;
    private int page;
    private String title;
    private String createdAt;

    public Bookmark(int eBookId, int page, String title, String createdAt) {
        this.eBookId = eBookId;
        this.page = page;
        this.title = title;
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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
