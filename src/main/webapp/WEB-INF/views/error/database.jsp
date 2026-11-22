<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
    <title>LIBIS | Database unavailable</title>
</head>
<body>
<main class="container my-5" style="max-width: 640px">
    <div class="page-card p-5 text-center">
        <h1 class="h3 mb-3">Database unavailable</h1>
        <p class="text-muted">LIBIS could not reach its database just now. Please try again in a moment, or ask the librarian if the problem continues.</p>
        <a class="btn btn-primary mt-2" href="${pageContext.request.contextPath}/catalogue">Back to the catalogue</a>
    </div>
</main>
</body>
</html>
