<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- Entry point: send everyone to the login controller (it skips to the catalogue if already signed in) --%>
<% response.sendRedirect(request.getContextPath() + "/login"); %>
