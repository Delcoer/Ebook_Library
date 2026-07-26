package kevin.teko.model;

public class Collection {
    private Integer id;
    private String name;
    private String description;

    /**
     * Konstruktor 1: Für NEUE Sammlungen (wird in der GUI erstellt).
     * Die ID bleibt vorerst null, da die DB diese später generiert.
     */
    public Collection(String name, String description) {
        setId(null);
        setName(name);
        setDescription(description);
    }

    /**
     * Konstruktor 2: Für EXISTIERENDE Sammlungen (aus der Datenbank geladen).
     * Hier muss die ID zwingend vorhanden sein.
     */
    public Collection(int id, String name, String description) {
        setId(id);
        setName(name);
        setDescription(description);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("Die Sammlungs-ID muss größer als 0 sein.");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Der Name der Sammlung darf nicht leer sein.");
        }
        this.name = name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = (description != null) ? description.trim() : "";
    }
}