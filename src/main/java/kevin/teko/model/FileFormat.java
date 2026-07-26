package kevin.teko.model;

public class FileFormat {
    private Integer id;
    private String name;
    private String mimeType;

    /**
     * Konstruktor 1: Für NEUE Dateiformate (wird in der GUI erstellt).
     * Die ID bleibt vorerst null, da die DB diese später generiert.
     */
    public FileFormat(String name, String mimeType) {
        setId(null);
        setName(name);
        setMimeType(mimeType);
    }

    /**
     * Konstruktor 2: Für EXISTIERENDE Dateiformate (aus der Datenbank geladen).
     * Hier muss die ID zwingend vorhanden sein.
     */
    public FileFormat(int id, String name, String mimeType) {
        setId(id);
        setName(name);
        setMimeType(mimeType);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("Die Dateiformat-ID muss größer als 0 sein.");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Der Name des Dateiformats darf nicht leer sein.");
        }
        this.name = name.trim().toUpperCase();
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = (mimeType != null) ? mimeType.trim() : "";
    }
}