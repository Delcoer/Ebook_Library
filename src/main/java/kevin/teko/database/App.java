package kevin.teko.database;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Hauptklasse der JavaFX-Applikation.
 * Steuert den Lebenszyklus der App und prüft die Datenbank-Verfügbarkeit.
*/
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        DatabaseManager.initializeDatabase();
        try {
            scene = new Scene(loadFXML("primary"), 950, 650);
            
            stage.setTitle("TEKO E-Book Library Management System");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Kritischer Fehler beim Laden der FXML-Oberfläche: " + e.getMessage());
            throw e;
        }
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}