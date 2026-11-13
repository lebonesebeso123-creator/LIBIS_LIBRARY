<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page import="com.libis.data.LibraryData" %>
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
    </style>
</head>
<body>
<c:if test="${empty sessionScope.loggedInMember}">
    <c:redirect url="login.jsp"/>
</c:if>

<header>
    <h1>LIBIS</h1>
    <div class="user-info">
        Signed in as <strong>${sessionScope.loggedInMember.fullName}</strong>
        &nbsp;|&nbsp;
        <a class="logout" href="${pageContext.request.contextPath}/login.jsp">Sign out</a>
    </div>
</header>

<main>
    <h2>Book Catalogue</h2>
    <p class="subtitle">Welcome, ${sessionScope.loggedInMember.firstName}. Here are all titles currently held by the library.</p>

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
        <c:forEach var="book" items="<%= LibraryData.getBooks() %>">
            <tr>
                <td>${book.title}</td>
                <td>${book.author}</td>
                <td>${book.genre}</td>
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
                    <a href="#">Search</a>
                    <a href="#">Borrow</a>
                    <a href="#">Reserve</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</main>
</body>
</html>