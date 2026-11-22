<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LIBIS | Sign in</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
            background: linear-gradient(135deg, #1a365d 0%, #2c5282 100%);
            color: #2d3748;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }
        .card {
            background: white;
            width: 100%;
            max-width: 400px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.25);
            padding: 36px 32px;
        }
        .brand { text-align: center; margin-bottom: 26px; }
        .brand h1 { color: #1a365d; font-size: 2rem; letter-spacing: 3px; }
        .brand p { color: #718096; font-size: 0.9rem; margin-top: 4px; }
        label { display: block; font-size: 0.85rem; font-weight: 600; margin-bottom: 6px; color: #4a5568; }
        input[type=email], input[type=password] {
            width: 100%;
            padding: 11px 13px;
            border: 1px solid #cbd5e0;
            border-radius: 8px;
            font-size: 0.95rem;
            margin-bottom: 18px;
        }
        input:focus { outline: none; border-color: #2b6cb0; box-shadow: 0 0 0 3px rgba(43,108,176,0.2); }
        button {
            width: 100%;
            padding: 12px;
            background: #2c5282;
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 1rem;
            font-weight: 600;
            cursor: pointer;
        }
        button:hover { background: #1a365d; }
        .msg { padding: 10px 12px; border-radius: 8px; font-size: 0.9rem; margin-bottom: 18px; }
        .error { background: #fff5f5; color: #c53030; border: 1px solid #feb2b2; }
        .info { background: #f0fff4; color: #276749; border: 1px solid #9ae6b4; }
        .hint { margin-top: 20px; font-size: 0.8rem; color: #a0aec0; text-align: center; }
    </style>
</head>
<body>
<div class="card">
    <div class="brand">
        <h1>LIBIS</h1>
        <p>Library Information System</p>
    </div>

    <c:if test="${not empty error}">
        <div class="msg error"><c:out value="${error}"/></div>
    </c:if>
    <c:if test="${param.loggedOut == 'true'}">
        <div class="msg info">You have been signed out.</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/login">
        <label for="email">Email</label>
        <input type="email" id="email" name="email" required autofocus
               placeholder="you@bac.ac.bw" value="<c:out value='${email}'/>">

        <label for="password">Password</label>
        <input type="password" id="password" name="password" required placeholder="••••••••">

        <button type="submit">Sign in</button>
    </form>

    <p class="hint">Demo: thabo.molefe@bac.ac.bw / pass123</p>
</div>
</body>
</html>
