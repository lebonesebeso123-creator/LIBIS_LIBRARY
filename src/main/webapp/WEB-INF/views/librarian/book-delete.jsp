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
    <title>LIBIS | Delete Book</title>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>
<main class="container my-4" style="max-width: 700px">
    <div class="page-card p-4">
        <h1 class="h3 mb-3 text-danger">Delete book?</h1>
        <p>
            You are about to delete <strong><c:out value="${book.title}"/></strong>
            by <c:out value="${book.author}"/> (ISBN <c:out value="${book.isbn}"/>).
        </p>
        <c:choose>
            <c:when test="${book.available}">
                <div class="alert alert-warning">Its past loan records will also be deleted. This cannot be undone.</div>
            </c:when>
            <c:otherwise>
                <div class="alert alert-info">This book is currently on loan, so LIBIS will refuse to delete it.</div>
            </c:otherwise>
        </c:choose>
        <form method="post" action="${ctx}/librarian/books/delete" class="d-flex gap-2">
            <input type="hidden" name="id" value="${book.id}">
            <button type="submit" class="btn btn-danger">Yes, delete book</button>
            <a class="btn btn-outline-secondary" href="${ctx}/librarian/books">Cancel</a>
        </form>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
