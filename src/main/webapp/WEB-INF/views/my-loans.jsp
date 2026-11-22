<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ include file="/WEB-INF/views/fragments/i18n.jspf" %>
<c:set var="langPath" value="/loans"/>
<!DOCTYPE html>
<html lang="${pageContext.response.locale.language}">
<head>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
    <title><fmt:message key="app.name"/> | <fmt:message key="loans.title"/></title>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>

<main class="container my-4">
    <div class="page-card p-4">
        <h1 class="h3 mb-1"><fmt:message key="loans.title"/></h1>
        <p class="text-muted"><fmt:message key="loans.subtitle"/></p>

        <%-- Success message after a borrow/return redirect (translated through the bundle) --%>
        <c:if test="${not empty sessionScope.flashKey}">
            <fmt:message key="${sessionScope.flashKey}" var="flashMsg">
                <fmt:param value="${sessionScope.flashArg}"/>
            </fmt:message>
            <div class="alert alert-success"><c:out value="${flashMsg}"/></div>
            <c:remove var="flashKey" scope="session"/>
            <c:remove var="flashArg" scope="session"/>
        </c:if>
        <c:if test="${not empty loanError}">
            <div class="alert alert-danger"><c:out value="${loanError}"/></div>
        </c:if>

        <fmt:message key="loans.inUse" var="inUse">
            <fmt:param value="${loanCount}"/>
            <fmt:param value="${maxLoans}"/>
        </fmt:message>
        <div class="alert ${loanCount >= maxLoans ? 'alert-warning' : 'alert-info'} py-2">
            <c:out value="${inUse}"/>
        </div>

        <h2 class="h5 mt-4"><fmt:message key="loans.current"/></h2>
        <c:choose>
            <c:when test="${empty currentLoans}">
                <p class="text-muted"><fmt:message key="loans.noCurrent"/>
                    <a href="${ctx}/catalogue"><fmt:message key="loans.browse"/></a></p>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table align-middle">
                        <thead>
                        <tr>
                            <th><fmt:message key="loans.col.title"/></th>
                            <th><fmt:message key="loans.col.author"/></th>
                            <th><fmt:message key="loans.col.borrowedOn"/></th>
                            <th><fmt:message key="loans.col.actions"/></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="loan" items="${currentLoans}">
                            <tr>
                                <td><c:out value="${loan.bookTitle}"/></td>
                                <td><c:out value="${loan.bookAuthor}"/></td>
                                <td><c:out value="${loan.borrowedOn}"/></td>
                                <td class="actions">
                                    <form method="post" action="${ctx}/loans/return">
                                        <input type="hidden" name="bookId" value="${loan.bookId}">
                                        <button type="submit" class="btn btn-sm btn-primary"><fmt:message key="loans.return"/></button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>

        <h2 class="h5 mt-4"><fmt:message key="loans.history"/></h2>
        <c:choose>
            <c:when test="${empty history}">
                <p class="text-muted"><fmt:message key="loans.noHistory"/></p>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-striped align-middle">
                        <thead>
                        <tr>
                            <th><fmt:message key="loans.col.title"/></th>
                            <th><fmt:message key="loans.col.author"/></th>
                            <th><fmt:message key="loans.col.borrowedOn"/></th>
                            <th><fmt:message key="loans.col.returnedOn"/></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="loan" items="${history}">
                            <tr>
                                <td><c:out value="${loan.bookTitle}"/></td>
                                <td><c:out value="${loan.bookAuthor}"/></td>
                                <td><c:out value="${loan.borrowedOn}"/></td>
                                <td><c:out value="${loan.returnedOn}"/></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
