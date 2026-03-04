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
 * Controller Servlet for first-login password change.
 *
 * <p><b>Flow:</b> When Admin creates a new staff account with
 * temporary credentials, the mustChangePassword flag is set to true.
 * On first login, the AuthenticationFilter redirects the staff
 * member to changePassword.jsp. This controller processes the
 * new username and password, updates the database, resets the
 * flag, and redirects to the dashboard.</p>
 *
 * <p><b>Security:</b> New password is hashed with SHA-256 before
 * storage. Username uniqueness is enforced by the database.</p>
 *
 * @author Dayani Samaraweera
 * @version 1.0
 */
@WebServlet("/ChangePassword")
public class ChangePasswordController extends HttpServlet {

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

    /**
     * Processes the password change form submission.
     *
     * <p>Validates:
     * - New username: minimum 3 characters
     * - New password: minimum 5 characters
     * - Confirm password: must match new password</p>
     */
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