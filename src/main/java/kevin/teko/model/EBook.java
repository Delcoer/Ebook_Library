package kevin.teko.model;

public class EBook {
    private int id;
    private String title;
    private String isbn;
    private String filePath;
    private int fileFormatId;
    private String coverPath;
    private String readingStatus;
    private int rating;
    private int pageCount;
    private String addedAt;
    private Integer publisherId;

    public EBook(String title, String isbn, String filePath, int fileFormatId, String coverPath, String readingStatus, int rating, int pageCount, String addedAt, Integer publisherId) {
        this.title = title;
        this.isbn = isbn;
        this.filePath = filePath;
        this.fileFormatId = fileFormatId;
        this.coverPath = coverPath;
        this.readingStatus = readingStatus;
        this.rating = rating;
        this.pageCount = pageCount;
        this.addedAt = addedAt;
        this.publisherId = publisherId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getFileFormatId() {
        return fileFormatId;
    }

    public void setFileFormatId(int fileFormatId) {
        this.fileFormatId = fileFormatId;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getReadingStatus() {
        return readingStatus;
    }

    public void setReadingStatus(String readingStatus) {
        this.readingStatus = readingStatus;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(String addedAt) {
        this.addedAt = addedAt;
    }

    public Integer getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Integer publisherId) {
        this.publisherId = publisherId;
    }
}