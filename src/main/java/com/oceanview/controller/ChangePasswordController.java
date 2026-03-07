package com.oceanview.controller;

import com.oceanview.dao.ISystemUserGateway;
import com.oceanview.dao.SystemUserGatewayImpl;
import com.oceanview.model.SystemUser;
import com.oceanview.service.IAuditTrailOrchestrator;
import com.oceanview.service.AuditTrailOrchestratorImpl;
import com.oceanview.util.PasswordHashGenerator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Handles first-time password change for new staff accounts.
 * Updates username/password and redirects to dashboard.
 */

@WebServlet("/ChangePassword")
public class ChangePasswordController extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /** Logger for password change events */
    private static final Logger PWD_LOGGER =
            Logger.getLogger(ChangePasswordController.class.getName());

    /** DAO for direct password update */
    private ISystemUserGateway userGateway;

    /** Audit trail service */
    private IAuditTrailOrchestrator auditService;

    @Override
    public void init() throws ServletException {
        userGateway = new SystemUserGatewayImpl();
        auditService = new AuditTrailOrchestratorImpl();
    }

    //Processes the password change form submission.

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Check session exists
        if (session == null
                || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath()
                    + "/login.jsp?error=session_expired");
            return;
        }

        SystemUser currentUser =
                (SystemUser) session.getAttribute("loggedInUser");

        // Extract form data
        String newUsername = request.getParameter("newUsername");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Validate new username
        if (newUsername == null || newUsername.trim().length() < 3) {
            response.sendRedirect(request.getContextPath()
                    + "/changePassword.jsp?error=username_short");
            return;
        }

        // Validate new password
        if (newPassword == null || newPassword.length() < 5) {
            response.sendRedirect(request.getContextPath()
                    + "/changePassword.jsp?error=password_short");
            return;
        }

        // Validate password confirmation
        if (!newPassword.equals(confirmPassword)) {
            response.sendRedirect(request.getContextPath()
                    + "/changePassword.jsp?error=password_mismatch");
            return;
        }

        // Hash the new password
        String newPasswordHash =
                PasswordHashGenerator.generateHash(newPassword);

        if (newPasswordHash == null) {
            response.sendRedirect(request.getContextPath()
                    + "/changePassword.jsp?error=system_error");
            return;
        }

        // Update password in database (also resets must_change_password to 0)
        boolean updateSuccess = userGateway.updatePassword(
                currentUser.getUserId(),
                newUsername.trim(),
                newPasswordHash);

        if (updateSuccess) {
            PWD_LOGGER.info("Password changed successfully for user: "
                    + currentUser.getUsername()
                    + " → new username: " + newUsername.trim());

            // Update session with new values
            currentUser.setUsername(newUsername.trim());
            currentUser.setPasswordHash(newPasswordHash);
            currentUser.setMustChangePassword(false);
            session.setAttribute("loggedInUser", currentUser);
            session.setAttribute("username", newUsername.trim());

            // Log to audit trail
            auditService.logActivity(
                    currentUser.getUserId(),
                    newUsername.trim(),
                    "PASSWORD_CHANGED",
                    "Staff member changed temporary credentials",
                    "users",
                    currentUser.getUserId(),
                    request.getRemoteAddr());

            // Redirect to dashboard
            response.sendRedirect(request.getContextPath()
                    + "/DashboardController");

        } else {
            PWD_LOGGER.warning("Password change failed for user: "
                    + currentUser.getUsername());
            response.sendRedirect(request.getContextPath()
                    + "/changePassword.jsp?error=update_failed");
        }
    }
}