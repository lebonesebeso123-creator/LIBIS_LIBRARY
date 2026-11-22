# LIBIS LIBRARY: CA3 (Database, Security and a Second Language)

CSE303 Advanced Web Development with Java. Model 2 (MVC) library system on Jakarta Servlet / JSP + JSTL/EL,
JDBC DAOs on MySQL, Jakarta Security FORM login (DataSourceRealm) and English / Setswana bundles.

## 1. Set up and run

1. **Database.** Open `libis.sql` in MySQL Workbench as `root` and run it (lightning icon), or
   `mysql -u root -p < libis.sql`. It drops and recreates the `libis` schema, seeds the data and creates the
   application account `libis_app` / `LibisApp#2026` used by `META-INF/context.xml`. Re-run it any time to reset.
2. **Tomcat 11** (Servlet 6.1). In IntelliJ: *Run > Edit Configurations > + > Tomcat Server > Local*,
   Deployment tab: add artifact `LIBIS_LIBRARY:war exploded`, and set **Application context** to `/LIBIS_LIBRARY`.
3. Run, then open `http://localhost:8080/LIBIS_LIBRARY/`. You will get the FORM login page.

Troubleshooting:
- *Cannot load JDBC driver class 'com.mysql.cj.jdbc.Driver'*: copy `mysql-connector-j-9.7.0.jar`
  (from `%USERPROFILE%\.m2\repository\com\mysql\mysql-connector-j\9.7.0\`) into Tomcat's `lib` folder and restart.
- *Access denied for user 'libis_app'*: re-run `libis.sql` as root, or put your own MySQL user/password in
  `src/main/webapp/META-INF/context.xml`.
- Bootstrap is loaded from the jsDelivr CDN, so the PC needs internet for the styling.

## 2. Test logins

| Role | Username | Password | Member no. | Notes |
|---|---|---|---|---|
| librarian | `librarian` | `admin123` | LIB-100 | Lesego Ramotswa |
| member | `thabo` | `pass123` | LIB-001 | holds **3 open loans** (loan-limit demo) |
| member | `kea` | `secure45` | LIB-002 | 1 open + 2 returned loans (**cascading-delete demo**) |
| member | `mpho` | `library1` | LIB-003 | 1 open + 1 returned |
| member | `naledi` | `read2024` | LIB-004 | returned loans only |
| member | `tshepo` | `books99` | LIB-005 | returned loans only |

Seeded open loans: *Things Fall Apart, Born a Crime, Sapiens* (Thabo), *Nervous Conditions* (Kea),
*Half of a Yellow Sun* (Mpho). All other books are available.

## 3. Changes to the model classes (for the evidence document)

- **Member**: now `id, memberNumber, fullName, email, username, password, role`. `getFirstName()` /
  `getLastName()` are kept and derived from `fullName`, because the schema stores one `full_name` column.
  Added `isLibrarian()`.
- **Book**: added `isbn` and `publishedYear`. `available` is still a property, but it is filled by `BookDao`
  from the loans table (it is not a database column).
- **New**: `Loan` (loan row plus book title/author for display) and the `Role` enum (`member`, `librarian`).
- **Removed**: `AuthenticationFilter` (replaced by web.xml security), `LoginController` (the container now
  handles `j_security_check`), `LibraryData` (replaced by MySQL and the DAOs), and `index.jsp` (only `login.jsp`
  and `login-error.jsp` may sit outside WEB-INF; `HomeController` now handles the context root).

## 4. Where each requirement is

| Requirement | Where |
|---|---|
| Schema, PK/UNIQUE/FK + `ON DELETE CASCADE`, seed | `libis.sql` |
| Container DataSource + JNDI helper | `META-INF/context.xml`, `config/DataSourceProvider` |
| DAOs (interface + impl, PreparedStatement, try-with-resources) | `dao/*Dao`, `dao/*DaoImpl` |
| Validation and duplicate checks | `service/MemberService`, `service/BookService` |
| Borrowing rules, `MAX_LOANS = 3` | `service/LoanService`, `exception/LoanException` |
| FORM login, 2 roles, constraints, 403 | `WEB-INF/web.xml`, `login.jsp`, `login-error.jsp`, Realm in `context.xml` |
| Signed-in user from the container | `ControllerSupport.currentMember()` uses `request.getRemoteUser()` |
| Post/Redirect/Get, views under WEB-INF | all controllers in `controller/` |
| i18n (fmt tags, session-scoped language) | `resources/messages*.properties`, `views/fragments/i18n.jspf` |
| No scriptlets (enforced) | `web.xml` `<scripting-invalid>true</scripting-invalid>` |
| Friendly errors, no stack traces | `exception/DataAccessException` + `<error-page>` entries |
| Back button after logout | `filter/NoCacheFilter` (no-store headers) |

## 5. URL map

| URL | Who | What |
|---|---|---|
| `/catalogue?q=&lang=` | member, librarian | catalogue + search |
| `/loans` | member | My Loans (current + history) |
| `POST /loans/borrow`, `POST /loans/return` | member | borrow / return (`bookId`) |
| `/librarian/members[/new, /edit?id=, /delete?id=]` | librarian | member management |
| `/librarian/books[/new, /edit?id=, /delete?id=]` | librarian | book management |
| `/logout` | anyone | ends the session, back to login |
