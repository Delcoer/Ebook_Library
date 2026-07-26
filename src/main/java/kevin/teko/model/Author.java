package kevin.teko.model;

public class Author {
    private Integer id;
    private String firstName;
    private String lastName;

    /**
     * Konstruktor 1: Für NEUE Autoren (die noch nicht in der Datenbank sind).
     * Die ID wird automatisch auf null gesetzt.
     */
    public Author(String firstName, String lastName) {
        setId(null); // Noch keine DB-ID vorhanden
        setFirstName(firstName);
        setLastName(lastName);
    }

    /**
     * Konstruktor 2: Für EXISTIERENDE Autoren (die aus der Datenbank geladen werden).
     */
    public Author(int id, String firstName, String lastName) {
        setId(id);
        setFirstName(firstName);
        setLastName(lastName);
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("Die Autoren-ID muss größer als 0 sein.");
        }
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = (firstName != null) ? firstName.trim() : "";
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Der Nachname des Autors darf nicht leer sein.");
        }
        this.lastName = lastName.trim();
    }
    
    public String getFullName() {
        if (firstName.isEmpty()) {
            return lastName;
        }
        return firstName + " " + lastName;
    }
}