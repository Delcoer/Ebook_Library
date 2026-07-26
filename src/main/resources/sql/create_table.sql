

/* Fiist Creation
-- Stammdaten Tabellen anlegen

CREATE TABLE Autor (id INTEGER PRIMARY KEY AUTOINCREMENT, vorname TEXT, nachname TEXT NOT NULL);
CREATE TABLE Verlag (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE);
CREATE TABLE Tag (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE);
CREATE TABLE Dateiformat (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE);
CREATE TABLE Sammlung (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE);

-- Alle Detailtabellen erstellen

CREATE TABLE EBook (
    id INTEGER PRIMARY KEY AUTOINCREMENT, titel TEXT NOT NULL, isbn TEXT UNIQUE, datei_pfad TEXT NOT NULL UNIQUE,
    dateiformat_id INTEGER NOT NULL, cover_pfad TEXT, lesestatus TEXT NOT NULL, bewertung INTEGER,
    seiten_anzahl INTEGER, hinzugefuegt_am TEXT NOT NULL, verlag_id INTEGER,
    FOREIGN KEY (dateiformat_id) REFERENCES Dateiformat(id), FOREIGN KEY (verlag_id) REFERENCES Verlag(id)
);

CREATE TABLE Markierung (
    id INTEGER PRIMARY KEY AUTOINCREMENT, ebook_id INTEGER NOT NULL, seite INTEGER, markierter_text TEXT NOT NULL,
    notiz_text TEXT, farbe TEXT, erstellt_am TEXT NOT NULL, FOREIGN KEY (ebook_id) REFERENCES EBook(id) ON DELETE CASCADE
);

CREATE TABLE Lesezeichen (
    id INTEGER PRIMARY KEY AUTOINCREMENT, ebook_id INTEGER NOT NULL, seite INTEGER NOT NULL, titel TEXT,
    erstellt_am TEXT NOT NULL, FOREIGN KEY (ebook_id) REFERENCES EBook(id) ON DELETE CASCADE
);

CREATE TABLE Notiz (
    id INTEGER PRIMARY KEY AUTOINCREMENT, ebook_id INTEGER NOT NULL, seite INTEGER, notiz_text TEXT NOT NULL,
    erstellt_am TEXT NOT NULL, FOREIGN KEY (ebook_id) REFERENCES EBook(id) ON DELETE CASCADE
);

CREATE TABLE EBook_Sammlung (
    ebook_id INTEGER NOT NULL, sammlung_id INTEGER NOT NULL, PRIMARY KEY (ebook_id, sammlung_id),
    FOREIGN KEY (ebook_id) REFERENCES EBook(id) ON DELETE CASCADE, FOREIGN KEY (sammlung_id) REFERENCES Sammlung(id) ON DELETE CASCADE
);

CREATE TABLE EBook_Autor (
    ebook_id INTEGER NOT NULL, autor_id INTEGER NOT NULL, PRIMARY KEY (ebook_id, autor_id),
    FOREIGN KEY (ebook_id) REFERENCES EBook(id) ON DELETE CASCADE, FOREIGN KEY (autor_id) REFERENCES Autor(id) ON DELETE CASCADE
);

CREATE TABLE EBook_Tag (
    ebook_id INTEGER NOT NULL, tag_id INTEGER NOT NULL, PRIMARY KEY (ebook_id, tag_id),
    FOREIGN KEY (ebook_id) REFERENCES EBook(id) ON DELETE CASCADE, FOREIGN KEY (tag_id) REFERENCES Tag(id) ON DELETE CASCADE
);
*/


-- Optimized starting Skript for DBMS

-- Fremdschlüssel-Unterstützung in SQLite aktivieren
PRAGMA foreign_keys = ON;

-- =====================================================
-- 1. STAMMDATEN-TABELLEN (Unabhängige Entitäten)
-- =====================================================

CREATE TABLE Author (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name TEXT,
    last_name TEXT NOT NULL
);

CREATE TABLE Publisher (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE FileFormat (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE Tag (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE Collection (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE
);

-- =====================================================
-- 2. HAUPT-TABELLE
-- =====================================================

CREATE TABLE EBook (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    isbn TEXT UNIQUE,
    file_path TEXT NOT NULL UNIQUE,
    file_format_id INTEGER NOT NULL,
    cover_path TEXT,
    reading_status TEXT NOT NULL DEFAULT 'NOT_STARTED',
    rating INTEGER,
    page_count INTEGER,
    added_at TEXT NOT NULL,
    publisher_id INTEGER,
    FOREIGN KEY (file_format_id) REFERENCES FileFormat(id),
    FOREIGN KEY (publisher_id) REFERENCES Publisher(id) ON DELETE SET NULL
);

-- =====================================================
-- 3. ABHÄNGIGE 1:N ENTITÄTEN
-- =====================================================

CREATE TABLE Note (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ebook_id INTEGER NOT NULL,
    page INTEGER,
    note_text TEXT NOT NULL,
    created_at TEXT NOT NULL,
    FOREIGN KEY (ebook_id) REFERENCES EBook(id) ON DELETE CASCADE
);

CREATE TABLE Bookmark (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ebook_id INTEGER NOT NULL,
    page INTEGER NOT NULL,
    title TEXT,
    created_at TEXT NOT NULL,
    FOREIGN KEY (ebook_id) REFERENCES EBook(id) ON DELETE CASCADE
);

CREATE TABLE Highlight (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    ebook_id INTEGER NOT NULL,
    page INTEGER,
    highlighted_text TEXT NOT NULL,
    note_text TEXT,
    color TEXT,
    created_at TEXT NOT NULL,
    FOREIGN KEY (ebook_id) REFERENCES EBook(id) ON DELETE CASCADE
);

-- =====================================================
-- 4. ZWISCHENTABELLEN (N:M Beziehungen)
-- =====================================================

CREATE TABLE EBook_Author (
    ebook_id INTEGER NOT NULL,
    author_id INTEGER NOT NULL,
    PRIMARY KEY (ebook_id, author_id),
    FOREIGN KEY (ebook_id) REFERENCES EBook(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES Author(id) ON DELETE CASCADE
);

CREATE TABLE EBook_Tag (
    ebook_id INTEGER NOT NULL,
    tag_id INTEGER NOT NULL,
    PRIMARY KEY (ebook_id, tag_id),
    FOREIGN KEY (ebook_id) REFERENCES EBook(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES Tag(id) ON DELETE CASCADE
);

CREATE TABLE EBook_Collection (
    ebook_id INTEGER NOT NULL,
    collection_id INTEGER NOT NULL,
    PRIMARY KEY (ebook_id, collection_id),
    FOREIGN KEY (ebook_id) REFERENCES EBook(id) ON DELETE CASCADE,
    FOREIGN KEY (collection_id) REFERENCES Collection(id) ON DELETE CASCADE
);
