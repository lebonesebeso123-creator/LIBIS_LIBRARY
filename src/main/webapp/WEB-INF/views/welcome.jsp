<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ include file="/WEB-INF/views/fragments/i18n.jspf" %>
<c:set var="langPath" value="/catalogue"/>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
    <title><fmt:message key="app.name"/> | <fmt:message key="catalogue.title"/></title>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>

<main class="container my-4">
    <div class="page-card p-4">
        <h1 class="h3 mb-1"><fmt:message key="catalogue.title"/></h1>
        <p class="text-muted"><fmt:message key="catalogue.subtitle"/></p>

        <%-- Specific reason from LoanService (LoanException) --%>
        <c:if test="${not empty loanError}">
            <div class="alert alert-danger"><c:out value="${loanError}"/></div>
        </c:if>

        <form class="row g-2 mb-2" method="get" action="${ctx}/catalogue">
            <div class="col-md-8">
                <fmt:message key="catalogue.searchPlaceholder" var="ph"/>
                <input type="text" name="q" class="form-control" placeholder="${fn:escapeXml(ph)}"
                       value="${fn:escapeXml(query)}">
            </div>
            <div class="col-md-4 d-flex gap-2">
                <button type="submit" class="btn btn-primary"><fmt:message key="catalogue.search"/></button>
                <c:if test="${not empty query}">
                    <a class="btn btn-outline-secondary" href="${ctx}/catalogue"><fmt:message key="catalogue.clear"/></a>
                </c:if>
            </div>
        </form>

        <p class="small text-muted">
            <c:choose>
                <c:when test="${empty query}">
                    <fmt:message key="catalogue.showing" var="countMsg"><fmt:param value="${fn:length(books)}"/></fmt:message>
                </c:when>
                <c:otherwise>
                    <fmt:message key="catalogue.showingFor" var="countMsg">
                        <fmt:param value="${fn:length(books)}"/>
                        <fmt:param value="${query}"/>
                    </fmt:message>
                </c:otherwise>
            </c:choose>
            <c:out value="${countMsg}"/>
        </p>

        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead>
                <tr>
                    <th><fmt:message key="catalogue.col.title"/></th>
                    <th><fmt:message key="catalogue.col.author"/></th>
                    <th><fmt:message key="catalogue.col.genre"/></th>
                    <th><fmt:message key="catalogue.col.year"/></th>
                    <th><fmt:message key="catalogue.col.availability"/></th>
                    <th><fmt:message key="catalogue.col.actions"/></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="book" items="${books}">
                    <tr>
                        <td><c:out value="${book.title}"/></td>
                        <td><c:out value="${book.author}"/></td>
                        <td><c:out value="${book.genre}"/></td>
                        <td><c:out value="${book.publishedYear}"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${book.available}">
                                    <span class="badge badge-available"><fmt:message key="catalogue.available"/></span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-onloan"><fmt:message key="catalogue.onLoan"/></span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="actions">
                            <c:if test="${isMember}">
                                <%-- Borrow is a real POST action; LoanService decides --%>
                                <form method="post" action="${ctx}/loans/borrow">
                                    <input type="hidden" name="bookId" value="${book.id}">
                                    <button type="submit" class="btn btn-sm btn-success"><fmt:message key="catalogue.borrow"/></button>
                                </form>
                                <a href="#" class="btn btn-sm btn-outline-secondary"><fmt:message key="catalogue.reserve"/></a>
                            </c:if>
                            <c:if test="${isLibrarian}">
                                <c:url var="editUrl" value="/librarian/books/edit"><c:param name="id" value="${book.id}"/></c:url>
                                <a href="${editUrl}" class="btn btn-sm btn-outline-primary"><fmt:message key="catalogue.edit"/></a>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty books}">
                    <tr><td colspan="6" class="text-center text-muted py-4"><fmt:message key="catalogue.noResults"/></td></tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
