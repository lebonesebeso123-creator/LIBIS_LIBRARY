<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ include file="/WEB-INF/views/fragments/i18n.jspf" %>
<c:set var="langPath" value="/librarian/members"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
    <title>LIBIS | Delete Member</title>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>
<main class="container my-4" style="max-width: 700px">
    <div class="page-card p-4">
        <h1 class="h3 mb-3 text-danger">Delete member?</h1>
        <p>
            You are about to delete <strong><c:out value="${member.memberNumber}"/>
            (<c:out value="${member.fullName}"/>)</strong>.
        </p>
        <div class="alert alert-warning">
            <strong>Warning:</strong> this member's whole borrowing history (current and returned loans) will also
            be deleted. Any book they still hold will become available again. This cannot be undone.
        </div>
        <c:if test="${isSelf}">
            <div class="alert alert-info">This is the account you are signed in with. LIBIS will refuse to delete it.</div>
        </c:if>
        <form method="post" action="${ctx}/librarian/members/delete" class="d-flex gap-2">
            <input type="hidden" name="id" value="${member.id}">
            <button type="submit" class="btn btn-danger">Yes, delete member and history</button>
            <a class="btn btn-outline-secondary" href="${ctx}/librarian/members">Cancel</a>
        </form>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
