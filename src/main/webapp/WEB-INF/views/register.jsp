<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
    <title>LIBIS | Create an account</title>
    <style>
        body { min-height: 100vh; display: flex; align-items: center; justify-content: center;
               background: linear-gradient(135deg, #1a365d 0%, #2c5282 100%); }
        .register-card { width: 100%; max-width: 480px; }
    </style>
</head>
<body>
<div class="register-card page-card p-4 p-md-5 m-3">
    <div class="text-center mb-4">
        <h1 class="h2 fw-bold" style="color:#1a365d; letter-spacing:3px">LIBIS</h1>
        <p class="text-muted small mb-0">Create a library member account</p>
    </div>

    <c:if test="${not empty errors.general}">
        <div class="alert alert-danger py-2"><c:out value="${errors.general}"/></div>
    </c:if>

    <form method="post" action="${ctx}/register" novalidate>
        <div class="mb-3">
            <label class="form-label" for="fullName">Full name</label>
            <input class="form-control ${not empty errors.fullName ? 'is-invalid' : ''}" id="fullName" name="fullName"
                   value="${fn:escapeXml(form.fullName)}" autocomplete="name" autofocus>
            <div class="invalid-feedback"><c:out value="${errors.fullName}"/></div>
        </div>
        <div class="mb-3">
            <label class="form-label" for="email">Email</label>
            <input type="email" class="form-control ${not empty errors.email ? 'is-invalid' : ''}" id="email" name="email"
                   value="${fn:escapeXml(form.email)}" autocomplete="email" placeholder="you@bac.ac.bw">
            <div class="invalid-feedback"><c:out value="${errors.email}"/></div>
        </div>
        <div class="mb-3">
            <label class="form-label" for="username">Username</label>
            <input class="form-control ${not empty errors.username ? 'is-invalid' : ''}" id="username" name="username"
                   value="${fn:escapeXml(form.username)}" autocomplete="username">
            <div class="invalid-feedback"><c:out value="${errors.username}"/></div>
            <div class="form-text">You will sign in with this. 3 to 50 letters, numbers, dot, dash or underscore.</div>
        </div>
        <div class="row g-3 mb-3">
            <div class="col-sm-6">
                <label class="form-label" for="password">Password</label>
                <input type="password" class="form-control ${not empty errors.password ? 'is-invalid' : ''}"
                       id="password" name="password" autocomplete="new-password">
                <div class="invalid-feedback"><c:out value="${errors.password}"/></div>
            </div>
            <div class="col-sm-6">
                <label class="form-label" for="confirmPassword">Confirm password</label>
                <input type="password" class="form-control ${not empty errors.confirmPassword ? 'is-invalid' : ''}"
                       id="confirmPassword" name="confirmPassword" autocomplete="new-password">
                <div class="invalid-feedback"><c:out value="${errors.confirmPassword}"/></div>
            </div>
        </div>
        <button type="submit" class="btn btn-primary w-100">Create account</button>
    </form>

    <p class="text-center small mt-3 mb-0">
        Already have an account? <a href="${ctx}/catalogue">Sign in</a>
    </p>
</div>
</body>
</html>
