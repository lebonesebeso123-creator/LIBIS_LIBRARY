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
    <title>LIBIS | ${editing ? 'Edit' : 'Add'} Book</title>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>
<main class="container my-4" style="max-width: 760px">
    <div class="page-card p-4">
        <h1 class="h3 mb-3">${editing ? 'Edit book' : 'Add book'}</h1>
        <%@ include file="/WEB-INF/views/fragments/form-errors.jspf" %>

        <form method="post" action="${ctx}/librarian/books/${editing ? 'update' : 'create'}" novalidate>
            <c:if test="${editing}">
                <input type="hidden" name="id" value="${fn:escapeXml(form.id)}">
            </c:if>
            <div class="row g-3">
                <div class="col-md-5">
                    <label class="form-label" for="isbn">ISBN *</label>
                    <input class="form-control ${not empty errors.isbn ? 'is-invalid' : ''}" id="isbn" name="isbn"
                           value="${fn:escapeXml(form.isbn)}" placeholder="9780385474542">
                    <div class="invalid-feedback"><c:out value="${errors.isbn}"/></div>
                </div>
                <div class="col-md-7">
                    <label class="form-label" for="title">Title *</label>
                    <input class="form-control ${not empty errors.title ? 'is-invalid' : ''}" id="title" name="title"
                           value="${fn:escapeXml(form.title)}">
                    <div class="invalid-feedback"><c:out value="${errors.title}"/></div>
                </div>
                <div class="col-md-6">
                    <label class="form-label" for="author">Author *</label>
                    <input class="form-control ${not empty errors.author ? 'is-invalid' : ''}" id="author" name="author"
                           value="${fn:escapeXml(form.author)}">
                    <div class="invalid-feedback"><c:out value="${errors.author}"/></div>
                </div>
                <div class="col-md-3">
                    <label class="form-label" for="genre">Genre</label>
                    <input class="form-control ${not empty errors.genre ? 'is-invalid' : ''}" id="genre" name="genre"
                           value="${fn:escapeXml(form.genre)}">
                    <div class="invalid-feedback"><c:out value="${errors.genre}"/></div>
                </div>
                <div class="col-md-3">
                    <label class="form-label" for="publishedYear">Published year</label>
                    <%-- type="text" on purpose: the SERVER validates the year (e.g. "20x4") --%>
                    <input type="text" class="form-control ${not empty errors.publishedYear ? 'is-invalid' : ''}"
                           id="publishedYear" name="publishedYear" value="${fn:escapeXml(form.publishedYear)}" placeholder="2014">
                    <div class="invalid-feedback"><c:out value="${errors.publishedYear}"/></div>
                </div>
                <div class="col-12">
                    <label class="form-label" for="description">Description</label>
                    <textarea class="form-control ${not empty errors.description ? 'is-invalid' : ''}" id="description"
                              name="description" rows="3"><c:out value="${form.description}"/></textarea>
                    <div class="invalid-feedback"><c:out value="${errors.description}"/></div>
                </div>
            </div>
            <div class="mt-4 d-flex gap-2">
                <button type="submit" class="btn btn-primary">${editing ? 'Save changes' : 'Create book'}</button>
                <a class="btn btn-outline-secondary" href="${ctx}/librarian/books">Cancel</a>
            </div>
        </form>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
