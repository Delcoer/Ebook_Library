package kevin.teko.gui;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kevin.teko.dao.AuthorDao;
import kevin.teko.dao.EBookDao;
import kevin.teko.database.DatabaseManager;
import kevin.teko.model.EBook;
import kevin.teko.service.EBookService;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class LibraryGUI extends Application {

    private static final ObservableList<String> READING_STATUS_OPTIONS = FXCollections.observableArrayList(
            "NOT_STARTED",
            "READING",
            "COMPLETED"
    );

    private static final List<FileFormatOption> FILE_FORMAT_OPTIONS = List.of(
            new FileFormatOption(1, "PDF"),
            new FileFormatOption(2, "EPUB"),
            new FileFormatOption(3, "MOBI"),
            new FileFormatOption(4, "AZW3")
    );

    private final ObservableList<EBook> ebookItems = FXCollections.observableArrayList();
    private final ListView<EBook> bookListView = new ListView<>(ebookItems);
    private final SimpleObjectProperty<EBook> selectedEBookProperty = new SimpleObjectProperty<>();
    private final Label statisticsLabel = new Label();

    private EBookService eBookService;
    private Stage primaryStage;

    @Override
    public void init() throws Exception {
        DatabaseManager.initializeDatabase();
        EBookDao eBookDao = new EBookDao();
        AuthorDao authorDao = new AuthorDao();
        this.eBookService = new EBookService(eBookDao, authorDao);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("E-Book Bibliothek");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(18));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f8f7f4, #ede8df);");

        Label headline = new Label("Meine E-Books");
        headline.setFont(Font.font(22));
        Label subtitle = new Label("Liste mit Cover-Vorschau, Bearbeiten und Löschen direkt aus der Auswahl.");
        subtitle.setStyle("-fx-text-fill: #5a5a5a;");
        statisticsLabel.setStyle("-fx-text-fill: #6a5d4d; -fx-font-weight: bold;");

        VBox headerBox = new VBox(4, headline, subtitle, statisticsLabel);
        headerBox.setPadding(new Insets(0, 0, 14, 0));

        bookListView.setPlaceholder(new Label("Noch keine E-Books vorhanden."));
        bookListView.setCellFactory(listView -> new ListCell<>() {
            private final StackPane coverPane = createCoverPane();
            private final Label titleLabel = new Label();
            private final Label detailsLabel = new Label();
            private final VBox textBox = new VBox(6, titleLabel, detailsLabel);
            private final HBox cellBox = new HBox(14, coverPane, textBox);

            {
                titleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
                detailsLabel.setStyle("-fx-text-fill: #4f4f4f;");
                detailsLabel.setWrapText(true);
                textBox.setAlignment(Pos.CENTER_LEFT);
                cellBox.setAlignment(Pos.CENTER_LEFT);
                cellBox.setPadding(new Insets(10));
                cellBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #ded7c8; -fx-border-radius: 12;");
            }

            @Override
            protected void updateItem(EBook ebook, boolean empty) {
                super.updateItem(ebook, empty);

                if (empty || ebook == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                titleLabel.setText(ebook.getTitle());
                detailsLabel.setText(buildBookDetails(ebook));
                updateCoverPane(coverPane, ebook.getCoverPath());
                setText(null);
                setGraphic(cellBox);
            }
        });

        bookListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                selectedEBookProperty.set(newValue));

        VBox listContainer = new VBox(12, headerBox, bookListView);
        listContainer.setPadding(new Insets(0, 0, 0, 0));
        VBox.setVgrow(bookListView, Priority.ALWAYS);

        Button newButton = new Button("Neues E-Book");
        Button editButton = new Button("Auswahl bearbeiten");
        Button deleteButton = new Button("Auswahl löschen");

        newButton.setOnAction(event -> openEBookEditor(null));
        editButton.setOnAction(event -> {
            EBook selected = selectedEBookProperty.get();
            if (selected != null) {
                openEBookEditor(selected);
            }
        });
        deleteButton.setOnAction(event -> deleteSelectedEBook());

        editButton.disableProperty().bind(selectedEBookProperty.isNull());
        deleteButton.disableProperty().bind(selectedEBookProperty.isNull());

        HBox actionBar = new HBox(10, newButton, editButton, deleteButton);
        actionBar.setPadding(new Insets(25, 0, 0, 0));
        actionBar.setAlignment(Pos.CENTER_LEFT);

        VBox centerArea = new VBox(12, listContainer, new Separator(), actionBar);
        root.setCenter(centerArea);

        Scene scene = new Scene(root, 1050, 720);
        primaryStage.setScene(scene);
        primaryStage.show();

        refreshBookList(null);
    }

    private void openEBookEditor(EBook bookToEdit) {
        boolean updateMode = bookToEdit != null;

        Stage dialogStage = new Stage();
        if (primaryStage != null) {
            dialogStage.initOwner(primaryStage);
        }
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setTitle(updateMode ? "E-Book bearbeiten" : "Neues E-Book anlegen");

        Label title = new Label(updateMode ? "E-Book bearbeiten" : "Neues E-Book anlegen");
        title.setFont(Font.font(18));

        TextField idField = createDisabledTextField(updateMode && bookToEdit != null && bookToEdit.getId() != null
                ? String.valueOf(bookToEdit.getId())
                : "Wird automatisch vergeben");
        TextField titleField = new TextField(updateMode ? bookToEdit.getTitle() : "");
        TextField isbnField = new TextField(updateMode && bookToEdit.getIsbn() != null ? bookToEdit.getIsbn() : "");
        TextField filePathField = new TextField(updateMode ? bookToEdit.getFilePath() : "");
        TextField coverPathField = new TextField(updateMode && bookToEdit.getCoverPath() != null ? bookToEdit.getCoverPath() : "");
        ComboBox<FileFormatOption> fileFormatCombo = new ComboBox<>(FXCollections.observableArrayList(FILE_FORMAT_OPTIONS));
        ComboBox<String> readingStatusCombo = new ComboBox<>(READING_STATUS_OPTIONS);
        Spinner<Integer> ratingSpinner = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5,
                updateMode ? bookToEdit.getRating() : 0));
        Spinner<Integer> pageCountSpinner = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100_000,
                updateMode ? bookToEdit.getPageCount() : 1));
        TextField addedAtField = new TextField(updateMode && bookToEdit.getAddedAt() != null ? bookToEdit.getAddedAt() : LocalDateTime.now().toString());
        TextField publisherIdField = new TextField(updateMode && bookToEdit.getPublisherId() != null ? String.valueOf(bookToEdit.getPublisherId()) : "");

        TextField authorFirstNameField = new TextField();
        TextField authorLastNameField = new TextField();

        if (updateMode) {
            selectFileFormat(fileFormatCombo, bookToEdit.getFileFormatId());
            selectReadingStatus(readingStatusCombo, bookToEdit.getReadingStatus());
        } else {
            fileFormatCombo.getSelectionModel().selectFirst();
            readingStatusCombo.getSelectionModel().selectFirst();
        }

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(12, 0, 0, 0));

        int row = 0;
        addEditorRow(formGrid, row++, "ID", idField);
        addEditorRow(formGrid, row++, "Titel", titleField);
        addEditorRow(formGrid, row++, "ISBN", isbnField);
        addEditorRow(formGrid, row++, "Dateipfad", createBrowseRow(filePathField, "E-Book Datei auswählen", false));
        //addEditorRow(formGrid, row++, "Cover-Pfad", createBrowseRow(coverPathField, "Cover-Datei auswählen", true));
        addEditorRow(formGrid, row++, "Dateiformat", fileFormatCombo);
        addEditorRow(formGrid, row++, "Lesestatus", readingStatusCombo);
        addEditorRow(formGrid, row++, "Bewertung", ratingSpinner);
        addEditorRow(formGrid, row++, "Seitenzahl", pageCountSpinner);
        addEditorRow(formGrid, row++, "Hinzugefügt am", addedAtField);
        addEditorRow(formGrid, row++, "Publisher ID", publisherIdField);

        if (!updateMode) {
            Label authorSection = new Label("Autor für das neue E-Book");
            authorSection.setStyle("-fx-font-weight: bold;");
            formGrid.add(authorSection, 0, row++, 2, 1);
            addEditorRow(formGrid, row++, "Vorname", authorFirstNameField);
            addEditorRow(formGrid, row++, "Nachname", authorLastNameField);
        }

        Button saveButton = new Button(updateMode ? "Änderungen speichern" : "E-Book speichern");
        Button cancelButton = new Button("Abbrechen");
        saveButton.setDefaultButton(true);
        cancelButton.setCancelButton(true);

        saveButton.setOnAction(event -> {
            try {
                EBook ebook = buildEBookFromForm(
                        updateMode ? bookToEdit.getId() : null,
                        titleField,
                        isbnField,
                        filePathField,
                        coverPathField,
                        fileFormatCombo,
                        readingStatusCombo,
                        ratingSpinner,
                        pageCountSpinner,
                        addedAtField,
                        publisherIdField
                );

                if (updateMode) {
                    eBookService.updateEBook(ebook);
                    showAlert(Alert.AlertType.INFORMATION, "Erfolg", "E-Book wurde aktualisiert.");
                    refreshBookList(ebook.getId());
                } else {
                    EBook saved = eBookService.registerNewEBook(
                            ebook,
                            authorFirstNameField.getText(),
                            authorLastNameField.getText()
                    );
                    showAlert(Alert.AlertType.INFORMATION, "Erfolg", "E-Book wurde gespeichert.");
                    refreshBookList(saved.getId());
                }

                dialogStage.close();
            } catch (IllegalArgumentException exception) {
                showAlert(Alert.AlertType.ERROR, "Validierungsfehler", resolveMessage(exception));
            } catch (Exception exception) {
                showAlert(Alert.AlertType.ERROR, "Fehler", resolveMessage(exception));
            }
        });

        cancelButton.setOnAction(event -> dialogStage.close());

        HBox buttonBar = new HBox(10, saveButton, cancelButton);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(12, 0, 0, 0));

        ScrollPane formScrollPane = new ScrollPane(formGrid);
        formScrollPane.setFitToWidth(true);
        formScrollPane.setPrefViewportHeight(420);

        VBox dialogContent = new VBox(12, title, new Separator(), formScrollPane, buttonBar);
        dialogContent.setPadding(new Insets(16));

        Scene dialogScene = new Scene(dialogContent, 720, 650);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    private void deleteSelectedEBook() {
        EBook selected = selectedEBookProperty.get();
        if (selected == null || selected.getId() == null) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.initOwner(primaryStage);
        confirm.setTitle("E-Book löschen");
        confirm.setHeaderText("E-Book wirklich löschen?");
        confirm.setContentText(selected.getTitle());

        confirm.showAndWait().ifPresent(result -> {
            if (result.getButtonData().isDefaultButton()) {
                try {
                    eBookService.deleteEBook(selected.getId());
                    showAlert(Alert.AlertType.INFORMATION, "Erfolg", "E-Book wurde gelöscht.");
                    refreshBookList(null);
                } catch (Exception exception) {
                    showAlert(Alert.AlertType.ERROR, "Fehler", resolveMessage(exception));
                }
            }
        });
    }

    private HBox createBrowseRow(TextField targetField, String chooserTitle, boolean coverChooser) {
        Button browseButton = new Button("Durchsuchen...");
        browseButton.setOnAction(event -> chooseFile(targetField, chooserTitle, coverChooser));

        HBox box = new HBox(8, targetField, browseButton);
        box.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(targetField, Priority.ALWAYS);
        targetField.setMaxWidth(Double.MAX_VALUE);
        targetField.setPrefColumnCount(28);
        return box;
    }

    private void chooseFile(TextField targetField, String chooserTitle, boolean coverChooser) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(chooserTitle);
        if (coverChooser) {
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Bilddateien", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp"),
                    new FileChooser.ExtensionFilter("Alle Dateien", "*.*")
            );
        } else {
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("E-Books (*.pdf, *.epub)", "*.pdf", "*.epub"),
                    new FileChooser.ExtensionFilter("PDF Dateien (*.pdf)", "*.pdf"),
                    new FileChooser.ExtensionFilter("EPUB Dateien (*.epub)", "*.epub"),
                    new FileChooser.ExtensionFilter("Alle Dateien", "*.*")
            );
        }

        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            targetField.setText(selectedFile.getAbsolutePath());
        }
    }

    private EBook buildEBookFromForm(
            Integer ebookId,
            TextField titleField,
            TextField isbnField,
            TextField filePathField,
            TextField coverPathField,
            ComboBox<FileFormatOption> fileFormatCombo,
            ComboBox<String> readingStatusCombo,
            Spinner<Integer> ratingSpinner,
            Spinner<Integer> pageCountSpinner,
            TextField addedAtField,
            TextField publisherIdField
    ) {
        FileFormatOption selectedFormat = fileFormatCombo.getSelectionModel().getSelectedItem();
        if (selectedFormat == null) {
            throw new IllegalArgumentException("Bitte ein Dateiformat auswählen.");
        }

        String title = titleField.getText();
        String isbn = normalizeText(isbnField.getText());
        String filePath = filePathField.getText();
        String coverPath = normalizeText(coverPathField.getText());
        String readingStatus = readingStatusCombo.getSelectionModel().getSelectedItem();
        Integer publisherId = parseOptionalInteger(publisherIdField.getText(), "Publisher ID");

        EBook ebook = ebookId == null
                ? new EBook(
                        title,
                        isbn,
                        filePath,
                        selectedFormat.id(),
                        coverPath,
                        readingStatus,
                        ratingSpinner.getValue(),
                        pageCountSpinner.getValue(),
                        publisherId)
                : new EBook(
                        ebookId,
                        title,
                        isbn,
                        filePath,
                        selectedFormat.id(),
                        coverPath,
                        readingStatus,
                        ratingSpinner.getValue(),
                        pageCountSpinner.getValue(),
                        addedAtField.getText(),
                        publisherId);

        if (addedAtField.getText() != null) {
            ebook.setAddedAt(addedAtField.getText());
        }

        return ebook;
    }

    private void refreshBookList(Integer selectedId) {
        ebookItems.setAll(eBookService.getAllEBooks());
        updateStatisticsLabel();
        if (selectedId == null) {
            bookListView.getSelectionModel().clearSelection();
            return;
        }

        ebookItems.stream()
                .filter(book -> Objects.equals(book.getId(), selectedId))
                .findFirst()
                .ifPresent(book -> bookListView.getSelectionModel().select(book));
    }

    private void updateStatisticsLabel() {
        Map<String, Long> counts = eBookService.getEBookCountByReadingStatus();

        if (counts.isEmpty()) {
            statisticsLabel.setText("Statistik: keine E-Books vorhanden.");
            return;
        }

        StringJoiner joiner = new StringJoiner(" | ", "E-Book-Statistik nach Lesestatus: ", "");
        counts.forEach((status, count) -> joiner.add(status + ": " + count));
        statisticsLabel.setText(joiner.toString());
    }

    private void addEditorRow(GridPane gridPane, int row, String labelText, Node fieldNode) {
        Label label = new Label(labelText + ":");
        label.setMinWidth(120);
        gridPane.add(label, 0, row);
        gridPane.add(fieldNode, 1, row);
    }

    private TextField createDisabledTextField(String value) {
        TextField field = new TextField(value);
        field.setDisable(true);
        return field;
    }

    private StackPane createCoverPane() {
        StackPane coverPane = new StackPane();
        coverPane.setPrefSize(84, 112);
        coverPane.setMinSize(84, 112);
        coverPane.setMaxSize(84, 112);
        coverPane.setStyle("-fx-background-color: #f4efe6; -fx-border-color: #d8cfc1; -fx-border-radius: 10; -fx-background-radius: 10;");
        return coverPane;
    }

    private void updateCoverPane(StackPane coverPane, String coverPath) {
        coverPane.getChildren().clear();

        Label placeholder = new Label("Kein Cover");
        placeholder.setStyle("-fx-text-fill: #7b7164; -fx-font-size: 12px; -fx-font-weight: bold;");

        Image image = loadImage(coverPath);
        if (image == null) {
            coverPane.getChildren().add(placeholder);
            return;
        }

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(78);
        imageView.setFitHeight(106);
        imageView.setPreserveRatio(true);
        coverPane.getChildren().add(imageView);
    }

    private Image loadImage(String coverPath) {
        if (coverPath == null || coverPath.isBlank()) {
            return null;
        }

        File file = new File(coverPath);
        if (!file.exists() || !file.isFile()) {
            return null;
        }

        Image image = new Image(file.toURI().toString(), 0, 0, true, true, true);
        return image.isError() ? null : image;
    }

    private String buildBookDetails(EBook ebook) {
        StringBuilder builder = new StringBuilder();
        builder.append("Status: ").append(ebook.getReadingStatus());
        builder.append("  |  Format: ").append(resolveFileFormatLabel(ebook.getFileFormatId()));
        builder.append("  |  Bewertung: ").append(ebook.getRating());
        builder.append("  |  Seiten: ").append(ebook.getPageCount());

        if (ebook.getIsbn() != null && !ebook.getIsbn().isBlank()) {
            builder.append("\nISBN: ").append(ebook.getIsbn());
        }
        if (ebook.getAddedAt() != null && !ebook.getAddedAt().isBlank()) {
            builder.append("\nHinzugefügt: ").append(ebook.getAddedAt());
        }
        if (ebook.getPublisherId() != null) {
            builder.append("\nPublisher ID: ").append(ebook.getPublisherId());
        }

        return builder.toString();
    }

    private String resolveFileFormatLabel(int fileFormatId) {
        return FILE_FORMAT_OPTIONS.stream()
                .filter(option -> option.id() == fileFormatId)
                .map(FileFormatOption::label)
                .findFirst()
                .orElse("Format-ID " + fileFormatId);
    }

    private void selectFileFormat(ComboBox<FileFormatOption> comboBox, int fileFormatId) {
        FILE_FORMAT_OPTIONS.stream()
                .filter(option -> option.id() == fileFormatId)
                .findFirst()
                .ifPresentOrElse(
                        option -> comboBox.getSelectionModel().select(option),
                        () -> comboBox.getSelectionModel().selectFirst()
                );
    }

    private void selectReadingStatus(ComboBox<String> comboBox, String readingStatus) {
        if (readingStatus == null || readingStatus.isBlank()) {
            comboBox.getSelectionModel().selectFirst();
            return;
        }

        comboBox.getSelectionModel().select(readingStatus);
        if (comboBox.getSelectionModel().isEmpty()) {
            comboBox.getSelectionModel().selectFirst();
        }
    }

    private Integer parseOptionalInteger(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + " muss eine Zahl sein.");
        }
    }

    private String normalizeText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.initOwner(primaryStage);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String resolveMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage() != null ? current.getMessage() : throwable.getMessage();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private record FileFormatOption(int id, String label) {
        @Override
        public String toString() {
            return label;
        }
    }

}