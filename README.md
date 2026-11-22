# LIBIS LIBRARY

A small library catalogue web app built with Java Servlets, JSP, JSTL and EL (Jakarta EE / Tomcat 11).

## Features
- Sign in with a member email and password (session-based)
- Book catalogue with availability status
- Search by title, author or genre
- Sign out
- An authentication filter protects every page except the login page

## Structure
| Layer | File |
|---|---|
| Model | `com.libis.model.Book`, `com.libis.model.Member` |
| Data (DAO-style) | `com.libis.data.LibraryData` |
| Controllers | `LoginController` (`/login`), `CatalogueController` (`/catalogue`), `LogoutController` (`/logout`) |
| Filter | `com.libis.filter.AuthenticationFilter` |
| Views | `login.jsp`, `WEB-INF/welcome.jsp` |

## Run
Requirements: JDK 26, Maven, Tomcat 11.

```
mvnw clean package
```
Deploy `target/LIBIS_LIBRARY.war` to Tomcat 11 (or use the IntelliJ Tomcat run config), then open
`http://localhost:8080/LIBIS_LIBRARY/`.

Demo login: `thabo.molefe@bac.ac.bw` / `pass123`
