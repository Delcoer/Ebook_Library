package kevin.teko.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import kevin.teko.dao.AuthorDao;
import kevin.teko.dao.EBookDao;
import kevin.teko.database.DatabaseManager;
import kevin.teko.model.EBook;
import kevin.teko.service.EBookService;

import java.io.File;
import java.time.LocalDateTime;

public class LibraryGUI extends Application {

    private EBookService eBookService;
    private final ListView<String> bookListView = new ListView<>();

    @Override
    public void init() throws Exception {
        // Backend-Schichten beim Anwendungsstart initialisieren

        java.io.File dbFile = new java.io.File("library.db"); // Passe "library.db" an deinen Dateinamen an
        System.out.println(">>> ECHTER DATENBANK-PFAD: " + dbFile.getAbsolutePath());

        DatabaseManager.initializeDatabase();
        EBookDao eBookDao = new EBookDao();
        AuthorDao authorDao = new AuthorDao();
        this.eBookService = new EBookService(eBookDao, authorDao);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("E-Book Bibliothek");

        // Layouts
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        // Formular zum Anlegen eines neuen E-Books
        GridPane formGrid = createFormGrid();

        // Anzeige-Bereich
        VBox centerArea = new VBox(10);
        Label titleLabel = new Label("Meine E-Books");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        bookListView.setPlaceholder(new Label("Noch keine E-Books vorhanden."));
        refreshBookList();
        centerArea.getChildren().addAll(titleLabel, bookListView);

        root.setLeft(formGrid);
        root.setCenter(centerArea);
        BorderPane.setMargin(centerArea, new Insets(0, 0, 0, 15));

        Scene scene = new Scene(root, 850, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createFormGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        // Eingabefelder
        TextField tfTitel = new TextField();
        TextField tfPfad = new TextField();
        tfPfad.setPromptText("Pfad wählen oder eintragen...");
        tfPfad.setPrefWidth(200);

        // FileChooser-Button direkt neben dem Pfad-Feld
        Button btnBrowse = new Button("Durchsuchen...");
        btnBrowse.setOnAction(e -> handleSelectFile(tfPfad, btnBrowse));

        HBox pathBox = new HBox(5, tfPfad, btnBrowse);

        TextField tfVorname = new TextField();
        TextField tfNachname = new TextField();
        Button btnSave = new Button("E-Book Speichern");

        grid.add(new Label("Neues E-Book anlegen"), 0, 0, 2, 1);
        grid.add(new Label("Titel:"), 0, 1);
        grid.add(tfTitel, 1, 1);

        grid.add(new Label("Dateipfad:"), 0, 2);
        grid.add(pathBox, 1, 2);

        grid.add(new Label("Autor Vorname:"), 0, 3);
        grid.add(tfVorname, 1, 3);

        grid.add(new Label("Autor Nachname:"), 0, 4);
        grid.add(tfNachname, 1, 4);

        grid.add(btnSave, 1, 5);

        // Action-Handler für den Speichern-Button
        btnSave.setOnAction(e -> {
            try {
                EBook newBook = new EBook(
                        tfTitel.getText(),
                        null,
                        tfPfad.getText(),
                        1,
                        null,
                        "NOT_STARTED",
                        0,
                        1,
                        null);
                newBook.setAddedAt(LocalDateTime.now().toString());

                // Aufruf der Service-Schicht
                eBookService.registerNewEBook(newBook, tfVorname.getText(), tfNachname.getText());

                // Erfolgsmeldung & Felder leeren
                showAlert(Alert.AlertType.INFORMATION, "Erfolg", "E-Book erfolgreich gespeichert!");
                refreshBookList();
                tfTitel.clear();
                tfPfad.clear();
                tfVorname.clear();
                tfNachname.clear();

            } catch (IllegalArgumentException ex) {
                String realCause = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                showAlert(Alert.AlertType.ERROR, "Validierungsfehler", realCause);
            } catch (Exception ex) {
                String realCause = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                showAlert(Alert.AlertType.ERROR, "Fehler beim Speichern", realCause);
            }
        });

        return grid;
    }

    // Hilfsmethode für den FileChooser
    private void handleSelectFile(TextField targetTextField, Button sourceButton) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("E-Book Datei auswählen");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("E-Books (*.pdf, *.epub)", "*.pdf", "*.epub"),
                new FileChooser.ExtensionFilter("PDF Dateien (*.pdf)", "*.pdf"),
                new FileChooser.ExtensionFilter("EPUB Dateien (*.epub)", "*.epub"),
                new FileChooser.ExtensionFilter("Alle Dateien", "*.*")
        );

        Window stage = sourceButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            targetTextField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void refreshBookList() {
        bookListView.getItems().setAll(
                eBookService.getAllEBooks().stream()
                        .map(ebook -> {
                            String addedAt = ebook.getAddedAt() != null ? ebook.getAddedAt() : "ohne Datum";
                            return ebook.getId() + " - " + ebook.getTitle() + " (" + addedAt + ")";
                        })
                        .toList());
    }

    public static void main(String[] args) {
        launch(args);
    }
}