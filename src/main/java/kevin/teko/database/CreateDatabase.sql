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

-- =====================================================
-- 5. SEED DATEN (Initiale Stammdaten)
-- =====================================================

-- Dateiformate (PFLICHTFELD für EBook! ID 1 = PDF, ID 2 = EPUB)
INSERT OR IGNORE INTO FileFormat (id, name) VALUES (1, 'PDF');
INSERT OR IGNORE INTO FileFormat (id, name) VALUES (2, 'EPUB');
INSERT OR IGNORE INTO FileFormat (id, name) VALUES (3, 'MOBI');
INSERT OR IGNORE INTO FileFormat (id, name) VALUES (4, 'AZW3');

-- Verlage / Publisher
INSERT OR IGNORE INTO Publisher (id, name) VALUES (1, 'Eigenverlag / Unbekannt');
INSERT OR IGNORE INTO Publisher (id, name) VALUES (2, 'Rheinwerk Verlag');
INSERT OR IGNORE INTO Publisher (id, name) VALUES (3, 'O''Reilly Media');

-- Tags / Kategorien
INSERT OR IGNORE INTO Tag (id, name) VALUES (1, 'Programmierung');
INSERT OR IGNORE INTO Tag (id, name) VALUES (2, 'Java');
INSERT OR IGNORE INTO Tag (id, name) VALUES (3, 'Datenbanken');

-- Sammlungen / Collections
INSERT OR IGNORE INTO Collection (id, name) VALUES (1, 'Favoriten');
INSERT OR IGNORE INTO Collection (id, name) VALUES (2, 'Fachliteratur');

-- Beispiel-Autoren
INSERT OR IGNORE INTO Author (id, first_name, last_name) VALUES (1, 'Joshua', 'Bloch');
INSERT OR IGNORE INTO Author (id, first_name, last_name) VALUES (2, 'Robert C.', 'Martin');