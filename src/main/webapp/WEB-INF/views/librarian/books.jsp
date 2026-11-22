<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ include file="/WEB-INF/views/fragments/i18n.jspf" %>
<c:set var="langPath" value="/librarian/books"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
    <title>LIBIS | Manage Books</title>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>
<main class="container my-4">
    <div class="page-card p-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h1 class="h3 m-0">Books</h1>
            <a class="btn btn-success" href="${ctx}/librarian/books/new">+ Add book</a>
        </div>
        <%@ include file="/WEB-INF/views/fragments/flash.jspf" %>

        <form class="row g-2 mb-3" method="get" action="${ctx}/librarian/books">
            <div class="col-md-8">
                <input type="text" name="q" class="form-control" placeholder="Search by title or author"
                       value="${fn:escapeXml(query)}">
            </div>
            <div class="col-md-4 d-flex gap-2">
                <button class="btn btn-primary" type="submit">Search</button>
                <c:if test="${not empty query}"><a class="btn btn-outline-secondary" href="${ctx}/librarian/books">Clear</a></c:if>
            </div>
        </form>

        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead>
                <tr><th>ISBN</th><th>Title</th><th>Author</th><th>Genre</th><th>Year</th><th>Availability</th><th>Actions</th></tr>
                </thead>
                <tbody>
                <c:forEach var="b" items="${books}">
                    <tr>
                        <td class="text-nowrap"><c:out value="${b.isbn}"/></td>
                        <td><c:out value="${b.title}"/></td>
                        <td><c:out value="${b.author}"/></td>
                        <td><c:out value="${b.genre}"/></td>
                        <td><c:out value="${b.publishedYear}"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${b.available}"><span class="badge badge-available">Available</span></c:when>
                                <c:otherwise><span class="badge badge-onloan">On Loan</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-nowrap">
                            <c:url var="editUrl" value="/librarian/books/edit"><c:param name="id" value="${b.id}"/></c:url>
                            <c:url var="delUrl" value="/librarian/books/delete"><c:param name="id" value="${b.id}"/></c:url>
                            <a class="btn btn-sm btn-outline-primary" href="${editUrl}">Edit</a>
                            <a class="btn btn-sm btn-outline-danger" href="${delUrl}">Delete</a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty books}">
                    <tr><td colspan="7" class="text-center text-muted py-4">No books match your search.</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
