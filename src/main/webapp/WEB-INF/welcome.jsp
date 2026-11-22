<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LIBIS Catalogue</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
            background: #f7fafc;
            color: #2d3748;
            line-height: 1.5;
        }
        header {
            background: #1a365d;
            color: white;
            padding: 18px 40px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        header h1 { font-size: 1.5rem; letter-spacing: 1px; }
        .user-info { font-size: 0.95rem; }
        main { max-width: 1100px; margin: 30px auto; padding: 0 20px; }
        h2 { margin-bottom: 8px; color: #1a365d; }
        .subtitle { color: #718096; margin-bottom: 24px; }
        table {
            width: 100%;
            border-collapse: collapse;
            background: white;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
        }
        th {
            background: #2c5282;
            color: white;
            text-align: left;
            padding: 14px 16px;
            font-weight: 600;
            font-size: 0.9rem;
        }
        td {
            padding: 13px 16px;
            border-bottom: 1px solid #e2e8f0;
            font-size: 0.95rem;
        }
        tr:last-child td { border-bottom: none; }
        tr:hover { background: #f7fafc; }
        .available { color: #276749; font-weight: 600; }
        .unavailable { color: #c53030; font-weight: 600; }
        .actions a {
            display: inline-block;
            margin-right: 10px;
            font-size: 0.85rem;
            color: #2b6cb0;
            text-decoration: none;
            font-weight: 500;
        }
        .actions a:hover { text-decoration: underline; }
        .logout {
            color: #bee3f8;
            text-decoration: none;
            font-size: 0.9rem;
        }
        .logout:hover { color: white; }
        .search { display: flex; gap: 10px; margin-bottom: 10px; align-items: center; }
        .search input {
            flex: 1; padding: 11px 13px; border: 1px solid #cbd5e0;
            border-radius: 8px; font-size: 0.95rem;
        }
        .search input:focus { outline: none; border-color: #2b6cb0; }
        .search button {
            padding: 11px 20px; background: #2c5282; color: white; border: none;
            border-radius: 8px; font-weight: 600; cursor: pointer;
        }
        .search button:hover { background: #1a365d; }
        .search .clear { color: #2b6cb0; text-decoration: none; font-size: 0.9rem; }
        .count { color: #718096; font-size: 0.85rem; margin-bottom: 14px; }
        .empty { text-align: center; color: #718096; padding: 28px; }
    </style>
</head>
<body>
<header>
    <h1>LIBIS</h1>
    <div class="user-info">
        Signed in as <strong>${sessionScope.loggedInMember.fullName}</strong>
        &nbsp;|&nbsp;
        <a class="logout" href="${pageContext.request.contextPath}/logout">Sign out</a>
    </div>
</header>

<main>
    <h2>Book Catalogue</h2>
    <p class="subtitle">Welcome, ${sessionScope.loggedInMember.firstName}. Here are all titles currently held by the library.</p>

    <form class="search" method="get" action="${pageContext.request.contextPath}/catalogue">
        <input type="text" name="q" placeholder="Search by title, author or genre" value="<c:out value='${query}'/>">
        <button type="submit">Search</button>
        <c:if test="${not empty query}">
            <a class="clear" href="${pageContext.request.contextPath}/catalogue">Clear</a>
        </c:if>
    </form>
    <p class="count">Showing ${books.size()} of ${totalBooks} books<c:if test="${not empty query}"> for &ldquo;<c:out value="${query}"/>&rdquo;</c:if></p>

    <table>
        <thead>
        <tr>
            <th>Title</th>
            <th>Author</th>
            <th>Genre</th>
            <th>Availability</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="book" items="${books}">
            <tr>
                <td><c:out value="${book.title}"/></td>
                <td><c:out value="${book.author}"/></td>
                <td><c:out value="${book.genre}"/></td>
                <td>
                    <c:choose>
                        <c:when test="${book.available}">
                            <span class="available">Available</span>
                        </c:when>
                        <c:otherwise>
                            <span class="unavailable">On Loan</span>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td class="actions">
                    <a href="#">Borrow</a>
                    <a href="#">Reserve</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty books}">
            <tr><td colspan="5" class="empty">No books match your search.</td></tr>
        </c:if>
        </tbody>
    </table>
</main>
</body>
</html>