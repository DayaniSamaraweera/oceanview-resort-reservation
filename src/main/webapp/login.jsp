<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%


    // Check if user is already logged in - redirect to dashboard
    if (session.getAttribute("loggedInUser") != null) {
        response.sendRedirect(request.getContextPath() + "/DashboardController");
        return;
    }

    // Read error and success messages from URL parameters
    String errorParam = request.getParameter("error");
    String messageParam = request.getParameter("message");

    // Check for Remember Me cookie to pre-fill username
    String savedUsername = "";
    Cookie[] browserCookies = request.getCookies();
    if (browserCookies != null) {
        for (Cookie singleCookie : browserCookies) {
            if ("oceanview_username".equals(singleCookie.getName())) {
                savedUsername = singleCookie.getValue();
                break;
            }
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In - Ocean View Resort</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/oceanview-styles.css">
</head>
<body>

    <div class="login-container">

       
        <div class="login-left-panel">
<img src="<%= request.getContextPath() %>/images/resortLogo.png" alt="Ocean View Resort Logo" style="width: 100px; height: 100px; border-radius: 50%; margin-bottom: 15px; border: 3px solid rgba(255,255,255,0.3); object-fit: cover; background: white;">            <h1>Ocean View Resort</h1>
            <p>Experience luxury by the ocean in beautiful Galle, Sri Lanka. 
               Your gateway to serene beachside hospitality.</p>
        </div>

       
        <div class="login-right-panel">
            <div class="login-form-wrapper">

                <h2>Welcome Back</h2>
                <p class="login-subtitle">Sign in to manage reservations</p>

                <!-- Error Messages -->
                <% if ("invalid_credentials".equals(errorParam)) { %>
                    <div class="alert alert-error">
                        ⚠️ Invalid username or password. Please try again.
                    </div>
                <% } else if ("empty_fields".equals(errorParam)) { %>
                    <div class="alert alert-error">
                        ⚠️ Please enter both username and password.
                    </div>
                <% } else if ("session_expired".equals(errorParam)) { %>
                    <div class="alert alert-warning">
                        🔒 Your session has expired. Please sign in again.
                    </div>
                <% } %>

                <!-- Success Messages -->
                <% if ("logged_out".equals(messageParam)) { %>
                    <div class="alert alert-success">
                        ✅ You have been signed out successfully.
                    </div>
                <% } %>

                <!-- Login Form - Posts to LoginController servlet -->
                <form action="<%= request.getContextPath() %>/LoginController" method="POST">

                    <div class="form-group">
                        <label for="username">Username</label>
                        <input type="text" id="username" name="username" 
                               placeholder="Enter your username"
                               value="<%= savedUsername %>" required>
                    </div>

                    <div class="form-group">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" 
                               placeholder="Enter your password" required>
                    </div>

                    <div class="remember-row">
                        <label>
                            <input type="checkbox" name="rememberMe"
                                   <%= !savedUsername.isEmpty() ? "checked" : "" %>>
                            Remember Me
                        </label>
                    </div>

                    <button type="submit" class="btn-primary">Sign In</button>

                </form>

                <div style="text-align: center; margin-top: 25px; font-size: 0.8rem; color: #999;">
                    <p>&copy; 2026 Ocean View Resort | Galle, Sri Lanka</p>
                </div>

            </div>
        </div>

    </div>

   
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            var usernameField = document.getElementById('username');
            var passwordField = document.getElementById('password');
            
            // If username is pre-filled (Remember Me), focus password
            if (usernameField.value.trim() !== '') {
                passwordField.focus();
            } else {
                usernameField.focus();
            }

            // Enter key submits form from password field
            passwordField.addEventListener('keypress', function(event) {
                if (event.key === 'Enter') {
                    event.target.closest('form').submit();
                }
            });
        });
    </script>

</body>
</html>