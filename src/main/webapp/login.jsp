<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
    <title>LIBIS | Sign in</title>
    <style>
        body { min-height: 100vh; display: flex; align-items: center; justify-content: center;
               background: linear-gradient(135deg, #1a365d 0%, #2c5282 100%); }
        .login-card { width: 100%; max-width: 400px; }
    </style>
</head>
<body>
<div class="login-card page-card p-4 p-md-5 m-3">
    <div class="text-center mb-4">
        <h1 class="h2 fw-bold" style="color:#1a365d; letter-spacing:3px">LIBIS</h1>
        <p class="text-muted small mb-0">Library Information System</p>
    </div>
    <c:if test="${not empty param.registered}">
        <div class="alert alert-success py-2">
            Your account has been created (member number <c:out value="${param.registered}"/>). Sign in below.
        </div>
    </c:if>
    <c:if test="${param.loggedOut == 'true'}">
        <div class="alert alert-success py-2">You have been signed out.</div>
    </c:if>
    <%-- Jakarta Security FORM login: the container handles j_security_check --%>
    <form method="post" action="${pageContext.request.contextPath}/j_security_check">
        <div class="mb-3">
            <label class="form-label" for="j_username">Username</label>
            <input class="form-control" type="text" id="j_username" name="j_username" required autofocus autocomplete="username">
        </div>
        <div class="mb-3">
            <label class="form-label" for="j_password">Password</label>
            <input class="form-control" type="password" id="j_password" name="j_password" required autocomplete="current-password">
        </div>
        <button type="submit" class="btn btn-primary w-100">Sign in</button>
    </form>
    <p class="text-center small mt-3 mb-0">
        New to LIBIS? <a href="${pageContext.request.contextPath}/register">Create an account</a>
    </p>
</div>
</body>
</html>
