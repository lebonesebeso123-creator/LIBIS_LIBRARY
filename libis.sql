-- =====================================================================
--  LIBIS - Library Information System
--  CSE303 Continuous Assessment 3: schema and seed data
--
--  Re-runnable: drops and recreates the whole `libis` schema.
--  Run it as root (or another admin user) in MySQL Workbench:
--      File > Open SQL Script > libis.sql > Execute (lightning icon)
--  or from a terminal:
--      mysql -u root -p < libis.sql
-- =====================================================================

SET NAMES utf8mb4;

DROP SCHEMA IF EXISTS libis;
CREATE SCHEMA libis
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;
USE libis;

-- ---------------------------------------------------------------------
--  members: library members AND librarians (role column)
--  The Tomcat DataSourceRealm reads username / password / role from here.
-- ---------------------------------------------------------------------
CREATE TABLE members (
    member_id     INT           NOT NULL AUTO_INCREMENT,
    member_number VARCHAR(20)   NOT NULL,
    full_name     VARCHAR(100)  NOT NULL,
    email         VARCHAR(120)  NOT NULL,
    username      VARCHAR(50)   NOT NULL,
    password      VARCHAR(255)  NOT NULL,
    role          VARCHAR(20)   NOT NULL DEFAULT 'member',
    CONSTRAINT pk_members              PRIMARY KEY (member_id),
    CONSTRAINT uq_members_member_number UNIQUE (member_number),
    CONSTRAINT uq_members_email         UNIQUE (email),
    CONSTRAINT uq_members_username      UNIQUE (username),
    CONSTRAINT ck_members_role          CHECK (role IN ('member', 'librarian'))
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
--  books: NO "available" column. Availability is worked out from loans
--  (a book is on loan when it has a loan row with returned_on IS NULL).
-- ---------------------------------------------------------------------
CREATE TABLE books (
    book_id        INT           NOT NULL AUTO_INCREMENT,
    isbn           VARCHAR(20)   NOT NULL,
    title          VARCHAR(200)  NOT NULL,
    author         VARCHAR(150)  NOT NULL,
    genre          VARCHAR(50)   NULL,
    description    VARCHAR(500)  NULL,
    published_year INT           NULL,
    CONSTRAINT pk_books      PRIMARY KEY (book_id),
    CONSTRAINT uq_books_isbn UNIQUE (isbn)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
--  loans: resolves the many-to-many between members and books.
--  ON DELETE CASCADE on both foreign keys:
--   * deleting a member deletes their whole borrowing history, and any
--     book they still held becomes available (its open loan row is gone)
--   * deleting a book deletes its past loan rows
-- ---------------------------------------------------------------------
CREATE TABLE loans (
    loan_id     INT   NOT NULL AUTO_INCREMENT,
    member_id   INT   NOT NULL,
    book_id     INT   NOT NULL,
    borrowed_on DATE  NOT NULL,
    returned_on DATE  NULL,          -- NULL while the book is still out
    CONSTRAINT pk_loans PRIMARY KEY (loan_id),
    CONSTRAINT fk_loans_member FOREIGN KEY (member_id)
        REFERENCES members (member_id) ON DELETE CASCADE,
    CONSTRAINT fk_loans_book   FOREIGN KEY (book_id)
        REFERENCES books (book_id)     ON DELETE CASCADE,
    CONSTRAINT ck_loans_dates  CHECK (returned_on IS NULL OR returned_on >= borrowed_on)
) ENGINE = InnoDB;

CREATE INDEX ix_loans_member_open ON loans (member_id, returned_on);
CREATE INDEX ix_loans_book_open   ON loans (book_id, returned_on);

-- ---------------------------------------------------------------------
--  Seed: the 5 members from the CA1/CA2 ArrayList + 1 librarian
--  (plain-text passwords are allowed for this assessment)
-- ---------------------------------------------------------------------
INSERT INTO members (member_id, member_number, full_name, email, username, password, role) VALUES
 (1, 'LIB-001', 'Thabo Molefe',      'thabo.molefe@bac.ac.bw',   'thabo',     'pass123',  'member'),
 (2, 'LIB-002', 'Keabetswe Dlamini', 'kea.dlamini@bac.ac.bw',    'kea',       'secure45', 'member'),
 (3, 'LIB-003', 'Mpho Kgosi',        'mpho.kgosi@bac.ac.bw',     'mpho',      'library1', 'member'),
 (4, 'LIB-004', 'Naledi Sebego',     'naledi.sebego@bac.ac.bw',  'naledi',    'read2024', 'member'),
 (5, 'LIB-005', 'Tshepo Motsumi',    'tshepo.motsumi@bac.ac.bw', 'tshepo',    'books99',  'member'),
 (6, 'LIB-100', 'Lesego Ramotswa',   'lesego.ramotswa@bac.ac.bw','librarian', 'admin123', 'librarian');

-- ---------------------------------------------------------------------
--  Seed: the 10 books from the CA1/CA2 ArrayList
-- ---------------------------------------------------------------------
INSERT INTO books (book_id, isbn, title, author, genre, description, published_year) VALUES
 (1,  '9780385474542', 'Things Fall Apart', 'Chinua Achebe', 'Fiction',
      'A classic novel about pre-colonial life in Nigeria and the arrival of Europeans.', 1958),
 (2,  '9780435905408', 'The Beautiful Ones Are Not Yet Born', 'Ayi Kwei Armah', 'Fiction',
      'A powerful story of corruption and disillusionment in post-independence Ghana.', 1968),
 (3,  '9780954702335', 'Nervous Conditions', 'Tsitsi Dangarembga', 'Fiction',
      'A coming-of-age story set in colonial Rhodesia exploring gender and race.', 1988),
 (4,  '9780399588174', 'Born a Crime', 'Trevor Noah', 'Memoir',
      'Trevor Noah''s autobiographical account of growing up in South Africa under apartheid.', 2016),
 (5,  '9780062315007', 'The Alchemist', 'Paulo Coelho', 'Fiction',
      'A philosophical novel about a shepherd''s journey to fulfil his personal legend.', 1988),
 (6,  '9780062316097', 'Sapiens', 'Yuval Noah Harari', 'Non-Fiction',
      'A brief history of humankind from the Stone Age to the modern age.', 2011),
 (7,  '9781400095209', 'Half of a Yellow Sun', 'Chimamanda Ngozi Adichie', 'Fiction',
      'A novel set during the Nigerian Civil War, exploring love, loyalty and loss.', 2006),
 (8,  '9780735211292', 'Atomic Habits', 'James Clear', 'Self-Help',
      'Practical strategies for forming good habits and breaking bad ones.', 2018),
 (9,  '9780435905484', 'The River Between', 'Ngũgĩ wa Thiong''o', 'Fiction',
      'A story of two rival villages in Kenya and the impact of colonialism.', 1965),
 (10, '9780316548182', 'Long Walk to Freedom', 'Nelson Mandela', 'Autobiography',
      'The autobiography of Nelson Mandela, detailing his fight against apartheid.', 1994);

-- ---------------------------------------------------------------------
--  Seed: 10 loans
--   * Thabo (LIB-001) holds 3 OPEN loans          -> demonstrates the 3-book limit
--   * Keabetswe (LIB-002) has 1 open + 2 returned -> demonstrates the cascading delete
--   * Mpho (LIB-003) has 1 open + 1 returned
--   * Naledi and Tshepo have returned loans only (history)
--  Open loans: books 1, 3, 4, 6, 7 -> shown as "On Loan" in the catalogue
-- ---------------------------------------------------------------------
INSERT INTO loans (member_id, book_id, borrowed_on, returned_on) VALUES
 (1, 1,  '2026-11-02', NULL),
 (1, 4,  '2026-11-05', NULL),
 (1, 6,  '2026-11-10', NULL),
 (2, 3,  '2026-11-12', NULL),
 (2, 5,  '2026-09-01', '2026-09-15'),
 (2, 8,  '2026-10-03', '2026-10-20'),
 (3, 7,  '2026-11-08', NULL),
 (3, 2,  '2026-08-11', '2026-08-30'),
 (4, 10, '2026-07-14', '2026-08-01'),
 (5, 9,  '2026-10-21', '2026-11-04');

-- ---------------------------------------------------------------------
--  Application database account used by Tomcat's DataSource
--  (matches META-INF/context.xml). Change the password in BOTH places
--  if you like.
-- ---------------------------------------------------------------------
CREATE USER IF NOT EXISTS 'libis_app'@'localhost' IDENTIFIED BY 'LibisApp#2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON libis.* TO 'libis_app'@'localhost';
FLUSH PRIVILEGES;

-- Quick check
SELECT b.book_id, b.title,
       CASE WHEN EXISTS (SELECT 1 FROM loans l
                         WHERE l.book_id = b.book_id AND l.returned_on IS NULL)
            THEN 'On Loan' ELSE 'Available' END AS availability
FROM books b ORDER BY b.book_id;
