package kevin.teko.model;

public class EBook {
    private Integer id;
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

    /**
     * Konstruktor 1: Für NEUE E-Books (ohne ID).
     * Die ID wird automatisch auf 0 gesetzt und später von der DB per Auto-Increment vergeben.
     */
    public EBook(String title, String isbn, String filePath, int fileFormatId, String coverPath, 
                 String readingStatus, int rating, int pageCount, Integer publisherId) {
        setId(null);
        setTitle(title);
        setIsbn(isbn);
        setFilePath(filePath);
        setFileFormatId(fileFormatId);
        setCoverPath(coverPath);
        setReadingStatus(readingStatus);
        setRating(rating);
        setPageCount(pageCount);
        setAddedAt(null);
        setPublisherId(publisherId);
    }

    /**
     * Konstruktor 2: Für BESTEHENDE E-Books (mit ID).
     * Wird vom Data Access Object genutzt, wenn Daten aus der Datenbank geladen werden.
     */
    public EBook(int id, String title, String isbn, String filePath, int fileFormatId, String coverPath, 
                 String readingStatus, int rating, int pageCount, String addedAt, Integer publisherId) {
        setId(id);
        setTitle(title);
        setIsbn(isbn);
        setFilePath(filePath);
        setFileFormatId(fileFormatId);
        setCoverPath(coverPath);
        setReadingStatus(readingStatus);
        setRating(rating);
        setPageCount(pageCount);
        setAddedAt(addedAt);
        setPublisherId(publisherId);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("Die E-Book-ID muss größer als 0 sein.");
        }
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Der Titel kann nicht leer sein.");
        }
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            this.isbn = null;
            return;
        }

        String cleanIsbn = isbn.replaceAll("[\\s-]", "");

        if (!cleanIsbn.matches("\\d{9}[0-9Xx]") && !cleanIsbn.matches("\\d{13}")) {
            throw new IllegalArgumentException("Ungültiges ISBN-Format. Erwartet werden 10 oder 13 Ziffern.");
        }

        this.isbn = isbn;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Der Dateipfad darf nicht null oder leer sein.");
        }
        this.filePath = filePath;
    }

    public int getFileFormatId() {
        return fileFormatId;
    }

    public void setFileFormatId(int fileFormatId) {
        if (fileFormatId < 1) {
            throw new IllegalArgumentException("Die Dateiformat-ID muss eine gültige positive Zahl sein.");
        }
        this.fileFormatId = fileFormatId;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        if (coverPath != null && coverPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Der Cover-Pfad darf nicht leer sein, wenn er angegeben wird.");
        }
        this.coverPath = coverPath;
    }

    public String getReadingStatus() {
        return readingStatus;
    }

    public void setReadingStatus(String readingStatus) {
        if (readingStatus == null || readingStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Der Lesestatus darf nicht null oder leer sein.");
        }

        String upperStatus = readingStatus.toUpperCase().trim();

        if (!upperStatus.equals("NOT_STARTED") &&
                !upperStatus.equals("READING") &&
                !upperStatus.equals("COMPLETED")) {
            throw new IllegalArgumentException("Ungültiger Lesestatus. Erlaubt sind: NOT_STARTED, READING, COMPLETED.");
        }

        this.readingStatus = upperStatus;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Die Bewertung muss zwischen 0 und 5 liegen.");
        }
        this.rating = rating;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        if (pageCount < 1) {
            throw new IllegalArgumentException("Die Seitenzahl muss über 0 sein.");
        }
        this.pageCount = pageCount;
    }

    public String getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(String addedAt) {
        this.addedAt = (addedAt != null) ? addedAt.trim() : null;
    }

    public Integer getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Integer publisherId) {
        // Null-Check verhindert die NullPointerException beim Unboxing
        if (publisherId != null && publisherId < 1) {
            throw new IllegalArgumentException("Die Publisher-ID muss eine gültige positive Zahl sein.");
        }
        this.publisherId = publisherId;
    }
}