# Projektarbeit – Datenbanken (TEKO Semester 4)

Ein JavaFX-basiertes Desktop-Anwendungsprojekt zur Verwaltung und Interaktion mit einer SQLite-Datenbank, entwickelt im Rahmen des 4. Semesters an der TEKO.

---

## Tech Stack & Voraussetzungen

* **Programmiersprache:** Java 21
* **Framework:** JavaFX 21 (UI)
* **Datenbank:** SQLite (`sqlite-jdbc`)
* **Build-Tool:** Apache Maven
* **Entwicklungsumgebung:** VS Code / Fedora Linux
* **Extension:** Extension Pack for Java

---

## Java Development Kit (JDK) & Maven

Da die Applikation nicht kompiliert ist, wird das JDK benötigt: [JDK 21 – Eclipse Temurin herunterladen](https://adoptium.net/temurin/releases/?version=21)

Für Apache Maven gibt es **zwei gleichwertige Alternativen** – du brauchst nur eine davon:

* **Alternative A – Maven selbst installieren:** [Maven herunterladen & installieren](https://maven.apache.org/install.html). Damit funktionieren sowohl Option 1 (VS Code Maven-Panel) als auch Option 2 (Terminal-Befehl).
* **Alternative B – VS Code Extension nutzen:** Die im "Extension Pack for Java" enthaltene "Maven for Java"-Extension bringt bei Bedarf eine eigene, gebündelte Maven-Version mit, falls keine auf dem System gefunden wird. Damit ist keine separate Installation nötig – **dies funktioniert aber nur mit Option
1 (VS Code Maven-Panel), nicht mit dem Terminal-Befehl aus Option 2.**

---

## Anpassen des statischen Datenbank-Pfads (WICHTIG)

Im `DatabaseManager` muss die Konstante für den Datenbank-Pfad angepasst werden. Ersetze den Pfad durch einen beliebigen Speicherort auf deinem eigenen System – die Datei muss nicht existieren, sie wird beim ersten Start automatisch erstellt.

```java
private static final String DB_FILE_PATH = "/home/kev/Documents/Teko/Modul_Datenbanken/Datenbanken_Project_Code/Projektarbeit__Code/ebook_library.db";
```

**Windows-Beispiel:** `C:/Users/DEINNAME/ebook_library.db` (normale Schrägstriche funktionieren in Java auch unter Windows)

---

## Projekt starten & Technische Hintergründe

Da JavaFX ein modulares Framework ist, schreibt es strenge Sicherheits- und Modulgrenzen (JPMS) vor. Eine Standard-Java-Anwendung sucht Bibliotheken im normalen Klassenpfad, was bei JavaFX zu dem bekannten Fehler führt, dass Laufzeitkomponenten fehlen. JavaFX benötigt zwingend einen **Modulpfad** (`--module-path`) und die Angabe der zu ladenden Module (`--add-modules javafx.controls,javafx.fxml`).

Das `javafx-maven-plugin` automatisiert diesen Prozess komplett, indem es die Abhängigkeiten aus dem lokalen Maven-Repository (`~/.m2/repository`) ausliest und der JVM im Hintergrund mit den korrekten Parametern übergibt.

### Option 1: Über das Maven-Panel in VS Code (Empfohlen)
1. Öffne das **Maven-Panel** (das Würfel-Symbol mit dem "M") in der linken Seitenleiste von VS Code:
   
<img width="396" height="187" alt="grafik" src="https://github.com/user-attachments/assets/45dd8279-6e0f-4e5e-8972-57ceaf0e7b3b" />
   
2. Navigiere zum Projekt `application_teko_database` -> **Plugins** -> **javafx**:
   
3. Mache einen **Rechtsklick** auf `javafx:run` und wähle **Run**.

<img width="503" height="270" alt="grafik" src="https://github.com/user-attachments/assets/ca6ecaac-1fba-4237-88d3-c0479af13b1b" />






### Option 2: Über das Terminal
Alternativ kannst du das Projekt direkt über die Konsole starten: IM RICHTIGEN PFAD BEFINDEN (Wo pom.xml drin ist)!
```bash
mvn clean javafx:run 
