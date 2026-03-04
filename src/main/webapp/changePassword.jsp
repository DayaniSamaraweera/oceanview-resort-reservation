<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.oceanview.model.SystemUser" %>
<%
    /*
     * changePassword.jsp - First Login Password Change Page
     * 
     * Purpose: When Admin creates a new staff account with temporary
     * credentials, the staff member is forced to change their username
     * and password on first login before accessing the dashboard.
     * 
     * Flow:
     * 1. Admin creates staff account (mustChangePassword = true)
     * 2. Staff logs in with temporary credentials
     * 3. AuthenticationFilter redirects to this page
     * 4. Staff sets new username and password
     * 5. ChangePasswordController updates DB and resets flag
     * 6. Staff is redirected to dashboard
     * 
     * Author: Dayani Samaraweera
     */

    // Get logged-in user from session
    SystemUser currentUser = (SystemUser) session.getAttribute("loggedInUser");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    // Read error parameter
    String errorParam = request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Change Password - Ocean View Resort</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/oceanview-styles.css">
</head>
<body>

    <div class="change-password-container">
        <div class="change-password-card">

            <div class="lock-icon">🔐</div>
            <h2>Change Your Credentials</h2>
            <p class="info-text">
                Welcome, <strong><%= currentUser.getFullName() %></strong>! 
                For security purposes, please set your own username and 
                password before continuing.
            </p>

            <!-- Error Messages -->
            <% if ("username_short".equals(errorParam)) { %>
                <div class="alert alert-error">
                    ⚠️ Username must be at least 3 characters long.
                </div>
            <% } else if ("password_short".equals(errorParam)) { %>
                <div class="alert alert-error">
                    ⚠️ Password must be at least 5 characters long.
                </div>
            <% } else if ("password_mismatch".equals(errorParam)) { %>
                <div class="alert alert-error">
                    ⚠️ Passwords do not match. Please try again.
                </div>
            <% } else if ("update_failed".equals(errorParam)) { %>
                <div class="alert alert-error">
                    ⚠️ Update failed. Username may already be taken.
                </div>
            <% } else if ("system_error".equals(errorParam)) { %>
                <div class="alert alert-error">
                    ⚠️ A system error occurred. Please try again.
                </div>
            <% } %>

            <!-- Password Change Form -->
            <form action="<%= request.getContextPath() %>/ChangePassword" method="POST"
                  id="changePasswordForm">

                                <div class="form-group">
                    <label for="newUsername">New Username</label>
                    <input type="text" id="newUsername" name="newUsername" 
                           placeholder="Choose a new username (min 3 chars)"
                           pattern=".{3,}" title="Minimum 3 characters required" required>
                </div>

                <div class="form-group">
                    <label for="newPassword">New Password</label>
                    <input type="password" id="newPassword" name="newPassword" 
                           placeholder="Choose a new password (min 5 chars)"
                           pattern=".{5,}" title="Minimum 5 characters required" required>
                </div>

                <div class="form-group">
                    <label for="confirmPassword">Confirm Password</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" 
                           placeholder="Re-enter your new password"
                           pattern=".{5,}" title="Minimum 5 characters required" required>
                </div>

                <button type="submit" class="btn-primary">
                    Update Credentials &amp; Continue
                </button>

            </form>

            <div style="margin-top: 20px; font-size: 0.75rem; color: #999;">
                <p>&copy; 2026 Ocean View Resort | Galle, Sri Lanka</p>
            </div>

        </div>
    </div>

    <!-- Event-Driven: Password match validation -->
    <script>
        document.addEventListener('DOMContentLoaded', function() {

            var changeForm = document.getElementById('changePasswordForm');
            var newPasswordField = document.getElementById('newPassword');
            var confirmPasswordField = document.getElementById('confirmPassword');

            // Validate passwords match before form submission
            changeForm.addEventListener('submit', function(event) {
                var newPwd = newPasswordField.value;
                var confirmPwd = confirmPasswordField.value;

                if (newPwd !== confirmPwd) {
                    event.preventDefault();
                    alert('Passwords do not match. Please try again.');
                    confirmPasswordField.focus();
                    return false;
                }

                if (newPwd.length < 5) {
                    event.preventDefault();
                    alert('Password must be at least 5 characters long.');
                    newPasswordField.focus();
                    return false;
                }
            });

            // Real-time password match indicator
            confirmPasswordField.addEventListener('input', function() {
                if (this.value === newPasswordField.value && this.value.length > 0) {
                    this.style.borderColor = '#27ae60';
                } else if (this.value.length > 0) {
                    this.style.borderColor = '#e74c3c';
                } else {
                    this.style.borderColor = '#e0e0e0';
                }
            });

            // Focus first field
            document.getElementById('newUsername').focus();
        });
    </script>

</body>
</html>