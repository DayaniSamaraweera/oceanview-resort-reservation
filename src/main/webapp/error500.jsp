<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%
    
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>500 - System Error | Ocean View Resort</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/oceanview-styles.css">
</head>
<body>
    <div class="error-container">
        <div class="error-card">
            <div style="font-size: 5rem;">⚠️</div>
            <div class="error-code">500</div>
            <h2 class="error-message">Internal Server Error</h2>
            <p style="color: #888; margin-bottom: 30px;">
                Something went wrong on our end. Our technical team has been notified. 
                Please try again later or contact the administrator.
            </p>
            <div class="btn-group" style="justify-content: center;">
                <a href="javascript:history.back()" class="btn-secondary">Go Back</a>
                <a href="<%= request.getContextPath() %>/DashboardController" class="btn-primary" style="width:auto;">
                    Dashboard
                </a>
            </div>
        </div>
    </div>
</body>
</html>