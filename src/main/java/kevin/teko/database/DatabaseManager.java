package kevin.teko.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:ebook_library.db";

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        
        // WICHTIG: Fremdschlüssel-Unterstützung bei SQLite für diese Verbindung aktivieren
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        
        return conn;
    }


    public static void initializeDatabase() {
        try (Connection conn = getConnection()) {
            
            System.out.println("Datenbank-Check erfolgreich: Datei ist einsatzbereit.");
        } catch (SQLException e) {
            System.err.println("Fehler beim Datenbank-Check: " + e.getMessage());
        }
    }
}