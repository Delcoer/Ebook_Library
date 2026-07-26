package kevin.teko.model;

public class Publisher {
    private Integer id;
    private String name;

    /**
     * Konstruktor 1: Für NEUE Verlage (wird in der GUI erstellt).
     * Die ID bleibt vorerst null, da die DB diese später generiert.
     */
    public Publisher(String name) {
        setId(null);
        setName(name);
    }

    /**
     * Konstruktor 2: Für EXISTIERENDE Verlage (aus der Datenbank geladen).
     * Hier muss die ID zwingend vorhanden sein.
     */
    public Publisher(int id, String name) {
        setId(id);
        setName(name);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("Die Verlags-ID muss größer als 0 sein.");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Der Name des Verlags darf nicht leer sein.");
        }
        this.name = name.trim();
    }
}
