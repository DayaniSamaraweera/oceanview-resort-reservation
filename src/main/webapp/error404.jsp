<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    /*
     * error404.jsp - Custom Page Not Found
     * Security: Prevents showing default Tomcat error pages.
     */
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>404 - Page Not Found | Ocean View Resort</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/oceanview-styles.css">
</head>
<body>
    <div class="error-container">
        <div class="error-card">
            <div class="error-code">404</div>
            <h2 class="error-message">Oops! Page Not Found</h2>
            <p style="color: #888; margin-bottom: 30px;">
                The page you are looking for might have been removed, had its name changed, or is temporarily unavailable.
            </p>
            <a href="<%= request.getContextPath() %>/DashboardController" class="btn-primary">
                Return to Dashboard
            </a>
        </div>
    </div>
</body>
</html>