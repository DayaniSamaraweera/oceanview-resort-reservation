package com.oceanview.controller;

import com.oceanview.model.SystemUser;
import com.oceanview.service.IAuditTrailOrchestrator;
import com.oceanview.service.AuditTrailOrchestratorImpl;
import com.oceanview.service.IUserAuthenticationOrchestrator;
import com.oceanview.service.UserAuthenticationOrchestratorImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

/**Handles user login and logout.
Authenticates credentials, manages sessions and remember-me cookies,
and redirects based on role. Forces password change on first login. */


@WebServlet("/LoginController")
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    /** Logger for login events */
    private static final Logger LOGIN_LOGGER =
            Logger.getLogger(LoginController.class.getName());

    /** Authentication service dependency */
    private IUserAuthenticationOrchestrator authService;

    /** Audit trail service dependency */
    private IAuditTrailOrchestrator auditService;

    @Override
    public void init() throws ServletException {
        authService = new UserAuthenticationOrchestratorImpl();
        auditService = new AuditTrailOrchestratorImpl();
    }

    /**
     * Handles login form POST submission.
     *
     * <p>Flow:
     * 1. Extract username and password from form
     * 2. Check "Remember Me" checkbox
     * 3. Authenticate via service layer
     * 4. Create session and store user object
     * 5. Set Remember Me cookie if checked
     * 6. Log login event to audit trail
     * 7. Check mustChangePassword flag
     * 8. Redirect based on role (ADMIN/RECEPTIONIST)</p>
     */
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        // Step 1: Extract form data
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");

        // Step 2: Validate inputs
        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath()
                    + "/login.jsp?error=empty_fields");
            return;
        }

        // Step 3: Authenticate via service layer
        SystemUser authenticatedUser =
                authService.authenticateCredentials(
                        username.trim(), password);

        if (authenticatedUser == null) {
            // Authentication failed
            LOGIN_LOGGER.warning(
                    "Login failed for username: " + username);
            response.sendRedirect(request.getContextPath()
                    + "/login.jsp?error=invalid_credentials");
            return;
        }

        // Step 4: Create session and store user
        HttpSession newSession = request.getSession(true);
        newSession.setAttribute("loggedInUser", authenticatedUser);
        newSession.setAttribute("userId", authenticatedUser.getUserId());
        newSession.setAttribute("username", authenticatedUser.getUsername());
        newSession.setAttribute("userRole", authenticatedUser.getUserRole());
        newSession.setAttribute("fullName", authenticatedUser.getFullName());
        newSession.setMaxInactiveInterval(30 * 60); // 30 minutes

        // Step 5: Set Remember Me cookie if checked
        if ("on".equals(rememberMe)) {
            Cookie usernameCookie = new Cookie("oceanview_username",
                    authenticatedUser.getUsername());
            usernameCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            usernameCookie.setPath(request.getContextPath());
            response.addCookie(usernameCookie);
            LOGIN_LOGGER.info("Remember Me cookie set for: "
                    + authenticatedUser.getUsername());
        }

        // Step 6: Log login to audit trail
        auditService.logActivity(
                authenticatedUser.getUserId(),
                authenticatedUser.getUsername(),
                "USER_LOGIN",
                "User logged in successfully (Role: "
                        + authenticatedUser.getUserRole() + ")",
                "users",
                authenticatedUser.getUserId(),
                request.getRemoteAddr());

        // Step 7: Check if user must change password (first login)
        if (authenticatedUser.getMustChangePassword()) {
            LOGIN_LOGGER.info("First login detected for: "
                    + authenticatedUser.getUsername()
                    + " - redirecting to password change");
            response.sendRedirect(request.getContextPath()
                    + "/changePassword.jsp");
            return;
        }

        // Step 8: Redirect based on role
        LOGIN_LOGGER.info("Login successful: "
                + authenticatedUser.getUsername()
                + " (Role: " + authenticatedUser.getUserRole() + ")");
        response.sendRedirect(request.getContextPath()
                + "/DashboardController");
    }

    /**
     * Handles logout via GET request.
     * Invalidates session, clears cookies, and logs the event.
     */
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("logout".equals(action)) {
            HttpSession existingSession = request.getSession(false);

            if (existingSession != null) {
                SystemUser loggedUser = (SystemUser)
                        existingSession.getAttribute("loggedInUser");

                if (loggedUser != null) {
                    // Log logout to audit trail
                    auditService.logActivity(
                            loggedUser.getUserId(),
                            loggedUser.getUsername(),
                            "USER_LOGOUT",
                            "User logged out",
                            "users",
                            loggedUser.getUserId(),
                            request.getRemoteAddr());
                }

                existingSession.invalidate();
            }

            // Clear Remember Me cookie
            Cookie clearCookie = new Cookie("oceanview_username", "");
            clearCookie.setMaxAge(0);
            clearCookie.setPath(request.getContextPath());
            response.addCookie(clearCookie);

            response.sendRedirect(request.getContextPath()
                    + "/login.jsp?message=logged_out");

        } else {
            response.sendRedirect(request.getContextPath()
                    + "/login.jsp");
        }
    }
}