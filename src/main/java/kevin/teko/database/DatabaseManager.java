package kevin.teko.database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class DatabaseManager {

    // Central absolute path to the SQLite database
    private static final String DB_FILE_PATH = "/home/kev/Documents/Teko/Java_Code/Datenbanken_Project_Code/Projektarbeit__Code/application_teko_database/ebook_library.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE_PATH;

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        
        // WICHTIG: Fremdschlüssel-Unterstützung bei SQLite für diese Verbindung aktivieren
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        
        return conn;
    }

    public static void initializeDatabase() {
        Path databasePath = Paths.get(DB_FILE_PATH);
        boolean databaseAlreadyExists = Files.exists(databasePath);

        try (Connection conn = getConnection()) {
            if (!databaseAlreadyExists) {
                createDatabaseSchema(conn);
            } else if (!isCurrentSchema(conn)) {
                resetDatabaseSchema(conn);
            }
            System.out.println("Datenbank-Check erfolgreich: Datei ist einsatzbereit unter -> " + DB_FILE_PATH);
        } catch (SQLException e) {
            System.err.println("Fehler beim Datenbank-Check: " + e.getMessage());
        }
    }

    private static boolean isCurrentSchema(Connection conn) throws SQLException {
        Set<String> ebookColumns = new HashSet<>();
        DatabaseMetaData metaData = conn.getMetaData();

        try (var rs = metaData.getColumns(null, null, "EBook", null)) {
            while (rs.next()) {
                ebookColumns.add(rs.getString("COLUMN_NAME").toLowerCase());
            }
        }

        return ebookColumns.contains("title")
                && ebookColumns.contains("file_path")
                && ebookColumns.contains("file_format_id")
                && ebookColumns.contains("reading_status")
                && ebookColumns.contains("added_at");
    }

    private static void resetDatabaseSchema(Connection conn) throws SQLException {
        String[] dropStatements = {
                "DROP TABLE IF EXISTS EBook_Author",
                "DROP TABLE IF EXISTS EBook_Tag",
                "DROP TABLE IF EXISTS EBook_Collection",
                "DROP TABLE IF EXISTS Highlight",
                "DROP TABLE IF EXISTS Note",
                "DROP TABLE IF EXISTS Bookmark",
                "DROP TABLE IF EXISTS EBook",
                "DROP TABLE IF EXISTS Author",
                "DROP TABLE IF EXISTS Publisher",
                "DROP TABLE IF EXISTS FileFormat",
                "DROP TABLE IF EXISTS Tag",
                "DROP TABLE IF EXISTS Collection"
        };

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = OFF");

            for (String dropStatement : dropStatements) {
                stmt.execute(dropStatement);
            }

            executeSqlScript(stmt, "/sql/CreateDatabase.sql");

            stmt.execute("PRAGMA foreign_keys = ON");
        } catch (IOException e) {
            throw new SQLException("Fehler beim Laden des Datenbankschemas.", e);
        }
    }

    private static void createDatabaseSchema(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = OFF");
            executeSqlScript(stmt, "/sql/CreateDatabase.sql");
            stmt.execute("PRAGMA foreign_keys = ON");
        } catch (IOException e) {
            throw new SQLException("Fehler beim Erstellen des Datenbankschemas.", e);
        }
    }

    private static void executeSqlScript(Statement stmt, String resourcePath) throws IOException, SQLException {
        try (InputStream inputStream = DatabaseManager.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("SQL-Ressource nicht gefunden: " + resourcePath);
            }

            String script = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            script = script.replaceAll("(?s)/\\*.*?\\*/", "");

            StringBuilder cleanedScript = new StringBuilder();
            for (String line : script.split("\\R")) {
                String trimmedLine = line.replaceAll("--.*$", "").trim();
                if (!trimmedLine.isEmpty()) {
                    cleanedScript.append(trimmedLine).append('\n');
                }
            }

            for (String statement : cleanedScript.toString().split(";")) {
                String sql = statement.trim();
                if (!sql.isEmpty()) {
                    stmt.execute(sql);
                }
            }
        }
    }
}