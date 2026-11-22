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
    <title>LIBIS | Manage Members</title>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>
<main class="container my-4">
    <div class="page-card p-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h1 class="h3 m-0">Members</h1>
            <a class="btn btn-success" href="${ctx}/librarian/members/new">+ Add member</a>
        </div>
        <%@ include file="/WEB-INF/views/fragments/flash.jspf" %>

        <form class="row g-2 mb-3" method="get" action="${ctx}/librarian/members">
            <div class="col-md-8">
                <input type="text" name="q" class="form-control" placeholder="Search by name or member number"
                       value="${fn:escapeXml(query)}">
            </div>
            <div class="col-md-4 d-flex gap-2">
                <button class="btn btn-primary" type="submit">Search</button>
                <c:if test="${not empty query}"><a class="btn btn-outline-secondary" href="${ctx}/librarian/members">Clear</a></c:if>
            </div>
        </form>

        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead>
                <tr><th>Member no.</th><th>Full name</th><th>Email</th><th>Username</th><th>Role</th><th>Actions</th></tr>
                </thead>
                <tbody>
                <c:forEach var="m" items="${members}">
                    <tr>
                        <td><c:out value="${m.memberNumber}"/></td>
                        <td><c:out value="${m.fullName}"/></td>
                        <td><c:out value="${m.email}"/></td>
                        <td><c:out value="${m.username}"/></td>
                        <td>
                            <span class="badge ${m.librarian ? 'text-bg-dark' : 'text-bg-secondary'}"><c:out value="${m.role.value}"/></span>
                        </td>
                        <td class="text-nowrap">
                            <c:url var="editUrl" value="/librarian/members/edit"><c:param name="id" value="${m.id}"/></c:url>
                            <c:url var="delUrl" value="/librarian/members/delete"><c:param name="id" value="${m.id}"/></c:url>
                            <a class="btn btn-sm btn-outline-primary" href="${editUrl}">Edit</a>
                            <a class="btn btn-sm btn-outline-danger" href="${delUrl}">Delete</a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty members}">
                    <tr><td colspan="6" class="text-center text-muted py-4">No members match your search.</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
