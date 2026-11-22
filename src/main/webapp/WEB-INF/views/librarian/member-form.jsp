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
    <title>LIBIS | ${editing ? 'Edit' : 'Add'} Member</title>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>
<main class="container my-4" style="max-width: 760px">
    <div class="page-card p-4">
        <h1 class="h3 mb-3">${editing ? 'Edit member' : 'Add member'}</h1>
        <%@ include file="/WEB-INF/views/fragments/form-errors.jspf" %>

        <form method="post" action="${ctx}/librarian/members/${editing ? 'update' : 'create'}" novalidate>
            <c:if test="${editing}">
                <input type="hidden" name="id" value="${fn:escapeXml(form.id)}">
            </c:if>
            <div class="row g-3">
                <div class="col-md-4">
                    <label class="form-label" for="memberNumber">Member number *</label>
                    <input class="form-control ${not empty errors.memberNumber ? 'is-invalid' : ''}" id="memberNumber"
                           name="memberNumber" value="${fn:escapeXml(form.memberNumber)}" placeholder="LIB-006">
                    <div class="invalid-feedback"><c:out value="${errors.memberNumber}"/></div>
                </div>
                <div class="col-md-8">
                    <label class="form-label" for="fullName">Full name *</label>
                    <input class="form-control ${not empty errors.fullName ? 'is-invalid' : ''}" id="fullName"
                           name="fullName" value="${fn:escapeXml(form.fullName)}">
                    <div class="invalid-feedback"><c:out value="${errors.fullName}"/></div>
                </div>
                <div class="col-md-6">
                    <label class="form-label" for="email">Email *</label>
                    <input type="email" class="form-control ${not empty errors.email ? 'is-invalid' : ''}" id="email"
                           name="email" value="${fn:escapeXml(form.email)}">
                    <div class="invalid-feedback"><c:out value="${errors.email}"/></div>
                </div>
                <div class="col-md-6">
                    <label class="form-label" for="username">Username *</label>
                    <input class="form-control ${not empty errors.username ? 'is-invalid' : ''}" id="username"
                           name="username" value="${fn:escapeXml(form.username)}">
                    <div class="invalid-feedback"><c:out value="${errors.username}"/></div>
                </div>
                <div class="col-md-6">
                    <label class="form-label" for="password">Password ${editing ? '' : '*'}</label>
                    <input type="password" class="form-control ${not empty errors.password ? 'is-invalid' : ''}"
                           id="password" name="password" autocomplete="new-password">
                    <div class="invalid-feedback"><c:out value="${errors.password}"/></div>
                    <c:if test="${editing}">
                        <div class="form-text">Leave blank to keep the current password.</div>
                    </c:if>
                </div>
                <div class="col-md-6">
                    <label class="form-label" for="role">Role *</label>
                    <select class="form-select ${not empty errors.role ? 'is-invalid' : ''}" id="role" name="role">
                        <option value="member" ${form.role == 'member' ? 'selected' : ''}>Member</option>
                        <option value="librarian" ${form.role == 'librarian' ? 'selected' : ''}>Librarian</option>
                    </select>
                    <div class="invalid-feedback"><c:out value="${errors.role}"/></div>
                </div>
            </div>
            <div class="mt-4 d-flex gap-2">
                <button type="submit" class="btn btn-primary">${editing ? 'Save changes' : 'Create member'}</button>
                <a class="btn btn-outline-secondary" href="${ctx}/librarian/members">Cancel</a>
            </div>
        </form>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
