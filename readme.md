# Projektarbeit – Datenbanken (TEKO Semester 4)

Ein JavaFX-basiertes Desktop-Anwendungsprojekt zur Verwaltung und Interaktion mit einer SQLite-Datenbank, entwickelt im Rahmen des 4. Semesters an der TEKO.

---

## Tech Stack & Voraussetzungen

* **Programmiersprache:** Java 21
* **Framework:** JavaFX 21 (UI)
* **Datenbank:** SQLite (`sqlite-jdbc`)
* **Build-Tool:** Apache Maven
* **Entwicklungsumgebung:** VS Code / Fedora Linux

---

## Projekt starten & Technische Hintergründe

Da JavaFX ein modulares Framework ist, schreibt es strenge Sicherheits- und Modulgrenzen (JPMS) vor. Eine Standard-Java-Anwendung sucht Bibliotheken im normalen Klassenpfad, was bei JavaFX zu dem bekannten Fehler führt, dass Laufzeitkomponenten fehlen. JavaFX benötigt zwingend einen **Modulpfad** (`--module-path`) und die Angabe der zu ladenden Module (`--add-modules javafx.controls,javafx.fxml`).

Das `javafx-maven-plugin` automatisiert diesen Prozess komplett, indem es die Abhängigkeiten aus dem lokalen Maven-Repository (`~/.m2/repository`) ausliest und der JVM im Hintergrund mit den korrekten Parametern übergibt.

### Option 1: Über das Maven-Panel in VS Code (Empfohlen)
1. Öffne das **Maven-Panel** (das Würfel-Symbol mit dem "M") in der linken Seitenleiste von VS Code.
2. Navigiere zu deinem Projekt `application_teko_database` -> **Plugins** -> **javafx**.
3. Mache einen **Rechtsklick** auf `javafx:run` und wähle **Run**.

### Option 2: Über das Terminal
Alternativ kannst du das Projekt direkt über die Konsole starten: IM RICHTIGEN ORDNER BEFINDEN (Wo pom.xml drin ist)!
```bash
mvn clean javafx:run 